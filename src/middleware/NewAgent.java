/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.util.LinkedList;

/**
 *
 * @author cswan
 */
public class NewAgent
{
    String handle;
    NewPortal portal;
    Contactable client;
    
    /**
     * A list of the handles of agents that have previously contacted this
     * portal
     */
    LinkedList<String> contacts = new LinkedList<>();
    
    public NewAgent(String handle, NewPortal portal)
    {
        this.handle = handle;
        this.portal = portal;
        portal.addAgent(this);
    }
    
    public void sendMessage(String to, String content)
    {
        Message m = new Message("handle", to, MessageType.STANDARD);
        m.append(content);
        
        portal.enqueue(m);
        portal.sendMessage();
    }
    
    public void receiveMessage(Message m)
    {
        System.out.println(handle + " has received a message");
        System.out.println("To: " + m.getTo());
        System.out.println("From: " + m.getFrom());
        System.out.println("Content: " + m.getContent());
        
        if (client != null)
        {
            client.handleMessage(m);
        }
        
        if (!contacts.contains(m.getFrom()))
        {
            contacts.add(m.getFrom());
        }
    }

    public String getHandle()
    {
        return handle;
    }
    
    public void setClient(Contactable c)
    {
        client = c;
    }
    
}

