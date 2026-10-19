package se.liu.ida.tdp024.account.data.api.entity;
import java.io.Serializable;

public interface Transaction extends Serializable {
    public long getId();
    public long getAccountId();
    public String getTransactionType();
    public long getAmount();
    public String getCreated();

    public void setId(long id);
    public void setAccountId(long accountId);
    public void setTransactionType(String transactionType);
    public void setAmount(long amount);
    public void setCreated(String created);

    String getStatus();
    void setStatus(String status); 

    Account getAccount();
    void setAccount(Account account);
    
}
