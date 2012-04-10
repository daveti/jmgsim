/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 */
public class MgRegThread extends Thread {
    
    public MgRegThread(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj) {
	this.isHalted = false;
        this.transObj = transObj;
	this.dbObj = dbObj;
	this.buildObj = buildObj;
	this.countForRange = 0;
	this.indexForGwInfo = 0;
	this.numOfLoops = 0;
	this.gwInfo = null;
	this.needToReg = false;
	this.transIdForReg = 0;
	this.h248MsgOut = null;
	this.msgData = null;
    }
    
    public void setIsHaltedFlag(boolean isHalted) {
	this.isHalted = isHalted;
    }
    
    public long getNumOfLoops() {
	return numOfLoops;
    }
    
    @Override
    public void run() {
	setPriority(Thread.NORM_PRIORITY+1);
	setName("mgRegThread");
	while (isHalted == false) {
	    gwInfo = dbObj.getGwInfoArray()[ indexForGwInfo];
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "indexForGwInfo: " +
		    indexForGwInfo + ", gwInfo: " + gwInfo);
	    
	    // Now lock this gwInfo
	    gwInfo.getGwLock().lock();
	    try {
		if (gwInfo.isGwRegistered() == false) {
		    // Do the gwInfo update and retrieve the necessary data
		    // for registration build and send - in this way, we are
		    // trying to low down the negative impact of lock by
		    // leaving all other stuffs out side lock block.
		    // NOTE: other thread/timer/timerTask should follow this
		    // rule. Thx, daveti.
			
		    // Mark the flag for start reg later
		    needToReg = true;
		    
		    // Update the counter for ServiceChange
		    gwInfo.increaseNumOfServiceChangeReq();
		    
		    // Retrieve transIdForReq
		    transIdForReg = gwInfo.getTransIdForReq();
		    
		    // Update the transIdForReq
		    gwInfo.increaseTransIdForReq();
		    
		} else {
		    // Mark the flag to avoid re-reg
		    needToReg = false;
		}  
	    } finally {
	        // Have to release the lock anyway
		gwInfo.getGwLock().unlock();
	    }
	    
	    // Start the registration for this GwInfo
	    if (needToReg == true) {
		// Build and send the registration
		buildAndSendMsgForGwReg(transIdForReg, 
					gwInfo.getMidDomainName(),
					gwInfo.getMidPortNum());
		
		// Mark the reg flag for next gwInfo
		needToReg = false;
	    }
	    
	    // Update countForRange, indexForGwInfo, numOfLoops and
	    // Sleep if needed
	    updateCountIndexLoopsAndSleep();
	}
    }
    
    private void mySleep(int millisec) {
        try {
	    Thread.sleep(millisec);
	} catch (InterruptedException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
	}
    }
    
    private void buildAndSendMsgForGwReg(long transId, String mIdDomainName, int mIdPort) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transId: " + transId +
		", mIdDomainName: " + mIdDomainName + ", mIdPort: " + mIdPort);
	
	// Build the ServiceChange Root Req with method 'restart'
	msgData = buildObj.buildServiceChangeRootReqForGwReg(transId, mIdDomainName, mIdPort);
	
	// Construct a new H248Msg structure
	h248MsgOut = new H248Msg(GlobalConfig.getRemoteIPv4Addr(),
				GlobalConfig.getRemotePortNum(),
				msgData,
				msgData.length());
	
	// Send the damn msg, Man!
	transObj.udpSend(h248MsgOut);
    }
    
    private void updateCountIndexLoopsAndSleep() {
	// Update indexForGwInfo and numOfLoops
	indexForGwInfo++;
	if (indexForGwInfo > GlobalConfig.getNumOfGateways()-1) {
	    // Init the index again for next loop
	    indexForGwInfo = 0;
	    // Update the loop
	    numOfLoops++;
	}
	
	// Update countForRange
	countForRange++;
	if (countForRange >= GlobalConfig.getNumOfGwPerRange()) {
	    // Init the count again for next range
	    countForRange = 0;
	    // Let's sleep here
	    mySleep(GlobalConfig.getSleepTimePerRange());
	}
    }
    
    private int countForRange;
    private int indexForGwInfo;
    private long numOfLoops;
    private GwInfo gwInfo;
    private boolean needToReg;
    private long transIdForReg;
    private H248Msg h248MsgOut;
    private String msgData;
    private volatile boolean isHalted;
    private final Transport transObj;
    private final GwInfoDB dbObj;
    private final MsgBuild buildObj;
}
