package middleware;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for message throughput from agent to agent, or agent to portal to
 * agent
 *
 * @author s6089488
 */
public class Portal extends MetaAgent implements Connectable
{

    /**
     * The queue of messages that the portal will send
     */
    private BlockingQueue queue = new ArrayBlockingQueue(1024);

    /**
     * Hashmap containing handle and connection of all connected portals
     */
    private HashMap<String, Connection> portals = new HashMap<>();

    /**
     * Hashmap containing handle and connection of all connected portals
     */
    private HashMap<String, Agent> agents = new HashMap<>();

    /**
     * Creates a new portal from the handle given
     *
     * @param handle The unique identifier of the portal
     */
    public Portal(String handle)
    {
        super(handle);
    }

    /**
     * Creates a new portal from the handle and ip
     *
     * @param handle the unique identifier of the portal
     * @param receiveIp the ip range that this portal can receive requests from
     */
    public Portal(String handle, String receiveIp)
    {
        super(handle, receiveIp);
    }

    /**
     * Creates a new portal from the handle and ip
     *
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
     */
    public void sendMessage()
    {
        synchronized (lock)
        {
            try
            {
                Message message = (Message) queue.take();

                if (nodeMonitor != null)
                {
                    nodeMonitor.handleMessage(message);
                }

                if (message.getType().equals(MessageType.BROADCAST))
                {
                    agents.values().forEach((a) ->
                    {
                        a.receiveMessage(message);
                    });
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
                        agents.get(message.getTo()).receiveMessage(message);
                    }
                    else
                    {

                        portals.values().forEach((c) ->
                        {
                            c.sendMessage(message);
                        });
                    }

                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
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

                    connections.forEach((c) ->
                    {
                        try
                        {
                            if (c.hasMessage())
                            {
                                Message receivedMessage = c.receiveMessage();

                                if (nodeMonitor != null)
                                {
                                    nodeMonitor.handleMessage(receivedMessage);
                                }

                                if (receivedMessage.getType().equals(MessageType.AGENT))
                                {
                                    final String handle = receivedMessage.getFrom();
                                    if (handle != null)
                                    {
                                        synchronized (lock)
                                        {
                                            if (agents.containsKey(handle))
                                            {
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
                                    queue.put(receivedMessage);

                                    //1 second delay to show the queue in action
                                    Thread.sleep(1000);
                                    sendMessage();
                                }
                                else if (agents.containsKey(receivedMessage.getFrom()))
                                {
                                    //Only send message if it's from one of your agents, stops infinite loop issues
                                    portals.values().forEach((con) ->
                                    {
                                        con.sendMessage(receivedMessage);
                                    });
                                }
                            }

                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(MetaAgent.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        catch (InterruptedException ex)
                        {
                            Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        }
    });

    /**
     * Connects this portal to the specified address
     *
     * @param remoteIpAddress the ip address to connect to
     * @param remotePort the port to connect to
     */
    @Override
    public void connectTo(final String remoteIpAddress, final int remotePort)
    {
        if (!MetaAgent.checkIp(remoteIpAddress))
        {
            throw new IllegalArgumentException("Invalid IP Address");
        }

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

                                    Matcher m = ipPattern.matcher(c.getSocket().toString());

                                    if (m.group().equals(ip))
                                    {
                                        newConnection = false;
                                    }
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
                    Logger.getLogger(MetaAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MetaAgent.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        helloThread.start();

    }

    /**
     * The thread running to handle new connections from other chatnodes
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

                    int timeout = 0;

                    while (!newConnection.hasMessage())
                    {
                        timeout++;
                        //waits 25,000,000 [units] (roughly 10 seconds) and then times out
                        if (timeout >= 25000000)
                        {
                            newClientSocket.close();
                        }
                    }

                    //Wait for a PORTAL message
                    final Message receivedMessage = newConnection.receiveMessage();
                    if (nodeMonitor != null)
                    {
                        nodeMonitor.handleMessage(receivedMessage);
                    }

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
                        default:
                            System.err.println("Invalid HELLO message, connection attempt will be dropped.");
                            break;
                    }
                }
                catch (IOException ex)
                {
                    System.err.println("Connection timeout");
                }
            }
        }

    }
    );

    /**
     * Open this portal to receive new connections from the network
     *
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
     *
     * @throws IOException
     */
    @Override
    public void begin() throws IOException
    {
        startPeerReceiver();
        receiveThread.start();
    }

    /**
     * Add a message to the end of the queue
     *
     * @param m the message to be added
     */
    public void enqueue(Message m)
    {
        try
        {
            queue.put(m);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * method to check if this portal has any agent
     *
     * @return a boolean representing if this has agents
     */
    public boolean hasAgents()
    {
        return !agents.isEmpty();
    }

    /**
     * Adds a new agent to this portal
     *
     * @param a The agent to be added to the portal
     */
    public void addAgent(Agent a)
    {
        String agentHandle = a.getHandle();
        synchronized (lock)
        {
            if (agents.containsKey(agentHandle))
            {
                System.err.println("[" + agentHandle + "] is already an established connection.");
                return;
            }
            agents.put(agentHandle, a);
        }
    }

    /**
     * Gets the handles of all agents connected to this portal
     *
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
     *
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
     *
     * @return true if any portals are connected, false otherwise
     */
    public boolean hasPortals()
    {
        return !portals.isEmpty();
    }

    /**
     * Adds a new portal to the portals map
     *
     * @param c the connection information of the portal to add
     */
    private void addPortal(Connection c)
    {
        String connectionHandle = c.getHandle();
        synchronized (lock)
        {
            if (portals.containsKey(connectionHandle))
            {
                System.err.println("[" +connectionHandle + "] is already an established connection.");
                return;
            }
            portals.put(connectionHandle, c);
        }
    }

    /**
     * Returns a list of all the handles of connected portals
     *
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
     *
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
     *
     * @param handle the new handle of the portal
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    /**
     * Connect to another meta-agent
     *
     * @param remoteIpAddress the ip address to connect to
     */
    @Override
    public void connectTo(String remoteIpAddress)
    {
        this.connectTo(remoteIpAddress, DEFAULT_PORT);
    }
}
