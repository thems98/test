package hello;
//import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONObject;
//import org.json.simple.*;
@RestController 
public class AccountController {

    
    @GetMapping("/account/create")
    public String createAccount(
        @RequestParam("accounttype") String accountType,
        @RequestParam("person") String personKey,
        @RequestParam("bank") String bankName) {
        
        return "Logiklagerstuff anrop";
    }

    @GetMapping("account/find/person")
    public String findAccount(
        @RequestParam("person") String personKey
        ) {
            
        return "Logiklagerstuff anrop";
    }

    @GetMapping("account/debit")
    public String debitAccount(
        @RequestParam("id") String accountKey,
        @RequestParam("amount") int amount) {

        return "Logiklagerstuff anrop";
    }

    @GetMapping("account/credit")
    public String creditAccount(
        @RequestParam("id") String accountKey,
        @RequestParam ("amount") int amount
    ) {
        return "logiklagerstuff anrop";
    }

    @GetMapping("account/transactions")
    public  JSONObject getTransactions(
        @RequestParam("id") String accountKey
    ){
        return new JSONObject();
    }   
}
