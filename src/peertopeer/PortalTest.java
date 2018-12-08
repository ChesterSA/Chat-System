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
                System.out.println("2. Show portals");
                System.out.println("3. Show agents");
                System.out.println("4. Remove portals");
                System.out.println("5. Remove agents");
                System.out.println("6. Connect to dir");
                System.out.println("7. Send message");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        newConnection(portal);
                        break;
                    case "2":
                        displayPortalList(portal);
                        break;
                    case "3":
                        displayAgentList(portal);
                        break;
                    case "4":
                        portal.removePortals();
                        break;
                    case "5":
                        portal.removeAgents();
                        break;
                    case "6":
                        connectToDir(portal);
                        break;
                    case "7":
                        sendMessage(portal);
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

        Message newMessage = new Message(me.getHandle(), peerHandle);

        newMessage.append(gets());

        System.out.println("---msg From:" + newMessage.getFrom() + " To:" + newMessage.getTo() + " Content:" + newMessage.getContent());

        me.sendMessage(newMessage);
    }
}
