package middleware;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Group B
 */
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for message throughput from agent to agent, or agent to portal to agent
 * @author s6089488
 */
public class Portal extends ChatNode implements Connectable
{
    /**
     * Hashmap containing handle and connection of all connected portals
     */
    protected HashMap<String, Connection> portals = new HashMap<>();
    
    /**
     * Hashmap containing handle and connection of all connected portals
     */
    protected HashMap<String, Connection> agents = new HashMap<>();

    /**
     * Creates a new portal from the handle given
     * @param handle The unique identifier of the portal
     */
    public Portal(String handle)
    {
        super(handle);
    }

    /**
     * Creates a new portal from the handle and ip
     * @param handle the unique identifier of the portal
     * @param receiveIp the ip range that this portal can receive requests from
     */
    public Portal(String handle, String receiveIp)
    {
        super(handle, receiveIp);
    }

    /**
     * Creates a new portal from the handle and ip
     * @param handle the unique identifier of the portal
     * @param receiveIp the ip range that this portal can receive requests from
     * @param receivePort the port that this portal can receive requests from
     */
    public Portal(String handle, String receiveIp, int receivePort)
    {
        super(handle, receiveIp, receivePort);
    }

    /**
     * Sends a message from this portal to the receiver specified in message
     * @param message the message to be sent
     */
    @Override
    public void sendMessage(Message message)
    {
        System.out.println("Sending message " + message.toString());
        synchronized (lock)
        {
            if (message.getType().equals(MessageType.BROADCAST))
            {
                for (Connection c : agents.values())
                {
                    c.sendMessage(message);
                }
                if (agents.containsKey(message.getFrom()))
                {
                    for (Connection c : portals.values())
                    {
                        c.sendMessage(message);
                    }
                }
            }
            else
            {
                if (agents.containsKey(message.getTo()))
                {
                    agents.get(message.getTo()).sendMessage(message);
                }
                else
                {

                    for (Connection c : portals.values())
                    {
                        c.sendMessage(message);
                    }
                }

            }
        }
    }

