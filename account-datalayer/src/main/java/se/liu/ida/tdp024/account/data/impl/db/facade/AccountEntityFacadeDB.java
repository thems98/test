package se.liu.ida.tdp024.account.data.impl.db.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.util.PersistenceManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class AccountEntityFacadeDB implements AccountEntityFacade {

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------
    @Override
    public String createAccount(String accountType, String personKey, long bankKey) {
        EntityManager em = PersistenceManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AccountDB account = new AccountDB();
            account.setPersonId(personKey);
            account.setBankId(bankKey);
            account.setAccountType(accountType);   // "CHECK" or "SAVINGS"
            account.setHoldings(0);
            em.persist(account);
            tx.commit();
            return "OK";
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return "FAILED";
        } finally {
            em.close();
        }
    }

    // ---------------------------------------------------------------
    // FIND by id
    // ---------------------------------------------------------------
    @Override
    public Account find(long id) {
        EntityManager em = PersistenceManager.createEntityManager();
        try {
            return em.find(AccountDB.class, id);
        } finally {
            em.close();
        }
    }

    // ---------------------------------------------------------------
    // FIND by person key (all accounts belonging to one person)
    // ---------------------------------------------------------------
    @Override
    public List<Account> findByPerson(String personKey) {
        EntityManager em = PersistenceManager.createEntityManager();
        try {
            TypedQuery<Account> q = em.createQuery(
                    "SELECT a FROM AccountDB a WHERE a.personId = :pk",
                    Account.class);
            q.setParameter("pk", personKey);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    // ---------------------------------------------------------------
    // FIND by bank key (all accounts belonging to one bank)
    // ---------------------------------------------------------------
    @Override
    public List<Account> findByBank(long bankKey) {
        EntityManager em = PersistenceManager.createEntityManager();
        try {
            TypedQuery<Account> q = em.createQuery(
                    "SELECT a FROM AccountDB a WHERE a.bankId = :bk",
                    Account.class);
            q.setParameter("bk", bankKey);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean credit(long id, long amount) {
        EntityManager em = PersistenceManager.createEntityManager();
        try {
            em.getTransaction().begin();
            int rows = em.createQuery(
                    "UPDATE AccountDB a SET a.holdings = a.holdings + :amt WHERE a.id = :id")
                .setParameter("amt", amount)
                .setParameter("id", id)
                .executeUpdate();
            em.getTransaction().commit();
            return rows == 1;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean debit(long id, long amount) {
        EntityManager em = PersistenceManager.createEntityManager();
        try {
            em.getTransaction().begin();
            int rows = em.createQuery(
                    "UPDATE AccountDB a SET a.holdings = a.holdings - :amt " +
                    "WHERE a.id = :id AND a.holdings >= :amt")
                .setParameter("amt", amount)
                .setParameter("id", id)
                .executeUpdate();
            em.getTransaction().commit();
            return rows == 1;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }
}