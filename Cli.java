/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author daveti
 */
public class Cli {
    
    // This constructor is used for MGC cli obj init
    public Cli(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj, MsgProc procObj,
		CommonWorkerThread commonObj, Timer timerObj,
		TimerTask hbTimerTaskObj) {
	this(transObj, dbObj, buildObj, procObj, commonObj, timerObj);
	this.hbTimerTaskObj = hbTimerTaskObj;
    }

    // This constructor is used for MG cli obj init
    public Cli(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj, MsgProc procObj,
		CommonWorkerThread commonObj, Timer timerObj,
		MgRegThread mgRegObj, TimerTask hbTimerTaskObj) {
	this(transObj, dbObj, buildObj, procObj, commonObj, timerObj);
	this.mgRegObj = mgRegObj;
	this.hbTimerTaskObj = hbTimerTaskObj;
    }
    
    // This constructor is used for common cli obj init
    public Cli(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj, MsgProc procObj,
		CommonWorkerThread commonObj, Timer timerObj) {
        this.transObj = transObj;
	this.dbObj = dbObj;
	this.buildObj = buildObj;
	this.procObj = procObj;
	this.commonObj = commonObj;
	this.timerObj = timerObj;
	this.mgRegObj = null;
	this.hbTimerTaskObj = null;
	// Set main/cli thread as the highest priority
	Thread.currentThread().setPriority(Thread.NORM_PRIORITY+3);
	Thread.currentThread().setName("mainCliThread");
    }
    
    public void printHelp() {
        System.out.println("COMMON COMMANDS:");
        System.out.println("\tstart                     - start jmgsim/jmgcsim");
        System.out.println("\tquit/exit                 - quit jmgsim/jmgcsim");
        System.out.println("\thelp                      - show help menu");
        System.out.println("\tversion                   - show build version");
        System.out.println("\tlogLevel/ll               - show/change log level");
        System.out.println("\tsend <msg>                - send msg to MG/MGC; " +
		"msg file needs to have suffix: " + GlobalConfig.SENDING_MSG_SUFFIX);
        System.out.println("\tdumpGlobalConfig/dgc      - dump global config data");
        System.out.println("\treqGarbageCollect/rgc     - request garbage collection");
	System.out.println("\tdisplayThreadInfo/dti	- display thread info");
        System.out.println();
        System.out.println("MG COMMANDS:");
        System.out.println("\tdisplayRegStatus/drs      - display media gateway registration status");
        System.out.println("\tdumpGwInfo/dgi <mID/gwId> - dump gateway info based on mID/gwId; " +
		"mId should be domainName including '<>'");
	System.out.println("\tsend <msg>		- send msg to remote MGC");
        System.out.println();
        System.out.println("MGC COMMANDS:");
	System.out.println("\tsend <msg> <mID/gwId>	- send msg to certain MG");
    }
    
