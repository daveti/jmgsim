/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author daveti
 */
public class GlobalConfig {

    public static String getLocalIPv4Addr() {
        return localIPv4;
    }

    public static String getLocalIPv6Addr() {
        return localIPv6;
    }

    public static int getLocalPortNum() {
        return localPort;
    }

    public static String getRemoteIPv4Addr() {
        return remoteIPv4;
    }

    public static String getRemoteIPv6Addr() {
        return remoteIPv6;
    }

    public static int getRemotePortNum() {
        return remotePort;
    }

    public static String getTransportType() {
        return transportType;
    }

    public static boolean isIPv4Connection() {
        return useIPv4;
    }
    
    public static boolean isMgSim() {
        return isMediaGateway;
    }

    public static boolean isLongTokenEncoding() {
        return isLongToken;
    }

    public static int getNumOfGateways() {
        return numOfGateways;
    }

    public static String getMidDomainNameForMg() {
        return mIdDomainNameForMg;
    }
    
    public static int getMidPortNumForMg() {
        return mIdPortForMg;
    }
    
    public static int getGwIdBaseNum() {
        return gwIdBaseNum;
    }
    
    public static String getMidDomainNameForMgLeft() {
        return mIdDomainNameForMgLeft;
    }
    
    public static String getMidDomainNameForMgRight() {
        return mIdDomainNameForMgRight;
    }

    public static String getMidDomainNameForMgc() {
        return mIdDomainNameForMgc;
    }
    
    public static int getMidPortNumForMgc() {
        return mIdPortForMgc;
    }

    public static int getInactTimer() {
        return inactTimer;
    }

    public static int getAuditTimer() {
        return auditTimer;
    }

    public static int getNumOfGwPerRange() {
        return numOfGwPerRange;
    }

