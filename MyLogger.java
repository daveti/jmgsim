/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daveti
 * Basic logging methods
 * 
 */
public class MyLogger {
    
    public static String getTimeStampForLog() {
        Date timeStamp = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return formatter.format(timeStamp);
    }
    
    public static String getTimeStampForH248Msg() {
        Date timeStamp = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSS");
        return formatter.format(timeStamp);
    }
    
    public static String getLogLevelString(int logLevel) {
        String logLevelName;
        switch(logLevel) {
            case GlobalConfig.LOG_LEVEL_NONE:
                logLevelName = "LOG_LEVEL_NONE";
                break;
            case GlobalConfig.LOG_LEVEL_LOW:
                logLevelName = "LOG_LEVEL_LOW";
                break;
            case GlobalConfig.LOG_LEVEL_MEDIUM:
                logLevelName = "LOG_LEVEL_MEDIUM";
                break;
            case GlobalConfig.LOG_LEVEL_HIGH:
                logLevelName = "LOG_LEVEL_HIGH";
                break;
            default:
                logLevelName = "LOG_LEVEL_UNKNOWN";
                break;
        }
        return logLevelName;
    }
    
    public static synchronized void log(int logLevel, String content) { 
        if (GlobalConfig.getLogLevel() >= logLevel) {
            try {
                Writer logWriter = new FileWriter(GlobalConfig.LOG_FILE_NAME, true);
                logWriter.write("+++ " + getTimeStampForLog() + " " + getLogLevelString(logLevel) 
                        + " " + Thread.currentThread().getStackTrace()[2].getMethodName() 
                        + "() +++\n" + content + "\n\r\n");
                logWriter.close();
            } catch(IOException ex) {
                Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // This function is only used by dev for config data load debug!
    public static synchronized void log4LoadDebug(String content) {
	try {
	    Writer logWriter = new FileWriter("jmgsim.log", true);
                logWriter.write("+++ " + getTimeStampForLog() + " " + "LOG_LEVEL_DEBUG" 
                        + " " + Thread.currentThread().getStackTrace()[2].getMethodName() 
                        + "() +++\n" + content + "\n\r\n");
                logWriter.close();
            } catch(IOException ex) {
                Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
