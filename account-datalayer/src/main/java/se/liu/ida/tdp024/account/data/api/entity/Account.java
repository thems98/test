package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Account extends Serializable {
    public long getId();
    public long getPersonId();
    public long getBankId();
    public String getAccountType();
    public long getBalance();

    public void setPersonId(long personKey);
    public void setBankId(long bankKey);
    public void setBalance(long bal);
    public void setAccountType(String accountType);
    public void setId(long id);

    public boolean addBalance(long bal);
    public boolean removeBalance(long bal);
}
