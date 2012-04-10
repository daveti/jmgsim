/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * Definition of H.248 Transaction
 * 
 */
public class H248Transaction {
    
    public H248Transaction() {
        numOfActions = 0;
        transLength = 0;
        transTag = 0;
        transId = 0;
        h248ActionArray = null;
    }
    
    public int getNumOfActions() {
        return numOfActions;
    }
    
    public void increaseNumOfActions() {
        this.numOfActions++;
    }
    
    public int getTransLength() {
        return transLength;
    }
    
    public void setTransLength(int transLength) {
        this.transLength = transLength;
    }
    
    public int getTransTag() {
        return transTag;
    }
    
    public void setTransTag(int transTag) {
        this.transTag = transTag;
    }
    
    public long getTransId() {
        return transId;
    }
    
    public void setTransId(long transId) {
        this.transId = transId;
    }
    
    public H248Action[] getH248ActionArray() {
        return h248ActionArray;
    }
    
    public void setH248ActionArray(H248Action[] h248ActionArray) {
        this.h248ActionArray = h248ActionArray;
    }
    
    private int numOfActions;
    private int transLength;
    private int transTag;
    private long transId;
    private H248Action[] h248ActionArray;
}
