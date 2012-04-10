/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.util.Timer;

/**
 *
 * @author daveti
 */
public class Jmgsim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Load and check global data from config.properties 
        GlobalConfig.loadConfigData();
	GlobalConfig.checkConfigData();
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Global config has been loaded and verified");
        
        // Create MsgBuild, MsgProc, Transport and Timer objects
        try {
	    Transport transObj = new Transport();
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Transport obj has been created");
	    
            GwInfoDB dbObj = new GwInfoDB();
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "GwInfoDB obj has been created");
	    
	    MsgBuild buildObj = new MsgBuild();
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MsgBuild obj has been created");
	    
	    Timer timerObj = new Timer();
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Timer obj has been created");
	    
	    MsgProc procObj = new MsgProc(transObj, dbObj, buildObj, timerObj);
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MsgProc obj has been created");
	    
	    Cli cliObj;
	    String appTitle;
            
            // Create working threads and DB init
            CommonWorkerThread commonObj = new CommonWorkerThread(transObj, procObj);
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Common Worker Thread has been created");
	    
            if (GlobalConfig.isMgSim() == true) {
		// Init DB for Mg
		dbObj.dbInitForMg();
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "DB init for MG is done");
		
		// Create MgReg thread
                MgRegThread mgRegObj = new MgRegThread(transObj, dbObj, buildObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MG Registration Thread has been created");
		
		// New MgHb timer task
		MgHbTimerTask mgHbObj = new MgHbTimerTask(transObj, dbObj, buildObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MG HB TimerTask has been created");
		
		// Add MgHb into MsgProc
		procObj.setHbTimerTask(mgHbObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MG HB TimerTask has been added into MsgProc");
		
		// New cli object for Mg
		cliObj = new Cli(transObj, dbObj, buildObj, procObj, commonObj, timerObj,
				mgRegObj, mgHbObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Cli obj for MG has been created");
		
		// Add the title
		appTitle = "\nH.248 Java Media Gateway (MGW) Simulator\n";
            } else {
		// Init DB for Mgc
		dbObj.dbInitForMgc();
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "DB init for MGC is done");
		
		// New MgcHb timer task;
                MgcHbTimerTask mgcHbObj = new MgcHbTimerTask(transObj, dbObj, buildObj, procObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MGC HB TimerTask has been created");
		
		// Add MgcHb into MsgProc
		procObj.setHbTimerTask(mgcHbObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "MGC HB TimerTask has been added into MsgProc");
		
		// New cli object for Mgc
		cliObj = new Cli(transObj, dbObj, buildObj, procObj, commonObj, timerObj,
				mgcHbObj);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Cli obj for MGC has been created");
		
		// Add the title
		appTitle = "\nH.248 Java Media Gateway Controller (MGC) Simulator\n";
            }
	    
	    System.out.println(appTitle + 
			"Developed by Dave Tian @ Alcatel-Lucent.\n" +
			"Type 'help<ENTER>' for a list of available commands\n");
	    
            // Let main thread fall into cli mode
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Cli mode is ready - wait for command");
            cliObj.cliProc();
	    
        } catch (Exception ex) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
        }
        
        System.exit(0);
    }
}
