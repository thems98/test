package se.liu.ida.tdp024.account.data.impl.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;


import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.entity.Account;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "transactions")
@JsonIncludeProperties({"id", "type", "amount", "created", "status", "account"})
public class TransactionDB implements Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long accountId;
    private String transactionType;
    private long amount;
    private String created;
    private String status;                 

    // --- required by JPA ---
    public TransactionDB() { }

    public TransactionDB(long accountId, String transactionType,
                         long amount, String created, String status) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.created = created;
        this.status = status;
    }

    @Override public long getId() { return this.id; }
    @Override public void setId(long id) { this.id = id; }

    @Override public long getAccountId() { return this.accountId; }
    @Override public void setAccountId(long accountId) { this.accountId = accountId; }

    @Override
    @JsonProperty("type")
    public String getTransactionType() { return transactionType; } 

    @Override
    @JsonProperty("type")
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    @Override public long getAmount() { return this.amount; }
    @Override public void setAmount(long amount) { this.amount = amount; }

    @Override public String getCreated() { return this.created; }
    @Override public void setCreated(String created) { this.created = created; }

    @Override public String getStatus() { return this.status; }
    @Override public void setStatus(String status) { this.status = status; }

    @Transient
    private Account account;

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }
}