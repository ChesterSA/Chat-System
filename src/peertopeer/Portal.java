package peertopeer;

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
            if (message.isBroadcast())
            {
                //
                // Not handling broadcast messages presently...
                //
            }
            else
            {
                if (agents.containsKey(message.getTo()))
                {
                    System.out.println("Message is to local agent");
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

                                System.out.println("---Portal: " + handle + " has received message from an agent");

                                if (agents.containsKey(receivedMessage.getTo()))
                                {
                                    System.out.println("---Message is to local agent of portal " + handle);
                                    sendMessage(receivedMessage);
                                }
                                else
                                {
                                    System.out.println("---No local agents, contacting external portals");
                                    System.out.println("---portals size = " + portals.values().size());
                                    for (Connection con : portals.values())
                                    {
                                        System.out.println("---trying socket " + con.socket.toString());
                                         
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
                    
//                    for (Connection c : portals.values())
//                    {
//                        try
//                        {
//                            if (c.hasMessage())
//                            {
//                                Message receivedMessage = c.receiveMessage();
//
//                                System.out.println("---Portal: " + handle + " has received message");
//
//                                if (agents.containsKey(receivedMessage.getTo()))
//                                {
//                                    System.out.println("---Message is to local agent of portal " + handle);
//                                    sendMessage(receivedMessage);
//                                }
//                                else
//                                {
//                                    System.out.println("---Agent not present at portal " + handle);
//                                }
//                            }
//                            
//                        }
//                        catch (IOException ex)
//                        {
//                            Logger.getLogger(ChatNode.class
//                                    .getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
                }
            }
        }
    });

    @Override
    public void connectTo(final String remoteIpAddress, final int remotePort)
    {
        // check if we're already connected, perhaps the remote device
        // instigated a connection previously.
        if (isalreadyConnected(remoteIpAddress))
        {
            //System.err.println(String.format("Already connected to the peer with IP: '%s'", remoteIpAddress));
            return;
        }

//        Create a thread to instigate the HELLO handshake between this peer
//        and the remote peer
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
                    partialConnection.sendMessage(Message.createPortalMessage(handle));

                    //Wait for a response from this connection.
                    while (!partialConnection.hasMessage())
                    {
                        // ... Do nothing ...
                        // assumes it will eventually connect... probably not a good idea...
                    }

                    //We should have a HELLOACK message, which will have
                    //the handle of the remote peer
                    final Message receivedMessage = partialConnection.receiveMessage();
                    //Message ackMessage = partialConnection.receiveMessage();

                    if (receivedMessage.isHelloAckMessage())
                    {
                        System.out.println("---Hello Ack Message received from " + partialConnection.toString());
                        partialConnection.setHandle(receivedMessage.getFrom());
                        addPortal(partialConnection);
                    }
                    if (receivedMessage.isPortalAckMessage())
                    {
                        System.out.println("---Portal Ack Message received from " + partialConnection.toString());
                        partialConnection.setHandle(receivedMessage.getFrom());
                        addPortal(partialConnection);
                    }
                    else if (receivedMessage.isDirMessage())
                    {
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

                    //At this point in the connection process, only a HELLO message
                    //will do, anything else will be ignored.
                    //
                    final Message receivedMessage = newConnection.receiveMessage();
                    System.out.println("Message Recieved");
                    System.out.println("Message Content: " + receivedMessage.toString());

                    if (receivedMessage.isPortalMessage())
                    {
                        final String newConnectionHandle = receivedMessage.getFrom();

                        if (newConnectionHandle != null)
                        {
                            synchronized (lock)
                            {

                                if (portals.get(newConnectionHandle) == null)
                                {
                                    //Complete the connection by setting its handle.
                                    //this is essential as we use the handle to send
                                    //messages to our peers.
                                    //
                                    newConnection.setHandle(newConnectionHandle);

                                    //update our register of peer connections
                                    //
                                    addPortal(newConnection);

                                    //The HELLOACK allows the peer to know our handle
                                    //
                                    newConnection.sendMessage(Message.createPortalAckMessage(handle, newConnectionHandle));
                                }
                                else
                                {
                                    System.err.println("Already connected to a peer with name: '" + newConnectionHandle + "'");
                                }
                            }
                        }
                    }
                    else if (receivedMessage.isAgentsMessage())
                    {
                        System.out.println("---Agent connecting to me");
                        final String newConnectionHandle = receivedMessage.getFrom();

                        if (newConnectionHandle != null)
                        {
                            synchronized (lock)
                            {

                                if (agents.get(newConnectionHandle) == null)
                                {
                                    //Complete the connection by setting its handle.
                                    //this is essential as we use the handle to send
                                    //messages to our peers.
                                    //
                                    newConnection.setHandle(newConnectionHandle);

                                    //update our register of peer connections
                                    //
                                    addAgent(newConnection);

                                    //The HELLOACK allows the peer to know our handle
                                    //
                                    newConnection.sendMessage(Message.createHelloAckMessage(handle, newConnectionHandle));
                                }
                                else
                                {
                                    System.err.println("Already connected to an agent with name: '" + newConnectionHandle + "'");
                                }
                            }
                        }
                    }
                    else
                    {
                        System.err.println("Malformed peer HELLO message, connection attempt will be dropped.");

                    }

                    // Check for HELLO message with client name.
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
        synchronized (lock)
        {
            if (portals.containsKey(c.getHandle()))
            {
                System.err.println("[" + c.getHandle() + "] is already an established connection.");
                return;
            }
            System.out.println("---Adding portal " + c.toString() + " to " + handle);
            portals.put(c.getHandle(), c);
        }
    }
    
    private void addAgent(Connection c)
    {
        synchronized (lock)
        {
            if (agents.containsKey(c.getHandle()))
            {
                System.err.println("[" + c.getHandle() + "] is already an established connection.");
                return;
            }
            System.out.println("---Adding agent " + c.toString() + " to " + handle);
            agents.put(c.getHandle(), c);
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
        List<String> agentHandleList = new ArrayList<>();
        agents.
                values().
                stream().
                forEach(
                        (connection) ->
                {
                    agentHandleList.add(connection.getHandle());
                }
                );

        Collections.sort(agentHandleList);

        return Collections.unmodifiableList(agentHandleList);
    }

    public synchronized List<String> getPortalHandles()
    {
        List<String> agentHandleList = new ArrayList<>();
        portals.
                values().
                stream().
                forEach(
                        (connection) ->
                {
                    agentHandleList.add(connection.getHandle());
                }
                );

        Collections.sort(agentHandleList);

        return Collections.unmodifiableList(agentHandleList);
    }
    
    public void removePortals()
    {
        portals = new HashMap<>();
    }

    public void removeAgents()
    {
        agents = new HashMap<>();
    }

    @Override
    public void removeConnections()
    {
        portals = new HashMap<>();
        agents = new HashMap<>();
    }
    
    public boolean hasPortals()
    {
        return !portals.isEmpty();
    }
    
    public boolean hasAgents()
    {
        return !agents.isEmpty();
    }
}
