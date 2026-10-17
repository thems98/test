package se.liu.ida.tdp024.account.data.util;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;

@Configuration
public class DataLayerConfig {

    @Bean
    public AccountEntityFacade accountEntityFacade() {
        return new AccountEntityFacadeDB();
    }

    @Bean
    public TransactionEntityFacade transactionEntityFacade() {
        return new TransactionEntityFacadeDB();
    }
}