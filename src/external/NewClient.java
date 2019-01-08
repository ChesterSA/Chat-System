/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import middleware.Contactable;
import middleware.Message;
import middleware.NewAgent;
import middleware.NewPortal;

/**
 *
 * @author Group B
 */
public class NewClient implements Contactable
{
    /**
     * The client name, used to set the agent's handle
     */
    String name;
    
    /**
     * The agent that this client has connected to
     */
    NewAgent agent;
    
    /**
     * Set client name and create an attached agent
     * @param name String name of client
     * @param portal The portal to connect to
     */
    public NewClient(String name, NewPortal portal)
    {
        this.name = name;
        agent = new NewAgent(name, portal);
        agent.setClient(this);
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

    /**
     * Send a message from this client to another
     * @param to The name of the client to send the message to
     * @param content the content of the message
     */
    @Override
    public void sendMessage(String to, String content)
    {
        agent.sendMessage(to, content);
    }

    /**
     * @return Current agent connected to client
     */
    public NewAgent getAgent()
    {
        return agent;
    }
   
}
