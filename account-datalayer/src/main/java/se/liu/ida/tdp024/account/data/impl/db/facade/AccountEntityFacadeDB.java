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
    public boolean createAccount(String accountType, String personKey, String bankKey) {
        EntityManager em = PersistenceManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AccountDB account = new AccountDB();
            account.setPersonId(personKey);
            account.setBankId(bankKey);
            account.setAccountType(accountType);   // "CHECK" or "SAVINGS"
            account.setBalance(0);
            em.persist(account);
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
    public List<Account> findByBank(String bankKey) {
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

    // ---------------------------------------------------------------
    // DEBIT  (subtract from balance)
    // ---------------------------------------------------------------
    @Override
    public boolean debit(long id, Integer amount) {
        EntityManager em = PersistenceManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AccountDB account = em.find(AccountDB.class, id);
            if (account == null) {
                tx.rollback();
                return false;
            }
            boolean ok = account.removeBalance(amount);
            if (ok) {
                em.merge(account);
            }
            tx.commit();
            return ok;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // ---------------------------------------------------------------
    // CREDIT  (add to balance)
    // ---------------------------------------------------------------
    @Override
    public boolean credit(long id, Integer amount) {
        EntityManager em = PersistenceManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AccountDB account = em.find(AccountDB.class, id);
            if (account == null) {
                tx.rollback();
                return false;
            }
            account.addBalance(amount);
            em.merge(account);
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
}