    /**
     * Thread to receive messages from other chatnodes
     */
    protected final Thread receiveThread = new Thread(
            new Runnable()
    {
        @Override
        public void run()
        {
            while (true)
            {
                synchronized (lock)
                {
                    LinkedList<Connection> connections = new LinkedList(portals.values());
                    connections.addAll(agents.values());

                    for (Connection c : connections)
                    {
                        try
                        {
                            if (c.hasMessage())
                            {
                                Message receivedMessage = c.receiveMessage();

                                if (receivedMessage.getType().equals(MessageType.AGENT))
                                {
                                    final String handle = receivedMessage.getFrom();
                                    if (handle != null)
                                    {
                                        synchronized (lock)
                                        {
                                            if (agents.containsKey(handle))
                                            {
                                                System.out.println("Removing agent " + handle);
                                                removeAgent(handle);
                                            }
                                            else
                                            {
                                                System.err.println("Not connected to an agent with name: '" + handle + "'");
                                            }
                                        }
                                    }
                                }
                                else if (agents.containsKey(receivedMessage.getTo()) || receivedMessage.getType().equals(MessageType.BROADCAST))
                                {
                                    sendMessage(receivedMessage);
                                }
                                else if (agents.containsKey(receivedMessage.getFrom()))
                                {
                                    //Only send message if it's from one of your agents, stops infinite loop issues
                                    for (Connection con : portals.values())
                                    {
                                        con.sendMessage(receivedMessage);
                                    }
                                }
                            }

                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(ChatNode.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    });

    /**
     * Connects this portal to the specified address
     * @param remoteIpAddress the ip address to connect to
     * @param remotePort the port to connect to
     */
    @Override
    public void connectTo(final String remoteIpAddress, final int remotePort)
    {
        if (isalreadyConnected(remoteIpAddress))
        {
            return;
        }

//        Create a thread to start handshake between this peer and remote peer
//        Portals can only connect to each other, not to agents
        Thread helloThread = new Thread(
                new Runnable()
        {
            @Override
            public void run()
            {
                InetAddress bindAddress;
                try
                {
                    bindAddress = InetAddress.getByName(remoteIpAddress);
                    Socket newSocket = new Socket(bindAddress, remotePort);
                    Connection partialConnection = new Connection(newSocket);
                    partialConnection.sendMessage(new Message(handle, MessageType.PORTAL));

                    //Wait for a response from this connection.
                    while (!partialConnection.hasMessage())
                    {
                        
                    }

                    //Message comes in, the type will be assessed
                    final Message receivedMessage = partialConnection.receiveMessage();

                    if (receivedMessage.getType().equals(MessageType.HELLOACK))
                    {
                        partialConnection.setHandle(receivedMessage.getFrom());
                        addPortal(partialConnection);
                    }
                    if (receivedMessage.getType().equals(MessageType.PORTALACK))
                    {
                        partialConnection.setHandle(receivedMessage.getFrom());
                        addPortal(partialConnection);
                    }
                    else if (receivedMessage.getType().equals(MessageType.DIR))
                    {
                        //Split the dir message into its respective IPs
                        String[] ips = receivedMessage.getContent().split(",");

                        for (String ip : ips)
                        {
                            boolean newConnection = true;

                            if (ip.equals(Inet4Address.getLocalHost().getHostAddress()))
                            {
                                newConnection = false;
                            }
                            else
                            {

                                for (Connection c : portals.values())
                                {
                                    //regex to select the ip from a socket output
                                    Pattern ipPattern = Pattern.compile("(?<=/)(.*?)(?=,)");

                                    Matcher m = ipPattern.matcher(c.socket.toString());

                                    if (m.group().equals(ip))
                                    {
                                        newConnection = false;
                                    }
                                    System.out.println(m.group() + "    " + newConnection);
                                }
                            }
                            if (newConnection)
                            {
                                connectTo(ip);
                            }
                        }
                    }
                }
                catch (UnknownHostException ex)
                {
                    Logger.getLogger(ChatNode.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ChatNode.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        helloThread.start();

    }

    /**
     *  The thread running to handle new connections from other chatnodes
     */
    protected Thread acceptThread = new Thread(
            new Runnable()
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final Socket newClientSocket = serverSocket.accept();

                    //Create a partial connection
                    final Connection newConnection = new Connection(newClientSocket);

                    System.out.println("Awaiting HELLO message from new connection");

                    while (!newConnection.hasMessage())
                    {
                        // wait for a message from the new connection...
                        // should probably handle timeouts...
                    }

                    //Wait for a PORTAL, AGENT, or AGENTREMOVE message
                    final Message receivedMessage = newConnection.receiveMessage();
                    System.out.println("Message Recieved - " + receivedMessage.toString());

                    switch (receivedMessage.getType())
                    {
                        case PORTAL:
                        {
                            final String newConnectionHandle = receivedMessage.getFrom();
                            if (newConnectionHandle != null)
                            {
                                synchronized (lock)
                                {
                                    //if not already connected, set the connection up then respond with an ack message
                                    if (portals.get(newConnectionHandle) == null)
                                    {
                                        newConnection.setHandle(newConnectionHandle);
                                        addPortal(newConnection);
                                        newConnection.sendMessage(new Message(handle, newConnectionHandle, MessageType.PORTALACK));
                                    }
                                    else
                                    {
                                        System.err.println("Already connected to a peer with name: '" + newConnectionHandle + "'");
                                    }
                                }
                            }
                            break;
                        }
                        case AGENT:
                        {
                            final String newConnectionHandle = receivedMessage.getFrom();
                            if (newConnectionHandle != null)
                            {
                                synchronized (lock)
                                {
                                    //if not already connected, set the connection up then respond with an ack message
                                    if (agents.get(newConnectionHandle) == null)
                                    {
                                        newConnection.setHandle(newConnectionHandle);
                                        addAgent(newConnection);
                                        newConnection.sendMessage(new Message(handle, newConnectionHandle, MessageType.HELLOACK));
                                    }
                                    else
                                    {
                                        System.err.println("Already connected to an agent with name: '" + newConnectionHandle + "'");
                                    }
                                }
                            }
                            break;

                        }
                        default:
                            System.err.println("Invalid HELLO message, connection attempt will be dropped.");
                            break;
                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ChatNode.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
    );

    /**
     * Open this portal to receive new connections from the network
     * @throws UnknownHostException
     * @throws IOException
     */
    @Override
    protected void startPeerReceiver() throws UnknownHostException, IOException
    {
        if (serverSocket == null)
        {
            InetAddress bindAddress = InetAddress.getByName(this.receiveIp);
            serverSocket = new ServerSocket(this.receivePort, 0, bindAddress);
            acceptThread.start();
        }
    }

    /**
     * Starts this thread so it can receive messages from connections
     * @throws IOException
     */
    @Override
    public void begin() throws IOException
    {
        startPeerReceiver();
        receiveThread.start();
    }

    /**
     * method to check if this portal has any agent
     * @return a boolean representing if this has agents
     */
    public boolean hasAgents()
    {
        return !agents.isEmpty();
    }
    
    /**
     * Adds a new agent to this portal
     * @param c the connection details of the portal to add
     */
    private void addAgent(Connection c)
    {
        String handle = c.getHandle();
        synchronized (lock)
        {
            if (agents.containsKey(handle))
            {
                System.err.println("[" + handle + "] is already an established connection.");
                return;
            }
            agents.put(handle, c);
        }
    }
    
    /**
     * Gets the handles of all agents connected to this portal
     * @return A list of agent handles
     */
    public synchronized List<String> getAgentHandles()
    {
        List<String> handles = new LinkedList<>();
        handles.addAll(agents.keySet());
        return handles;
    }

    /**
     * Removes all agents connected to the portal
     */
    public void removeAgents()
    {
        agents = new HashMap<>();
    }

    /**
     * Removes a specified agent from the portal
     * @param key the handle of the agent to remove
     */
    public void removeAgent(String key)
    {
        if (agents.containsKey(key))
        {
            agents.remove(key);
        }
    }
    
    /**
     * Returns a boolean representing if any portals are connected
     * @return true if any portals are connected, false otherwise
     */
    public boolean hasPortals()
    {
        return !portals.isEmpty();
    }
    
    /**
     * Adds a new portal to the portals map
     * @param c the connection information of the portal to add
     */
    private void addPortal(Connection c)
    {
        String handle = c.getHandle();
        synchronized (lock)
        {
            if (portals.containsKey(handle))
            {
                System.err.println("[" + handle + "] is already an established connection.");
                return;
            }
            portals.put(handle, c);
        }
    }
    
    /**
     * Returns a list of all the handles of connected portals
     * @return all connected portal's handles in a list
     */
    public synchronized List<String> getPortalHandles()
    {
        List<String> handles = new LinkedList<>();
        handles.addAll(portals.keySet());
        return handles;
    }

    /**
     * Remove all connected portals
     */
    public void removePortals()
    {
        portals = new HashMap<>();
    }

    /**
     * Removes a specified portal from the map
     * @param key the handle of the portal to remove
     */
    public void removePortal(String key)
    {
        if (portals.containsKey(key))
        {
            portals.remove(key);
        }
    }

    /**
     * Removes all connected portals and agents from the portal
     */
    @Override
    public void removeConnections()
    {
        portals = new HashMap<>();
        agents = new HashMap<>();
    }

    /**
     * Set the handle of the portal
     * @param handle the new handle of the portal
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    @Override
    public void connectTo(String remoteIpAddress) 
    {
        this.connectTo(remoteIpAddress, DEFAULT_PORT);
    }
}
