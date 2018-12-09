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
 *
 * @author v8269590
 */
public class Agent extends ChatNode
{

    Pair<String, Connection> portal;
    LinkedList<String> contacts = new LinkedList<>();

    public Agent(String handle)
    {
        super(handle);
    }

    public Agent(String handle, String receiveIp)
    {
        super(handle, receiveIp);
    }

    public Agent(String handle, String receiveIp, int receivePort)
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
        //System.out.println("---Agent is sending message");
        synchronized (lock)
        {
            //System.out.println("---Message has a set receiver");
            if (portal != null)
            {
                //System.out.println("---Portal: " + portal.getKey() + " - " + portal.getValue() + " is handling message");
                portal.getValue().sendMessage(message);
            }
            else
            {
                System.out.println("Portal is null");
            }
        }
    }

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
                        if (portal != null)
                        {
                            sendMessage(new Message(handle, portal.getKey(), MessageType.AGENTREMOVE));
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

            }
        }
        );

        helloThread.start();

    }

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
                            System.out.println(m);

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
                                newConnection.sendMessage(new Message(handle, newConnectionHandle, MessageType.HELLOACK));
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

    protected void addConnection(final Connection connection)
    {
        synchronized (lock)
        {
            portal = new Pair<>(connection.getHandle(), connection);
            //System.out.println("---Connected to portal " + portal.getKey() + " ... " + portal.getValue());
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

    @Override
    public void removeConnections()
    {
        portal = null;
    }

    @Override
    public void begin() throws IOException
    {
        startPeerReceiver();
        receiveThread.start();
    }

    public LinkedList<String> getContacts()
    {
        return contacts;
    }
    
    public void setHandle(String handle)
    {
        this.handle = handle;
    }
}
