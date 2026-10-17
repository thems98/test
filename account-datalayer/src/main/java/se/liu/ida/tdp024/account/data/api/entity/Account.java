package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Account extends Serializable {
    public String getId();
    public String getPersonId();
    public String getBankId();
    public String getAccountType();
    public long getBalance();

    public void setPersonId(String personKey);
    public void setBankId(String bankKey);
    public void setBalance(long bal);
    public void setAccountType(String accountType);
    public void setId(String id);

    public boolean addBalance(long bal);
    public boolean removeBalance(long bal);
}
