package hello;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;

@RestController
public class AccountController {

    private final AccountLogicFacade logic;

    public AccountController(AccountLogicFacade logic) {
        this.logic = logic;
    }

    @GetMapping({"/account/create", "/account/create/"})
    public String createAccount(
        @RequestParam(value = "accounttype", required = false, defaultValue = "") String accountType,
        @RequestParam(value = "person",      required = false, defaultValue = "") String personKey,
        @RequestParam(value = "bank",        required = false, defaultValue = "") String bankName) {

    return logic.createAccount(personKey, bankName, accountType);
    }
    
    @GetMapping({"/account/find/person", "/account/find/person/"})
    public List<Account> findAccountByPerson(
        @RequestParam(value = "person", required = false, defaultValue = "") String personKey) {

    return logic.findAccountsByPerson(personKey);
    }

    @GetMapping({"/account/debit", "/account/debit/"})
    public String debitAccount(
            @RequestParam("id") long accountId,
            @RequestParam("amount") int amount) {

        boolean ok = logic.debit(accountId, amount);
        return ok ? "OK" : "FAILED";
    }

    @GetMapping({"/account/credit", "/account/credit/"})
    public String creditAccount(
            @RequestParam("id") long accountId,
            @RequestParam("amount") int amount) {

        boolean ok = logic.credit(accountId, amount);
        return ok ? "OK" : "FAILED";
    }

    // Spec: /account/transactions
    @GetMapping({"/account/transactions", "/account/transactions/"})
    public List<Transaction> getTransactions(
            @RequestParam("id") long accountId) {

        return logic.getTransactions(accountId);
    }
}