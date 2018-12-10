/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.Agent;
import middleware.ChatNode;
import middleware.Message;
import middleware.MessageType;

/**
 *
 * @author s6089488
 */
public class AgentTest
{
    /**
     * The IP elements all connections inside the system share.
     */
    static String ipBase = "192.168.5.";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
        System.out.println("Agent Handle?");
        String myHandle = gets();

        //0.0.0.0 would be changed to reflect the company's ip
        Agent agent = new Agent(myHandle, "0.0.0.0");

        try
        {
            agent.begin();

            while (true)
            {

                System.out.println("Agent Options:");
                System.out.println("1. New Connection");
                System.out.println("2. Send Message");
                System.out.println("3. Show Portal");
                System.out.println("4. Connect to dir");
                System.out.println("5. Remove Portal");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        newConnection(agent);
                        break;
                    case "2":
                        sendMessage(agent);
                        break;
                    case "3":
                        displayConnectionList(agent);
                        break;
                    case "4":
                        connectToDir(agent);
                        break;
                    case "5":
                        agent.removeConnections();
                        break;
                    default:
                        System.err.println("Invalid option.");
                }
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(AgentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays to the user the steps needed to send a message to a connection
     * in the system.
     * @param me The agent sending the message.
     */
    private static void sendMessage(Agent me)
    {
        System.out.println("Current connections:");
        for (String c : me.getContacts())
        {
            System.out.print(c + " ");
            System.out.println();
        }
        System.out.println("Who would you like to send a message to?");
        final String handle = gets();

        System.out.println("What message would you like to send to " + handle + "?");

        Message newMessage;
        if (handle.equals("all"))
        {
            newMessage = new Message(me.getHandle(), handle, MessageType.BROADCAST);
        }
        else
        {
            newMessage = new Message(me.getHandle(), handle, MessageType.STANDARD);
        }

        newMessage.append(gets());

        //System.out.println("---msg From:" + newMessage.getFrom() + " To:" + newMessage.getTo() + " Content:" + newMessage.getContent());
        me.sendMessage(newMessage);
    }

    /**
     * Displays to the user what is needed to add a new connection to the agent.
     * @param me 
     */
    private static void newConnection(Agent me)
    {
        System.out.println("What is the IP address of the portal to connect to?");
        System.out.print(ipBase);
        String ipAddressOfPeer = gets();
        me.connectTo(ipBase + ipAddressOfPeer);
    }

    /**
     * Displays current portal handle of agent.
     * @param me 
     */
    private static void displayConnectionList(Agent me)
    {
        System.out.println();
        if (me.getPortal() == null)
        {
            System.out.println("No Portal Connected");
        }
        else
        {
            System.out.println("Current Portal handle is: " + me.getPortal());
        }
        System.out.println();
    }

    /**
     * Gets string from user.
     * @return Entered string.
     */
    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /**
     * Connects to current directory inside the system.
     * @param a Agent to connect.
     */
    private static void connectToDir(Agent a)
    {
        a.connectTo("152.105.67.116");
    }
}
