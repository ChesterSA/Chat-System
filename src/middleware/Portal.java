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
 * @author s6089488
 */
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Portal extends ChatNode
{

    protected HashMap<String, Connection> portals = new HashMap<>();
    protected HashMap<String, Connection> agents = new HashMap<>();

    public Portal(String handle)
    {
        super(handle);
    }

    public Portal(String handle, String receiveIp)
    {
        super(handle, receiveIp);
    }

    public Portal(String handle, String receiveIp, int receivePort)
    {
        super(handle, receiveIp, receivePort);
    }

    /*
     * @param peer The peer that the message is being sent to 
     * @param message The message to send to all peers
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
                    //System.err.println("'" + receiver + "' is an unknown peer");
                }

            }
        }
    }

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

    @Override
    public void connectTo(final String remoteIpAddress, final int remotePort)
    {
        if (isalreadyConnected(remoteIpAddress))
        {
            return;
        }

//        Create a thread to startt handshake between this peer and remote peer
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
                        // ... Do nothing ...
                        // assumes it will eventually connect... probably not a good idea...
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

    @Override
    public void begin() throws IOException
    {
        startPeerReceiver();
        receiveThread.start();
    }

    public synchronized List<String> getAgentHandles()
    {
        List<String> handles = new LinkedList<>();
        handles.addAll(agents.keySet());
        return handles;
    }

    public void removeAgents()
    {
        agents = new HashMap<>();
    }

    public void removeAgent(String key)
    {
        if (agents.containsKey(key))
        {
            agents.remove(key);
        }
    }
    
    public boolean hasAgents()
    {
        return !agents.isEmpty();
    }
    
    public boolean hasPortals()
    {
        return !portals.isEmpty();
    }
    
    public synchronized List<String> getPortalHandles()
    {
        List<String> handles = new LinkedList<>();
        handles.addAll(portals.keySet());
        return handles;
    }

    public void removePortals()
    {
        portals = new HashMap<>();
    }

    public void removePortal(String key)
    {
        if (portals.containsKey(key))
        {
            portals.remove(key);
        }
    }

    @Override
    public void removeConnections()
    {
        portals = new HashMap<>();
        agents = new HashMap<>();
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }
}
