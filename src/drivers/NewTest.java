/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import static drivers.AgentTest.agent;
import static drivers.PortalTest.portal;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.*;

/**
 *
 * @author cswan
 */
public class NewTest
{

    static NewPortal portal;
    static LinkedList<NewAgent> agents = new LinkedList<NewAgent>();

    public static void main(String[] args)
    {
        System.out.println("Portal Handle?");
        String myHandle = gets();

        //0.0.0.0 would be changed to reflect the company's ip
        portal = new NewPortal(myHandle, "0.0.0.0");

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
                        default:
                            System.err.println("Invalid option.");
                    }
                }
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(PortalTest.class.getName()).log(Level.SEVERE, null, ex);
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
        agents.add(new NewAgent(handle, portal));
    }

    private static void sendMessage()
    {
        System.out.println("Which agent is the message from?");

        for (NewAgent a : agents)
        {
            System.out.println(a.getHandle());
        }
        String handle = gets();

        NewAgent from = new NewAgent();
        for (NewAgent a : agents)
        {
            if (a.getHandle().equals(handle))
            {
                from = a;
            }
        }
            
        System.out.println("Who is the message to?");

        for(String s : from.getContacts())
            System.out.println(from);
        
        String to = gets();
        
        System.out.println("What is the message content");
        String content = gets();
        
        from.sendMessage(from.getHandle(), to, content);
        
        System.out.println("Message Sent");
    }

    private static void viewAgents()
    {
        System.out.println("Agent List");
        for (NewAgent a : agents)
        {
            System.out.print(a.getHandle() + " ");
        }
    }
}