    public static int getSleepTimePerRange() {
        return sleepTimePerRange;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLoglevel(int newLogLevel) {
        if ((newLogLevel<LOG_LEVEL_NONE) || (newLogLevel>LOG_LEVEL_HIGH)) {
            System.out.println("Bad log level - only 0~3 is valid");
        } else {
            logLevel = newLogLevel;
        }
    }
    
    public static String getVersion() {
	return VERSION;
    }
    
    // Get mIdDomainNameForMgLeft, mIdDomainNameForMgRight, gwIdBaseNum
    // from mIdDomainNameForMg respectively.
    public static void parseMidDomainNameForMg() {
        int indexOfLeftBrace = mIdDomainNameForMg.indexOf("(");
        int indexOfRightBrace = mIdDomainNameForMg.indexOf(")");
        mIdDomainNameForMgLeft = mIdDomainNameForMg.substring(0, indexOfLeftBrace);
        mIdDomainNameForMgRight = mIdDomainNameForMg.substring(indexOfRightBrace+1);
        gwIdBaseNum = Integer.parseInt(mIdDomainNameForMg.substring((indexOfLeftBrace+1), indexOfRightBrace));
    }
    
    // Check the values of 'numOfGateways', 'inactTimer/auditTimer'
    // 'numOfGwPerRange' and 'sleepTimePerRange'
    public static boolean isBadThreadParameters() {
        // Basic rules:
	// For MG:
	// (numOfGateways/numOfGwPerRange)*(timeOfSendingOneRange+sleepTimePerRange)+
	// timeOfThreadScheduling <= inactTimer
	// For MGC:
	// (numOfGateways/numOfGwPerRange)*(timeOfSendingOneRange+sleepTimePerRange)+
	// timeOfThreadScheduling <= inactTimer
	// Assumption for this computing:
	// numOfGwPerRange CPU-time(millisecond)
	//	20		1
	// timeOfThreadScheduling is NOT considered here.
	boolean isBadData = false;
	if (numOfGwPerRange > numOfGateways) {
	    System.out.println(CONFIG_BAD_DATA + "invalid 'numOfGwPerRange': " +
		    numOfGwPerRange + ", bigger than 'numOfGateways': " + numOfGateways);
	    isBadData = true;
	} else {
	    double totalTime = (((double)numOfGateways)/numOfGwPerRange)*(0.05+sleepTimePerRange);
	
	    if ((isMgSim() == true) && (totalTime >= inactTimer)) {
		isBadData = true;
	    } else if ((isMgSim() == false) && (totalTime >= auditTimer)) {
		isBadData = true;
	    }
	
	    if (isBadData == true) {
		System.out.println(CONFIG_BAD_DATA + "invalid thread parameters - " +
			"try to enlarge 'inactTimer' or 'auditTimer'");
	    }
	}
	
	return isBadData;
    }

    public static void dumpConfigData() {
        System.out.println("localIPv4 = " + localIPv4);
        System.out.println("localIPv6 = " + localIPv6);
        System.out.println("localPort = " + localPort);
        System.out.println("remoteIPv4 = " + remoteIPv4);
        System.out.println("remoteIPv6 = " + remoteIPv6);
        System.out.println("remotePort = " + remotePort);
        System.out.println("transportType = " + transportType);
        System.out.println("useIPv4 = " + useIPv4);
        System.out.println("isMediaGateway = " + isMediaGateway);
        System.out.println("isLongToken = " + isLongToken);
        System.out.println("numOfGateways = " + numOfGateways);
        System.out.println("mIdDomainNameForMg = " + mIdDomainNameForMg);
        System.out.println("mIdPortForMg = " + mIdPortForMg);
        System.out.println("gwIdBaseNum = " + gwIdBaseNum);
        System.out.println("mIdDomainNameForMgLeft = " + mIdDomainNameForMgLeft);
        System.out.println("mIdDomainNameForMgRight = " + mIdDomainNameForMgRight);
        System.out.println("mIdDomainNameForMgc = " + mIdDomainNameForMgc);
        System.out.println("mIdPortForMgc = " + mIdPortForMgc);
        System.out.println("inactTimer = " + inactTimer);
        System.out.println("auditTimer = " + auditTimer);
        System.out.println("numOfGwPerRange = " + numOfGwPerRange);
        System.out.println("sleepTimePerRange = " + sleepTimePerRange);
        System.out.println("logLevel = " + logLevel);
        System.out.println("version = " + VERSION);
    }
    
    public static void loadConfigData () {
        Properties prop = new Properties();
        try {
            if ((new File(GlobalConfig.CONFIG_FILE_NAME)).exists()) {
                // Read Data from configuration file
                prop.load(new FileInputStream(GlobalConfig.CONFIG_FILE_NAME));
		// Dev debugging for config data load
		MyLogger.log4LoadDebug("Config data load debugging:\n" +
			"localIPv4[" + prop.getProperty("localIPv4") + "]\n" +
			"localIPv6[" + prop.getProperty("localIPv6") + "]\n" +
			"localPort[" + prop.getProperty("localPort") + "]\n" +
			"remoteIPv4[" + prop.getProperty("remoteIPv4") + "]\n" +
			"remoteIPv6[" + prop.getProperty("remoteIPv6") + "]\n" +
			"remotePort[" + prop.getProperty("remotePort") + "]\n" +
			"transportType[" + prop.getProperty("transportType") + "]\n" +
			"useIPv4[" + prop.getProperty("useIPv4") + "]\n" +
			"isMediaGateway[" + prop.getProperty("isMediaGateway") + "]\n" +
			"isLongToken[" + prop.getProperty("isLongToken") + "]\n" +
			"numOfGateways[" + prop.getProperty("numOfGateways") + "]\n" +
			"mIdDomainNameForMg[" + prop.getProperty("mIdDomainNameForMg") + "]\n" +
			"mIdPortForMg[" + prop.getProperty("mIdPortForMg") + "]\n" +
			"mIdDomainNameForMgc[" + prop.getProperty("mIdDomainNameForMgc") + "]\n" +
			"mIdPortForMgc[" + prop.getProperty("mIdPortForMgc") + "]\n" +
			"inactTimer[" + prop.getProperty("inactTimer") + "]\n" +
			"auditTimer[" + prop.getProperty("auditTimer") + "]\n" +
			"numOfGwPerRange[" + prop.getProperty("numOfGwPerRange") + "]\n" +
			"sleepTimePerRange[" + prop.getProperty("sleepTimePerRange") + "]");
		
                localIPv4 = prop.getProperty("localIPv4");
                localIPv6 = prop.getProperty("localIPv6");
                localPort = Integer.parseInt(prop.getProperty("localPort"));
                remoteIPv4 = prop.getProperty("remoteIPv4");
                remoteIPv6 = prop.getProperty("remoteIPv6");
                remotePort = Integer.parseInt(prop.getProperty("remotePort"));
                transportType = prop.getProperty("transportType");
                useIPv4 = Boolean.parseBoolean(prop.getProperty("useIPv4"));
                isMediaGateway = Boolean.parseBoolean(prop.getProperty("isMediaGateway"));
                isLongToken = Boolean.parseBoolean(prop.getProperty("isLongToken"));
                numOfGateways = Integer.parseInt(prop.getProperty("numOfGateways"));
                mIdDomainNameForMg = prop.getProperty("mIdDomainNameForMg");
                mIdPortForMg = Integer.parseInt(prop.getProperty("mIdPortForMg"));
                mIdDomainNameForMgc = prop.getProperty("mIdDomainNameForMgc");
                mIdPortForMgc = Integer.parseInt(prop.getProperty("mIdPortForMgc"));
                inactTimer = Integer.parseInt(prop.getProperty("inactTimer"));
                auditTimer = Integer.parseInt(prop.getProperty("auditTimer"));
                numOfGwPerRange = Integer.parseInt(prop.getProperty("numOfGwPerRange"));
                sleepTimePerRange = Integer.parseInt(prop.getProperty("sleepTimePerRange"));
            } else {
                System.out.println("Configuration file missing - jmgsim exits");
                System.exit(1);
            }
        } catch(IOException ex) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
        }
    }
    
