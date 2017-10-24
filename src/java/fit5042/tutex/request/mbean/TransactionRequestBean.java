/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fit5042.tutex.request.mbean;

import fit5042.tutex.repository.AccountRepository;
import fit5042.tutex.repository.BankTransactionRepository;
import fit5042.tutex.repository.BankUserRepository;
import fit5042.tutex.repository.entities.Account;
import fit5042.tutex.repository.entities.Address;
import fit5042.tutex.repository.entities.BankTransaction;
import fit5042.tutex.repository.entities.BankUser;
import fit5042.tutex.repository.entities.TransactionType;
import fit5042.tutex.repository.entities.TransferBankTransaction;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author jmid3
 */
@ManagedBean(name = "transactionRequestBean")
@RequestScoped
public class TransactionRequestBean implements Serializable {
    
    @EJB
    private AccountRepository accRepo;
    
    @EJB
    private BankTransactionRepository repository;
    
    @EJB
    private BankUserRepository userRepo;
    
    private BankTransaction transaction;
    private BankUser customer;
    private TransactionType type;
    private Account accPrimary;
    private Account accSecondary;
    private String status;
    private Integer targetAccount; 
    
    List<BankTransaction> dataList;
    List<Account> totalAccountList;
    
    /**
     * Creates a new instance of TransactionManagedBean
     */
    public TransactionRequestBean() {
        transaction = new BankTransaction();
        customer = new BankUser();
        type = new TransactionType();
        accPrimary = new Account();
        accSecondary = new Account();
        status = "";
    
        dataList = new ArrayList<>();
        totalAccountList = new ArrayList<>();
    }
    
    @PostConstruct
    public void init() {
        this.getLoggedUser();
        this.totalAccountList = this.getAllAccounts();
    }
    
    public void getLoggedUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        String loggedOne = context.getExternalContext().getRemoteUser();
        
        System.out.println(loggedOne);
        
