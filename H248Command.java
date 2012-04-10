/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * Definition of H.248 Command
 * 
 */
public class H248Command {
    
    public H248Command() {
        numOfCommands = 0;
        comLength = 0;
        comTag = 0;
        termId = null;
    }
    
    public int getNumOfCommands() {
        return numOfCommands;
    }
    
    public void increaseNumOfCommands() {
        this.numOfCommands++;
    }
    
    public int getComLength() {
        return comLength;
    }
    
    public void setComLength(int comLength) {
        this.comLength = comLength;
    }
    
    public int getComTag() {
        return comTag;
    }
    
    public void setComTag(int comTag) {
        this.comTag = comTag;
    }
    
    public String getTermId() {
        return termId;
    }
    
    public void setTermId(String termId) {
        this.termId = termId;
    }
    
    private int numOfCommands;
    private int comLength;
    private int comTag;
    private String termId;
}
