/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * Definition of H.248 msg tokens
 * Definition of H.248 msg decoding tags
 * 
 */
public class MsgToken {
    // H.248.1 - basic tokens (long and short)
    public static final String MEGACO_2_L = "MEGACO/2";
    public static final String MEGACO_2_S = "!/2";
    public static final String MEGACO_S_SLASH = "!/";
    public static final String TRANSACTION_L = "Transaction";
    public static final String TRANSACTION_S = "T";
    public static final String REPLY_L = "Reply";
    public static final String REPLY_S = "P";
    public static final String PENDING_L = "Pending";
    public static final String PENDING_S = "PN";
    public static final String ACK_L = "TransactionResponseAck";
    public static final String ACK_S = "K";
    public static final String ERROR_L = "Error";
    public static final String ERROR_S = "ER";
    public static final String MODIFY_L = "Modify";
    public static final String MODIFY_S = "MF";
    public static final String O_MODIFY_L = "O-Modify";
    public static final String O_MODIFY_S = "O-MF";
    public static final String SUBTRACT_L = "Subtract";
    public static final String SUBTRACT_S = "S";
    public static final String O_SUBTRACT_L = "O-Subtract";
    public static final String O_SUBTRACT_S = "O-S";
    public static final String AUDITVALUE_L = "AuditValue";
    public static final String AUDITVALUE_S= "AV";
    public static final String O_AUDITVALUE_L = "O-AuditValue";
    public static final String O_AUDITVALUE_S = "O-AV";
    public static final String AUDIT_L = "Audit";
    public static final String AUDIT_S = "AT";
    public static final String NOTIFY_L = "Notify";
    public static final String NOTIFY_S = "N";
    public static final String SERVICECHANGE_L = "ServiceChange";
    public static final String SERVICECHANGE_S = "SC";
    public static final String ADD_L = "Add";
    public static final String ADD_S = "A";
    public static final String CONTEXT_L = "Context";
    public static final String CONTEXT_S = "C";
    public static final String OBSERVEDEVENTS_L = "ObservedEvents";
    public static final String OBSERVEDEVENTS_S = "OE";
    public static final String VERSION_L = "Version";
    public static final String VERSION_S = "V";
    public static final String METHOD_L = "Method";
    public static final String METHOD_S = "MT";
    public static final String REASON_L = "Reason";
    public static final String REASON_S = "RE";
    public static final String SERVICES_L = "Services";
    public static final String SERVICES_S = "SV";
    public static final String RESTART_L = "Restart";
    public static final String RESTART_S = "RS";
    public static final String FORCED_L = "Forced";
    public static final String FORCED_S = "FO";
    public static final String DISCONNECTED_L = "Disconnected";
    public static final String DISCONNECTED_S = "DC";
    public static final String HANDOFF_L = "Handoff";
    public static final String HANDOFF_S = "HO";
    public static final String PROFILE_L = "Profile";
    public static final String PROFILE_S = "PF";
    public static final String DELAY_L = "Delay";
    public static final String DELAY_S = "DL";
    
    // H.248 - token value
    public static final String CONTEXT_NULL = "-";
    public static final String CONTEXT_ALL = "*";
    public static final String TERMINATION_ROOT = "ROOT";
    public static final String TERMINATION_ALL = "*";
    public static final String REASON_CODE_901 = "901";
    public static final String REASON_CODE_903 = "903";
    public static final String REASON_CDOE_906 = "906";
    public static final String DELAY_VALUE = "0";
    public static final String PROFILE_VALUE = "BOSTON1/1";
    public static final String ITITO_EVENT_VALUE = "1111";
    public static final String VERSION_VALUE = "2";
    public static final String ERROR_CODE_443 = "443";
    public static final String ERROR_CODE_443_DESCRIPTION = "Unsupported or unknown command";
    public static final String ERROR_CODE_435 = "435";
    public static final String ERROR_CODE_435_DESCRIPTION = "TerminationID is not in specified Context";
    
    // H.248.X - pkg tokens
    public static final String ITITO_EVENT = "it/ito";
    
    // H.248 decoding - trans tags
    public static final int TRANS_TAG_INVALID = -1;
    public static final int TRANS_TAG_REQUEST = 1;
    public static final int TRANS_TAG_REPLY = 2;
    public static final int TRANS_TAG_PENDING = 3;
    public static final int TRANS_TAG_RESPONSEACK = 4;
    public static final int TRANS_TAG_ERROR = 5;
    
    // H.248 decoding - com tags
    public static final int COM_TAG_INVALID = -1;
    public static final int COM_TAG_ADD = 1;
    public static final int COM_TAG_MOVE = 2;
    public static final int COM_TAG_MODIFY = 3;
    public static final int COM_TAG_NOTIFY = 4;
    public static final int COM_TAG_SUBTRACT = 5;
    public static final int COM_TAG_AUDITVALUE = 6;
    public static final int COM_TAG_SERVICECHANGE = 7;
    
    // H.248 decoding helper routines
    public static boolean isTermIdSpecific(String termId) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "termId: " + termId);
	if ((termId.equalsIgnoreCase(MsgToken.TERMINATION_ROOT) == true) ||
	    (termId.equalsIgnoreCase(MsgToken.TERMINATION_ALL) == true)) {
	    return false;
	}
	return true;
    }
}
