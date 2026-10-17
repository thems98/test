package se.liu.ida.tdp024.account.data.api.facade;
import se.liu.ida.tdp024.account.data.api.entity.Account;

public interface AccountEntityFacade {
    public boolean createAccount(String accountType, String personKey, String bankKey);
    public String find(long id);
    public boolean debit(long id, Integer amount);
    public boolean credit(long id, Integer amount);
    // void update(Account account);
    // public Account getAccount(long id);
    
}
