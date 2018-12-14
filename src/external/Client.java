/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.Agent;
import middleware.Contactable;
import middleware.Message;

/**
 *
 * @author Group B
 */
public class Client implements Contactable
{
    String name;
    Agent agent;
    
    /**
     * Set client name
     * New agent object
     * Start agent
     * @param name String name of client
     */
    public Client(String name)
    {
        this.name = name;
        agent = new Agent(name, this);
        try
        {
            agent.begin();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Output formatted received message 
     * @param m message received
     */
    @Override
    public void handleMessage(Message m)
    {
        System.out.println("Client has message");
        System.out.println("To: " + m.getTo());
        System.out.println("From: " + m.getFrom());
        System.out.println("Type: " + m.getType());
        System.out.println("Content: " + m.getContent());
        System.out.println();
    }

    @Override
    public void sendMessage(String to, String content)
    {
        Message m = new Message(agent.getHandle(), to);
        m.append(content);
        agent.sendMessage(m);
    }
    
    /**
     * Set new agent connection
     * @param ip String passed to connectTo()
     */
    @Override
    public void connectTo(String ip)
    {
        agent.connectTo(ip);
    }

    /**
     * @return Current agent connected to client
     */
    public Agent getAgent()
    {
        return agent;
    }

}
