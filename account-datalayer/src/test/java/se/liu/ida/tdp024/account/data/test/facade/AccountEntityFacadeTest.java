package se.liu.ida.tdp024.account.data.test.facade;

import org.junit.After;
import org.junit.Test;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;

public class AccountEntityFacadeTest {
    
    //---- Unit under test ----//
    private AccountEntityFacade accountEntityFacade;
    private StorageFacade storageFacade;
    
    @After
    public void tearDown() {
       // storageFacade.emptyStorage();
    }
    
    @Test
    public void testCreate() {
    }
}