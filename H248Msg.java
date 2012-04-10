/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

/**
 *
 * @author daveti
 * H.248 msg wrapper class for socket to recv/send.
 * 
 */
public class H248Msg {
    
    public H248Msg(String ip, int port, String msg, int msgLength) {
        this.ip = ip;
        this.port = port;
        this.msg = msg;
        this.msgLength = msgLength;
        h248MsgDecoded = null;
    }
    
    public String getIP() {
        return ip;
    }
    
    public void setIP(String ip) {
        this.ip = ip;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPortNum(int port) {
        this.port = port;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public int getMsgLength() {
        return msgLength;
    }
    
    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }
    
    public H248MsgDecoded getH248MsgDecoded() {
        return h248MsgDecoded;
    }
    
    public void setH248MsgDecoded(H248MsgDecoded h248MsgDecoded) {
        this.h248MsgDecoded = h248MsgDecoded;
    }
    
    private String ip;    // either origIP or destIP based on incoming or outgoing
    private int port;     // either origPort or destPort based on imcoming or outgoing
    private String msg;   // H.248 msg data from socket
    private int msgLength;// H.248 msg length - to avoid too many length() calling
    private H248MsgDecoded h248MsgDecoded;    // Decoded H.248 msg
}
