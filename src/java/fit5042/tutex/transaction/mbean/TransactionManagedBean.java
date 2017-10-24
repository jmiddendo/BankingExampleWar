/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fit5042.tutex.transaction.mbean;

import fit5042.tutex.repository.AccountRepository;
import fit5042.tutex.repository.BankTransactionRepository;
import fit5042.tutex.repository.BankUserRepository;
import fit5042.tutex.repository.entities.Account;
import fit5042.tutex.repository.entities.Address;
import fit5042.tutex.repository.entities.BankTransaction;
import fit5042.tutex.repository.entities.TransferBankTransaction;
import fit5042.tutex.repository.entities.BankUser;
import fit5042.tutex.repository.entities.TransactionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author jmid3
 */
@ManagedBean(name = "transactionManagedBean")
@SessionScoped
public class TransactionManagedBean implements Serializable {
    
    @EJB
    private AccountRepository accRepo;
    
    @EJB
    private BankTransactionRepository repository;
    
    @EJB
    private BankUserRepository userRepo;
    
    private TransferBankTransaction transaction;
    private BankUser customer;
    private BankUser user;
    private TransactionType type;
    private Account accPrimary;
    private Account accSecondary;
    private Address addy;
    private Address newAddress;
    private String status;
    
    List<BankTransaction> dataList;
    List<BankUser> userList;
    List<Account> primaryAccountList;
    List<Account> relatedAccountList;
    List<Account> totalAccountList;
    
    /**
     * Creates a new instance of TransactionManagedBean
     */
    public TransactionManagedBean() {
        transaction = new TransferBankTransaction();
        customer = new BankUser();
        user = new BankUser();
        type = new TransactionType();
        accPrimary = new Account();
        accSecondary = new Account();
        addy = new Address();
        newAddress = new Address();
        status = "";
    
        dataList = new ArrayList<>();
        userList = new ArrayList<>();
        primaryAccountList = new ArrayList<>();
        relatedAccountList = new ArrayList<>();
        totalAccountList = new ArrayList<>();
    }
    
    /**
     * Stores the logged in user into an attribute.
     */
    public void getLoggedUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        String loggedOne = context.getExternalContext().getRemoteUser();
        
