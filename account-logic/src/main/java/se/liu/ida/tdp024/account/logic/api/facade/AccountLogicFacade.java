package se.liu.ida.tdp024.account.logic.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import java.util.List;

public interface AccountLogicFacade {

    boolean createAccount(long personKey, long bankKey, String accountType);

    List<Account> findAccountsByPerson(long personKey);

    List<Account> findAccountsByBank(long bankKey);

    boolean debit(long accountId, long amount);

    boolean credit(long accountId, long amount);

    List<Transaction> getTransactions(long accountId);
}