/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The abstract extended by all classes that need to communicate over the
 * network
 *
 * @author Group B
 */
public abstract class MetaAgent extends Node
{

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
     * Creates a new ChatNode from the specified handle
     *
     * @param handle the unique handle of the chatnode
     */
    public MetaAgent(String handle)
    {
        this(handle, DEFAULT_RECV_IP_ADDRESS, DEFAULT_PORT);
    }

    /**
     * Creates a new ChatNode from the specified handle and IP
     *
     * @param handle the unique handle of the chatnode
     * @param receiveIp the ip that this node can receive requests from
     */
    public MetaAgent(String handle, String receiveIp)
    {
        this(handle, receiveIp, DEFAULT_PORT);
    }

    /**
     * Creates a new ChatNode from the specified handle, IP, and port
     *
     * @param handle the unique handle of the chatnode
     * @param receiveIp the ip that this node can receive requests from
     * @param receivePort the port that this node can receive requests from
     */
    public MetaAgent(String handle, String receiveIp, int receivePort)
    {
        if (checkHandle(handle))
        {
            this.handle = handle;
        }
        else
        {
            throw new IllegalArgumentException("Invalid Handle Provided");
        }
        this.receiveIp = receiveIp;
        this.receivePort = receivePort;
    }

    /**
     * abstract method to start the nodes running so they can receive messages
     *
     * @throws IOException
     */
    abstract public void begin() throws IOException;

    /**
     * Open this node to receive new connections from the network
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    protected abstract void startPeerReceiver() throws UnknownHostException, IOException;

    /**
     * cannot declare abstract, but needs to be overriden
     *
     * @param ipAddress the ip address to be checked
     * @return
     */
    protected synchronized boolean isalreadyConnected(final String ipAddress)
    {
        return false;
    }


    
    /**
     * abstract method to remove connections from the node
     */
    public abstract void removeConnections();

    /**
     * Checks that the handle is a valid format
     * @param handle the handle value to check
     * @return a boolean indicating if it is true
     */
    public static boolean checkHandle(String handle)
    {
        return handle.matches("^[^\\d\\s]+$");
    }

    /**
     * Checks that an ip is a valid IPv4 format
     * @param ip the ip to check
     * @return a boolean indicating if the ip is valid
     */
    public static boolean checkIp(String ip)
    {
        Pattern ipPattern = Pattern.compile("\\b(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\."
                + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\."
                + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\."
                + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b");
        Matcher matcher = ipPattern.matcher(ip);

        return matcher.matches();
    }
    
}
