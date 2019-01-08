package drivers;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.Portal;

/**
 * Driver to create a portal class for testing
 * @author Group B
 */
public class PortalTest extends Driver
{ 
    static Portal portal;
    /**
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
        if(ifNodeMonitor)
        {  
            portal.addNodeMonitor();
        }

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
                        newConnection();
                        break;
                    case "2":
                        manageAgents();
                        break;
                    case "3":
                        managePortals();
                        break;
                    case "4":
                        portal.removeConnections();
                        System.out.println("All connections removed");
                    case "5":
                        connectToDir();
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
    private static void newConnection()
    {
        System.out.println("What is the IP address of the peer to connect to?");
        System.out.print(ipBase);
        String ipAddressOfPeer = gets();
        portal.connectTo(ipBase + ipAddressOfPeer);
    }

    /**
     * Method for managing agent details
     * @param me 
     */
    private static void manageAgents()
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
                displayAgentList();
                break;
            case "2":
                portal.removeAgents();
                System.out.println("All agents removed");
                break;
            case "3":
                removeAgent();
                break;
            default:
                System.err.println("Invalid option.");
        }
    }

    /**
     * Displays a list of agents connected to the portal
     * @param me the portal whose agents are to be displayed
     */
    private static void displayAgentList()
    {
        System.out.println("\nAmount of agents = " + portal.getAgentHandles().size());

        System.out.println(String.format("Connected agents handles\n%s\n",
                        String.join(", ", portal.getAgentHandles())
                )
        );
    }

    /**
     * Removes a specified agent from the portal
     * @param me the portal where the agent is connected
     */
    private static void removeAgent()
    {
        System.out.println("What is the handle of the agent to remove?");
        String handle = gets();
        portal.removeAgent(handle);
    }

    /**
     * Method used to handle different portal based actions
     * @param me the portal to be managed
     */
    private static void managePortals()
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
                displayPortalList();
            case "2":
                portal.removePortals();
                System.out.println("All portals removed");
                break;
            case "3":
                removePortal();
                break;
            default:
                System.err.println("Invalid option.");
        }
    }

    /**
     * Displays a list of portals connected to the portal
     * @param me the portal whose portals are to be displayed
     */
    private static void displayPortalList()
    {

        System.out.println("\nAmount of portals = " + portal.getPortalHandles().size());

        System.out.println(String.format("Connected portals handles\n%s\n",
                        String.join(", ", portal.getPortalHandles())
                )
        );
    }

    /**
     * Removes a specified agent from the portal
     * @param me the portal where the agent is connected
     */
    private static void removePortal()
    {
        System.out.println("What is the handle of the portal to remove?");
        String handle = gets();
        portal.removePortal(handle);
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
    private static void connectToDir()
    {
        portal.connectTo(dir);
    }

}
