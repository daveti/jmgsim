/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author daveti
 * NOTE: GwInfoDB is a Map and Array combined data structure
 * to organize all the GwInfos with unified methods both for jmgsim and jmgcsim.
 * NOTE: thread-safe is guaranteed by the lock within each GwInfo!
 * 
 * Why Array?
 * Array looping to send the corresponding msgs for each GwInfo may be the most
 * efficient way both for jmgsim and jmgcsim.
 * Why Map?
 * However, since each GwInfo is identified by MID not the index of the Array,
 * a Map is needed to do the mapping between MID and gwIndex for fast searching.
 * The other Map is provided as well for the mapping between gwId and gwIndex.
 * 
 */
public class GwInfoDB {
    
    public GwInfoDB() {
        numOfGwRegistered = 0;
        gwInfoMidMap = new ConcurrentHashMap<String,Integer>();
        gwInfoGwIdMap = new ConcurrentHashMap<Integer,Integer>();
        gwInfoArray = new GwInfo[GlobalConfig.getNumOfGateways()];
    }
    
    public void dbInitForMg() {
        for (int i = 0; i < GlobalConfig.getNumOfGateways(); i++) {
            // Init gwInfoArray for Mg
            int gwId = GlobalConfig.getGwIdBaseNum() + i;
            String mIdDomainName = GlobalConfig.getMidDomainNameForMgLeft()
                    + gwId + GlobalConfig.getMidDomainNameForMgRight();
            gwInfoArray[ i] = new GwInfo(mIdDomainName,
                    GlobalConfig.getMidPortNumForMg(),
                    GlobalConfig.getLocalIPv4Addr(),
                    GlobalConfig.getLocalPortNum(),
                    gwId,
                    i);
            
            // Add entry to Maps
            gwInfoMidMap.put(mIdDomainName, i);
            gwInfoGwIdMap.put(gwId, i);
        }
    }
    
    public void dbInitForMgc() {
        for (GwInfo elem : gwInfoArray) {
            // Init gwInfoArray for Mgc
            elem = null;
        }
    }
    
    // NOTE: This funciton should be called by jmgcsim when a new GW is being
    // registered - transId for this request needs to be passed as well!
    public final GwInfo addGwInfoForMgc(String mIdDomainName, int mIdPort, String origIP, int origPort) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mIdDomainName: " + mIdDomainName +
		", mIdPort" + mIdPort + ", origIP: " + origIP + ", origPort: " + origPort);
	if (numOfGwRegistered >= GlobalConfig.getNumOfGateways()) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: unable to add new GW into DB" +
		    ", which has reached the max capacity: " +
		    GlobalConfig.getNumOfGateways() + " GWs");
	    return null;
	}
        // Find the first null gwInfo in gwInfoArray
        for (int i = 0; i < GlobalConfig.getNumOfGateways(); i++) {
            if (gwInfoArray[ i] == null) {
                // Add the new gwInfo into gwInfoArray
                // NOTE: gwId and gwIndex are the same for jmgcsim!
                gwInfoArray[ i] = new GwInfo(mIdDomainName, mIdPort, origIP, origPort, i, i, true);
                MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH,
                        "New GwInfo added - mIdDomainName: " + mIdDomainName +
                        ", mIdPort: " + mIdPort +
                        ", origIP: " + origIP +
                        ", origPort: " + origPort +
                        ", gwId: " + i +
                        ", gwIndex: " + i +
                        ", isRegistered: " + true);
		
		// Update the GW counter for reg'd
                numOfGwRegistered++;
		// Add this GW into Maps
		gwInfoMidMap.put(mIdDomainName, i);
		gwInfoGwIdMap.put(i, i);
		
                return gwInfoArray[ i];
            }
        }
        return null;
    }
    
    public final GwInfo getGwInfoFromMid(String mId) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mId: " + mId);
        Integer gwIndex = gwInfoMidMap.get(mId);
        if (gwIndex == null) {
            return null;
        }
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH,
                "For mId: " + mId + ", found gwIndex: " + gwIndex);
        return gwInfoArray[ gwIndex];
    }
    
    public final GwInfo getGwInfoFromGwId(int gwId) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "gwId: " + gwId);
        Integer gwIndex = gwInfoGwIdMap.get(gwId);
        if (gwIndex == null) {
            return null;
        }
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH,
                "For gwId: " + gwId + ", found gwIndex: " + gwIndex);
        return gwInfoArray[ gwIndex];
    }
    
    public final int getNumOfGwRegistered() {
        return numOfGwRegistered;
    }
    
    public final void increaseNumOfGwRegistered() {
	this.numOfGwRegistered++;
    }
    
    public final GwInfo[] getGwInfoArray() {
        return gwInfoArray;
    }
    
    // As original design is to take the place of 'per-GW-per-thread' with
    // 'all-GWs-one-thread', as long as the first registration is succeeded,
    // For Mg: 
    // then GW HB would be started after it/ito timer.
    // For Mgc:
    // then MGC HB would be started after audit timer if there is no msg got
    // by MGC within the time range.
    // NOTE: for MGC, a flag is needed to do this judgement. 
    private int numOfGwRegistered;
    private ConcurrentHashMap<String,Integer> gwInfoMidMap;
    private ConcurrentHashMap<Integer,Integer> gwInfoGwIdMap;
    private GwInfo[] gwInfoArray; 
}
