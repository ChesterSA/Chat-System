/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6089488
 */
public class PortalTest
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Portal Handle?");
        String myHandle = gets();

        // TODO code application logic here
        //Change 0.0.0.0 to a more specific ip address range or
        //specific ip address.
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

    private static void newConnection(Portal me)
    {
        System.out.println("What is the IP address of the peer to connect to?");
        String ipAddressOfPeer = gets();
        me.connectTo(ipAddressOfPeer);
    }

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

    private static void removeAgent(Portal me)
    {
        System.out.println("What is the handle of the agent to remove?");
        String handle = gets();
        me.removeAgent(handle);
    }

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

    private static void removePortal(Portal me)
    {
        System.out.println("What is the handle of the portal to remove?");
        String handle = gets();
        me.removePortal(handle);
    }

    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private static void connectToDir(ChatNode me)
    {
        me.connectTo("152.105.67.116");
    }

    private static void sendMessage(Portal me)
    {
        System.out.println("Who would you like to send a message to?");
        final String peerHandle = gets();

        System.out.println("What message would you like to send to " + peerHandle + "?");

        Message newMessage = new Message(me.getHandle(), peerHandle, MessageType.STANDARD);

        newMessage.append(gets());

        System.out.println("---msg From:" + newMessage.getFrom() + " To:" + newMessage.getTo() + " Content:" + newMessage.getContent());

        me.sendMessage(newMessage);
    }

}
