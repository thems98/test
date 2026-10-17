package se.liu.ida.tdp024.account.data.api.facade;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import java.util.List;

public interface AccountEntityFacade {
    public boolean createAccount(String accountType, long personKey, long bankKey);
    public List<Account> findByPerson(long personKey);
    public List<Account> findByBank(long bankKey);
    public Account find(long id);
    public boolean debit(long id, long amount);
    public boolean credit(long id, long amount);
    // void update(Account account);
    // public Account getAccount(long id);
    
}
