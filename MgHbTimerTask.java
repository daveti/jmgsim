/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.util.TimerTask;

/**
 *
 * @author daveti
 *
 */
public class MgHbTimerTask extends TimerTask {
   
    public MgHbTimerTask(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj) {
	this.isStarted = false;
	this.isHalted = false;
        this.transObj = transObj;
	this.dbObj = dbObj;
	this.buildObj = buildObj;
	this.countForRange = 0;
	this.indexForGwInfo = 0;
	this.numOfLoops = 0;
	this.gwInfo = null;
	this.needToHb = false;
	this.transIdForHb = 0;
	this.h248MsgOut = null;
	this.msgData = null;
    }
    
    public boolean isTimerTaskStarted() {
	return isStarted;
    }
    
    public void setIsStartedFlag(boolean isStarted) {
	this.isStarted = isStarted;
    }
    
    public void setIsHaltedFlag(boolean isHalted) {
	this.isHalted = isHalted;
    }
    
    public long getNumOfLoops() {
	return numOfLoops;
    }
    
    public void dumpMyThreadInfo() {
	System.out.println(Thread.currentThread().getId() + ":" +
			Thread.currentThread().getName() + ":" +
			Thread.currentThread().getPriority() + ":" +
			Thread.currentThread().getState());
    }
    
    @Override
    public void run() {
	Thread.currentThread().setPriority(Thread.NORM_PRIORITY+2);
	Thread.currentThread().setName("mgHbTimerTask");
	if (isHalted == true) {
	    return;
	}
	// Count init
	indexForGwInfo = 0;
	countForRange = 0;
	for (; indexForGwInfo < GlobalConfig.getNumOfGateways(); indexForGwInfo++) {
	    gwInfo = dbObj.getGwInfoArray()[ indexForGwInfo];
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "indexForGwInfo: " +
		    indexForGwInfo + ", gwInfo: " + gwInfo);
	    
	    // Now lock this gwInfo
	    gwInfo.getGwLock().lock();
	    try {
		if (gwInfo.isGwRegistered() == true) {
		    // Do the gwInfo update and retrieve the necessary data
		    // for registration build and send - in this way, we are
		    // trying to low down the negative impact of lock by
		    // leaving all other stuffs out side lock block.
		    // NOTE: other thread/timer/timerTask should follow this
		    // rule. Thx, daveti.
			
		    // Mark the flag for start GW HB later
		    needToHb = true;
		    
		    // Update the counter for it/ito Notify Root
		    gwInfo.increaseNumOfItItoNotifyRootReq();
		    
		    // Update the counter for Notify Req
		    gwInfo.increaseNumOfNotifyReq();
		    
		    // Set the time stamp of this HB
		    gwInfo.setTimeStampOfLatestItItoNotifyRootReqNow();
		    
		    // Retrieve transIdForReq
		    transIdForHb = gwInfo.getTransIdForReq();
		    
		    // Update the transIdForReq
		    gwInfo.increaseTransIdForReq();
		    
		} else {
		    // Mark the flag to avoid re-HB
		    needToHb = false;
		}  
	    } finally {
	        // Have to release the lock anyway
		gwInfo.getGwLock().unlock();
	    }
	    
	    // Start the GW HB for this GwInfo
	    if (needToHb == true) {
		// Build and send the GW HB
		buildAndSendMsgForGwHb(transIdForHb, 
					gwInfo.getMidDomainName(),
					gwInfo.getMidPortNum());
		
		// Flash the HB flag for next gwInfo
		needToHb = false;
	    }
	    
	    // Update countForRange and sleep if needed
	    updateCountAndSleep();
	}
	// Update the numOfLoops
	numOfLoops++;
    }
    
    private void mySleep(int millisec) {
        try {
	    Thread.sleep(millisec);
	} catch (InterruptedException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
	}
    }
    
    private void buildAndSendMsgForGwHb(long transId, String mIdDomainName, int mIdPort) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transId: " + transId +
		", mIdDomainName: " + mIdDomainName + ", mIdPort: " + mIdPort);
	
	// Build the it/ito Notify Root Req
	msgData = buildObj.buildItItoNotifyRootReqForGwHb(transId, mIdDomainName, mIdPort);
	
	// Construct a new H248Msg structure
	h248MsgOut = new H248Msg(GlobalConfig.getRemoteIPv4Addr(),
				GlobalConfig.getRemotePortNum(),
				msgData,
				msgData.length());
	
	// Send the damn msg, Man!
	transObj.udpSend(h248MsgOut);
    }
    
    private void updateCountAndSleep() {
	// Update countForRange
	countForRange++;
	if (countForRange >= GlobalConfig.getNumOfGwPerRange()) {
	    // Init count again for next range
	    countForRange = 0;
	    // Let's sleep here
	    mySleep(GlobalConfig.getSleepTimePerRange());
	}
    }
      
    private int countForRange;
    private int indexForGwInfo;
    private long numOfLoops;
    private GwInfo gwInfo;
    private boolean needToHb;
    private long transIdForHb;
    private H248Msg h248MsgOut;
    private String msgData;
    private boolean isStarted;
    private volatile boolean isHalted;
    private final Transport transObj;
    private final GwInfoDB dbObj;
    private final MsgBuild buildObj;   
}
