package se.liu.ida.tdp024.account.data.impl.db.facade;

import java.util.ArrayList;

import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import org.apache.kafka.clients.producer.KafkaProducer;

public class AccountEntityFacadeDB implements AccountEntityFacade {

    // public static final Kafkaobject kafkaObject = new Kafkaobject();
    public boolean createAccount(String accountType, String personKey, String bankKey){
        return true;
    }
    public String find(long id){
        return "account";
    }

    public ArrayList<Account> findByPerson(String personKey){
        return new ArrayList<>();
        
    }

    public boolean debit(long id, Integer amount){
        return true;
    }

    public boolean credit(long id, Integer amount){
        return true;
    }

//     void update(Account account){
//         return new Account();
//     }
}
