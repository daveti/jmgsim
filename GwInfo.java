/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author daveti
 * NOTE: GwInfo would be used by both jmgsim and jmgcsim.
 * All the numOfXXXXX is statistical data for msgs either incoming
 * or outgoing from this process.
 * 
 */
public class GwInfo {
    
    public GwInfo(String mIdDomainName, int mIdPort, String origIP, int origPort, int gwId, int gwIndex) {
        this();
        this.mIdDomainName = mIdDomainName;
        this.mIdPort = mIdPort;
        this.origIP = origIP;
        this.origPort = origPort;
        this.gwId = gwId;
        this.gwIndex = gwIndex;
    }
    
    // This constuctor is used for adding new gwInfo into DB by MGC sim
    public GwInfo(String mIdDomainName, int mIdPort, String origIP, int origPort, int gwId, int gwIndex,
            boolean isRegistered) {
        this(mIdDomainName, mIdPort, origIP, origPort, gwId, gwIndex);
        this.isRegistered = isRegistered;
    }
    
    public GwInfo() {
        mIdDomainName = null;
        mIdPort = -1;
        origIP = null;
        origPort = -1;
        gwId = -1;
        gwIndex = -1;
        isRegistered = false;
        errorCode = null;
        numOfItItoNotifyRootReq = 0;
        numOfAuditValueRootReq = 0;
        numOfServiceChangeReq = 0;
        numOfServiceChangeRep = 0;
        numOfAuditValueReq = 0;
        numOfAuditValueReq = 0;
        numOfSubtractReq = 0;
        numOfSubtractRep = 0;
        numOfModifyReq = 0;
        numOfModifyRep = 0;
        numOfNotifyReq = 0;
        numOfNotifyRep = 0;
        numOfAddReq = 0;
        numOfAddRep = 0;
        numOfOtherReq = 0;
        numOfOtherRep = 0;
        numOfAck = 0;
        transIdForReq = 1;
        timeStampOfLatestItItoNotifyRootReq = null;
        timeStampOfLatestAuditValueRootReq = null;
        gwLock = new ReentrantLock();
    }
    
    public String getMidDomainName() {
        return mIdDomainName;
    }
    
    public void setMidDomainName(String mIdDomainName) {
        this.mIdDomainName = mIdDomainName;
    }
    
    public int getMidPortNum() {
        return mIdPort;
    }
    
    public void setMidPortNum(int portNum) {
        this.mIdPort = portNum;
    }
    
    public String getOrigIP() {
        return origIP;
    }
    
    public void setOrigIP(String origIP) {
        this.origIP = origIP;
    }
    
    public int getOrigPortNum() {
        return origPort;
    }
    
    public void setOrigPortNum(int origPort) {
        this.origPort = origPort;
    }
    
    public int getGwId() {
        return gwId;
    }
    
    public void setGwId(int gwId) {
        this.gwId = gwId;
    }
    
    public int getGwIndex() {
        return gwIndex;
    }
    
    public void setGwIndex(int gwIndex) {
        this.gwIndex = gwIndex;
    }
    
    public boolean isGwRegistered() {
        return isRegistered;
    }
    
