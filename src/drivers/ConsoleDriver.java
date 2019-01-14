/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import external.Client;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.*;

/**
 * A simple driver that uses the console rather than a GUI
 * @author Group B
 */
public class ConsoleDriver extends Driver
{

    /**
     * The portal used on this pc for agents to connect to
     */
    static Portal portal;

    /**
     * The list of clients created on this pc
     */
    static LinkedList<Client> clients = new LinkedList<Client>();

    /**
     * The main driver of the class
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Portal Handle?");
        String myHandle = gets();

        //0.0.0.0 would be changed to reflect the company's ip
        portal = new Portal(myHandle, "0.0.0.0");

        System.out.println("Do you want a NodeMonitor on this portal? (true/false)");

        boolean ifNodeMonitor = Boolean.parseBoolean(gets());
        if (ifNodeMonitor)
        {
            portal.addNodeMonitor();
        }

        try
        {
            portal.begin();


            while (true)
            {
                while (true)
                {
                    System.out.println("Options:");
                    System.out.println("1. Create New Agent");
                    System.out.println("2. Send Message");
                    System.out.println("3. View Agents");
                    System.out.println("4. Connect Portal to External IP");
                    System.out.println("5. Show Portal's connections");
                    System.out.println("> ");
                    final String option = gets();

                    switch (option)
                    {
                        case "1":
                            newAgent();
                            break;
                        case "2":
                            sendMessage();
                            break;
                        case "3":
                            viewAgents();
                            break;
                        case "4":
                            connectTo();
                            break;
                        case "5":
                            displayConnections();
                            break;
                        case "6":
                            removeAgent();
                            break;
                        default:
                            System.err.println("Invalid option.");
                    }
                }
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the last line typed by the user
     *
     * @return the last line typed into the console
     */
    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /**
     * Create a new agent and add to the list
     */
    private static void newAgent()
    {
        System.out.println("Agent Handle?");
        System.out.print(">");
        String handle = gets();
        clients.add(new Client(handle, portal));
    }

    /**
     * Send a message from one agent to another
     */
    private static void sendMessage()
    {
        System.out.println("Which agent is the message from?");

        clients.forEach((c) ->
        {
            System.out.println(c.getAgent().getHandle());
        });
        String handle = gets();

        Client from = new Client();
        for (Client c : clients)
        {
            if (c.getAgent().getHandle().equals(handle))
            {
                from = c;
            }
        }

        System.out.println("Who is the message to?");

        for (String s : from.getAgent().getContacts())
        {
            System.out.println(s);
        }

        String to = gets();

        System.out.println("What is the message content");
        String content = gets();

        from.sendMessage(to, content);

        System.out.println("Message Sent");
    }

    /**
     * View all agents created on this machine
     */
    private static void viewAgents()
    {
        System.out.println("Agent List");
        clients.forEach((c) ->
        {
            System.out.print(c.getAgent().getHandle() + " ");
        });
    }

    /**
     * Connects the current portal to an external IP
     */
    private static void connectTo()
    {
        System.out.println("What is the IP to connect to?");
        System.out.print(ipBase);
        String ip = ipBase + gets();
        portal.connectTo(ip);
    }

    /**
     * Display every portal connected to this one
     */
    private static void displayConnections()
    {
        portal.getPortalHandles().forEach((p) ->
        {
            System.out.print(p + " ");
        });
    }

    /**
     * Delete one of the current agents
     */
    private static void removeAgent()
    {
        System.out.println("Which agent do you want to delete?");

        clients.forEach((c) ->
        {
            System.out.println(c.getAgent().getHandle());
        });
        String handle = gets();

        Client toDelete = new Client();
        for (Client c : clients)
        {
            if (c.getAgent().getHandle().equals(handle))
            {
                toDelete = c;
            }
        }

        toDelete.getAgent().delete();
    }
    
}
