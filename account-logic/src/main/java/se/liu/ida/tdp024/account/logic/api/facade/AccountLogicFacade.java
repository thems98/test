package se.liu.ida.tdp024.account.logic.api.facade;

import java.util.List;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

public interface AccountLogicFacade {

    String createAccount(String personKey, String BankName, String accountType);

    List<Account> findAccountsByPerson(String personKey);

    List<Account> findAccountsByBank(long bankKey); //maybe it should be string bankName instead of long bankKey

    boolean debit(long accountId, long amount);

    boolean credit(long accountId, long amount);

    List<Transaction> getTransactions(long accountId);
}