    public static void checkConfigData() {
        boolean isBadData = false;
        if ((localIPv4 == null) || (localIPv4.equals(""))) {
            System.out.println(CONFIG_BAD_DATA + "invalid 'localIPv4'");
            isBadData = true;
	} else if ((isMediaGateway == true) &&
		((remoteIPv4 == null) || (remoteIPv4.equals("")))) {
	    System.out.println(CONFIG_BAD_DATA + "invalid 'remoteIPv4'");
	    isBadData = true;
        } else if (!(transportType.equals("udp"))) {
            System.out.println(CONFIG_BAD_DATA + "invalid 'transportType' - only 'udp' is supported");
            isBadData = true;
        } else if (useIPv4 == false) {
            System.out.println(CONFIG_BAD_DATA + "invalid 'useIPv4' - only IPv4 is supported");
            isBadData = true;
        } else if (isMediaGateway == true) {
            if (    (mIdDomainNameForMg == null)
                 || (mIdDomainNameForMg.equals(""))
                 || (!(mIdDomainNameForMg.matches(CONFIG_MID_REGX_FOR_MG)))) {
                System.out.println(CONFIG_BAD_DATA + "invalid 'mIdDomainNameForMg'");
                isBadData = true;
            }
        } else if (isMediaGateway == false) {
            if (    (mIdDomainNameForMgc == null)
                 || (mIdDomainNameForMgc.equals(""))
                 || (!(mIdDomainNameForMgc.matches(CONFIG_MID_REGX_FOR_MGC)))) {
                System.out.println(CONFIG_BAD_DATA + "invalid 'mIdDomainNameForMgc'");
                isBadData = true;
            }
        } else {
            // Check the Thread related parameters
	    isBadData = isBadThreadParameters();
        }
        
        if (isBadData == true) {
            System.exit(1);
        } else {
            if (isMediaGateway == true) {
                parseMidDomainNameForMg();
            }
        }
    }

    private static String localIPv4;
    private static String localIPv6;
    private static int localPort;
    private static String remoteIPv4;
    private static String remoteIPv6;
    private static int remotePort;
    private static String transportType;
    private static boolean useIPv4;
    private static boolean isMediaGateway;
    private static boolean isLongToken;
    private static int numOfGateways;
    private static String mIdDomainNameForMg;
    private static int mIdPortForMg;
    private static int gwIdBaseNum;
    private static String mIdDomainNameForMgLeft;
    private static String mIdDomainNameForMgRight;
    private static String mIdDomainNameForMgc;
    private static int mIdPortForMgc;
    private static int inactTimer;
    private static int auditTimer;
    private static int numOfGwPerRange;
    private static int sleepTimePerRange;
    public static final String CONFIG_FILE_NAME = "config.properties";
    public static final String LOG_FILE_NAME = "jmgsim.log";
    public static final int LOG_LEVEL_NONE = 0;
    public static final int LOG_LEVEL_LOW = 1;
    public static final int LOG_LEVEL_MEDIUM = 2;
    public static final int LOG_LEVEL_HIGH = 3;
    public static final int H248_MSG_SIZE_MAX = 300000;
    public static final int ONE_SECOND_FOR_MILLISECOND = 1000; // ms
    public static final String SENDING_MSG_SUFFIX = ".msg";
    private static int logLevel = LOG_LEVEL_LOW; // daveti: HIGH for debug; LOW for common
    private static final String VERSION = "0.1";
    private static final String CONFIG_BAD_DATA = "Configuration bad data: ";
    private static final String CONFIG_MID_REGX_FOR_MG = "<[0-9a-zA-Z\\.\\-\\*]*\\([0-9]+\\)[0-9a-zA-Z\\.\\-\\*]*>";
    private static final String CONFIG_MID_REGX_FOR_MGC = "<[0-9a-zA-Z\\.\\-\\*]+>";
}
