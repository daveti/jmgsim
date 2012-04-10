/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmgsim;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author daveti
 * DATE: 02/24/2012
 * NOTE: Only UDP is supported right now!
 * 
 */
public class Transport {

    public Transport() {
        udpSocket = null;
        socketAddr = null;
    }
    
    public DatagramSocket getUdpSocket() {
	return udpSocket;
    }

    public final int getSocketTimeOut() throws SocketException {
        return udpSocket.getSoTimeout();
    }

    public final void setSocketTimeOut(int timeOut) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "timeOut: " + timeOut);
	try {
            udpSocket.setSoTimeout(timeOut);
	} catch (SocketException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exceptoin: " + ex);
	}
    }

    public void createUdpSocket(final String ipAddr, final int port) {
	MyLogger.log(GlobalConfig.LOG_LEVEL_HIGH, "ipAddr: " + ipAddr + ", port: " + port);
	try {
	    socketAddr = new InetSocketAddress(ipAddr, port);
            udpSocket = new DatagramSocket(socketAddr);
	} catch (SocketException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
	}
    }
    
    public final void closeUdpSocket() {
        try {
            udpSocket.close();
        } catch (Exception ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
        }
    }

    public final H248Msg udpRecv() throws IOException {
        byte[] recvBuffer = new byte[GlobalConfig.H248_MSG_SIZE_MAX];
        DatagramPacket udpPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
	udpSocket.receive(udpPacket);
        String origIP = udpPacket.getAddress().getHostAddress();
        int origPort = udpPacket.getPort();
        String msgData = new String(udpPacket.getData(), 0, udpPacket.getLength());
        int msgLength = msgData.length();
        MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, 
                "origIP: " + origIP + 
                ", origPort: " + origPort + 
                ", msgLength: " + msgLength + "\n" + msgData);
        return (new H248Msg(origIP, origPort, msgData, msgLength));
    }

    public final void udpSend(H248Msg h248Msg) {
        String destIP = h248Msg.getIP();
        int destPort = h248Msg.getPort();
        String msgData = h248Msg.getMsg();
        int msgLength = h248Msg.getMsgLength();
        MyLogger.log(GlobalConfig.LOG_LEVEL_LOW,
                "destIP: " + destIP + 
                ", destPort: " + destPort + 
                ", msgLength: " + msgLength + "\n" + msgData);
        byte[] sendBuffer = msgData.getBytes();
        DatagramPacket udpPacket;
	try {
	    udpPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(destIP), destPort);
	} catch (UnknownHostException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
	    return;
	}
	
	try {
	    udpSocket.send(udpPacket);
	} catch (IOException ex) {
	    MyLogger.log(GlobalConfig.LOG_LEVEL_LOW, "Exception: " + ex);
	}
    }

    // NOTE: recvBuffer, sendBuffer and udpPackt need to be implemented within
    // functions udpRecv and udpSend independently to make these functions
    // thread safe, though the fact is only udpSend would be re-entered...
    private DatagramSocket udpSocket;
    private InetSocketAddress socketAddr;
}