    public void cliProc() {
        try {
            System.out.print(">>");
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            String command = reader.readLine();
            
            while (command != null) {
                command = command.trim();
                MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "cmd: " + command);
                if (command.equals("exit") || command.equals("quit")) {
                    quitMgMgcSim();
                } else if (command.equals("help")) {
                    printHelp();
                } else if (command.equals("start")) {
                    startMgMgcSim();
                } else if (command.equals("reqGarbageCollect") || command.equals("rgc")) {
                    System.gc();
                } else if (command.equals("version")) {
		    printBuildInfo();
		} else if (command.equals("dumpGlobalConfig") || command.equals("dgc")) {
		    GlobalConfig.dumpConfigData();
		} else if (command.startsWith("logLevel") || command.startsWith("ll")) {
		    logLevelControl(command);
		} else if (command.startsWith("send")) {
		    sendMsg(command);
		} else if (command.startsWith("dumpGwInfo") || command.startsWith("dgi")) {
		    dumpGwInfo(command);
		} else if (command.equals("displayRegStatus") || command.equals("drs")) {
		    displayRegStatus();
		} else if (command.equals("displayThreadInfo") || command.equals("dts")) {
		    displayThreadInfo();
		} else {
		    System.out.println("Unknown command");
		}
		System.out.print("\n>>");
		command = reader.readLine();
            }
        } catch (IOException ex) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
        }
    }
    
    private void displayThreadInfo() {
	System.out.println("Id:Name:Priority:State");
	System.out.println(Thread.currentThread().getId() + ":" +
			Thread.currentThread().getName() + ":" +
			Thread.currentThread().getPriority() + ":" +
			Thread.currentThread().getState());
	System.out.println(commonObj.getId() + ":" +
			commonObj.getName() + ":" +
			commonObj.getPriority() + ":" +
			commonObj.getState());
	if (GlobalConfig.isMgSim() == true) {
	    System.out.println(mgRegObj.getId() + ":" +
			mgRegObj.getName() + ":" +
			mgRegObj.getPriority() + ":" +
			mgRegObj.getState());
	    ((MgHbTimerTask)hbTimerTaskObj).dumpMyThreadInfo();
	} else {
	    ((MgcHbTimerTask)hbTimerTaskObj).dumpMyThreadInfo();
	}
    }
    
    private void dumpGwInfo(String command) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "command: " + command);
	String cmdName;
	String dgiPara;
	GwInfo gwInfo;
	if (command.equals("dumpGwInfo") || command.equals("dgi")) {
	    System.out.println("Need to follow mIdDomainName/gwId after this command");
	    System.out.println("E.g,");
	    System.out.println("dgi <jmgsim1.lucent.com>");
	    System.out.println("dgi 1024");
	} else {
	    if (command.startsWith("dumpGwInfo")) {
		cmdName = "dumpGwInfo";
	    } else {
		cmdName = "dgi";
	    }
	    
	    // Get the comand parameter
	    dgiPara = command.substring(cmdName.length()).trim();
	    MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "command parameter: " + dgiPara);
	    
	    // Determine if this is mIdDomainName or gwId
	    if (dgiPara.startsWith("<")) {
		// mIdDomain handling - try to get the gwInfo
		gwInfo = dbObj.getGwInfoFromMid(dgiPara);
	    } else {
		// gwId handling - try to get the gwInfo
		gwInfo = dbObj.getGwInfoFromGwId(Integer.parseInt(dgiPara));
	    }
	    
	    if (gwInfo != null) {
		gwInfo.dumpGwInfo();    
	    } else {
		System.out.println("invalid parameter: " + dgiPara +
			" - unable to find the corresponding gwInfo in DB");
	    }
	}
	    
    }
    
    private void displayRegStatus() {
        System.out.println("Num of GW configured: " + GlobalConfig.getNumOfGateways());
	System.out.println("Num of GW registered: " + dbObj.getNumOfGwRegistered());
	if (GlobalConfig.isMgSim() == true) {
	    System.out.println("Num of loops in Reg thread: " +
		    mgRegObj.getNumOfLoops());
	    System.out.println("Num of loops in HB thread: " +
		    ((MgHbTimerTask)hbTimerTaskObj).getNumOfLoops());
	} else {
	    System.out.println("Num of loops in HB thread: " +
		    ((MgcHbTimerTask)hbTimerTaskObj).getNumOfLoops());
	}
    }
    
    private void sendMsg(String command) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "command: " + command);
	String sendPara = command.substring(("send").length()).trim();
	// Assume sendPara would be like below:
	// xxxx.msg<jmgsim1.lucent.com>
	// xxxx.msg1024
	if (sendPara.contains(GlobalConfig.SENDING_MSG_SUFFIX) == false) {
	    System.out.println("invalid parameter - msg file needs suffix: " +
		    GlobalConfig.SENDING_MSG_SUFFIX);
	    return;
	}
	
	// Get file name and mIdOrGwId
	int endIndex = sendPara.indexOf(GlobalConfig.SENDING_MSG_SUFFIX) +
				GlobalConfig.SENDING_MSG_SUFFIX.length();			
	String msgFile = sendPara.substring(0, endIndex).trim();
	String mIdOrGwId = sendPara.substring(endIndex).trim();
	
	// Check the file
	if (new File(msgFile).exists() == false) {
	    System.out.println("Unfound file: " + msgFile);
	    return;
	}
	
	// Open, read and close the file
	StringBuilder sb = new StringBuilder();
	try {
	    BufferedReader br = new BufferedReader(new FileReader(msgFile));
	    String line;
	    // NOTE: readLine has some bitchy properties...
	    while ((line = br.readLine()) != null) {
		sb.append(line);
		sb.append(System.getProperty("line.separator"));
		//sb.append("\n");
	    }
	    br.close();
	} catch (IOException ex) {
	    System.out.println("Excpetion: " + ex);
	    return;
	}
	
	GwInfo gwInfo = null; // Only needed for MGC sim
	if (GlobalConfig.isMgSim() == false) {
	    // Find the gwInfo based on mId/gwIndex
	    if (mIdOrGwId.startsWith("<") == true) {
		gwInfo = dbObj.getGwInfoFromMid(mIdOrGwId);
	    } else {
		gwInfo = dbObj.getGwInfoFromGwId(Integer.parseInt(mIdOrGwId));
	    }
	
	    if (gwInfo == null) {
		System.out.println("invalid parameter: " + mIdOrGwId +
			" - unable to find the corresponding gwInfo in DB");
		return;
	    }
	}
	
	// Construct a new H248Msg structure
	H248Msg h248MsgOut;
	if (GlobalConfig.isMgSim() == true) {
	    h248MsgOut = new H248Msg(GlobalConfig.getRemoteIPv4Addr(),
					GlobalConfig.getRemotePortNum(),
					sb.toString(),
					sb.toString().length());
	} else {
	    h248MsgOut = new H248Msg(gwInfo.getOrigIP(),
					gwInfo.getOrigPortNum(),
					sb.toString(),
					sb.toString().length());
	}
	
	// Send the msg
	transObj.udpSend(h248MsgOut);   
    }
    
    private void logLevelControl(String command) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "command: " + command);
	String cmdName;
	if (command.equals("logLevel") || command.equals("ll")) {
	    // Display the current log level
	    System.out.println("Current log level: " + GlobalConfig.getLogLevel());
	} else {
	    if (command.startsWith("logLevel")) {
		cmdName = "logLevel";
	    } else {
		cmdName = "ll";
	    }
	    
	    // Get the new log level
	    int logLevel = Integer.parseInt(command.substring(cmdName.length()).trim());
	    MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "new log level: " + logLevel);
	    
	    // Set the new log level
	    GlobalConfig.setLoglevel(logLevel);
	}
    }
    
    private void printBuildInfo() {
	// Currently only retrieve the data in GlobalConfig
	// daveti - need to retrieve much more info in manifest.mf
	// such as build data...
	System.out.println(GlobalConfig.getVersion());
    }
    
    private void quitMgMgcSim() {
/*
	if (GlobalConfig.isMgSim() == true) {
	    // Stop MG HB timerTask
	    ((MgHbTimerTask)hbTimerTaskObj).setIsHaltedFlag(true);
	    
	    // Stop MG Reg thread
	    mgRegObj.setIsHaltedFlag(true);
	} else {
	    // Stop MGC HB timerTask
	    ((MgcHbTimerTask)hbTimerTaskObj).setIsHaltedFlag(true);
	}
	
	// Stop the timer
	timerObj.cancel();
	
	// Try to close the socket
	if (transObj.getUdpSocket() != null) {
	    transObj.closeUdpSocket();
	}
*/
	
	// Stop the main and exit
	System.exit(0);
    }
    
    private void startMgMgcSim() {
	// Start the common worker thread
	commonObj.start();
	MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "Common worker thread is started");
	
	if (GlobalConfig.isMgSim() == true) {
	    // Start the Mg reg thread
	    mgRegObj.start();
	    MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "MG Registration thread is started");
	}
	// For Mg HB and Mgc HB:
	// HB timer task would be triggered within MsgProc once there is a GW
	// got registered. Do not need to do anything here again.
    }
    
    private Transport transObj;
    private GwInfoDB dbObj;
    private MsgBuild buildObj;
    private MsgProc procObj;
    private CommonWorkerThread commonObj;
    private Timer timerObj;
    private MgRegThread mgRegObj;
    private TimerTask hbTimerTaskObj;
}
