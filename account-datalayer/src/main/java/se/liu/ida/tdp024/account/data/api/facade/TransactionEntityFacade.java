package se.liu.ida.tdp024.account.data.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import java.util.List;

public interface TransactionEntityFacade {

    boolean addTransaction(long accountId, String type, long amount, String status);

    List<Transaction> getTransactions(long accountId);
}