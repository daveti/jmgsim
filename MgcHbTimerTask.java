/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.util.TimerTask;

/**
 *
 * @author daveti
 */
public class MgcHbTimerTask extends TimerTask {
  
    public MgcHbTimerTask(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj, MsgProc procObj) {
	this.isStarted = false;
	this.isHalted = false;
        this.transObj = transObj;
	this.dbObj = dbObj;
	this.buildObj = buildObj;
	this.procObj = procObj;
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
	Thread.currentThread().setName("mgcHbTimerTask");
	if (isHalted == true) {
	    return;
	}
	// Count init
	indexForGwInfo = 0;
	countForRange = 0;
	for (; indexForGwInfo < GlobalConfig.getNumOfGateways(); indexForGwInfo++) {
	    gwInfo = dbObj.getGwInfoArray()[ indexForGwInfo];
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "indexForGwInfo: " + indexForGwInfo);
	    
	    // If we do not consider gwInfo deleting after certain num of 
	    // MGC HB, then the following gwInfo after this index would be
	    // null too because of the way gwInfo added into DB of MGC.
	    // However, when we implement gwInfo deleting in future, this
	    // assumption would not work anymore - thus need to loop each
	    // one till 'numOfGateways'
	    if (gwInfo != null) {
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
			
			// Mark the flag for start MGC HB later
			needToHb = true;
		    
			// Update the counter for AuditValue Root
			gwInfo.increaseNumOfAuditValueRootReq();
		    
			// Update the counter for AuditValue Req
			gwInfo.increaseNumOfAuditValueReq();
		    
			// Set the time stamp of this HB
			gwInfo.setTimeStampOfLatestAuditValueRootReqNow();
		    
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
	    }
	    
	    // Start the MGC HB for this GwInfo
	    if (needToHb == true) {
		// Build and send the MGC HB
		buildAndSendMsgForMgcHb(transIdForHb, gwInfo);
		
		// Flash the reg flag for next gwInfo
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
    
    private void buildAndSendMsgForMgcHb(long transId, GwInfo gwInfo) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transId: " + transId +
		", gwInfo: " + gwInfo);
	
	// Generate mId domain name for MGC
	String mIdDomainName = procObj.generateMidDomainNameForMgc(gwInfo.getMidDomainName());
	
	// Build the AuditValue Root Req
	msgData = buildObj.buildAuditValueRootReqForMgcHb(transId, mIdDomainName,
						GlobalConfig.getMidPortNumForMgc());
	
	// Construct a new H248Msg structure
	h248MsgOut = new H248Msg(gwInfo.getOrigIP(),
				gwInfo.getOrigPortNum(),
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
    private final MsgProc procObj;
}
