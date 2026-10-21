package se.liu.ida.tdp024.account.data.impl.db.entity;

import se.liu.ida.tdp024.account.data.api.entity.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "accounts")
@JsonIncludeProperties({"id", "personKey", "bankKey", "accountType", "holdings"})
public class AccountDB implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String personId;
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
    @JsonProperty("personKey")
    public String getPersonId() {
        return personId;
    }

    @Override
    @JsonProperty("personKey")
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Override
    @JsonProperty("bankKey")
    public String getBankId() {
        // return bankId;
        return String.valueOf(bankId);        // coerce to string for JSON
    }

    @Override
    @JsonProperty("bankKey")
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