        try {
            List<BankUser> results = new ArrayList<>(this.userRepo.searchUserByEmail(loggedOne));
            this.customer = results.get(0);
        } catch(Exception e) {
            this.customer = new BankUser();
        }
    }
    
    /**
     * Populates the primary account.
     */
    public void assignPrimaryAccount() {
        try {
            if (this.accPrimary != null && this.accPrimary.getAccountId() > 0) {
                this.accPrimary = this.accRepo.searchAccountById(this.accPrimary.getAccountId());
            } else {
                this.accPrimary = new Account();
            }
        } catch (Exception e) {
            this.accPrimary = new Account();
        }
    }
    
    /**
     * Populates the secondary account.
     */
    public void assignSecondaryAccount() {
        try {
            if (this.accSecondary != null && this.accSecondary.getAccountId() > 0) {
                this.accSecondary = this.accRepo.searchAccountById(this.accSecondary.getAccountId());
            } else {
                this.accSecondary = new Account();
            }
        } catch (Exception e) {
            this.accSecondary = new Account();
        }
    }
    
    /**
     * Looks up a user and returns the appropriate navigation.
     * 
     * @return String The page the user should be at.
     */
    public String fakeLogin(){
        
        try {
            customer = this.userRepo.searchBankUserById(customer.getBankUserId());
            addy = customer.getAddress();
           
            if (customer.getType().equals("Public")){
                clearFilter();
                return "userTransaction";
            } else if (customer.getType().equals("Worker")) {
                return "workerPortal";
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        
    }
    
    /**
     * Clears out the variables in the bean.
     */
    public void resetBean(){
        transaction = new TransferBankTransaction();
        customer = new BankUser();
        type = new TransactionType();
        accPrimary = new Account();
        accSecondary = new Account();
        addy = new Address();
        status = "";
        
        dataList = new ArrayList<>();
        userList = new ArrayList<>();
        primaryAccountList = new ArrayList<>();
        relatedAccountList = new ArrayList<>();
        totalAccountList = new ArrayList<>();
    }

    /**
     * The accessor for the newAddress attribute.
     * 
     * @return Address The newAddress attribute.
     */
    public Address getNewAddress() {
        return newAddress;
    }

    /**
     * The mutator for the newAddress attribute.
     * 
     * @param newAddress The new value for newAddress.
     */
    public void setNewAddress(Address newAddress) {
        this.newAddress = newAddress;
    }
    
    /**
     * The accessor method for the dataList attribute.
     * @return List The data list attribute
     */
    public List<BankTransaction> getDataList() {
        return dataList;
    }

    /**
     * The mutator method for the dataList member.
     * @param dataList The new datalist value.
     */
    public void setDataList(List<BankTransaction> dataList) {
        this.dataList = dataList;
    }

    /**
     * The accessor method for the userList attribute.
     * 
     * @return List The userList attribute.
     */
    public List<BankUser> getUserList() {
        return userList;
    }

    /**
     * The mutator method for the userList attribute.
     * 
     * @param userList The new userList value.
     */
    public void setUserList(List<BankUser> userList) {
        this.userList = userList;
    }
    
    /**
     * The accessor method for the repository attribute.
     * @return BankTransactionRepository The repository.
     */
    public BankTransactionRepository getRepository() {
        return repository;
    }

    /**
     * The mutator for the repository member.
     * @param repository The new value for the repository member.
     */
    public void setRepository(BankTransactionRepository repository) {
        this.repository = repository;
    }
    
    /**
     * The accessor for the user member.
     * 
     * @return BankUser The user attribute.
     */
    public BankUser getUser() {
        return user;
    }

    /**
     * The mutator method for the user attribute.
     * 
     * @param user The new value for user.
     */
    public void setUser(BankUser user) {
        this.user = user;
    }
    
    /**
     * The accessor method for the transaction member.
     * 
     * @return BankTransaction The bank transaction.
     */
    public BankTransaction getTransaction() {
        return transaction;
    }

    /**
     * The mutator method for the transaction member.
     * @param transaction The new value for the transaction.
     */
    public void setTransaction(TransferBankTransaction transaction) {
        this.transaction = transaction;
    }

    /**
     * The accessor for the customer member.
     * @return BankUser returns the customer.
     */
    public BankUser getCustomer() {
        return customer;
    }

    /**
     * The mutator for the customer member.
     * 
     * @param customer The new customer value.
     */
    public void setCustomer(BankUser customer) {
        this.customer = customer;
    }

    /**
     * The accessor for the totalAccountList.
     * 
     * @return List The totalAccountList member.
     */
    public List<Account> getTotalAccountList() {
        return totalAccountList;
    }

    /**
     * The mutator for the totalAccountList.
     * 
     * @param totalAccountList The new value for totalAccountList.
     */
    public void setTotalAccountList(List<Account> totalAccountList) {
        this.totalAccountList = totalAccountList;
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
    
    /**
     * Mutator method for the type attribute.
     * @return TransactionType The type member.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Accessor for the type member.
     * @param type The new type.
     */
    public void setType(TransactionType type) {
        this.type = type;
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
            Logger.getLogger(TransactionManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Retrieves all of the transactions from the database.
     * @return List List of the transaction.
     */
    public List<BankTransaction> getAllTransactions() {
        try {
            return repository.getAllTransactions();
        } catch (Exception ex) {
            Logger.getLogger(TransactionManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * The accessor method for the accRepo attribute.
     * 
     * @return AccountRepository The accRepo atrribute.
     */
    public AccountRepository getAccRepo() {
        return accRepo;
    }
    
    /**
     * The mutator method for the accRepo attribute.
     * 
     * @param accRepo The new value for the accRepo attribute.
     */
    public void setAccRepo(AccountRepository accRepo) {
        this.accRepo = accRepo;
    }

    /**
     * The accessor method for the userRepo attribute.
     * 
     * @return BankUserRepository The userRepo attribute.
     */
    public BankUserRepository getUserRepo() {
        return userRepo;
    }

    /**
     * The mutator method for the userRepo attribute.
     * 
     * @param userRepo The new value for the userRepo attribute.
     */
    public void setUserRepo(BankUserRepository userRepo) {
        this.userRepo = userRepo;
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
     * The accessor method for accPrimary attribute.
     * 
     * @return Account The accPrimary attribute.
     */
    public Account getAccPrimary() {
        this.refreshAccountList();
        return accPrimary;
    }

    /**
     * The mutator method for accPrimary attribute.
     * 
     * @param accPrimary The new Value for the accPrimary attribute.
     */
    public void setAccPrimary(Account accPrimary) {
        this.accPrimary = accPrimary;
    }

    /**
     * The accessor method for accSecondary attribute.
     * 
     * @return Account The accSecondary attribute.
     */
    public Account getAccSecondary() {
        this.refreshRelatedAccountList();
        return accSecondary;
    }

    /**
     * The mutator method for accSecondary attribute.
     * 
     * @param accSecondary The new value for accSecondary.
     */
    public void setAccSecondary(Account accSecondary) {
        this.accSecondary = accSecondary;
    }
    
    /**
     * Refreshes the list of accounts.
     */
    public void refreshAccountList() {
        
        try {
            primaryAccountList.clear();
            primaryAccountList.add(new Account(0,"Please choose an account...",null,null,null,false));
            primaryAccountList.addAll(customer.getMyAccounts());
            if (this.accPrimary.getAccountId() > 0) {
                this.accPrimary = this.accRepo.searchAccountById(this.accPrimary.getAccountId());
            } else {
                this.accPrimary = new Account();
            }
        } catch (Exception e) {
            Logger.getLogger(TransactionManagedBean.class.getName()).log(Level.SEVERE, null, e);
            primaryAccountList.add(new Account(0,"Please select a user...",null,null,null,false));
        }

    }

    /**
     * Refreshes the list of related accounts.
     */
    public void refreshRelatedAccountList(){
        
        relatedAccountList.clear();
        
        if (type.getTransactionTypeId() != 2) {
            relatedAccountList.add(new Account(0,"Please select transfer as a transaction type...",null,null,null,false));
        } else {
            try {
                relatedAccountList.clear();
                relatedAccountList.add(new Account(0,"Please select transfer as a transaction type...",null,null,null,false));
                relatedAccountList.addAll(customer.getMyAccounts());
                if (this.accSecondary.getAccountId() > 0) {
                    this.accSecondary = this.accRepo.searchAccountById(this.accSecondary.getAccountId());
                } else {
                    this.accSecondary = new Account();
                }
                for (int i = 0; i < relatedAccountList.size(); i++){
                    if (relatedAccountList.get(i).getAccountId() == this.accPrimary.getAccountId()) {
                        relatedAccountList.remove(i);
                    }
                }
            } catch (Exception e) {
                relatedAccountList.clear();
                Logger.getLogger(TransactionManagedBean.class.getName()).log(Level.SEVERE, null, e);
                relatedAccountList.add(new Account(0,"Holy error Batman...",null,null,null,false));
            }
        }
        
    }
    
    /**
     * The accessor method for the addy attribute.
     * 
     * @return Address The addy attribute.
     */
    public Address getAddy() {
        return addy;
    }

    /**
     * The mutator method for the addy attribute.
     * 
     * @param addy 
     */
    public void setAddy(Address addy) {
        this.addy = addy;
    }

    /**
     * The accessor method for the primaryAccountList attribute.
     * 
     * @return List The primaryAccountList attribute.
     */
    public List<Account> getPrimaryAccountList() {
        return primaryAccountList;
    }

    /**
     * The mutator method for the primaryAccountList attribute.
     * 
     * @param primaryAccountList The new value for primaryAccountList.
     */
    public void setPrimaryAccountList(List<Account> primaryAccountList) {
        this.primaryAccountList = primaryAccountList;
    }

    /**
     * The accessor method for relatedAccountList attribute.
     * 
     * @return List The relatedAccountList attribute.
     */
    public List<Account> getRelatedAccountList() {
        return relatedAccountList;
    }

    /**
     * The mutator method dor the relatedAccountList attribute.
     * 
     * @param relatedAccountList The new value for relatedAccountList attribute.
     */
    public void setRelatedAccountList(List<Account> relatedAccountList) {
        this.relatedAccountList = relatedAccountList;
    }
    
    /**
     * Retrieves all of the bank users from the database.
     * 
     * @return List List of the bank users
     */
    public List<BankUser> getAllBankUsers() {
        
        try {
            return repository.getAllBankUsers();
        } catch (Exception ex) {
            Logger.getLogger(TransactionManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            Logger.getLogger(TransactionManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Clears the filter on the view.
     */
    public void clearFilter(){
        this.dataList = this.getAllTransactions();
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
     * Updates the user profile in the database.
     * 
     * @return String The new navigation screen.
     */
    public String updateProfile() {
        
        try {
        customer.setAddress(addy);
        userRepo.editBankUser(customer);
        return "userTransaction";
        } catch (Exception e) {
            e.printStackTrace();
            return "userView";
        }
    }
    
    /**
     * Return a list of user types.
     * 
     * @return List List of user types.
     */
    public List<String> getAllUserTypes(){
        ArrayList<String> retValue = new ArrayList<>();
        
        retValue.add("Public");
        retValue.add("Worker");
        
        return retValue;
    }
    
    /**
     * Returns a list of account types. 
     * 
     * @return List List of account types.
     */
    public List<String> getAllAccountTypes(){
        ArrayList<String> retValue = new ArrayList<>();
        
        retValue.add("Checking");
        retValue.add("Savings");
        
        return retValue;
    }
    
    /**
     * Adds a user to the database.
     */
    public void addUser() {
        
        try {
            
            user.setBankUserId(this.userRepo.getNextUserId());
            
            user.setAddress(newAddress);
            user.setPassword(this.hashPassword(user.getPassword()));
            this.userRepo.addBankUser(user);
            
            userList = this.userRepo.getAllBankUsers();
            
            user = new BankUser();
            newAddress = new Address();
        } catch (Exception e) {
            this.status = "Failed to add user: " + e.getMessage();
        }
    }

    /**
     * The default accessor for the status attribute.
     * 
     * @return String The status attribute.
     */
    public String getStatus() {
        return status;
    }

    /**
     * The default mutator method for status.
     * @param status The new value for status.
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Updates a user in the database.
     */
    public void updateUser() {
        
        try {
            user.setAddress(newAddress);
            this.userRepo.editBankUser(user);
            
            userList = this.userRepo.getAllBankUsers();
            
            user = new BankUser();
            newAddress = new Address();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Deletes a user from the database.
     */
    public void deleteUser() {
        try {
            user.setAddress(newAddress);
            this.userRepo.removeBankUser(user.getBankUserId());
            
            userList = this.userRepo.getAllBankUsers();
            
            user = new BankUser();
            newAddress = new Address();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void applyCombinationFilter() {
        
        System.out.println(user.toString());
        
        try {
            this.userList = new ArrayList<>(this.userRepo.searchBankUserByCombination(user));
        } catch (Exception e) {
            this.userList = new ArrayList<>();
        }
        
    }
    
    /**
     * Loads all users into the userList attribute.
     */
    public void clearUserFilter() {
        
        try {
            this.userList = this.userRepo.getAllBankUsers();
        } catch (Exception e) {
            this.userList = new ArrayList<>();
        }
        
    }
    
    /**
     * Return a list of all Australian states.
     * 
     * @return List All Australian states.
     */
    public List<String> getAllStates(){
        ArrayList<String> retValue = new ArrayList<>();
        
        retValue.add("ACT");
        retValue.add("NSW");
        retValue.add("NT");
        retValue.add("QLD");
        retValue.add("SA");
        retValue.add("TAS");
        retValue.add("VIC");
        retValue.add("WA");
        
        return retValue;
    }
    
    /**
     * Searches the dataList for a particular transaction.
     * 
     * @param transaction The transaction being searched.
     * @return boolean Whether or not the transaction exists.
     */
    public boolean contains(BankTransaction transaction){
        
        for (BankTransaction t : dataList) {
            if (t.getBankTransactionId() == transaction.getBankTransactionId()){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Finds the common elements between two sets.
     * 
     * @param list1 The first searched set.
     * @param list2 The second searched set.
     * @return Set A set with the common elements.
     */
    public Set<BankTransaction> intersect(Set<BankTransaction> list1, Set<BankTransaction> list2){
        
        Set<BankTransaction> retValue = new HashSet<>();
        
        for (BankTransaction t : list1){
            for (BankTransaction b : list2){
                if (t.getBankTransactionId() == b.getBankTransactionId()){
                    retValue.add(t);
                }
            }
        }
        
        return retValue;
    }
    
    /**
     * Ends the session with the user.
     * 
     * @return String The login page.
     */
    public String logoutUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();
        this.customer = new BankUser();
        this.resetBean();
        return "/index.xhtml";
    }
    
    /**
     * This method hashes the password.  It was provided mkyong.
     * https://www.mkyong.com/java/java-sha-hashing-example/
     * 
     * @param password The password to be hashed.
     * @return String The hashed password.
     */
    public String hashPassword(String password) {
        
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();
            
            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //System.out.println("Hex format : " + sb.toString());

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<byteData.length;i++) {
    		String hex=Integer.toHexString(0xff & byteData[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
            }
    	//System.out.println("Hex format : " + hexString.toString());
        
            return hexString.toString();
            
        } catch (Exception e) {
            return password;
        }
        
    }
}
