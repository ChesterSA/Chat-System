/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.*;

/**
 *
 * @author cswan
 */
public class ConsoleDriver extends Driver
{

    static Portal portal;
    static LinkedList<Agent> agents = new LinkedList<Agent>();

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

    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private static void newAgent()
    {
        System.out.println("Agent Handle?");
        System.out.print(">");
        String handle = gets();
        agents.add(new Agent(handle, portal));
    }

    private static void sendMessage()
    {
        System.out.println("Which agent is the message from?");

        for (Agent a : agents)
        {
            System.out.println(a.getHandle());
        }
        String handle = gets();

        Agent from = new Agent();
        for (Agent a : agents)
        {
            if (a.getHandle().equals(handle))
            {
                from = a;
            }
        }

        System.out.println("Who is the message to?");

        for (String s : from.getContacts())
        {
            System.out.println(from);
        }

        String to = gets();

        System.out.println("What is the message content");
        String content = gets();

        from.sendMessage(to, content);

        System.out.println("Message Sent");
    }

    private static void viewAgents()
    {
        System.out.println("Agent List");
        for (Agent a : agents)
        {
            System.out.print(a.getHandle() + " ");
        }
    }

    private static void connectTo()
    {
        System.out.println("What is the IP to connect to?");
        System.out.print(ipBase);
        String ip = ipBase + gets();
        portal.connectTo(ip);
    }

    private static void displayConnections()
    {
        for (String p : portal.getPortalHandles())
        {
            System.out.print(p + " ");
        }
    }
}
