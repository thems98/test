package hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;

import java.util.List;

@RestController
public class AccountController {

    private final AccountLogicFacade logic;

    public AccountController(AccountLogicFacade logic) {
        this.logic = logic;
    }

    @GetMapping("/account/create")
    public String createAccount(
            @RequestParam("accounttype") String accountType,
            @RequestParam("person") Long personKey,
            @RequestParam("bank") String bankName) {

        String ok = logic.createAccount(personKey, bankName, accountType);
        System.out.println("--------------------------------------HEJ-------------------------------------");
        System.out.println(ok);
        return ok;
    }

    @GetMapping("/account/find/person")
    public List<Account> findAccountByPerson(
            @RequestParam("person") long personKey) {

        return logic.findAccountsByPerson(personKey);
    }

    @GetMapping("/account/debit")
    public String debitAccount(
            @RequestParam("id") long accountId,
            @RequestParam("amount") int amount) {

        boolean ok = logic.debit(accountId, amount);
        return ok ? "OK" : "FAILED";
    }

    @GetMapping("/account/credit")
    public String creditAccount(
            @RequestParam("id") long accountId,
            @RequestParam("amount") int amount) {

        boolean ok = logic.credit(accountId, amount);
        return ok ? "OK" : "FAILED";
    }

    // Spec: /account/transactions
    @GetMapping("/account/transactions")
    public List<Transaction> getTransactions(
            @RequestParam("id") long accountId) {

        return logic.getTransactions(accountId);
    }
}