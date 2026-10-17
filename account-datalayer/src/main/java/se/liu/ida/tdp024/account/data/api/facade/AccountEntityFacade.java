package se.liu.ida.tdp024.account.data.api.facade;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import java.util.List;

public interface AccountEntityFacade {
    public boolean createAccount(String accountType, String personKey, String bankKey);
    public List<Account> findByPerson(String personKey);
    public List<Account> findByBank(String bankKey);
    public Account find(long id);
    public boolean debit(long id, Integer amount);
    public boolean credit(long id, Integer amount);
    // void update(Account account);
    // public Account getAccount(long id);
    
}
