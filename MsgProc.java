/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author daveti
 * Basic H.248 msg decoding and processing
 * 
 */
public class MsgProc {
    
    public MsgProc(Transport transObj, GwInfoDB dbObj, MsgBuild buildObj, Timer timerObj) {
	this.transObj = transObj;
	this.dbObj = dbObj;
	this.buildObj = buildObj;
	this.timerObj = timerObj;
	this.hbTimerTaskObj = null;
    }
    
    // This function would bypass the white spaces and commas to next valid index.
    public int moveToNextNonSpaceCommaIndexFromLastSubString(String msg, String subString) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msg: " + msg +
                ", subString: " + subString);
        int indexMax = msg.length() - 1;
        int index = msg.indexOf(subString) + subString.length();
        if (index >= indexMax) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Warning: reaching end of the msg, " +
                    "index: " + index + ", indexMax: " + indexMax);
        } else {
            while (index < indexMax) {
                // Bypass the white space and comma character
                if ((Character.isWhitespace(msg.charAt(index)) == true) ||
                        (msg.charAt(index) == ',')) {
                    index++;
                } else {
                    return index;
                }
            }
        }
        return -1;
    }
    
    // This function would bypass the first right brace and spaces to next valid index.
    // This function should be called when extracting Action from Transaction and
    // extracting Command from Action. Otherwise, please do not call it!
    public int bypassFirstLeftBraceToNextNonSpaceIndexFromIndex(String msg, int index) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msg: " + msg + ", index: " + index);
	if (index == -1) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "Warning: invalid index: " + index);
	    return -1;
	}
	
	int indexMax = msg.length() - 1;
	if (index >= indexMax) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Warning: reaching end of the msg, " +
                    "index: " + index + ", indexMax: " + indexMax);
        } else {
	    // Find the first left barce
	    int indexBrace = msg.indexOf("{", index);
	    if (indexBrace == -1) {
		MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "Warning: unfound left barce");
		return -1;
	    }
	    
	    indexBrace++;
            while (indexBrace < indexMax) {
                // Bypass the white spaces
                if (Character.isWhitespace(msg.charAt(indexBrace)) == true) {
                    indexBrace++;
                } else {
                    return indexBrace;
                }
            }
        }
        return -1;
    }
    
    public String getMid(String msg, boolean isLongToken) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msg: " + msg +
                ", isLongToken " + isLongToken);
        // Generally H.248 msgs would be like below:
        // MegacoLogo/version__mId(:port)(__'\n')MSGBODY
        // NOTE: ":port" is optional.
        // NOTE: "__" means at least one space.
        // NOTE: '\n' would usually appear in long token. For this case
        // no other "__" could be found. For short token, either "__" or
        // '\n' would be common to see. Below is the regx for our solution.
        // \p{Space} - A whitespace character: [ \t\n\x0B\f\r] 
        // Currently, we do not care about long or short token - as we assume
        // either format could be distinguished by regx-space.
        String[] msgSplited = msg.split("(\\p{Space})+");
        
        // msgSplited[ 0] = Megaco
        // msgSplited[ 1] = mIdDomainName(:mIdPort)
        // msgSplited[ 2] = ...
        return msgSplited[ 1].trim();
    }
    
    public String getMidDomainName(String mId) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mId: " + mId);
        String[] mIdSplited = mId.split(":");
        return mIdSplited[ 0].trim();
    }
    
    public boolean isMgcMidContained(String mIdDomainName) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mIdDomainName: " + mIdDomainName);
	if ((mIdDomainName.contains("-") == true) || (mIdDomainName.contains("*") == true)) {
	    // Only '-' or '*' is supported as the mId connecting charactor.
	    return true;
	} else {
	    return false;
	}
    }
    
    public String getOriginalGwMid(String mIdDomainName) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mIdDomainName: " + mIdDomainName);
	// NOTE: only '-' or '*' is supported separately - NOT the both the same time!
	String origGwMid;
	int index = mIdDomainName.indexOf("-");
	if (index != -1) {
	    // Replace the connecting character with '>'
	    origGwMid = mIdDomainName.replace('-', '>');
	    // Get the GW mId
	    return origGwMid.substring(0, index+1);
	}
	// Try to hunt for '*'
	index = mIdDomainName.indexOf("*");
	if (index != -1) {
	    origGwMid = mIdDomainName.replace('*', '>');
	    return origGwMid.substring(0, index+1);
	}
	return null;
    }
    
    public int getMidPortNum(String mId) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mId: " + mId);
        if (mId.contains(":") == true) {
            String[] mIdSplited = mId.split(":");
            return Integer.parseInt(mIdSplited[ 1].trim());
        } else {
            // No mIdPort available.
            return -1;
        }
    }
    
    public String generateMidDomainNameForMgc(String mIdDomainNameForMg) {
	// This function would combine the mIdDomainName passed in:
	// <jmgsim.lucent.com> with global configured mId domain name
	// for MGC, like <jmgsim.lucent.com-jmgcsim.lucent.com>
	// NOTE: this function only works for MGC sim.
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mIdDomainNameForMg:" +
		mIdDomainNameForMg);
	int index = mIdDomainNameForMg.indexOf(">");
	String mIdLeft = mIdDomainNameForMg.substring(0, index);
	index = GlobalConfig.getMidDomainNameForMgc().indexOf("<");
	String mIdRight = GlobalConfig.getMidDomainNameForMgc().substring(index+1);
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mIdLeft: " + mIdLeft +
		", mIdRight: " + mIdRight);
	return (mIdLeft+mIdRight).trim();
    }
    
    public int getIndexOfLastMatchedRightBrace(String msg, int startIndex) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH,
                "msg: " + msg + ", startIndex = " + startIndex);
	// NOTE: this function handles Trasaction block and Action block smoothly;
	// however, when coming into Command block, it would be kind of frustrating
	// as Command block would NOT always have "{}" there... 
	// This would be common if command reply block only have the termId
	// without braces like below:
	// Reply = 1 {
	//	Context = - {
	//	  ServiceChange = ROOT
	//  }
	// }
	// Or:
	// Reply = 2 {
	//	Context = 100 {
	//	  Subtract = AL/FDV2/1/1/1/1,
	//	  Subtract = IP/1
	//  }
	// }
	// Or:
	// Reply = 3 {
	//  Context = 200 {
	//    Modify = AL/FDV2/1/1/1/1,
	//    Modify = IP/1 {
	//		......
	//    }
	//   }
	// }
	
	// Code supposed for command block handling at the beginning...
	// 1st - we need to know if there is a comma ahead of first left brace
	int firstComma = msg.indexOf(",", startIndex);
	int firstLeftBrace = msg.indexOf("{", startIndex);
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "firstComma: " + firstComma +
		", firstLeftBrace: " + firstLeftBrace);
	
	if (firstLeftBrace == -1) {
	    // All the Commands blocks within this Action block does not have "{}"
	    if (firstComma != -1) {
		// Multiple Commands blocks - just return from the comma
		// Calling for indexForXXXEnd would use (indexForXXXEnd+1),
		// so we need to omit the comma here for a real com block.
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "All the command blocks" +
			" do not have braces - multiple command blocks, " +
			"returnIndex: " + (firstComma-1));
		return (firstComma-1);
	    } else {
		// Single Command block - just return from the first right brace
		int firstRightBrace = msg.indexOf("}", startIndex);
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "All the command blocks" +
			" do not have braces - single command block, " +
			"returnIndex: " + (firstRightBrace-1));
		return (firstRightBrace-1);		
	    }
	} else {
	    // At least there is a commad block having "{}"
	    if ((firstComma != -1) && (firstComma < firstLeftBrace)) {
		MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Found a command block" +
			" does not have braces - multiple command blocks" +
			"returnIndex: " + (firstComma-1));
		return (firstComma-1);
	    } else {
		// Let other cases fall down into our common case handling below!
	    }
	}
	
        char[] msgCharArray = msg.toCharArray();
        int numOfLeftBrace = 0;
        int numOfRightBrace = 0;
        if ((msgCharArray != null) && (startIndex < msg.length())) {
            for (int i = startIndex; i < msg.length(); i++) {
                if (msgCharArray[ i] == '{') {
                    numOfLeftBrace++;
                } else if (msgCharArray[ i] == '}') {
                    numOfRightBrace++;
                } else {
                    continue;
                }
                
                if ((numOfLeftBrace != 0) && (numOfLeftBrace == numOfRightBrace)) {
		    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "returnIndex: " + i +
			    ", subString from returnIndex: " + msg.substring(i));
                    return i;
                } 
            }
            
            if ((numOfLeftBrace == 0) && (numOfRightBrace == 0)) {
                MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Warning: no braces found");
            } else {
                MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Warning: un-matched braces");
            }
        } else {
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: invalid msg or startIndex");
        }
        return -1;
    }
    
    public int getComTag(String comHead) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comHead: " + comHead);
        // "Modify = * {"
        String[] comHeadSplited = comHead.split("=");
        // comHeadSplited[ 0] = "Modify"
        // comHeadSplited[ 1] = "*...."
        String command = comHeadSplited[ 0].trim();
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "command: " + command);
        
        int comTag;
        if ((command.compareToIgnoreCase(MsgToken.AUDITVALUE_L) == 0) ||
                (command.compareToIgnoreCase(MsgToken.AUDITVALUE_S) == 0) ||
		(command.compareToIgnoreCase(MsgToken.O_AUDITVALUE_L) == 0) ||
		(command.compareToIgnoreCase(MsgToken.O_AUDITVALUE_S) == 0)) {
            comTag = MsgToken.COM_TAG_AUDITVALUE;
        } else if ((command.compareToIgnoreCase(MsgToken.SERVICECHANGE_L) == 0) ||
                (command.compareToIgnoreCase(MsgToken.SERVICECHANGE_S) == 0)) {
            comTag = MsgToken.COM_TAG_SERVICECHANGE;
        } else if ((command.compareToIgnoreCase(MsgToken.MODIFY_L) == 0) ||
                (command.compareToIgnoreCase(MsgToken.MODIFY_S) == 0) ||
		(command.compareToIgnoreCase(MsgToken.O_MODIFY_L) == 0) ||
		(command.compareToIgnoreCase(MsgToken.O_MODIFY_S) == 0)) {
            comTag = MsgToken.COM_TAG_MODIFY;
        } else if ((command.compareToIgnoreCase(MsgToken.SUBTRACT_L) == 0) ||
                (command.compareToIgnoreCase(MsgToken.SUBTRACT_S) == 0) ||
		(command.compareToIgnoreCase(MsgToken.O_SUBTRACT_L) == 0) ||
		(command.compareToIgnoreCase(MsgToken.O_SUBTRACT_S) == 0)) {
            comTag = MsgToken.COM_TAG_SUBTRACT;
        } else if ((command.compareToIgnoreCase(MsgToken.NOTIFY_L) == 0) ||
                (command.compareToIgnoreCase(MsgToken.NOTIFY_S) == 0)) {
            comTag = MsgToken.COM_TAG_NOTIFY;
        } else if ((command.compareToIgnoreCase(MsgToken.ADD_L) == 0) ||
                (command.compareToIgnoreCase(MsgToken.ADD_S) == 0)) {
            comTag = MsgToken.COM_TAG_ADD;
        } else {          
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: unsupported command: " +
                    command);
            comTag = MsgToken.COM_TAG_INVALID;
        }
        
        return comTag;
    }
    
    public String getTermId(String comHead) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comHead: " + comHead);
        // "Modify = AL/FDV2/1/1/1/1 {"
        String comHead2 = comHead.replace('{', '=');
        // "Modify = AL/FDV2/1/1/1/1 ="
        String[] comHeadSplited = comHead2.split("=");
        // comHeadSplited[ 0] = "Modify"
        // comHeadSplited[ 1] = "AL/FDV2/1/1/1/1"
        return comHeadSplited[ 1].trim();
    }
    
    public H248Command parseComBlock(String comBlock, boolean isLongToken) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comBlock: " + comBlock +
                ", isLongToken: " + isLongToken);
        H248Command h248Com = new H248Command();
        
        // Get the comLength
        h248Com.setComLength(comBlock.length());
        
        // Get the comHead
	// NOTE: command block may NOT have "{}"
        String comHead;
	int firstLeftBrace = comBlock.indexOf("{");
	if (firstLeftBrace != -1) {
	    comHead = comBlock.substring(0, firstLeftBrace).trim();
	} else {
	    comHead = comBlock.trim();
	}
        
        // Get the comTag
        h248Com.setComTag(getComTag(comHead));
        
        // Get the termId
        h248Com.setTermId(getTermId(comHead));
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comTag: " + h248Com.getComTag() +
                ", termId: " + h248Com.getTermId());
        
        // daveti: in future - there would be detailed command handler
        // based on different contextId, termId for different command.
        
        return h248Com;
    }
    
    public String getContextId(String actHead) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "actHead: " + actHead);
        // "Context = - {"...
        String actHead2 = actHead.replace('{', '=');
        String[] actHeadSplited = actHead2.split("=");
        // actHeadSplited[ 0] = "Context"
        // actHeadSplited[ 1] = "-"
        return actHeadSplited[ 1].trim();
    }
    
    public boolean isActionError(String actHead) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "actHead: " + actHead);
	// actHead: Error = 511
	if ((actHead.startsWith(MsgToken.ERROR_L)) ||
		(actHead.startsWith(MsgToken.ERROR_S))) {
	    // This is action error
	    return true;
	}
	return false;
    }
    
    public String getErrorCode(String errorDescriptor) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "errorDescriptor: " + errorDescriptor);
	// errorDescriptor: Error = 511
	String[] errorDescSplited = errorDescriptor.split("=");
	return errorDescSplited[ 1].trim();
    }
    
    public H248Action parseActBlock(String actBlock, boolean isLongToken) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "actBlock: " + actBlock + 
                ", isLongToken: " + isLongToken);
        H248Action h248Act = new H248Action();
        
        // Get the actLength
        h248Act.setActLength(actBlock.length());
        
        // Get the actHead
        String actHead = actBlock.substring(0, actBlock.indexOf("{")).trim();
	
	// Check if this is a reply error with Error descriptor contained -
	// if so, then there would be no contextId but only error code.
	// For this case, return after parsing error descriptor without command
	// level's decoding.
	if (isActionError(actHead) == true) {
	    // set this action as Action error
	    h248Act.setIsActionErrorFlag(true);
	    
	    // set the error code
	    String errorCode = getErrorCode(actHead);
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "errorCode: " + errorCode);
	    h248Act.setErrorCode(errorCode);
	    
	    // bypass the command level parsing
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Action Error: bypass command level parsing");
	    return h248Act;
	}
        
        // Get the contextId
        h248Act.setContextId(getContextId(actHead));
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "contextId: " + h248Act.getContextId());
        
        ArrayList<H248Command> h248ComArrayList = new ArrayList<H248Command>();
        H248Command h248Com;
        String comBlock;
        int indexForComStart = moveToNextNonSpaceCommaIndexFromLastSubString(actBlock, actHead);
	// Bypass the first left brace of Action
	indexForComStart = bypassFirstLeftBraceToNextNonSpaceIndexFromIndex(actBlock, indexForComStart);
        int indexForComEnd;
        while ((indexForComStart != -1) && (indexForComStart < h248Act.getActLength())) {
            indexForComEnd = getIndexOfLastMatchedRightBrace(actBlock, indexForComStart);
            if (indexForComEnd != -1) {
                // Get a new Com block
                comBlock = actBlock.substring(indexForComStart, indexForComEnd+1).trim();
                
                // Add the num of total commands
                h248Act.increaseNumOfCommands();
                
                // Parse this Com block
                h248Com = parseComBlock(comBlock, isLongToken);
                
                // Add the parsed Com obj into the list
                h248ComArrayList.add(h248Com);
                
                // Now try to move to the next Com block
                indexForComStart = moveToNextNonSpaceCommaIndexFromLastSubString(actBlock, comBlock);
            } else {
                MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Error: msg decoding failure on Command Layer");
                break;
            }
        }
        
        // Convert the H.248 Command ArrayList into Array
        if (h248Act.getNumOfCommands() != 0) {
            H248Command[] h248ComArray = new H248Command[h248ComArrayList.size()];
            h248ComArrayList.toArray(h248ComArray);
            h248Act.setH248CommandArray(h248ComArray);
        }
        
        return h248Act;
    }
    
    public long getTransId(String transHead) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transHead: " + transHead);
        // This call is assumed after 'getTransTag' and therefore
        // we assume there should be '=' in the transHead -
        // "Transaction = 11 {"...
        String transHead2 = transHead.replace('{', '=');
        String[] transHeadSplited = transHead2.split("=");
        // transHeadSplited[ 0] = "Transaction"
        // transHeadSplited[ 1] = "11"      
        return Long.parseLong(transHeadSplited[ 1].trim());
    }
    
    public int getTransTag(String transHead) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transHead: " + transHead);
        // Assume the transHead would be like "Transaction = 1 {"...
        // Remove the possible '=', '1', '{' and whitespaces
        char[] headArray = transHead.toCharArray();
        int i = 0;
        while (i < transHead.length()) {
            if (Character.isLetter(headArray[ i]) == true) {
                i++;
            } else {
                // Got the index of end
                break;
            }
        }
        String transTagString = transHead.substring(0, i).trim();
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transTagString: " + transTagString);
        
        // Do not care about the encoding type though passed in...
        int transTag;
        if ((transTagString.compareToIgnoreCase(MsgToken.TRANSACTION_L) == 0) ||
                (transTagString.compareToIgnoreCase(MsgToken.TRANSACTION_S) == 0)) {
            transTag = MsgToken.TRANS_TAG_REQUEST;
        } else if ((transTagString.compareToIgnoreCase(MsgToken.REPLY_L) == 0) ||
                (transTagString.compareToIgnoreCase(MsgToken.REPLY_S) == 0)) {
            transTag = MsgToken.TRANS_TAG_REPLY;
        } else if ((transTagString.compareToIgnoreCase(MsgToken.PENDING_L) == 0) ||
                (transTagString.compareToIgnoreCase(MsgToken.PENDING_S) == 0)) {
            transTag = MsgToken.TRANS_TAG_PENDING;
        } else if ((transTagString.compareToIgnoreCase(MsgToken.ACK_L) == 0) ||
                (transTagString.compareToIgnoreCase(MsgToken.ACK_S) == 0)) {
            transTag = MsgToken.TRANS_TAG_RESPONSEACK;
        } else if ((transTagString.compareToIgnoreCase(MsgToken.ERROR_L) == 0) ||
                (transTagString.compareToIgnoreCase(MsgToken.ERROR_S) == 0)) {
            transTag = MsgToken.TRANS_TAG_ERROR;
        } else {
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: unknown transTag - " +
                    transTagString);
            transTag = MsgToken.TRANS_TAG_INVALID;
        }
        
        return transTag;
    }
    
    public H248Transaction parseTransBlock(String transBlock, boolean isLongToken) {
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transBlock: " + transBlock +
                ", isLongToken: " + isLongToken);
        H248Transaction h248Trans = new H248Transaction();
        
        // Get the transLength
        h248Trans.setTransLength(transBlock.length());
        
        // Get the transaction head
        String transHead = transBlock.substring(0, transBlock.indexOf("{")).trim();
       
        // Get the transTag
        h248Trans.setTransTag(getTransTag(transHead));
        int transTag = h248Trans.getTransTag();
        if ((transTag == MsgToken.TRANS_TAG_ERROR) ||
                (transTag == MsgToken.TRANS_TAG_RESPONSEACK) ||
		(transTag == MsgToken.TRANS_TAG_PENDING) ||
                (transTag == MsgToken.TRANS_TAG_INVALID)) {
            // For transAck, transError, transPend and transInvalid,
            // no need to parse transId and the following stuffs.
            // daveti: for future, may need to parse the transId
            // in the transAck...
            MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "Return after transTag: " + transTag);
            return h248Trans;
        }
        
        // Get the transId
        h248Trans.setTransId(getTransId(transHead));
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "transTag: " + transTag +
                ", transId: " + h248Trans.getTransId());
        
        ArrayList<H248Action> h248ActArrayList = new ArrayList<H248Action>();
        H248Action h248Act;
        String actBlock;
        int indexForActStart = moveToNextNonSpaceCommaIndexFromLastSubString(transBlock, transHead);
	// Bypass the first left brace of Transaction
	indexForActStart = bypassFirstLeftBraceToNextNonSpaceIndexFromIndex(transBlock, indexForActStart);
        int indexForActEnd;
        while ((indexForActStart != -1) && (indexForActStart < h248Trans.getTransLength())) {
            indexForActEnd = getIndexOfLastMatchedRightBrace(transBlock, indexForActStart);
            if (indexForActEnd != -1) {
                // Got a new Act block
                actBlock = transBlock.substring(indexForActStart, indexForActEnd+1).trim();
                
                // Add the num of total actions
                h248Trans.increaseNumOfActions();
                
                // Parse this Act block
                h248Act = parseActBlock(actBlock, isLongToken);
                
                // Add the parsed Act obj into the list
                h248ActArrayList.add(h248Act);
                
                // Now try to move to the next Act block
                indexForActStart = moveToNextNonSpaceCommaIndexFromLastSubString(transBlock, actBlock);
            } else {
                MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: msg decoding failure on Action layer");
                break;
            }
        }
        
        // Convert the H.248 Action ArrayList into Array
        if (h248Trans.getNumOfActions() != 0) {
            H248Action[] h248ActArray = new H248Action[h248ActArrayList.size()];
            h248ActArrayList.toArray(h248ActArray);
            h248Trans.setH248ActionArray(h248ActArray);
        }
        
        return h248Trans;
    }
    
    public H248Msg decodeH248Msg(H248Msg h248Msg) {
        // Get raw msg data
        String rawMsg = h248Msg.getMsg();
        if (rawMsg == null) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: null msg");
            return null;
        }
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "rawMsg: " + rawMsg);
        
        // Get decoded msg pointer
        H248MsgDecoded decodedMsg = h248Msg.getH248MsgDecoded();
        if (decodedMsg != null) {
            MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM,
                    "Warning: non null decoded msg pointer - will be discarded for decoding");
        }
        
        // Begin H.248 decoding - God bless us!
        
        // Allocate new decoded msg structure
        decodedMsg = new H248MsgDecoded();
        
        // Check text encoding type - long or short
        if (rawMsg.startsWith(MsgToken.MEGACO_S_SLASH) == true) {
            // Short token
            decodedMsg.setIsLongTokenFlag(false);
        } else {
            // Long token
            decodedMsg.setIsLongTokenFlag(true);
        }
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "isLongToken: "
                + decodedMsg.isLongTokenMsg());
        
        // MID processing
        String mId = getMid(rawMsg, decodedMsg.isLongTokenMsg());
        String mIdDomainName = getMidDomainName(mId);
	boolean isMgcMidContainedFlag = isMgcMidContained(mIdDomainName);
        int mIdPort = getMidPortNum(mId);
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mId: " + mId +
                ", mIdDomainName: " + mIdDomainName + 
		", isMgcMidContained: " + isMgcMidContainedFlag +
		", mIdPort: " + mIdPort);
        
        decodedMsg.setMidDomainName(mIdDomainName);
	decodedMsg.setIsMgcMidContainedFlag(isMgcMidContainedFlag);
        decodedMsg.setMidPortNum(mIdPort);
        
        // Transaction decoding
        int indexForTransStart = moveToNextNonSpaceCommaIndexFromLastSubString(rawMsg, mId);
        ArrayList<H248Transaction> h248TransArrayList = new ArrayList<H248Transaction>();
        H248Transaction h248Trans;
        int indexForTransEnd;
        String transBlock;
        while ((indexForTransStart != -1) && (indexForTransStart < h248Msg.getMsgLength())) {
	    // Debugging log
	    MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "indexForTransStart: " + indexForTransStart +
		    ", subString from index: " + rawMsg.substring(indexForTransStart));
	    
            indexForTransEnd = getIndexOfLastMatchedRightBrace(rawMsg, indexForTransStart);
            if (indexForTransEnd != -1) {
                // Got a new Trans block
                transBlock = rawMsg.substring(indexForTransStart, indexForTransEnd+1).trim();
                
                // Add the num of total transactions
                decodedMsg.increaseNumOfTransactions();
                
                // Parse this Trans block
                h248Trans = parseTransBlock(transBlock, decodedMsg.isLongTokenMsg());
                
                // Add the parsed Trans obj into list
                h248TransArrayList.add(h248Trans);
                
                // Now try to move to the next Trans block
                indexForTransStart = moveToNextNonSpaceCommaIndexFromLastSubString(rawMsg, transBlock);
            } else {
                MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: msg decoding failure on Transaction layer");
                break;
            }
        }
        
        // Covert the H.248 Transaction ArrayList into Array
        if (decodedMsg.getNumOfTransactions() != 0) {
            H248Transaction[] h248TransArray = new H248Transaction[h248TransArrayList.size()];
            h248TransArrayList.toArray(h248TransArray);
            decodedMsg.setH248TransactionArray(h248TransArray);
        }
       
	// Set the decodedMsg into H248Msg
	h248Msg.setH248MsgDecoded(decodedMsg);
	
        return h248Msg;
    }
    
    public void pegCountBasedOnCommandTag(GwInfo gwInfo, int comTag, boolean isRequest) {
	// NOTE: this function only works for Request or Reply!
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comTag: " + comTag + 
		", isRequest: " + isRequest + ", gwInfo: " + gwInfo);
	switch (comTag) {
	    case MsgToken.COM_TAG_ADD:
	        if (isRequest == true) {
		    gwInfo.increaseNumOfAddReq();
		} else {
		    gwInfo.increaseNumOfAddRep();
		}
		break;
	    case MsgToken.COM_TAG_AUDITVALUE:
		if (isRequest == true) {
		    gwInfo.increaseNumOfAuditValueReq();
		} else {
		    gwInfo.increaseNumOfAuditValueRep();
		}
		break;
	    case MsgToken.COM_TAG_MODIFY:
		if (isRequest == true) {
		    gwInfo.increaseNumOfModifyReq();
		} else {
		    gwInfo.increaseNumOfModifyRep();
		}
		break;
	    case MsgToken.COM_TAG_NOTIFY:
		if (isRequest == true) {
		    gwInfo.increaseNumOfNotifyReq();
		} else {
		    gwInfo.increaseNumOfNotifyRep();
		}
		break;
	    case MsgToken.COM_TAG_SERVICECHANGE:
		if (isRequest == true) {
		    gwInfo.increaseNumOfServiceChangeReq();
		} else {
		    gwInfo.increaseNumOfServiceChangeRep();
		}
		break;
	    case MsgToken.COM_TAG_SUBTRACT:
		if (isRequest == true) {
		    gwInfo.increaseNumOfSubtractReq();
		} else {
		    gwInfo.increaseNumOfSubtractRep();
		}
		break;
	    case MsgToken.COM_TAG_MOVE:
	    case MsgToken.COM_TAG_INVALID:
	    default:
		if (isRequest == true) {
		    gwInfo.increaseNumOfOtherReq();
		} else {
		    gwInfo.increaseNumOfOtherRep();
		}
		break;
	}
    }
    
    public boolean isGwRegistrationReq(H248MsgDecoded decodedMsg) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "decodedMsg: " + decodedMsg);
	H248Transaction h248Trans;
	H248Action h248Act;
	H248Command h248Com;
	int indexForTrans;
	int indexForAct;
	int indexForCom;
	
	for (indexForTrans = 0; indexForTrans < decodedMsg.getNumOfTransactions(); indexForTrans++) {
	    h248Trans = decodedMsg.getH248TransactionArray()[ indexForTrans];
	    if (h248Trans.getTransTag() == MsgToken.TRANS_TAG_REQUEST) {
		for (indexForAct = 0; indexForAct < h248Trans.getNumOfActions(); indexForAct++) {
		    h248Act = h248Trans.getH248ActionArray()[ indexForAct];
		    for (indexForCom = 0; indexForCom < h248Act.getNumOfCommands(); indexForCom++) {
			h248Com = h248Act.getH248CommandArray()[ indexForCom];
			if ((h248Com.getComTag() == MsgToken.COM_TAG_SERVICECHANGE) &&
			    (h248Act.getContextId().equalsIgnoreCase(MsgToken.CONTEXT_NULL) == true) &&
			    (h248Com.getTermId().equalsIgnoreCase(MsgToken.TERMINATION_ROOT) == true)) {
			    // daveti: method needs to been considered in future...
			    return true;
			}
		    }
		}
	    }
	}
	
	return false;
    }
    
    public void updateGwInfo(GwInfo gwInfo, H248Msg h248Msg) {
	// daveti: we will do some bitchy stuffs here -
	// If this is a Request, not only requestCount gets peg'd
	// but also replyCount;
	// If this is a Reply, not only replyCount gets peg'd
	// but also ackCount.
	// In this way, we could low down the impact of lock within
	// 'updateGwInfo' and leave 'BuildAndSendMsg' clean for
	// performance consideration. In otherwords, MG/MGC sim would
	// always try to respond sth to the remote side...
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "gwInfo: " + gwInfo +
		", h248Msg: " + h248Msg);
	H248MsgDecoded decodedMsg = h248Msg.getH248MsgDecoded();
	H248Transaction h248Trans;
	H248Action h248Act;
	H248Command h248Com;
	int indexForTrans;
	int indexForAct;
	int indexForCom;
	
	// Now lock this gwInfo
	gwInfo.getGwLock().lock();
	try {
	    // Basic info within H248Msg layer has been configured
	    // once DB is init'd - just focus on H248MsgDecoded layer
	    for (indexForTrans = 0; indexForTrans < decodedMsg.getNumOfTransactions(); indexForTrans++) {
		h248Trans = decodedMsg.getH248TransactionArray()[ indexForTrans];
		switch (h248Trans.getTransTag()) {
		    case MsgToken.TRANS_TAG_RESPONSEACK:
			// Peg count for ACK
			gwInfo.increaseNumOfAck();
			break;
				
		    case MsgToken.TRANS_TAG_REQUEST:
			// Peg count for Request
			for (indexForAct = 0; indexForAct < h248Trans.getNumOfActions(); indexForAct++) {
			    h248Act = h248Trans.getH248ActionArray()[ indexForAct];
			    // NOTE: though we have ActionError flag in H248Action, we assume
			    // only ActionReply would have ActionError - the logic is simple:
			    // ActionRequestError is meaningless!
			    for (indexForCom = 0; indexForCom < h248Act.getNumOfCommands(); indexForCom++) {
				h248Com = h248Act.getH248CommandArray()[ indexForCom];
				pegCountBasedOnCommandTag(gwInfo, h248Com.getComTag(), true);
				// Peg count for Reply the same time!
				pegCountBasedOnCommandTag(gwInfo, h248Com.getComTag(), false);
				
				
				if (GlobalConfig.isMgSim() == false) {
				    // Mark this GW as registered if possible
				    if ((h248Com.getComTag() == MsgToken.COM_TAG_SERVICECHANGE) &&
					(h248Act.getContextId().equalsIgnoreCase(MsgToken.CONTEXT_NULL) == true) &&
					(h248Com.getTermId().equalsIgnoreCase(MsgToken.TERMINATION_ROOT) == true)) {
					// Check the contextId and termId to see
					// if this is a registration reply.
					// NOTE: we do not have transId saving for the register SVC for now.
					// we do not implement method decoding in the Request either.
					// daveti: need future works.
					if (gwInfo.isGwRegistered() == false) {
					    gwInfo.setGwRegisterState(true);
					    // Increase the global counter in DB
					    dbObj.increaseNumOfGwRegistered();
					}
					// For Mg:
					// As original design is to take the place of 'per-GW-per-thread' with
					// 'all-GWs-one-thread', as long as the first registration is succeeded,
					// then GW HB would be started right now.
					// For Mgc:
					// Pretty much same, then MGC HB would be started
					if ((dbObj.getNumOfGwRegistered() == 1) &&
					    (GlobalConfig.getAuditTimer() != -1) &&
					    (((MgcHbTimerTask)hbTimerTaskObj).isTimerTaskStarted() == false)) {
					    // Start HB for MG
					    ((MgcHbTimerTask)hbTimerTaskObj).setIsStartedFlag(true);
					    timerObj.scheduleAtFixedRate(hbTimerTaskObj,
									GlobalConfig.getAuditTimer(),
									GlobalConfig.getAuditTimer());
					    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "MGC HB has been started -" +
						    " hbTimerTaskObj: " + hbTimerTaskObj +
						    ", delay: " + GlobalConfig.getAuditTimer() +
						    ", period: " + GlobalConfig.getAuditTimer());
					}
				    }
				}
			    }
			}
			break;
				
		    case MsgToken.TRANS_TAG_REPLY:
			// Peg count for Reply
			for (indexForAct = 0; indexForAct < h248Trans.getNumOfActions(); indexForAct++) {
			    h248Act = h248Trans.getH248ActionArray()[ indexForAct];
			    // NOTE: this may be an ActionReplyError!
			    if (h248Act.getIsActionErrorFlag() == true) {
				// Set the corresponding error info from H248Action to GwInfo
				gwInfo.setErrorCode(h248Act.getErrorCode());
				// Peg count for Ack the same time!
				gwInfo.increaseNumOfAck();
			    } else {
				// Fall into command level
			        for (indexForCom = 0; indexForCom < h248Act.getNumOfCommands(); indexForCom++) {
				    h248Com = h248Act.getH248CommandArray()[ indexForCom];
				    pegCountBasedOnCommandTag(gwInfo, h248Com.getComTag(), false);
				    // Peg count for Ack the same time!
				    gwInfo.increaseNumOfAck();
				
				    if (GlobalConfig.isMgSim() == true) {
				        // Mark this GW as registered if possible
				        if ((h248Com.getComTag() == MsgToken.COM_TAG_SERVICECHANGE) &&
					    (h248Act.getContextId().equalsIgnoreCase(MsgToken.CONTEXT_NULL) == true) &&
					    (h248Com.getTermId().equalsIgnoreCase(MsgToken.TERMINATION_ROOT) == true)) {
					    // Check the contextId and termId to see
					    // if this is a registration reply.
					    // NOTE: we do not have transId saving for the register SVC for now.
					    // we do not implement method decoding in the Request either.
					    // daveti: need future works.
					    if (gwInfo.isGwRegistered() == false) {
					        gwInfo.setGwRegisterState(true);
					        // Increase the global counter in DB
					        dbObj.increaseNumOfGwRegistered();
					    }
					    // For Mg:
					    // As original design is to take the place of 'per-GW-per-thread' with
					    // 'all-GWs-one-thread', as long as the first registration is succeeded,
					    // then GW HB would be started right now.
					    // For Mgc:
					    // Pretty much same, then MGC HB would be started
					    if ((dbObj.getNumOfGwRegistered() == 1) &&
						(GlobalConfig.getInactTimer() != -1) &&
					        (((MgHbTimerTask)hbTimerTaskObj).isTimerTaskStarted() == false)) {
					        // Start HB for MG
					        ((MgHbTimerTask)hbTimerTaskObj).setIsStartedFlag(true);
					        timerObj.scheduleAtFixedRate(hbTimerTaskObj,
									GlobalConfig.getInactTimer(),
									GlobalConfig.getInactTimer());
					        MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "GW HB has been started -" +
						        " hbTimerTaskObj: " + hbTimerTaskObj +
						        ", delay: " + GlobalConfig.getInactTimer() +
						        ", period: " + GlobalConfig.getInactTimer());
					    }
				        }
				    }
			        }
			    }
			}
			break;
				
		    case MsgToken.TRANS_TAG_ERROR:
		    case MsgToken.TRANS_TAG_INVALID:
		    case MsgToken.TRANS_TAG_PENDING:
		    default:
			// daveti: do nothing currently
			break;	
		}
	    }
	} finally {
	    // Have to release the lock anyway
	    gwInfo.getGwLock().unlock();
	}
    }
    
    public boolean isMsgReplyOrAckNeeded(H248MsgDecoded decodedMsg) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "decodedMsg: " + decodedMsg);
	H248Transaction h248Trans;
	int indexForTrans;
	for (indexForTrans = 0; indexForTrans < decodedMsg.getNumOfTransactions(); indexForTrans++) {
	    h248Trans = decodedMsg.getH248TransactionArray()[ indexForTrans];
	    if ((h248Trans.getTransTag() == MsgToken.TRANS_TAG_REQUEST) ||
		(h248Trans.getTransTag() == MsgToken.TRANS_TAG_REPLY) ||
		(h248Trans.getTransTag() == MsgToken.TRANS_TAG_PENDING)) {
		// For all the cases above, we need either Reply or Ack.
		return true;
	    }
	}
	return false;
    }
    
    public void buildAndSendMsg(GwInfo gwInfo, H248Msg h248Msg) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "gwInfo: " + gwInfo +
		", h248Msg: " + h248Msg);
	// Currently MG handling here does not need gwInfo update here and
	// this is the intention to separate the decoded msg parsing into
	// 'updataGwInfo' and 'buildAndSendMsg' to avoid lock impact. However,
	// for MGC handling - which may need gwInfo if new Req is needed to
	// GW. Anyway, we only care about MG handling right now.
	H248Msg h248MsgOut;
	String msgData;
	if (GlobalConfig.isMgSim() == true) {
	    // MG:
	    // Build the reply based on request
	    msgData = buildObj.buildReplyAckForDecodedH248Msg(
					gwInfo.getMidDomainName(),
					gwInfo.getMidPortNum(),
					h248Msg.getH248MsgDecoded());
	    // Construct a new H248Msg structure
	    h248MsgOut = new H248Msg(GlobalConfig.getRemoteIPv4Addr(),
					GlobalConfig.getRemotePortNum(),
					msgData,
					msgData.length());
	} else {
	    // MGC:
	    // Build the reply based on request
	    msgData = buildObj.buildReplyAckForDecodedH248Msg(
					generateMidDomainNameForMgc(gwInfo.getMidDomainName()),
					GlobalConfig.getMidPortNumForMgc(),
					h248Msg.getH248MsgDecoded());
	    // Construct a new H248Msg structure
	    h248MsgOut = new H248Msg(gwInfo.getOrigIP(),
					gwInfo.getOrigPortNum(),
					msgData,
					msgData.length());
	}
	
	// Send the damn msg, Man!
	transObj.udpSend(h248MsgOut);
    }
    
    public void processMsg(H248Msg h248Msg) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "h248Msg: " + h248Msg);
        if (decodeH248Msg(h248Msg) != null) {
	    // The detailed processing may be different between jmgsim and jmgcsim
	    // jmgsim: an existing gwInfo needs to be found based on the mId recvd
	    // jmgcsim: try to find if there is an existing gwInfo there; if not
	    // create a new gwInfo in GwInfoDB.
	    H248MsgDecoded decodedMsg = h248Msg.getH248MsgDecoded();
	    String origGwMid;
	    GwInfo gwInfo;
	    if (GlobalConfig.isMgSim() == true) {
		// MG:
	        // isMgcMidContained flag is only cared for jmgsim
		if (decodedMsg.isMgcMidContainedMid() == true) {
		    // Try to get the original mId for MG
		    origGwMid = getOriginalGwMid(decodedMsg.getMidDomanName());
		    MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "origGwMid: " + origGwMid);
		    
		    // Get GwInfo based on mId
		    gwInfo = dbObj.getGwInfoFromMid(origGwMid);
		} else {
		    gwInfo = dbObj.getGwInfoFromMid(decodedMsg.getMidDomanName());
		}
		
		if (gwInfo == null) {
		    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: unknown GW with mId: " +
			    decodedMsg.getMidDomanName() + ", msg will be dropped");
		    return;
		}
	    } else {
	        // MGC:
		gwInfo = dbObj.getGwInfoFromMid(decodedMsg.getMidDomanName());
		if (gwInfo == null) {
		    // This is a new GW - try to add it into DB if this is a registration
		    if (isGwRegistrationReq(decodedMsg) == true) {
			// Try to add the new gwInfo into DB
			gwInfo = dbObj.addGwInfoForMgc(decodedMsg.getMidDomanName(),
						decodedMsg.getMidPortNum(),
						h248Msg.getIP(),
						h248Msg.getPort());
			if (gwInfo == null) {
			    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: adding GW failure" +
				    " - all msgs from GW: " + decodedMsg.getMidDomanName() +
				    " would be dropped without processing");
			    return;
			}
		    } else {
			MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Warning: got non-registratoin" +
				" msg from unregistered GW: " + decodedMsg.getMidDomanName() +
				" - msg would not be processed until registration");
			return;
		    }
		}
	    }
	    
	    // Now assume we have got the valid gwInfo either for MG or MGC.
	    // Now we need to reparse the decoded msg structure twice respectively
	    // for gwInfo update and Reply/ACK build. The intention is to reduce
	    // the lock impact(time) on gwInfo with mgWokerThread/MgcWorkerThread.
	    // Thx, daveti.
	    
	    // Update the gwInfo based on h248Msg
	    updateGwInfo(gwInfo, h248Msg);
	    
	    if (isMsgReplyOrAckNeeded(decodedMsg) == true) {
	        // Build msg and send
	        buildAndSendMsg(gwInfo, h248Msg);
	    }
	} else {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Error: invalid H248Msg - decoding failure");
	}
    }
    
    public void setHbTimerTask(TimerTask hbTimerTaskObj) {
	this.hbTimerTaskObj = hbTimerTaskObj;
    }
    
    private final Transport transObj;
    private final GwInfoDB dbObj;
    private final MsgBuild buildObj;
    private final Timer timerObj;
    private TimerTask hbTimerTaskObj;
}
