package peertopeer;

import java.io.IOException;
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
import java.util.Set;

public class Portal extends ChatNode
{

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
        System.out.println("sending message " + message.toString());
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
                    System.out.println("message is to local agent");
                    agents.get(message.getTo()).sendMessage(message);
                }
                else
                {

                    for (Connection c : peerGroupConnections.values())
                    {
                        c.sendMessage(message);
                    }
                    //System.err.println("'" + receiver + "' is an unknown peer");
                }

            }
        }
    }

    protected final Thread portalReceiveThread = new Thread(
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

                                System.out.println("Portal: " + handle + " has received message");

                                if (agents.containsKey(receivedMessage.getTo()))
                                {
                                    System.out.println("message is to local agent");
                                    sendMessage(receivedMessage);
                                }
                                else
                                {
                                    System.out.println("Agent not present at portal " + handle);

                                    for (Connection c : peerGroupConnections.values())
                                    {
                                        System.out.println("Sending to next portal");
                                        c.sendMessage(receivedMessage);
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

    protected Thread portalAcceptThread = new Thread(
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

                    if (receivedMessage == null)
                    {
                        System.out.println(newConnection.socket.toString());
                    }
                    else
                    {
                        System.out.println("Message received: " + receivedMessage.toString());
                    }

                    if (receivedMessage.isHelloMessage())
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
                    else if (receivedMessage.isAgentsMessage())
                    {
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
                                    System.err.println("Already connected to a agent with name: '" + newConnectionHandle + "'");
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

    private void addAgent(Connection c)
    {
        synchronized (lock)
        {
            if (agents.containsKey(c.getHandle()))
            {
                System.err.println("[" + c.getHandle() + "] is already an established connection.");
                return;
            }
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
            System.out.println("portal initiated");
            portalAcceptThread.start();
        }
    }

    private LinkedList<String> recipients(Set<String> set, List<String> list)
    {
        LinkedList<String> recipients = new LinkedList<>();
        for (String s : list)
        {
            if (set.contains(s))
            {
                recipients.add(s);
            }
        }
        return recipients;
    }

    @Override
    public void begin() throws IOException
    {
        startPeerReceiver();
        portalReceiveThread.start();
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
}