    public void setGwRegisterState(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
    
    public ReentrantLock getGwLock() {
	return gwLock;
    }
 
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
  
    public long getNumOfItItoNotifyRootReq() {
        return numOfItItoNotifyRootReq;
    }
    
    public void increaseNumOfItItoNotifyRootReq() {
        this.numOfItItoNotifyRootReq++;
    }
    
    public long getNumOfAuditValueRootReq() {
        return numOfAuditValueRootReq;
    }
    
    public void increaseNumOfAuditValueRootReq() {
        this.numOfAuditValueRootReq++;
    }
   
    public long getNumOfServiceChangeReq() {
        return numOfServiceChangeReq;
    }
    
    public void increaseNumOfServiceChangeReq() {
        this.numOfServiceChangeReq++;
    }
    
    public long getNumOfServiceChangeRep() {
        return numOfServiceChangeRep;
    }
    
    public void increaseNumOfServiceChangeRep() {
        this.numOfServiceChangeRep++;
    }
    
    public long getNumOfAuditValueReq() {
	return numOfAuditValueReq;
    }
    
    public void increaseNumOfAuditValueReq() {
	this.numOfAuditValueReq++;
    }
    
    public long getNumOfAuditValueRep() {
	return numOfAuditValueRep;
    }
    
    public void increaseNumOfAuditValueRep() {
	this.numOfAuditValueRep++;
    }
    
    public long getNumOfSubtractReq() {
	return numOfSubtractReq;
    }
    
    public void increaseNumOfSubtractReq() {
	this.numOfSubtractReq++;
    }
    
    public long getNumOfSubtractRep() {
	return numOfSubtractRep;
    }
    
    public void increaseNumOfSubtractRep() {
	this.numOfSubtractRep++;
    }
    
    public long getNumOfModifyReq() {
	return numOfModifyReq;
    }
    
    public void increaseNumOfModifyReq() {
	this.numOfModifyReq++;
    }
    
    public long getNumOfModifyRep() {
	return numOfModifyRep;
    }
    
    public void increaseNumOfModifyRep() {
	this.numOfModifyRep++;
    }
    
    public long getNumOfNotifyReq() {
	return numOfNotifyReq;
    }
    
    public void increaseNumOfNotifyReq() {
	this.numOfNotifyReq++;
    }
    
    public long getNumOfNotifyRep() {
	return numOfNotifyRep;
    }
    
    public void increaseNumOfNotifyRep() {
	this.numOfNotifyRep++;
    }
    
    public long getNumOfAddReq() {
	return numOfAddReq;
    }
    
    public void increaseNumOfAddReq() {
	this.numOfAddReq++;
    }
    
    public long getNumOfAddRep() {
	return numOfAddRep;
    }
    
    public void increaseNumOfAddRep() {
	this.numOfAddRep++;
    }
    
    public long getNumOfAck() {
	return numOfAck;
    }
    
    public void increaseNumOfAck() {
	this.numOfAck++;
    }
    
    public long getNumOfOtherReq() {
	return numOfOtherReq;
    }
    
    public void increaseNumOfOtherReq() {
	this.numOfOtherReq++;
    }
    
    public long getNumOfOtherRep() {
	return numOfOtherRep;
    }
    
    public void increaseNumOfOtherRep() {
	this.numOfOtherRep++;
    }
    
    public long getTransIdForReq() {
	return transIdForReq;
    }
    
    public void increaseTransIdForReq() {
	this.transIdForReq++;
    }
    
    public Date getTimeStampOfLatestItItoNotifyRootReq() {
	return timeStampOfLatestItItoNotifyRootReq;
    }
    
    public void setTimeStampOfLatestItItoNotifyRootReqNow() {
	this.timeStampOfLatestItItoNotifyRootReq = new Date();
    }
 
    public Date getTimeStampOfLatestAuditValueRootReq() {
	return timeStampOfLatestAuditValueRootReq;
    }
    
    public void setTimeStampOfLatestAuditValueRootReqNow() {
	this.timeStampOfLatestAuditValueRootReq = new Date();
    }
    
    public void dumpGwInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        System.out.println("mIdDomainName = " + mIdDomainName);
        System.out.println("mIdPort = " + mIdPort);
        System.out.println("origIP = " + origIP);
        System.out.println("origPort = " + origPort);
        System.out.println("gwId = " + gwId);
        System.out.println("gwIndex = " + gwIndex);
        System.out.println("isRegistered = " + isRegistered);
        System.out.println("errorCode = " + errorCode);
        System.out.println("numOfItItoNotifyRootReq = " + numOfItItoNotifyRootReq);
        System.out.println("numOfAuditValueRootReq = " + numOfAuditValueRootReq);
        System.out.println("numOfServiceChangeReq = " + numOfServiceChangeReq);
        System.out.println("numOfServiceChangeRep = " + numOfServiceChangeRep);
        System.out.println("numOfAuditValueReq = " + numOfAuditValueReq);
        System.out.println("numOfAuditValueRep = " + numOfAuditValueRep);
        System.out.println("numOfSubtractReq = " + numOfSubtractReq);
        System.out.println("numOfSubtractRep = " + numOfSubtractRep);
        System.out.println("numOfModifyReq = " + numOfModifyReq);
        System.out.println("numOfModifyRep = " + numOfModifyRep);
        System.out.println("numOfNotifyReq = " + numOfNotifyReq);
        System.out.println("numOfNotifyRep = " + numOfNotifyRep);
        System.out.println("numOfAddReq = " + numOfAddReq);
        System.out.println("numOfAddRep = " + numOfAddRep);
        System.out.println("numOfOtherReq = " + numOfOtherReq);
        System.out.println("numOfOtherRep = " + numOfOtherRep);
        System.out.println("numOfAck = " + numOfAck);
        System.out.println("transIdForReq = " + transIdForReq);
	if (timeStampOfLatestItItoNotifyRootReq != null) {
	    System.out.println("timeStampOfLatestItItoNotifyRootReq = " +
			formatter.format(timeStampOfLatestItItoNotifyRootReq));
	} else {
	    System.out.println("timeStampOfLatestItItoNotifyRootReq = null");
	}
	if (timeStampOfLatestAuditValueRootReq != null) {
	    System.out.println("timeStampOfLatestAuditValueRootReq = " +
			formatter.format(timeStampOfLatestAuditValueRootReq));
	} else {
	    System.out.println("timeStampOfLatestAuditValueRootReq = null");
	}
        System.out.println("gwLock = " + gwLock);
    }
    
    private String mIdDomainName;
    private int mIdPort;
    private String origIP;
    private int origPort;
    private int gwId;    // For jmgsim, this field should be real gwId from '(baseNum)'
    private int gwIndex; // this field is used to tell the array index of gw in gw array
    private boolean isRegistered;
    private String errorCode;
    private long numOfItItoNotifyRootReq;
    private long numOfAuditValueRootReq;
    private long numOfServiceChangeReq;
    private long numOfServiceChangeRep;
    private long numOfAuditValueReq;
    private long numOfAuditValueRep;
    private long numOfSubtractReq;
    private long numOfSubtractRep;
    private long numOfModifyReq;
    private long numOfModifyRep;
    private long numOfNotifyReq;
    private long numOfNotifyRep;
    private long numOfAddReq;
    private long numOfAddRep;
    private long numOfOtherReq;
    private long numOfOtherRep;
    private long numOfAck;
    private long transIdForReq; // Caller has responsibility to increment the value after using
    private Date timeStampOfLatestItItoNotifyRootReq;
    private Date timeStampOfLatestAuditValueRootReq;
    private ReentrantLock gwLock;
}
