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
    private String id;

    private String accountId;
    private String transactionType;
    private long amount;
    private String timestamp;
    private String status;                 // <-- NEW FIELD

    // --- required by JPA ---
    public TransactionDB() { }

    public TransactionDB(String accountId, String transactionType,
                         long amount, String timestamp, String status) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    // --- existing getters/setters (unchanged) ---

    @Override public String getId() { return this.id; }
    @Override public void setId(String id) { this.id = id; }

    @Override public String getAccountId() { return this.accountId; }
    @Override public void setAccountId(String accountId) { this.accountId = accountId; }

    @Override public String getTransactionType() { return this.transactionType; }
    @Override public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    @Override public long getAmount() { return this.amount; }
    @Override public void setAmount(long amount) { this.amount = amount; }

    @Override public String getTimestamp() { return this.timestamp; }
    @Override public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // --- the two stubs you had are now real ---

    @Override public String getStatus() { return this.status; }
    @Override public void setStatus(String status) { this.status = status; }
}