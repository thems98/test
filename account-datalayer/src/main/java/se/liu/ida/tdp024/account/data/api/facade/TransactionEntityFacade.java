package se.liu.ida.tdp024.account.data.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import java.util.List;

public interface TransactionEntityFacade {

    /**
     * Persists a transaction. Always persists — even when status == "FAILED".
     */
    boolean addTransaction(long accountId, String type, long amount, String status);

    /**
     * Returns all transactions for the given account, oldest first.
     */
    List<Transaction> getTransactions(long accountId);
}