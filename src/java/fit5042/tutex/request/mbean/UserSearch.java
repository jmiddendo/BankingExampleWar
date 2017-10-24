/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fit5042.tutex.request.mbean;

import fit5042.tutex.repository.BankUserRepository;
import fit5042.tutex.repository.entities.BankUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author jmid3
 */
@ManagedBean(name = "userSearch")
@RequestScoped
public class UserSearch implements Serializable {
    
    @EJB
    private BankUserRepository repo;
    
    private Integer id;
    private String firstName;
    private String lastName;
    private String type;
    private String email;
    
    List<BankUser> searchResults;
    
    public UserSearch() {
        id = 0;
        firstName = "";
        lastName = "";
        type = "";
        email = "";
        
        searchResults = new ArrayList();
    }

    public BankUserRepository getRepo() {
        return repo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BankUser> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<BankUser> searchResults) {
        this.searchResults = searchResults;
    }
    
    
    
    public void performCombinationSearch() {
        BankUser user = new BankUser();
        
        if (id == null) {
            user.setBankUserId(0);
        } else {
            user.setBankUserId(id);
        }
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setType(type);
        user.setEmail(email);
        
        try {
            List<BankUser> results = new ArrayList(this.repo.searchBankUserByCombination(user));
            this.searchResults = results;
        } catch (Exception e) {
            
        }
    }
}
