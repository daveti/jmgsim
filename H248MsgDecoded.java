/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * Definition of decoded H.248 msg
 * 
 */
public class H248MsgDecoded {
    
    public H248MsgDecoded() {
        numOfTransactions = 0;
        mIdDomainName = null;
        mIdPort = -1;
	isMgcMidContained = false;
        isLongToken = false;
        h248TransactionArray = null;
    }
    
    public int getNumOfTransactions() {
        return this.numOfTransactions;
    }
    
    public void increaseNumOfTransactions() {
        this.numOfTransactions++;
    }
    
    public String getMidDomanName() {
        return mIdDomainName;
    }
    
    public void setMidDomainName(String mIdDomainName) {
        this.mIdDomainName = mIdDomainName;
    }
    
    public int getMidPortNum() {
        return mIdPort;
    }
    
    public void setMidPortNum(int mIdPort) {
        this.mIdPort = mIdPort;
    }
    
    public boolean isMgcMidContainedMid() {
	return isMgcMidContained;
    }
    
    public void setIsMgcMidContainedFlag(boolean isMgcMidContained) {
	this.isMgcMidContained = isMgcMidContained;
    }
    
    public boolean isLongTokenMsg() {
        return isLongToken;
    }
    
    public void setIsLongTokenFlag(boolean isLongToken) {
        this.isLongToken = isLongToken;
    }
    
    public H248Transaction[] getH248TransactionArray() {
        return this.h248TransactionArray;
    }
    
    public void setH248TransactionArray(H248Transaction[] h248TransactionArray) {
        this.h248TransactionArray = h248TransactionArray;
    }
    
    private int numOfTransactions;
    private String mIdDomainName;
    private int mIdPort;
    private boolean isMgcMidContained;
    private boolean isLongToken;
    private H248Transaction[] h248TransactionArray;
}