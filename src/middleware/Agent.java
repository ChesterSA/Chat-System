/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 * End point in message transmission, all messages pass through a portal then to
 * an agent
 *
 * @author Group B
 */
public class Agent extends ChatNode implements Connectable
{

    Contactable client;

    /**
     * The Portal this agent is currently connected to
     */
    Pair<String, Connection> portal;

    /**
     * A list of the handles of agents that have previously contacted this
     * portal
     */
    LinkedList<String> contacts = new LinkedList<>();

    /**
     * Calls ChatNode constructor with a handle
     *
     * @param handle
     */
    public Agent(String handle)
    {
        this(handle, DEFAULT_RECV_IP_ADDRESS, DEFAULT_PORT);
    }

    /**
     * Calls ChatNode constructor with a handle and an ip
     *
     * @param handle
     * @param receiveIp
     * @param c
     */
    public Agent(String handle, String receiveIp)
    {
        this(handle, receiveIp, DEFAULT_PORT);
    }

    /**
     * Calls ChatNode constructor with a handle, ip, and port
     *
     * @param handle
     * @param receiveIp
     * @param receivePort
     * @param c
     */
    public Agent(String handle, String receiveIp, int receivePort)
    {
        super(handle, receiveIp, receivePort);
    }

    /**
     * Calls ChatNode constructor with a handle
     *
     * @param handle
     * @param c
     */
    public Agent(String handle, Contactable c)
    {
        this(handle, DEFAULT_RECV_IP_ADDRESS, DEFAULT_PORT, c);
    }

    /**
     * Calls ChatNode constructor with a handle and an ip
     *
     * @param handle
     * @param receiveIp
     * @param c
     */
    public Agent(String handle, String receiveIp, Contactable c)
    {
        this(handle, receiveIp, DEFAULT_PORT, c);
    }

    /**
     * Calls ChatNode constructor with a handle, ip, and port
     *
     * @param handle
     * @param receiveIp
     * @param receivePort
     * @param c
     */
    public Agent(String handle, String receiveIp, int receivePort, Contactable c)
    {
        super(handle, receiveIp, receivePort);
        client = c;
    }

    /**
     * Passes message to an agents portal. If portal is null print to output.
     *
     * @param message object containing message attributes.
     */
    //@Override
    public void sendMessage(Message message)
    {
        synchronized (lock)
        {
            if (portal != null)
            {
                portal.getValue().sendMessage(message);
            }
        }
    }

    /**
     * Connect this agent to a portal with a default ip
     *
     * @param remoteIpAddress
     */
    @Override
    public void connectTo(String remoteIpAddress)
    {
        this.connectTo(remoteIpAddress, DEFAULT_PORT);
    }

    /**
     * Connect to a portal through ip. If hello acknowledge message received
     * from portal, add connection.
     *
     * @param remoteIpAddress
     * @param remotePort
     */
    @Override
    public void connectTo(final String remoteIpAddress, final int remotePort)
    {
        if (!ChatNode.checkIp(handle))
        {
            throw new IllegalArgumentException("Invalid IP Address");
        }
        // check if we're already connected, perhaps the remote device
        // instigated a connection previously.
        if (isalreadyConnected(remoteIpAddress))
        {
            //System.err.println(String.format("Already connected to the peer with IP: '%s'", remoteIpAddress));
            return;
        }

        //Create a thread to instigate the HELLO handshake between this peer
        //and the remote peer
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
                    Thread.sleep(60000);
                    partialConnection.sendMessage(new Message(handle, MessageType.AGENT));

                    //Wait for a response from this connection.
                    while (!partialConnection.hasMessage())
                    {
                        // ... Do nothing ...
                        // assumes it will eventually connect... probably not a good idea...
                    }

                    //We should have a HELLOACK message, which will have
                    //the handle of the remote peer
                    final Message receivedMessage = partialConnection.receiveMessage();

                    if (receivedMessage.getType().equals(MessageType.HELLOACK))
                    {
                        partialConnection.setHandle(receivedMessage.getFrom());

                        //As you're changing portal, notify old portal to remove you
                        if (portal != null)
                        {
                            sendMessage(new Message(handle, portal.getKey(), MessageType.AGENT));
                        }
                        if (client != null)
                        {
                            client.handleMessage(receivedMessage);
                        }
                        addConnection(partialConnection);
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
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        helloThread.start();

    }

    /**
     * The thread running to receive messages from other portals and agents
     */
    private final Thread receiveThread = new Thread(
            new Runnable()
    {
        @Override
        public void run()
        {
            while (true)
            {
                synchronized (lock)
                {
                    try
                    {
                        if (portal != null && portal.getValue().hasMessage())
                        {
                            Message m = portal.getValue().receiveMessage();
                            String from = m.getFrom();
                            if (!contacts.contains(from) && !from.equals(handle))
                            {
                                contacts.add(from);
                            }

                            //Only display message if it is standard or broadcast type
                            if (m.getType().equals(MessageType.STANDARD) || m.getType().equals(MessageType.BROADCAST))
                            {
                                //System.out.println(m);
                                if (client != null)
                                {
                                    client.handleMessage(m);
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(ChatNode.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    }
    );

    /**
     * Thread running to accept new connections from portals and chatnodes
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

                    //System.out.println("");
                    //Awaiting HELLO message from new connection
                    while (!newConnection.hasMessage())
                    {
                        // wait for a message from the new connection...
                        // should probably handle timeouts...
                    }

                    //At this point in the connection process, only a HELLO message
                    //will do, anything else will be ignored.
                    final Message receivedMessage = newConnection.receiveMessage();

                    //System.out.println("Message received: " + receivedMessage.toString());
                    if (!receivedMessage.getType().equals(MessageType.HELLO))
                    {
                        System.err.println("Invalid message type, connection attempt will be dropped.");
                    }
                    else
                    {
                        final String newConnectionHandle = receivedMessage.getFrom();

                        if (newConnectionHandle != null)
                        {
                            synchronized (lock)
                            {

                                //Set the connection handle so it can be used
                                newConnection.setHandle(newConnectionHandle);

                                //Update our register of connections
                                addConnection(newConnection);

                                //The HELLOACK allows the peer to know our handle
                                newConnection.sendMessage(new Message(handle, newConnectionHandle, MessageType.HELLOACK));
                            }
                        }
                    }

                }
                catch (IOException ex)
                {
                    Logger.getLogger(ChatNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
    );

    /**
     * set agents portal connection.
     *
     * @param connection
     */
    protected void addConnection(final Connection connection)
    {
        synchronized (lock)
        {
            portal = new Pair<>(connection.getHandle(), connection);
        }
    }

    /**
     * set serverSocket and ip to allow messages to be received.
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
            //Socket s = new Socket(bindAddress, receivePort)
            acceptThread.start();
        }
    }

    /**
     * get and agents portal.
     *
     * @return portal object.
     */
    public String getPortal()
    {
        if (portal != null)
        {
            return portal.getKey();
        }
        else
        {
            return null;
        }
    }

    /**
     * set the ip connections to null  this loses the ip and no longer can they connect to the them 
     * remove an agents portal.
     */
    @Override
    public void removeConnections()
    {
        portal = null;
    }

    /**
     * Starts accept and receive thread.
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
     * Returns portal handle.
     *
     * @return
     */
    public LinkedList<String> getContacts()
    {
        return contacts;
    }

    /**
     * Set the handle of an agent.
     *
     * @param handle
     */
    public void setHandle(String handle)
    {
        this.handle = handle;
    }
}
