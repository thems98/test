package se.liu.ida.tdp024.account.logic.test.facade;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.kafka.KafkaPublisher;
import se.liu.ida.tdp024.account.logic.util.KafkaConfig;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AccountLogicFacadeTest {

    @Mock AccountEntityFacade accountFacade;
    @Mock TransactionEntityFacade transactionFacade;
    @Mock RestTemplate restTemplate;
    @Mock KafkaPublisher kafkaPublisher;

    @InjectMocks AccountLogicFacadeImpl logic;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(logic, "personBaseUrl", "http://localhost:8060/person");
        ReflectionTestUtils.setField(logic, "bankBaseUrl",   "http://localhost:8070/bank");
    }

    private void personExists(String key) {
        when(restTemplate.getForEntity(
                contains("find.key"), 
                eq(String.class), 
                any(Object[].class)  // <--- Fix is here
        )).thenReturn(ResponseEntity.ok("{\"" + key + "\":\"Some Name\"}"));
    }

    private void bankExists(String name, String key) {
        when(restTemplate.getForEntity(
                contains("find.name"), 
                eq(String.class), 
                any(Object[].class)  // <--- Fix is here
        )).thenReturn(ResponseEntity.ok("{\"key\":\"" + key + "\",\"name\":\"" + name + "\"}"));
    }

    @Test
    public void createSuccess() {
        personExists("1");
        bankExists("SWEDBANK", "1");
        when(accountFacade.createAccount("CHECK", "1", 1L)).thenReturn("OK");

        assertEquals("OK", logic.createAccount("1", "SWEDBANK", "CHECK"));
        verify(accountFacade).createAccount("CHECK", "1", 1L);
        verify(kafkaPublisher).publish(eq(KafkaConfig.TOPIC_REST_REQUESTS), any());
    }

    @Test
    public void blankAndInvalidInputsFailWithoutHttp() {
        assertEquals("FAILED", logic.createAccount("",  "SWEDBANK", "CHECK"));
        assertEquals("FAILED", logic.createAccount("1", "",         "CHECK"));
        assertEquals("FAILED", logic.createAccount("1", "SWEDBANK", "CREDITCARD"));
        verifyNoInteractions(restTemplate);
    }

      @Test
    public void unknownPersonFails() {
        when(restTemplate.getForEntity(contains("find.key"), eq(String.class), any(Object[].class)))
            .thenReturn(ResponseEntity.ok("{}"));          // person service found nothing
        
        bankExists("SWEDBANK", "1");

        assertEquals("FAILED", logic.createAccount("bogus", "SWEDBANK", "CHECK"));
        verify(accountFacade, never()).createAccount(anyString(), anyString(), anyLong());
    }

    @Test
    public void unknownBankFails() {
        personExists("1");
        
        when(restTemplate.getForEntity(contains("find.name"), eq(String.class), any(Object[].class)))
            .thenReturn(ResponseEntity.ok("null"));

        assertEquals("FAILED", logic.createAccount("1", "NOPE", "CHECK"));
        verify(accountFacade, never()).createAccount(anyString(), anyString(), anyLong());
    }

    @Test
    public void creditAndDebitAlwaysLog() {
        when(accountFacade.credit(1L, 50L)).thenReturn(true);
        assertTrue(logic.credit(1L, 50L));
        verify(transactionFacade).addTransaction(1L, "CREDIT", 50L, "OK");
        verify(kafkaPublisher).publish(eq(KafkaConfig.TOPIC_TRANSACTIONS), any());

        assertFalse(logic.credit(1L, 0L));
        verify(transactionFacade).addTransaction(1L, "CREDIT", 0L, "FAILED");

        when(accountFacade.debit(1L, 20L)).thenReturn(true);
        assertTrue(logic.debit(1L, 20L));
        verify(transactionFacade).addTransaction(1L, "DEBIT", 20L, "OK");

        assertFalse(logic.debit(1L, -1L));
        verify(transactionFacade).addTransaction(1L, "DEBIT", -1L, "FAILED");
    }

    @Test
    public void getTransactionsAttachesAccount() {
        TransactionDB tx = new TransactionDB();
        tx.setId(10); tx.setAccountId(2); tx.setTransactionType("CREDIT");
        tx.setAmount(50); tx.setStatus("OK"); tx.setCreated("2026-01-01T00:00:00");

        AccountDB acc = new AccountDB();
        acc.setId(2); acc.setPersonId("1"); acc.setBankId(1);
        acc.setAccountType("CHECK"); acc.setHoldings(50);

        when(transactionFacade.getTransactions(2L)).thenReturn(List.of(tx));
        when(accountFacade.find(2L)).thenReturn(acc);

        List<Transaction> out = logic.getTransactions(2L);
        assertEquals(1, out.size());
        assertSame(acc, out.get(0).getAccount());
    }

    @Test
    public void lookupsGuardBlankInputs() {
        assertTrue(logic.findAccountsByPerson("").isEmpty());
        assertTrue(logic.findAccountsByBank(0).isEmpty());
        verifyNoInteractions(accountFacade);
    }
}