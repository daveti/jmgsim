/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * Definition of H.248 Action
 * 
 */
public class H248Action {
    
    public H248Action() {
        numOfCommands = 0;
        actLength = 0;
        contextId = null;
	errorCode = null;
	isActionError = false;
        h248CommandArray = null;
    }
    
    public int getNumOfCommands() {
        return numOfCommands;
    }
    
    public void increaseNumOfCommands() {
        this.numOfCommands++;
    }
    
    public int getActLength() {
        return actLength;
    }
    
    public void setActLength(int actLength) {
        this.actLength = actLength;
    }
    
    public String getContextId() {
        return contextId;
    }
    
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
    
    public String getErrorCode() {
	return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
    }
    
    public boolean getIsActionErrorFlag() {
	return isActionError;
    }
    
    public void setIsActionErrorFlag(boolean isActionError) {
	this.isActionError = isActionError;
    }
    
    public H248Command[] getH248CommandArray() {
        return h248CommandArray;
    }
    
    public void setH248CommandArray(H248Command[] h248CommandArray) {
        this.h248CommandArray = h248CommandArray;
    }
    
    private int numOfCommands;
    private int actLength;
    private String contextId;
    private String errorCode; // error code within the Error descriptor..
    private boolean isActionError; // Assume ActionError is only for ActionReply...
    private H248Command[] h248CommandArray;
}
