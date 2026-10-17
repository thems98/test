package se.liu.ida.tdp024.account.data.impl.db.entity;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

public class TransactionDB implements Transaction {
    
    private String id;
    private String accountId;
    private String transactionType;
    private long amount;
    private String timestamp;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getAccountId() {
        return this.accountId;
    }

    @Override
    public String getTransactionType() {
        return this.transactionType;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
}
