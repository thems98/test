package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Account extends Serializable {
    public long getId();
    public String getPersonId();
    public String getBankId();
    public String getAccountType();
    public long getHoldings();

    public void setPersonId(String personKey);
    public void setBankId(long bankKey);
    public void setHoldings(long bal);
    public void setAccountType(String accountType);
    public void setId(long id);

    public boolean addHoldings(long bal);
    public boolean removeHoldings(long bal);
}
