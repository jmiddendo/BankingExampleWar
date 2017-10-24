/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fit5042.tutex.request.mbean;

import fit5042.tutex.repository.TransactionTypeRepository;
import fit5042.tutex.repository.WebServiceConsumptionRepository;
import fit5042.tutex.repository.entities.TransactionType;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author jmid3
 */
@ManagedBean(name = "transactionTypeAdd")
@RequestScoped
public class TransactionTypeAdd implements Serializable {
    
    @EJB
    private TransactionTypeRepository typeRepo;
    
    @EJB
    private WebServiceConsumptionRepository ws;
    
    String webServiceResponse;
    TransactionType transactionType;

    public TransactionTypeAdd() {
        webServiceResponse = "";
        transactionType = new TransactionType();
    }

    public TransactionTypeRepository getTypeRepo() {
        return typeRepo;
    }

    public void setTypeRepo(TransactionTypeRepository typeRepo) {
        this.typeRepo = typeRepo;
    }

    public WebServiceConsumptionRepository getWs() {
        return ws;
    }

    public void setWs(WebServiceConsumptionRepository ws) {
        this.ws = ws;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getWebServiceResponse() {
        return webServiceResponse;
    }

    public void setWebServiceResponse(String webServiceResponse) {
        this.webServiceResponse = webServiceResponse;
    }
    
    public void addTransactionType() {
        
        try {
            this.transactionType.setTransactionTypeId(typeRepo.generateNextId());
            typeRepo.addTransactionType(transactionType);
            this.transactionType = new TransactionType();
        } catch (Exception ex) {
            Logger.getLogger(TransactionTypeAdd.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void searchBankTransactions() {
        this.webServiceResponse = ws.searchTransactionByTransactionType(this.transactionType.getTransactionTypeName());
    }
    
}
