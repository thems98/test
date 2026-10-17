package se.liu.ida.tdp024.account.logic.impl.facade;

import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import java.util.List;

public class AccountLogicFacadeImpl implements AccountLogicFacade {
    
    private AccountEntityFacade accountEntityFacade;
    
    public AccountLogicFacadeImpl(AccountEntityFacade accountEntityFacade) {
        this.accountEntityFacade = accountEntityFacade;
    }
    @Override
    // @Transactional
    public boolean createAccount(String accountType, String personKey, String bankKey) {
        if (!accountType.equals("CHECK") && !accountType.equals("SAVINGS")) {
            return false;
        }
        String personURLString = "http://localhost:8060/person/find.key/" + personKey;
        String bankURLString = "http://localhost:8070/bank/find.key/" + bankKey;
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String personResponse = restTemplate.getForObject(personURLString, String.class);
            String bankResponse = restTemplate.getForObject(bankURLString, String.class);
            if (personResponse == null || bankResponse == null) {
                return false;
            }
            // Call data layer function to create account
            //accountEntityFacade.createAccount(accountType, personKey, bankKey);
        } catch (Exception e) {
            return false;
        }

        

        return true;
    }

    //List<Account> findAccountsByPerson(String personKey);

    //List<Account> findAccountsByBank(String bankKey);

    public boolean debit(Long accountId, long amount){
        return true;
    }

    public boolean credit(Long accountId, long amount){
        return true;
    }
    
}