        try {
            List<BankUser> results = new ArrayList<>(this.userRepo.searchUserByEmail(loggedOne));
            this.customer = results.get(0);
        } catch(Exception e) {
            this.customer = new BankUser();
        }
    }

    public AccountRepository getAccRepo() {
        return accRepo;
    }

    public void setAccRepo(AccountRepository accRepo) {
        this.accRepo = accRepo;
    }

    public BankTransactionRepository getRepository() {
        return repository;
    }

    public void setRepository(BankTransactionRepository repository) {
        this.repository = repository;
    }

    public BankUserRepository getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(BankUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public BankTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(BankTransaction transaction) {
        this.transaction = transaction;
    }

    public BankUser getCustomer() {
        return customer;
    }

    public void setCustomer(BankUser customer) {
        this.customer = customer;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Account getAccPrimary() {
        return accPrimary;
    }

    public void setAccPrimary(Account accPrimary) {
        this.accPrimary = accPrimary;
    }

    public Account getAccSecondary() {
        return accSecondary;
    }

    public void setAccSecondary(Account accSecondary) {
        this.accSecondary = accSecondary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BankTransaction> getDataList() {
        return dataList;
    }

    public void setDataList(List<BankTransaction> dataList) {
        this.dataList = dataList;
    }

    public List<Account> getTotalAccountList() {
        return totalAccountList;
    }

    public void setTotalAccountList(List<Account> totalAccountList) {
        this.totalAccountList = totalAccountList;
    }
    
    /**
     * Fetches a list of all of the accounts.
     * 
     * @return List List of accounts.
     */
    public List<Account> getAllAccounts() {
        try {
            List<Account> retVal = new ArrayList(this.customer.getMyAccounts());
            
            for (int x = 0; x < retVal.size(); x++){
                if (!Objects.equals(retVal.get(x).getUserId().getBankUserId(), customer.getBankUserId())) {
                    retVal.remove(x);
                }
            }
            
            return retVal;
        } catch (Exception ex) {
            Logger.getLogger(TransactionRequestBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Sets the filter on the output table on the view.
     */
    public void setFilter(){
        
        if (this.transaction.getBankTransactionId() != null){
            System.out.println(this.transaction.getBankTransactionId());
        }
        
        dataList.clear();
        dataList.addAll(repository.getFilteredList(this.transaction.getBankTransactionId(), transaction.getBankTransactionName(), type));
        
        
    }
    
    /**
     * Fully populates the type attribute based on the types id.
     */
    public void populateType() throws Exception {
        
        List<TransactionType> all = this.repository.getAllTransactionTypes();
        for (TransactionType t : all){
            if (t.getTransactionTypeId() == type.getTransactionTypeId()) {
                type = t;
            }
        }
        
    }
    
    /**
     * Adds a transaction and updates the accounts.
     * 
     * @return int The outcome of the add process.
     */
    public void addAccount(){
        
        try {
            
            String some = "";
            
            if (this.accPrimary.getAccountId() != 0) {
                this.accPrimary = this.accRepo.searchAccountById(this.accPrimary.getAccountId());
            } else {
                this.status = "Please Select a valid account!";
            }
            
            float balance = Float.parseFloat(this.accPrimary.getAccountBalance().toString());    
            float thisAmount = transaction.getTransactionAmount();
            
            this.populateType();
            
            if (type.getTransactionTypeId() == 3 && balance - thisAmount < 0.0) {
                this.status = "Insufficient funds";
            } else if (type.getTransactionTypeId() == 2 && balance - thisAmount < 0.0) {
                this.status = "Insufficient funds";
            }
            
            if (type.getTransactionTypeId() == 2 && this.accSecondary.getAccountId() != 0) {
                this.accSecondary = this.accRepo.searchAccountById(this.accSecondary.getAccountId());
            } else if (type.getTransactionTypeId() != 2) {
                this.accSecondary = new Account();
            } else {
                this.status = "Please choose a valid transfer account!";
            }
            
            switch (type.getTransactionTypeId()) {
                case 0:
                    this.status = "Please choose a valid transaction type";
                case 1:
                    this.accPrimary.setAccountBalance(this.accPrimary.getAccountBalance().add(new BigDecimal(transaction.getTransactionAmount())));
                    break;
                case 2:
                    this.accPrimary.setAccountBalance(this.accPrimary.getAccountBalance().subtract(new BigDecimal(transaction.getTransactionAmount())));
                    this.accSecondary.setAccountBalance(this.accSecondary.getAccountBalance().add(new BigDecimal(transaction.getTransactionAmount())));
                    break;
                case 3:
                    this.accPrimary.setAccountBalance(this.accPrimary.getAccountBalance().subtract(new BigDecimal(transaction.getTransactionAmount())));
                    break;
                default:
                    this.status = "Please choose a valid transaction type";
            }
            
            this.transaction.setBankTransactionId(this.repository.getNextId());
            this.transaction.setUserAccount(accPrimary);
            this.transaction.setType(type);
            
            Calendar currentTimeStamp = Calendar.getInstance();
            SimpleDateFormat df =new SimpleDateFormat("yyyy-mm-dd hh:mm:ss:s");
            currentTimeStamp.setTime(df.parse(df.format(Calendar.getInstance().getTime())));
                               
            this.transaction.setTransactionDate(currentTimeStamp);
            
            this.accRepo.editAccount(accPrimary);
            
            if (type.getTransactionTypeId() == 2) {
                
               TransferBankTransaction newTrans = new TransferBankTransaction(this.transaction,this.accSecondary);
               this.repository.addTransaction(newTrans);
                
            } else {
               this.repository.addTransaction(transaction); 
            }
            
            this.dataList = this.repository.getAllTransactions();
            this.refreshTotalAccountList();

            this.accPrimary = new Account();
            this.accSecondary = new Account();
            this.transaction = new TransferBankTransaction();
            this.type = new TransactionType();
            
            this.status= "Transaction successful!";
            
        } catch (Exception e) {
            this.status= "Unable to process transaction: " + e.getMessage();
        }
        
        System.out.println(this.status);
        
    }
    
    /**
     * Returns of the TransactionTypes from the database.
     * 
     * @return List List of the TransactionTypes from the database.
     */
    public List<TransactionType> getAllTypes() {
        
        List<TransactionType> retVal = new ArrayList<>();
        retVal.add(new TransactionType(0,"Please choose a type...","Default value"));
        
        try {
            retVal.addAll(repository.getAllTransactionTypes());
            return retVal;
        } catch (Exception ex) {
            Logger.getLogger(TransactionRequestBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Update the totalAccountList.
     */
    public void refreshTotalAccountList(){
        
        boolean runOne = false, runTwo = false;
        
        try {
            this.totalAccountList.clear();
            
            if (this.accPrimary.getAccountId() > 0){
                this.totalAccountList.add(this.accRepo.searchAccountById(this.accPrimary.getAccountId()));
                runOne = true;
            }
            
            if (this.accSecondary.getAccountId() > 0){
                this.totalAccountList.add(this.accRepo.searchAccountById(this.accSecondary.getAccountId()));
                runTwo = true;
            }
            
            if (!runOne && !runTwo){
                this.totalAccountList = new ArrayList<>(customer.getMyAccounts());
            }
            
        } catch (Exception e) {
            this.totalAccountList = new ArrayList<>();
        }
        
    }
    
    
}
