package se.liu.ida.tdp024.account.data.impl.db.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import se.liu.ida.tdp024.account.data.api.entity.Account;

public class AccountDB implements Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    
	private String id;
	private long balance;
    private String accountType;
    private String personId;
    private String bankId;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public long getBalance() {
        return this.balance;
	}

     public void setPersonId(String personId){
      this.personId = personId;
    }
    
    public String getPersonId(){
        return personId;
    }
    
    
    public void setAccountType(String accountType){
      this.accountType = accountType;
    }
    
    public String getAccountType(){
      return accountType;
    }
    
    public String getBankId(){
      return this.bankId;
    }
    
    public void setBankId(String bankKey){
      this.bankId = bankKey;
    }
    
    public void setBalance(long bal){
      this.balance = bal;
    }
    
    public boolean addBalance(long bal){
      this.balance += balance;
      return true;
    }
    
    public boolean removeBalance(long bal){
      if(this.balance - bal >= 0) {
        this.balance -= bal;
        return true;
      }
      return false;
    }
    
    
      
}
