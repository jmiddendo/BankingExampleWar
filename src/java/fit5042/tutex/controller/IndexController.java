/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fit5042.tutex.controller;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author jmid3
 */
@Named("indexController")
@RequestScoped
public class IndexController {
    
    private String pageTitle;
    private int transactionId;

    public IndexController() {
        this.pageTitle = "Viewing Transactions";
        this.transactionId = 0;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public int getTransactionId() {
        
        transactionId = Integer.valueOf(FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("transactionId"));
        return transactionId;
        
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    
    
}
