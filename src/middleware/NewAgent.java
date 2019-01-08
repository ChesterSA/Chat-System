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
    /**
     * The handle of the agent, used for identification
     */
    String handle;
    
    /**
     * The portal that the agent is connected to
     */
    NewPortal portal;
    
    /**
     * the client that is using the agent
     */
    Contactable client;
    
    /**
     * A list of the handles of agents that have previously contacted this
     * portal
     */
    LinkedList<String> contacts = new LinkedList<>();
    
    /**
     *  A default constructor, essentially makes a null Agent
     */
    public NewAgent()
    {
        handle = null;
        portal = null;
    }
    
    /**
     * The standard constructor, gives the agent a handle and a portal to connect to
     * @param handle the identifying handle of the agent
     * @param portal the portal that the agent is connected to
     */
    public NewAgent(String handle, NewPortal portal)
    {
        this.handle = handle;
        this.portal = portal;
        portal.addAgent(this);
    }
    
    /**
     * sends a standard message from this agent to another
     * @param to the handle of the agent to send a message to
     * @param content the content of the message
     */
    public void sendMessage(String to, String content)
    {
        Message m = new Message("handle", to, MessageType.STANDARD);
        m.append(content);
        
        portal.enqueue(m);
        portal.sendMessage();
    }
    
    /**
     * This agent handles a message, then adds the sender to a contact list
     * @param m the message to receive
     */
    public void receiveMessage(Message m)
    {
        System.out.println("\t" + handle + " has received a message");
        System.out.println("\tTo: " + m.getTo());
        System.out.println("\tFrom: " + m.getFrom());
        System.out.println("\tContent: " + m.getContent());
        
        if (client != null)
        {
            client.handleMessage(m);
        }
        
        if (!contacts.contains(m.getFrom()))
        {
            contacts.add(m.getFrom());
        }
    }

    /**
     * returns the handle
     * @return the handle of the agent
     */
    public String getHandle()
    {
        return handle;
    }
    
    /**
     * Sets the client for this agent
     * @param c the client to connect to this agent
     */
    public void setClient(Contactable c)
    {
        client = c;
    }

    /**
     * gets the contact list
     * @return the list of all agents that have contacted this portal
     */
    public LinkedList<String> getContacts()
    {
        return contacts;
    }
}

