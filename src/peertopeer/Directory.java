/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author s6089488
 */
public class Directory extends ChatNode
{
    
    //Messages are sent as a client.
    //
    
    private LinkedList<String> ipAddresses = new LinkedList<>();
    
    public String getAddresses()
    {
        String output = "";
        for(Connection c : peerGroupConnections.values())
        {
            System.out.println(c.socket.toString().substring(13,27));
            
            output += c.socket.toString().substring(13,27) + ", ";
        }
        return output;
    }

    public Directory(String handle)
    {
        super(handle);
    }

    public Directory(String handle, String receiveIp)
    {
        super(handle, receiveIp);
    }
    
    
    private void updateAllConnections()
    {
    }
    

    public void sendDirMessage(Message message)
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
 