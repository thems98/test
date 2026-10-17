package se.liu.ida.tdp024.account.data.impl.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;

@Entity
@Table(name = "TRANSACTIONS")
public class TransactionDB implements Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Override public String getTransactionType() { return this.transactionType; }
    @Override public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    @Override public long getAmount() { return this.amount; }
    @Override public void setAmount(long amount) { this.amount = amount; }

    @Override public String getCreated() { return this.created; }
    @Override public void setCreated(String created) { this.created = created; }

    // --- the two stubs you had are now real ---

    @Override public String getStatus() { return this.status; }
    @Override public void setStatus(String status) { this.status = status; }
}