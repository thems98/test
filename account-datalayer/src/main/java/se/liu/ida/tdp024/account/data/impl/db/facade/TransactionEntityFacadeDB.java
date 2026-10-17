package se.liu.ida.tdp024.account.data.impl.db.facade;

import java.util.ArrayList;

import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

public class TransactionEntityFacadeDB implements TransactionEntityFacade {

    public boolean addTransaction(String accountId, String transactionType, long amount){
        return true;
    }

    public ArrayList<Transaction> getTransactions(long id){
        return new ArrayList<>();
    }
}