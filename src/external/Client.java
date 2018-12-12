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
 * @author s6089488
 */
public class Client implements Contactable
{
    String name;
    Agent agent;
    
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
    
    @Override
    public void connectTo(String ip)
    {
        agent.connectTo(ip);
    }

    public Agent getAgent()
    {
        return agent;
    }

}
