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
import middleware.ChatNode;
import middleware.Message;
import middleware.MessageType;
import middleware.Portal;

/**
 * Driver to create a portal class for testing
 * @author s6089488
 */
public class PortalTest
{
    /**
     * The first three groups of the ip, used to remove some user effort
     */
    static String ipBase = "152.105.67.";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Portal Handle?");
        String myHandle = gets();

        //0.0.0.0 would be changed to reflect the company's ip
        Portal portal = new Portal(myHandle, "0.0.0.0");

        try
        {
            portal.begin();

            while (true)
            {

                System.out.println("Portal Options:");
                System.out.println("1. New Connection");
                System.out.println("2. Manage agents");
                System.out.println("3. Manage portals");
                System.out.println("4. Remove all connections");
                System.out.println("5. Connect to dir");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        newConnection(portal);
                        break;
                    case "2":
                        manageAgents(portal);
                        break;
                    case "3":
                        managePortals(portal);
                        break;
                    case "4":
                        portal.removeConnections();
                        System.out.println("All connections removed");
                    case "5":
                        connectToDir(portal);
                        break;
                    default:
                        System.err.println("Invalid option.");
                }
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(PortalTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the ip to connect the portal to, and then call the connectTo method 
     * @param me the portal being used
     */
    private static void newConnection(Portal me)
    {
        System.out.println("What is the IP address of the peer to connect to?");
        System.out.print(ipBase);
        String ipAddressOfPeer = gets();
        me.connectTo(ipBase + ipAddressOfPeer);
    }

    /**
     * Method for managing agent details
     * @param me 
     */
    private static void manageAgents(Portal me)
    {
        System.out.println("Agent Management:");
        System.out.println("1. Show agents");
        System.out.println("2. Remove all agents");
        System.out.println("3. Remove specified agent");
        System.out.println("> ");
        final String option = gets();

        switch (option)
        {
            case "1":
                displayAgentList(me);
                break;
            case "2":
                me.removeAgents();
                System.out.println("All agents removed");
                break;
            case "3":
                removeAgent(me);
                break;
            default:
                System.err.println("Invalid option.");
        }
    }

    /**
     * Displays a list of agents connected to the portal
     * @param me the portal whose agents are to be displayed
     */
    private static void displayAgentList(Portal me)
    {
        System.out.println("\nAmount of agents = " + me.getAgentHandles().size());

        System.out.println(
                String.format(
                        "Connected agents handles\n%s\n",
                        String.join(", ", me.getAgentHandles())
                )
        );
    }

    /**
     * Removes a specified agent from the portal
     * @param me the portal where the agent is connected
     */
    private static void removeAgent(Portal me)
    {
        System.out.println("What is the handle of the agent to remove?");
        String handle = gets();
        me.removeAgent(handle);
    }

    /**
     * Method used to handle different portal based actions
     * @param me the portal to be managed
     */
    private static void managePortals(Portal me)
    {
        System.out.println("Portal Management:");
        System.out.println("1. Show portals");
        System.out.println("2. Remove all portals");
        System.out.println("3. Remove specified portal");
        System.out.println("> ");
        final String option = gets();

        switch (option)
        {
            case "1":
                displayPortalList(me);
                break;
            case "2":
                me.removePortals();
                System.out.println("All portals removed");
                break;
            case "3":
                removePortal(me);
                break;
            default:
                System.err.println("Invalid option.");
        }
    }

    /**
     * Displays a list of portals connected to the portal
     * @param me the portal whose portals are to be displayed
     */
    private static void displayPortalList(Portal me)
    {

        System.out.println("\nAmount of portals = " + me.getPortalHandles().size());

        System.out.println(
                String.format(
                        "Connected portals handles\n%s\n",
                        String.join(", ", me.getPortalHandles())
                )
        );
    }

    /**
     * Removes a specified agent from the portal
     * @param me the portal where the agent is connected
     */
    private static void removePortal(Portal me)
    {
        System.out.println("What is the handle of the portal to remove?");
        String handle = gets();
        me.removePortal(handle);
    }

    /**
     * gets the last value the user typed in
     * @return a string containing the last value the user typed in
     */
    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /**
     * Connects to a hardcoded directory, needs to be updated for each directory
     * Currently mostly in for testing ease, there may be issues in deployment
     * @param p the Portal to be connected to the directory
     */
    private static void connectToDir(Portal p)
    {
        p.connectTo("152.105.67.116");
    }

}
