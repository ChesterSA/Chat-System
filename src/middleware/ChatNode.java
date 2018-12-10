/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * The abstract extended by all classes that need to communicate over the network
 * @author Group B
 */
public abstract class ChatNode {

    /**
     * The lock used by synchronised threads
     */
    protected final Object lock = new Object();

    /**
     * the default address to receive message from
     */
    protected static final String DEFAULT_RECV_IP_ADDRESS = "127.0.0.1";

    /**
     * the default port to receive message from
     */
    protected static final int DEFAULT_PORT = 9090;

    /**
     * The socket that messages are passed through for the chatnode
     */
    protected ServerSocket serverSocket;

    /**
     * The IP that this node can receive message from
     */
    protected String receiveIp;

    /**
     * The port that this node can receive message from
     */
    protected int receivePort;

    /**
     * the unique identifier of the node
     */
    protected String handle;

    /**
     * Creates a new ChatNode from the specified handle
     * @param handle the unique handle of the chatnode
     */
    public ChatNode(String handle) {
        this(handle, DEFAULT_RECV_IP_ADDRESS, DEFAULT_PORT);
    }

    /**
     * Creates a new ChatNode from the specified handle and IP
     * @param handle the unique handle of the chatnode
     * @param receiveIp the ip that this node can receive requests from
     */
    public ChatNode(String handle, String receiveIp) {
        this(handle, receiveIp, DEFAULT_PORT);
    }

    /**
     * Creates a new ChatNode from the specified handle, IP, and port
     * @param handle the unique handle of the chatnode
     * @param receiveIp the ip that this node can receive requests from
     * @param receivePort the port that this node can receive requests from
     */
    public ChatNode(String handle, String receiveIp, int receivePort) {
        this.handle = handle;
        this.receiveIp = receiveIp;
        this.receivePort = receivePort;
    }

    /**
     * abstract method to start the nodes running so they can receive messages
     * @throws IOException
     */
    abstract public void begin() throws IOException;

    /**
     * Open this node to receive new connections from the network
     * @throws UnknownHostException
     * @throws IOException
     */
    protected abstract void startPeerReceiver() throws UnknownHostException, IOException;
    
    /**
     * 
     * @param remoteIpAddress
     */
    public void connectTo(final String remoteIpAddress) {
        this.connectTo(remoteIpAddress, DEFAULT_PORT);
    }

    /**
     *
     * @param remoteIpAddress
     * @param remotePort
     */
    public abstract void connectTo(final String remoteIpAddress, final int remotePort);

    
    /**
     * cannot declare abstract, but needs to be overriden
     * @param ipAddress the ip address to be checked
     * @return
     */
    protected synchronized boolean isalreadyConnected(final String ipAddress) {
        return false;
    }
   
    /**
     * Gets the handle of the node
     * @return the handle of the node
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Abstract method to send a message
     * @param message the message to be sent
     */
    public abstract void sendMessage(Message message);
    
    /**
     * abstract method to remove connections from the node
     */
    public abstract void removeConnections();
}
