/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author u0012604
 */
public class ChatNode
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

    //Messages are sent as a client.
    //
    protected HashMap<String, Connection> peerGroupConnections = new HashMap<>();

    public void removeConnections()
    {
        peerGroupConnections = new HashMap<>();
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

                    System.out.println("Message received: " + receivedMessage.toString());

                    if (!receivedMessage.isHelloMessage())
                    {
                        System.err.println("Malformed peer HELLO message, connection attempt will be dropped.");
                    }

                    else
                    {
                        final String newConnectionHandle = receivedMessage.getFrom();

                        if (newConnectionHandle != null)
                        {
                            synchronized (lock)
                            {

                                if (peerGroupConnections.get(newConnectionHandle) == null)
                                {
                                    //Complete the connection by setting its handle.
                                    //this is essential as we use the handle to send
                                    //messages to our peers.
                                    //
                                    newConnection.setHandle(newConnectionHandle);

                                    //update our register of peer connections
                                    //
                                    addConnection(newConnection);

                                    //The HELLOACK allows the peer to know our handle
                                    //
                                    newConnection.sendMessage(Message.createHelloAckMessage(handle, newConnectionHandle));
                                }
                                else
                                {
                                    System.err.println("Already connected to a peer with name: '" + newConnectionHandle + "'");
                                }
                            }
                        }
                    }
                    // Check for HELLO message with client name.

                }
                catch (IOException ex)
                {
                    Logger.getLogger(ChatNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
    );

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
                    for (Connection connection : peerGroupConnections.values())
                    {
                        try
                        {
                            if (connection.hasMessage())
                            {
                                Message receivedMessage = connection.receiveMessage();
                                System.out.println(receivedMessage);
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
    }
    );

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

    public void begin() throws IOException
    {
        startPeerReceiver();
        receiveThread.start();
    }

    public String getHandle()
    {
        return handle;
    }

    public synchronized boolean hasPeerConnections()
    {
        return peerConnectionCount() > 0;
    }

    public synchronized int peerConnectionCount()
    {
        return peerGroupConnections.size();
    }

    public synchronized List<String> getConnectionHandles()
    {
        List<String> peerGroupHandleList = new ArrayList<>();
        peerGroupConnections.
                values().
                stream().
                forEach(
                        (connection) ->
                {
                    peerGroupHandleList.add(connection.getHandle());
                }
                );

        Collections.sort(peerGroupHandleList);

        return Collections.unmodifiableList(peerGroupHandleList);
    }

    protected void startPeerReceiver() throws UnknownHostException, IOException
    {
        if (serverSocket == null)
        {
            InetAddress bindAddress = InetAddress.getByName(this.receiveIp);
            serverSocket = new ServerSocket(this.receivePort, 0, bindAddress);
            acceptThread.start();
        }
    }

    public void connectTo(final String remoteIpAddress)
    {
        this.connectTo(remoteIpAddress, DEFAULT_PORT);
    }

    public void connectTo(final String remoteIpAddress, final int remotePort)
    {
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
                    partialConnection.sendMessage(Message.createHelloMessage(handle));

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
                        partialConnection.setHandle(receivedMessage.getFrom());
                        addConnection(partialConnection);
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
                                
                                for (Connection c : peerGroupConnections.values())
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

    protected void addConnection(final Connection connection)
    {
        synchronized (lock)
        {
            if (peerGroupConnections.containsKey(connection.getHandle()))
            {
                System.err.println("[" + connection.getHandle() + "] is already an established connection.");
                return;
            }
            peerGroupConnections.put(connection.getHandle(), connection);
        }
    }

    protected synchronized boolean isalreadyConnected(final String ipAddress)
    {
        for (Connection c : peerGroupConnections.values())
        {
            if (c.hasIpAddress(ipAddress))
            {
                return true;
            }
        }

        return false;
    }

    /*
     * @param peer The peer that the message is being sent to 
     * @param message The message to send to all peers
     */
    public void sendMessage(Message message)
    {
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
                final List<String> receivers = message.getTo();

                for (String receiver : receivers)
                {
                    //find the socket of the peer using their handle:
                    Connection peerConnection = peerGroupConnections.get(receiver);

                    if (peerConnection != null)
                    {
                        peerConnection.sendMessage(message);
                    }
                    else
                    {
                        System.err.println("'" + receiver + "' is an unknown peer");
                    }
                }
            }
        }
    }
}
