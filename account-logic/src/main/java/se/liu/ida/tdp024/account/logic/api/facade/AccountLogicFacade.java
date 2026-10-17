package se.liu.ida.tdp024.account.logic.api.facade;

import java.util.List;
import se.liu.ida.tdp024.account.data.api.entity.Account;
//import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public interface AccountLogicFacade {

    public boolean createAccount(String accountType, String personKey, String bankKey);

    // public List<Account> findAccountsByPerson(String personKey);

    // public List<Account> findAccountsByBank(String bankKey);

    public boolean debit(Long accountId, long amount);

    public boolean credit(Long accountId, long amount);

    //List<Transaction> getTransactions(Long accountId);
}