package se.liu.ida.tdp024.account.data.api.entity;
import java.io.Serializable;

public interface Transaction extends Serializable {
    public String getId();
    public String getAccountId();
    public String getTransactionType();
    public long getAmount();
    public String getTimestamp();

    public void setAccountId(String accountId);
    public void setTransactionType(String transactionType);
    public void setAmount(long amount);
    public void setTimestamp(String timestamp);
    
}
