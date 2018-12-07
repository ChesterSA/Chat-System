package peertopeer;

import java.io.IOException;
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
                    Connection peer;

                    //Check if reciever is in local agents list
                    if (agents.containsKey(receiver))
                    {
                        peer = agents.get(receiver);
                        peer.sendMessage(message);
                    }
                    else if (peerGroupConnections.containsKey(receiver))
                    {
                        //find the socket of the peer using their handle:
                        peer = peerGroupConnections.get(receiver);
                        peer.sendMessage(message);
                    }
                    else
                    {
                        System.err.println("'" + receiver + "' is an unknown peer");
                    }

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
                                LinkedList<String> receivers = recipients(agents.keySet(), receivedMessage.getTo());
                                if (receivers.size() > 0)
                                {
                                    Connection peer;
                                    for(String s : receivers)
                                    {
                                        peer = agents.get(s);
                                        peer.sendMessage(receivedMessage);
                                    }
                                }
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
    });
    
    private LinkedList<String> recipients(Set<String> set, List<String> list)
    {
        LinkedList<String> recipients = new LinkedList<>();
        for(String s : list)
        {
            if (set.contains(s))
                recipients.add(s);
        }
        return recipients;
    }
    
    
    @Override
    public void begin() throws IOException
    {
        startPeerReceiver();
        portalReceiveThread.start();
    }        
}
