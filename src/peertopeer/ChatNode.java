/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 *
 * @author u0012604
 */
public abstract class ChatNode
{

    protected final Object lock = new Object();

    protected static final String DEFAULT_RECV_IP_ADDRESS = "127.0.0.1";
    protected static final int DEFAULT_PORT = 9090;

    //Messages are received as a server, other peers need to connect.
    //
    protected ServerSocket serverSocket;
    protected String receiveIp;
    protected String handle;
    protected int receivePort;

    public abstract void removeConnections();

    public ChatNode(String handle)
    {
        this(handle, DEFAULT_RECV_IP_ADDRESS, DEFAULT_PORT);
    }

    public ChatNode(String handle, String receiveIp)
    {
        this(handle, receiveIp, DEFAULT_PORT);
    }

    public ChatNode(String handle, String receiveIp, int receivePort)
    {
        this.handle = handle;
        this.receiveIp = receiveIp;
        this.receivePort = receivePort;
    }

    abstract public void begin() throws IOException;

    public String getHandle()
    {
        return handle;
    }

    protected abstract void startPeerReceiver() throws UnknownHostException, IOException;

    public void connectTo(final String remoteIpAddress)
    {
        this.connectTo(remoteIpAddress, DEFAULT_PORT);
    }

    public abstract void connectTo(final String remoteIpAddress, final int remotePort);
    
    
    protected abstract void addConnection(final Connection connection);

    //cannot declare abstract, but needs to be overriden
    protected synchronized boolean isalreadyConnected(final String ipAddress)
    {
        return false;
    }

    /*
     * @param peer The peer that the message is being sent to 
     * @param message The message to send to all peers
     */
    public abstract void sendMessage(Message message);
}
