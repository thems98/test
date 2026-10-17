package se.liu.ida.tdp024.account.data.impl.db.entity;

import se.liu.ida.tdp024.account.data.api.entity.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountDB implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String personId;
    private String bankId;
    private String accountType;
    private long balance;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getPersonId() {
        return personId;
    }

    @Override
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Override
    public String getBankId() {
        return bankId;
    }

    @Override
    public void setBankId(String bankId) {
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
    public long getBalance() {
        return balance;
    }

    @Override
    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Override
    public boolean addBalance(long bal) {
        this.balance += bal;
        return true;
    }

    @Override
    public boolean removeBalance(long bal) {
        if (this.balance - bal < 0) {
            return false;
        }
        this.balance -= bal;
        return true;
    }
}