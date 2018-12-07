/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.util.List;

/**
 *
 * @author v8269590
 */
public class Agent extends ChatNode {
    
    ChatNode portal;
    
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
                    portal.sendMessage(message);
                }
            }
        }
    }
    
}
