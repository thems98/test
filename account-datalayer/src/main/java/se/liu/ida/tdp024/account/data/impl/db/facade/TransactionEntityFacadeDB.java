package se.liu.ida.tdp024.account.data.impl.db.facade;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.PersistenceManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class TransactionEntityFacadeDB implements TransactionEntityFacade {

    private static final DateTimeFormatter created =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // ---------------------------------------------------------------
    // ADD  — always persists, even on FAILED status
    // ---------------------------------------------------------------
    @Override
    public boolean addTransaction(long accountId, String type, long amount, String status) {
        EntityManager em = PersistenceManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TransactionDB t = new TransactionDB();
            t.setAccountId(accountId);
            t.setTransactionType(type);           // DEBIT | CREDIT
            t.setAmount(amount);                  // positive magnitude
            t.setStatus(status);                  // OK | FAILED
            t.setCreated(LocalDateTime.now().format(created));

            em.persist(t);
            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ---------------------------------------------------------------
    // GET all transactions for an account (ordered oldest first)
    // ---------------------------------------------------------------
    @Override
    public List<Transaction> getTransactions(long accountId) {
        EntityManager em = PersistenceManager.createEntityManager();
        try {
            TypedQuery<Transaction> q = em.createQuery(
                    "SELECT t FROM TransactionDB t " +
                    "WHERE t.accountId = :aid " +
                    "ORDER BY t.id ASC",
                    Transaction.class);
            q.setParameter("aid", accountId);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}