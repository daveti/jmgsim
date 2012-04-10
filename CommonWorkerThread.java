/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 *
 * @author daveti
 */
public class CommonWorkerThread extends Thread {
	
    public CommonWorkerThread(Transport transObj, MsgProc procObj) {
        isHalted = false;
	h248MsgRecvd = null;
	this.transObj = transObj;
	this.procObj = procObj;
	initUdpSocket();
    }
    
    public void setIsHaltedFlag(boolean isHalted) {
	this.isHalted = isHalted;
    }
    
    private void initUdpSocket() {
	// Open the UDP socket
	transObj.createUdpSocket(GlobalConfig.getLocalIPv4Addr(), GlobalConfig.getLocalPortNum());
		
	// Set the timeOut value
	transObj.setSocketTimeOut(UDP_SOCK_TIME_OUT);
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "UDP socket init done");
    }
    
    @Override
    public void run() {
	setPriority(Thread.NORM_PRIORITY+3);
	setName("commonWorkerThread");

	while (isHalted == false) {
	    // Try to receive the msg
	    try {
	        try {
		    h248MsgRecvd = transObj.udpRecv();
		} catch (SocketTimeoutException ex) {
		    // Sleep for a while to wait for the msg
		    mySleep(UDP_SOCK_WAIT_TIME);
		}
	    } catch (IOException ex) {
		MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exceptoin: " + ex);
	    }
		
	    // Process this incoming msg
	    if (h248MsgRecvd != null) {
	        procObj.processMsg(h248MsgRecvd);
		h248MsgRecvd = null;
	    }
	} 
    }
    
    private void mySleep(int millisec) {
        try {
	    Thread.sleep(millisec);
	} catch (InterruptedException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
	}
    }
    
    private H248Msg h248MsgRecvd;
    private volatile boolean isHalted;
    private final Transport transObj;
    private final MsgProc procObj;
    private static final int UDP_SOCK_TIME_OUT = 10; // ms
    private static final int UDP_SOCK_WAIT_TIME = 5; // ms
}
