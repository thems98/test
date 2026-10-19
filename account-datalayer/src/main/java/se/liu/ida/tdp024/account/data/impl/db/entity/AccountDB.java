package se.liu.ida.tdp024.account.data.impl.db.entity;

import se.liu.ida.tdp024.account.data.api.entity.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "accounts")
public class AccountDB implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long personId;
    private long bankId;
    private String accountType;
    private long holdings;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getPersonId() {
        return personId;
    }

    @Override
    public void setPersonId(long personId) {
        this.personId = personId;
    }

    @Override
    public long getBankId() {
        return bankId;
    }

    @Override
    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    @Override
    public String getAccountType() {
        return accountType;
    }

    @Override
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public long getHoldings() {
        return holdings;
    }

    @Override
    public void setHoldings(long balance) {
        this.holdings = balance;
    }

    @Override
    public boolean addHoldings(long bal) {
        this.holdings += bal;
        return true;
    }

    @Override
    public boolean removeHoldings(long bal) {
        if (this.holdings - bal < 0) {
            return false;
        }
        this.holdings -= bal;
        return true;
    }
}