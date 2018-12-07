/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;

/**
 *
 * @author v8269590
 */
public class Agent extends ChatNode {
    
    Pair<String, Connection> portal;
    
    public Agent(String handle) {
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
                if(portal != null)
                {
                    portal.getValue().sendMessage(message);
                }
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
        Thread helloAgentThread = new Thread(
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
                    partialConnection.sendMessage(Message.createAgentMessage(handle));

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

        helloAgentThread.start();

    }
    
    @Override
    protected void addConnection(final Connection connection)
    {
        synchronized (lock)
        {
            portal = new Pair<>(connection.getHandle(), connection);
        }
    }
    
}
