/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author s6089488
 */
public class Directory
{

    private final Object lock = new Object();

    private static final String DEFAULT_RECV_IP_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 9090;

    //Messages are received as a server, other peers need to connect.
    //
    private ServerSocket serverSocket;
    private String receiveIp;
    private String handle;
    private int receivePort;

    LinkedList<String> ipAddresses = new LinkedList<>();
    
    //Messages are sent as a client.
    //
    private HashMap<String, Connection> peerGroupConnections = new HashMap<>();
}
 