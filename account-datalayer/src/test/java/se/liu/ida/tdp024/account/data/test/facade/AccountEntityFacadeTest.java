package se.liu.ida.tdp024.account.data.test.facade;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.PersistenceManager;

public class AccountEntityFacadeTest {

    private AccountEntityFacade accounts;
    private TransactionEntityFacade transactions;

    @Before
    public void setUp() {
        accounts = new AccountEntityFacadeDB();
        transactions = new TransactionEntityFacadeDB();

        EntityManager em = PersistenceManager.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM TransactionDB").executeUpdate();
            em.createQuery("DELETE FROM AccountDB").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void createFindAndList() {
        assertEquals("OK", accounts.createAccount("CHECK", "1", 1L));

        List<Account> list = accounts.findByPerson("1");
        assertEquals(1, list.size());
        Account a = list.get(0);

        assertEquals("CHECK", a.getAccountType());
        assertEquals(0, a.getHoldings());
        assertSame(a.getId(), accounts.find(a.getId()).getId());

        assertEquals(1, accounts.findByBank(1L).size());
        assertTrue(accounts.findByPerson("nobody").isEmpty());
    }

    @Test
    public void creditDebitRespectBalance() {
        accounts.createAccount("SAVINGS", "2", 2L);
        long id = accounts.findByPerson("2").get(0).getId();

        assertTrue(accounts.credit(id, 100));
        assertTrue(accounts.debit(id, 40));
        assertFalse(accounts.debit(id, 1_000));   // insufficient
        assertEquals(60, accounts.find(id).getHoldings());

        assertFalse(accounts.credit(9_999, 10));  // unknown account
        assertFalse(accounts.debit(9_999, 10));
    }

    @Test
    public void transactionFacadePersistsAndOrders() {
        accounts.createAccount("CHECK", "9", 9L);
        long id = accounts.findByPerson("9").get(0).getId();

        assertTrue(transactions.addTransaction(id, "CREDIT", 25, "OK"));
        assertTrue(transactions.addTransaction(id, "DEBIT", 10, "FAILED"));

        List<Transaction> list = transactions.getTransactions(id);
        assertEquals(2, list.size());
        assertEquals("CREDIT", list.get(0).getTransactionType());   // ordered by id ASC
        assertEquals("DEBIT",  list.get(1).getTransactionType());
        assertEquals("FAILED", list.get(1).getStatus());

        assertTrue(transactions.getTransactions(9_999).isEmpty());
    }
}