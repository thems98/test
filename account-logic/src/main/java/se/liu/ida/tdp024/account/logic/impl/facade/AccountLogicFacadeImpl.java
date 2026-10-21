package se.liu.ida.tdp024.account.logic.impl.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.util.KafkaConfig;
import se.liu.ida.tdp024.account.logic.kafka.KafkaPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;

@Service
public class AccountLogicFacadeImpl implements AccountLogicFacade {

    private static final Logger log = LoggerFactory.getLogger(AccountLogicFacadeImpl.class);

    private final AccountEntityFacade accountFacade;
    private final TransactionEntityFacade transactionFacade;
    private final RestTemplate restTemplate;
    private final KafkaPublisher kafkaPublisher;

    @Value("${personapi.base-url:http://localhost:8060/person}")
    private String personBaseUrl;

    @Value("${bankapi.base-url:http://localhost:8070/bank}")
    private String bankBaseUrl;

    public AccountLogicFacadeImpl(AccountEntityFacade accountFacade,
                                  TransactionEntityFacade transactionFacade,
                                  RestTemplate restTemplate,
                                  KafkaPublisher kafkaPublisher) {
        this.accountFacade = accountFacade;
        this.transactionFacade = transactionFacade;
        this.restTemplate = restTemplate;
        this.kafkaPublisher = kafkaPublisher;
    }

    // ============================================================
    // CREATE
    // ============================================================
    @Override
    public String createAccount(String personKey, String bankName, String accountType) {
        if (personKey == null || personKey.isBlank()) return "FAILED";
        if (bankName   == null || bankName.isBlank()) return "FAILED";
        if (!"CHECK".equals(accountType) && !"SAVINGS".equals(accountType)) return "FAILED";

        // if (!validatePerson(personKey) || !validateBank(bankName)) {
        //     return "FAILED";
        // }
        
        String ok = "FAILED";
        Long bankKey = validateBank(bankName);

        if (personKey == null || personKey.isBlank()) {
            log.warn("createAccount: empty personKey");
        } else if (bankName == null || bankName.isBlank()) {
            log.warn("createAccount: empty bankName");
        } else if (!"CHECK".equals(accountType)
                && !"SAVINGS".equals(accountType)) {
            log.warn("createAccount: invalid accountType '{}'", accountType);
        } else if (!validatePerson(personKey)) {
            log.warn("createAccount: unknown personKey '{}'", personKey);
        } else if (bankKey == null) {
            log.warn("createAccount: unknown bank '{}'", bankName);
        } else {
            ok = accountFacade.createAccount(accountType, personKey, bankKey);
        }
        publishRestEvent("createAccount", Map.of(
                "personKey", String.valueOf(personKey),
                "bankName", String.valueOf(bankName),
                "bankKey", String.valueOf(bankKey),
                "accountType", String.valueOf(accountType),
                "success", String.valueOf(ok)
        ));

        return ok;
    }

    // ============================================================
    // DEBIT
    // ============================================================
    @Override
    public boolean debit(long accountId, long amount) {
        if (amount <= 0) {
            transactionFacade.addTransaction(accountId, "DEBIT", amount, "FAILED");
            publishTransactionEvent(accountId, "DEBIT", amount, "FAILED");
            return false;
        }

        boolean ok;
        try {
            ok = accountFacade.debit(accountId, amount);
        } catch (Exception e) {
            log.error("debit({}, {}) threw", accountId, amount, e);
            ok = false;
        }

        // ALWAYS record the attempt — even FAILED — per the lab spec
        transactionFacade.addTransaction(accountId, "DEBIT", amount, ok ? "OK" : "FAILED");
        publishTransactionEvent(accountId, "DEBIT", amount, ok ? "OK" : "FAILED");
        return ok;
    }

    // ============================================================
    // CREDIT
    // ============================================================
    @Override
    public boolean credit(long accountId, long amount) {
        if (amount <= 0) {
            transactionFacade.addTransaction(accountId, "CREDIT", amount, "FAILED");
            publishTransactionEvent(accountId, "CREDIT", amount, "FAILED");
            return false;
        }

        boolean ok;
        try {
            ok = accountFacade.credit(accountId, amount);
        } catch (Exception e) {
            log.error("credit({}, {}) threw", accountId, amount, e);
            ok = false;
        }

        transactionFacade.addTransaction(accountId, "CREDIT", amount, ok ? "OK" : "FAILED");
        publishTransactionEvent(accountId, "CREDIT", amount, ok ? "OK" : "FAILED");
        return ok;
    }

    // ============================================================
    // LOOKUPS
    // ============================================================
    @Override
    public List<Account> findAccountsByPerson(String personKey) {
        if (personKey == null || personKey.isBlank()) return Collections.emptyList();
        return accountFacade.findByPerson(personKey);
    }

    @Override
    public List<Account> findAccountsByBank(long bankKey) {
        if (bankKey == 0) return Collections.emptyList();
        return accountFacade.findByBank(bankKey);
    }

    @Override
    public List<Transaction> getTransactions(long accountId) {
        // 1. Fetch all transactions for this account from the database
        List<Transaction> transactions = transactionFacade.getTransactions(accountId);

        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Fetch the Account object once (since all transactions belong to the same account)
        Account account = accountFacade.find(accountId);

        // 3. Attach the account object to each transaction so Jackson serializes it
        for (Transaction tx : transactions) {
            tx.setAccount(account);
        }

        return transactions;
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private boolean validatePerson(String personKey) {
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(
                    personBaseUrl + "/find.key?key={k}", String.class, personKey);
            return resp.getStatusCode().is2xxSuccessful()
                    && resp.getBody() != null
                    && !resp.getBody().equals("null")
                    && !resp.getBody().isEmpty();
        } catch (Exception e) {
            log.warn("personapi unreachable for key '{}': {}", personKey, e.getMessage());
            return false;
        }
    }

    private Long validateBank(String bankName) {
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(
                    bankBaseUrl + "/find.name?name={name}",
                    String.class,
                    bankName
            );

            String body = resp.getBody();

            if (!resp.getStatusCode().is2xxSuccessful()
                    || body == null
                    || body.isBlank()
                    || body.equals("null")) {
                return null;
            }

            JsonNode bank = new ObjectMapper().readTree(body);

            if (!bank.hasNonNull("key")) {
                log.warn("Bank API response has no key field: {}", body);
                return null;
            }

            return bank.get("key").asLong();

        } catch (Exception e) {
            log.warn("Failed to retrieve bank '{}' from bank API: {}",
                    bankName, e.getMessage());
            return null;
        }
    }

    private void publishTransactionEvent(long accountId, String type, long amount, String status) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("accountId", accountId);
        event.put("type", type);
        event.put("amount", amount);
        event.put("status", status);
        event.put("created", LocalDateTime.now().toString());

        kafkaPublisher.publish(KafkaConfig.TOPIC_TRANSACTIONS, event);
    }

    private void publishRestEvent(String action, Map<String, Object> details) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("action", action);
        event.put("timestamp", LocalDateTime.now().toString());
        event.putAll(details);
        kafkaPublisher.publish(KafkaConfig.TOPIC_REST_REQUESTS, event);
    }
}