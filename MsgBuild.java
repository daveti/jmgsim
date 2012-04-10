/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * Basic msg build for request, reply and ack.
 * 
 */
public class MsgBuild {
	
    public String generateH248TimeStamp() {
	// This function would make sure the format/length of
	// H.248 timeStamp used in H.248 msg build.
	String rawStamp = MyLogger.getTimeStampForH248Msg();
        MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "rawStamp: " + rawStamp);
	
	// rawStamp: 20120315T021423108
	// 248Stamp: 20120313T00284734  - fixed 8+1+8 bytes
	if (rawStamp.length() > 17) {
	    rawStamp = rawStamp.substring(0, 17);
	}
	
	return rawStamp;
    }
	
    public String buildAuditValueRootReqForMgcHb(long transId, String mIdDomainName, int mIdPort) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "TransId: " + transId +
		", mIdDomainName: " + mIdDomainName + ", mIdPort: " + mIdPort);
	String msgData;
	String mIdPortString = (mIdPort == -1) ? ("") : (":" + mIdPort);
	if (GlobalConfig.isLongTokenEncoding() == true) {
	    msgData = MsgToken.MEGACO_2_L + " " + mIdDomainName + mIdPortString + " \n" +
		    MsgToken.TRANSACTION_L + " = " + transId + " {\n" +
		    "\t" + MsgToken.CONTEXT_L + " = " + MsgToken.CONTEXT_NULL + " {\n" +
		    "\t\t" + MsgToken.AUDITVALUE_L + " = " + MsgToken.TERMINATION_ROOT + " {\n" +
		    "\t\t\t" + MsgToken.AUDIT_L + " {  }\n" +
		    "\t\t}\n" +
		    "\t}\n" +
		    "}";
	} else {
	    msgData = MsgToken.MEGACO_2_S + " " + mIdDomainName + mIdPortString + " \n" +
		    MsgToken.TRANSACTION_S + "=" + transId + "{" +
		    MsgToken.CONTEXT_S + "=" + MsgToken.CONTEXT_NULL + "{" +
		    MsgToken.AUDITVALUE_S + "=" + MsgToken.TERMINATION_ROOT + "{" +
		    MsgToken.AUDIT_S + "{}" +
		    "}}}";
	}
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msgData: " + msgData);
	return msgData;
    }
    
    public String buildServiceChangeRootReqForGwReg(long transId, String mIdDomainName, int mIdPort) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "TransId: " + transId +
		", mIdDomainName: " + mIdDomainName + ", mIdPort: " + mIdPort);
        String msgData;
        String mIdPortString = (mIdPort == -1) ? ("") : (":" + mIdPort);
        if (GlobalConfig.isLongTokenEncoding() == true) {
            msgData = MsgToken.MEGACO_2_L + " " + mIdDomainName + mIdPortString + "\n" +
                    MsgToken.TRANSACTION_L + " = " + transId + " {\n" +
                    "\t" + MsgToken.CONTEXT_L + " = " + MsgToken.CONTEXT_NULL + " {\n" +
                    "\t\t" + MsgToken.SERVICECHANGE_L + " = " + MsgToken.TERMINATION_ROOT + " {\n" +
                    "\t\t\t" + MsgToken.SERVICES_L + " {\n" +
                    "\t\t\t\t" + MsgToken.METHOD_L + " = " + MsgToken.RESTART_L + ",\n" +
                    "\t\t\t\t" + MsgToken.REASON_L + " = \"" + MsgToken.REASON_CODE_901 + "\",\n" +
                    "\t\t\t\t" + MsgToken.DELAY_L + " = " + MsgToken.DELAY_VALUE + ",\n" +
                    "\t\t\t\t" + MsgToken.PROFILE_L + " = " + MsgToken.PROFILE_VALUE + ",\n" +
                    "\t\t\t\t" + MsgToken.VERSION_L + " = " + MsgToken.VERSION_VALUE + "\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "}";
        } else {
            msgData = MsgToken.MEGACO_2_S + " " + mIdDomainName + mIdPortString + "\n" +
                    MsgToken.TRANSACTION_S + "=" + transId + "{" +
                    MsgToken.CONTEXT_S + "=" + MsgToken.CONTEXT_NULL + "{" +
                    MsgToken.SERVICECHANGE_S + "=" + MsgToken.TERMINATION_ROOT + "{" +
                    MsgToken.SERVICES_S + "{" +
                    MsgToken.METHOD_S + "=" + MsgToken.RESTART_S + "," +
                    MsgToken.REASON_S + "=\"" + MsgToken.REASON_CODE_901 + "\"," +
                    MsgToken.DELAY_S + "=" + MsgToken.DELAY_VALUE + "," +
                    MsgToken.PROFILE_S + "=" + MsgToken.PROFILE_VALUE + "," +
                    MsgToken.VERSION_S + "=" + MsgToken.VERSION_VALUE +
                    "}}}}";
        }
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msgData: " + msgData);
        return msgData;
    }
    
    public String buildItItoNotifyRootReqForGwHb(long transId, String mIdDomainName, int mIdPort) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "TransId: " + transId +
		", mIdDomainName: " + mIdDomainName + ", mIdPort: " + mIdPort);
        String msgData;
        String mIdPortString = (mIdPort == -1) ? ("") : (":" + mIdPort);
        if ( GlobalConfig.isLongTokenEncoding() == true) {
            msgData = MsgToken.MEGACO_2_L + " " + mIdDomainName + mIdPortString + "\n" +
                    MsgToken.TRANSACTION_L + " = " + transId + " {\n" +
                    "\t" + MsgToken.CONTEXT_L + " = " + MsgToken.CONTEXT_NULL + " {\n" +
                    "\t\t" + MsgToken.NOTIFY_L + " = " + MsgToken.TERMINATION_ROOT + " {\n" +
                    "\t\t\t" + MsgToken.OBSERVEDEVENTS_L + " = " + MsgToken.ITITO_EVENT_VALUE + " {\n" +
                    "\t\t\t\t" + generateH248TimeStamp() + ":\n" +
                    "\t\t\t\t" + MsgToken.ITITO_EVENT + "\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "}";
        } else {
            msgData = MsgToken.MEGACO_2_S + mIdDomainName + mIdPortString + "\n" +
                    MsgToken.TRANSACTION_S + "=" + transId + "{" +
                    MsgToken.CONTEXT_S + "=" + MsgToken.CONTEXT_NULL + "{" +
                    MsgToken.NOTIFY_S + "=" + MsgToken.TERMINATION_ROOT + "{" +
                    MsgToken.OBSERVEDEVENTS_S + "=" + MsgToken.ITITO_EVENT_VALUE + "{" +
                    generateH248TimeStamp() + ":" +
                    MsgToken.ITITO_EVENT +
                    "}}}}";
        }
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msgData: " + msgData);
        return msgData;
    }
    
    public String buildCommandReplyBlock(H248Command h248Com, String contextId) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "h248Com: " + h248Com +
		", contextId: " + contextId);
	// NOTE: Comma is NOT contained here for multiple command reply blocks!
	// '\n' is also not added as suffix for consideration of possible comma!
	String comRepBlock;
	switch (h248Com.getComTag()) {
	    case MsgToken.COM_TAG_ADD:
		if (GlobalConfig.isLongTokenEncoding() == true) {
		    comRepBlock = "\t\t" + MsgToken.ADD_L + " = " + h248Com.getTermId() + " {\n" +
			    "\t\t\t" + MsgToken.ERROR_L + " = " + MsgToken.ERROR_CODE_443 + " {\"" + MsgToken.ERROR_CODE_443_DESCRIPTION + "\"}\n" +
			    "\t\t}";
		} else {
		    comRepBlock = MsgToken.ADD_S + "=" + h248Com.getTermId() + "{" +
			    MsgToken.ERROR_S + "=" + MsgToken.ERROR_CODE_443 + "{\"" + MsgToken.ERROR_CODE_443_DESCRIPTION + "\"}" +
			    "}";
		}
		break;
	    case MsgToken.COM_TAG_AUDITVALUE:
		if (GlobalConfig.isLongTokenEncoding() == true) {
		    comRepBlock = "\t\t" + MsgToken.AUDITVALUE_L + " = " + h248Com.getTermId();	    
		} else {
		    comRepBlock = MsgToken.AUDITVALUE_S + "=" + h248Com.getTermId();
		}
		// daveti: check if this is a channel audit based on contextId and termId
		// If so, need to include error code 435 in the reply to MGC; otherwise
		// common reply is fairly good enough.
		if ((contextId.equalsIgnoreCase(MsgToken.CONTEXT_ALL) == true) &&
		    (MsgToken.isTermIdSpecific(h248Com.getTermId()) == true)) {
		    // Include error code 435
		    if (GlobalConfig.isLongTokenEncoding() == true) {
			comRepBlock += " {\n" +
				"\t\t\t" + MsgToken.ERROR_L + " = " + MsgToken.ERROR_CODE_435 + " {\"" + MsgToken.ERROR_CODE_435_DESCRIPTION + "\"}\n" +
				"\t\t}";
		    } else {
			comRepBlock += "{" +
				MsgToken.ERROR_S + "=" + MsgToken.ERROR_CODE_435 + "{\"" + MsgToken.ERROR_CODE_435_DESCRIPTION + "\"}" +
				"}";
		    }
		}
		break;
	    case MsgToken.COM_TAG_MODIFY:
		if (GlobalConfig.isLongTokenEncoding() == true) {
		    comRepBlock = "\t\t" + MsgToken.MODIFY_L + " = " + h248Com.getTermId();
		} else {
		    comRepBlock = MsgToken.MODIFY_S + "=" + h248Com.getTermId();
		}
		break;
	    case MsgToken.COM_TAG_NOTIFY:
		if (GlobalConfig.isLongTokenEncoding() == true) {
		    comRepBlock = "\t\t" + MsgToken.NOTIFY_L + " = " + h248Com.getTermId();
		} else {
		    comRepBlock = MsgToken.NOTIFY_S + "=" + h248Com.getTermId();
		}
		break;
	    case MsgToken.COM_TAG_SERVICECHANGE:
		if (GlobalConfig.isLongTokenEncoding() == true) {
		    comRepBlock = "\t\t" + MsgToken.SERVICECHANGE_L + " = " + h248Com.getTermId();
		} else {
		    comRepBlock = MsgToken.SERVICECHANGE_S + "=" + h248Com.getTermId();
		}
		break;
	    case MsgToken.COM_TAG_SUBTRACT:
		if (GlobalConfig.isLongTokenEncoding() == true) {
		    comRepBlock = "\t\t" + MsgToken.SUBTRACT_L + " = " + h248Com.getTermId();
		} else {
		    comRepBlock = MsgToken.SUBTRACT_S + "=" + h248Com.getTermId();
		}
		break;
	    case MsgToken.COM_TAG_MOVE:
	    case MsgToken.COM_TAG_INVALID:
	    default:
		MyLogger.log(GlobalConfig.LOG_LEVEL_MEDIUM, "Error: invalid command tag - " + 
			h248Com.getComTag());
		comRepBlock = "";
		break;
	}
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comRepBlock: " + comRepBlock);
	return comRepBlock;
    }
    
    public String addCommaIfNeeded(int index, int indexMax) {
	// NOTE: this function should only works for Command and Action!
	// There is no comman between different transactions within one msg!
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "index: " + index +
		", indexMax: " + indexMax);
	String comma;
	if (index < indexMax) {
	    // Need to add the comma
	    if (GlobalConfig.isLongTokenEncoding() == true) {
		comma = ",\n";
	    } else {
		comma = ",";
	    }
	} else {
	    // Move to next starting position
	    if (GlobalConfig.isLongTokenEncoding() == true) {
		comma = "\n";
	    } else {
	        comma = "";
	    }
	}
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "comma: " + comma);
	return comma;
    }
    
    public String addClosingBrace(boolean isTrans) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "isTrans: " + isTrans);
	// NOTE: this function only works for Transaction and Action!
	// Command block does NOT need this!
	String closingBrace;
	if (isTrans == true) {
	    // Add closing brace for transaction
	    if (GlobalConfig.isLongTokenEncoding() == true) {
		closingBrace = "}";
	    } else {
		// Yep - no doubt - the same with above:)
		closingBrace = "}";
	    }
	} else {
	    // Add closing barce for action
	    if (GlobalConfig.isLongTokenEncoding() == true) {
	        closingBrace = "\t}\n";
	    } else {
		closingBrace = "}";
	    }
	}
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "closingBrace: " + closingBrace);
	return closingBrace;
    }
    
    public String addNewLineForNextTransIfNeeded(H248MsgDecoded decodedMsg, int indexForTrans) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "decodedMsg: " + decodedMsg +
		", indexForTrans: " + indexForTrans);
	String newLine;
	// Check if this is the last transaction
	if (indexForTrans == decodedMsg.getNumOfTransactions()-1) {
	    // Last transaction - no new line needed
	    newLine = "";
	} else {
	    // Prefetch the next transaction based on the current index
	    H248Transaction h248Trans = decodedMsg.getH248TransactionArray()[ indexForTrans+1];
	    switch (h248Trans.getTransTag()) {
		case MsgToken.TRANS_TAG_REQUEST:
		case MsgToken.TRANS_TAG_REPLY:
		case MsgToken.TRANS_TAG_PENDING:
		    if (GlobalConfig.isLongTokenEncoding() == true) {
		        // We need new line here
		        newLine = "\n";
		    } else {
			// Short token doe NOT need new line here
			newLine = "";
		    }
		    break;
			
		default:
		    // We do not need new line here
		    newLine = "";
		    break;
	    }
	}
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "newLine: " + newLine);
	return newLine;
    }
    
    public String buildAckBody(H248Transaction h248Trans) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "h248Trans: " + h248Trans);
	String ackBody;
	
	// Build ack body
	if (GlobalConfig.isLongTokenEncoding() == true) {
	    ackBody = MsgToken.ACK_L + " { " + h248Trans.getTransId() + " } ";
	} else {
	    ackBody = MsgToken.ACK_S + "{" + h248Trans.getTransId() + "}";
	}
	
	return ackBody;
    }
    
    public String buildReplyBody(H248Transaction h248Trans) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "h248Trans: " + h248Trans);
	String replyBody;
	H248Action h248Act;
	H248Command h248Com;
	int indexForAct;
	int indexForCom;
	
	// Build transaction header
	if (GlobalConfig.isLongTokenEncoding() == true) {
	    replyBody = MsgToken.REPLY_L + " = " + h248Trans.getTransId() + " {\n";
	} else {
	    replyBody = MsgToken.REPLY_S + "=" + h248Trans.getTransId() + "{";
	}
	
	// Loop the action array
	for (indexForAct = 0; indexForAct < h248Trans.getNumOfActions(); indexForAct++) {
	    h248Act = h248Trans.getH248ActionArray()[ indexForAct];
	    // Build context header
	    if (GlobalConfig.isLongTokenEncoding() == true) {
		replyBody += "\t" + MsgToken.CONTEXT_L + " = " + h248Act.getContextId() + " {\n";
	    } else {
		replyBody += MsgToken.CONTEXT_S + "=" + h248Act.getContextId() + "{";
	    }
			
	    // Loop the command array
	    for (indexForCom = 0; indexForCom < h248Act.getNumOfCommands(); indexForCom++) {
		h248Com = h248Act.getH248CommandArray()[ indexForCom];
		// Build command reply block
		// daveti: update the function below to include contextId
		replyBody += buildCommandReplyBlock(h248Com, h248Act.getContextId());
		// Add comma if needed
		replyBody += addCommaIfNeeded(indexForCom, h248Act.getNumOfCommands()-1);
	    }
			
	    // Add closing brace for Action
	    replyBody += addClosingBrace(false);
	    // Add comma if needed
	    replyBody += addCommaIfNeeded(indexForAct, h248Trans.getNumOfActions()-1);
	}
	// Add closing brace for Transaction
	replyBody += addClosingBrace(true);
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "replyBody: " + replyBody);
	return replyBody;
    }
    
    public String buildReplyAckForDecodedH248Msg(String mIdDomainName, int mIdPort, H248MsgDecoded decodedMsg) {
	// NOTE: This function only build the corresponding Reply/ACK msg based on the Request/Reply.
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "mIdDomainName: " + mIdDomainName +
		", mIdPort: " + mIdPort + ", decodedMsg: " + decodedMsg);
	String msgData;
	H248Transaction h248Trans;
	int indexForTrans;
	
	// Build msg header
	String mIdPortString = (mIdPort == -1) ? ("") : (":" + mIdPort);
	if (GlobalConfig.isLongTokenEncoding() == true) {
	    msgData = MsgToken.MEGACO_2_L + " " + mIdDomainName + mIdPortString + "\n";
	} else {
	    msgData = MsgToken.MEGACO_2_S + " " + mIdDomainName + mIdPortString + "\n";
	}
	
	// Loop the transaction array
	for (indexForTrans = 0; indexForTrans < decodedMsg.getNumOfTransactions(); indexForTrans++) {
	    h248Trans = decodedMsg.getH248TransactionArray()[ indexForTrans];
	    switch (h248Trans.getTransTag()) {
		case MsgToken.TRANS_TAG_REQUEST:
		    // Build the Reply body
		    msgData += buildReplyBody(h248Trans);
		    // Add possible new line for next Transaction
		    msgData += addNewLineForNextTransIfNeeded(decodedMsg, indexForTrans);
		    break;
			
		case MsgToken.TRANS_TAG_REPLY:
		case MsgToken.TRANS_TAG_PENDING:
		    // Build the Ack body
		    msgData += buildAckBody(h248Trans);
		    // Add possible new line for next Transaction
		    msgData += addNewLineForNextTransIfNeeded(decodedMsg, indexForTrans);
		    break;
			
		case MsgToken.TRANS_TAG_RESPONSEACK:
		case MsgToken.TRANS_TAG_ERROR:
		case MsgToken.TRANS_TAG_INVALID:
		default:
		    break;
	    }
	}
	
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "msgData: " + msgData);
        return msgData;
    }
}
