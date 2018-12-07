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
public class AgentTest
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Agent Handle?");
        String myHandle = gets();

        // TODO code application logic here
        //Change 0.0.0.0 to a more specific ip address range or
        //specific ip address.
        Agent agent = new Agent(myHandle, "0.0.0.0");

        try
        {
            agent.begin();

            while (true)
            {

                System.out.println("Agent Options:");
                System.out.println("1. New Connection");
                System.out.println("2. Send Message");
                System.out.println("3. Show portal");
                System.out.println("4. Connect to dir");
                System.out.println("5. Remove Connections");
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
            Logger.getLogger(PeerToPeerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void sendMessage(Agent me)
    {
        System.out.println("Who would you like to send a message to?");
        final String peerHandle = gets();

        System.out.println("What message would you like to send to " + peerHandle + "?");

        Message newMessage = new Message(me.getHandle(), peerHandle);

        newMessage.append(gets());

        me.sendMessage(newMessage);
    }
    
    private static void newConnection(ChatNode me)
    {
        System.out.println("What is the IP address of the portal to connect to?");
        String ipAddressOfPeer = gets();
        System.out.println("Connecting to " + ipAddressOfPeer);
        me.connectTo(ipAddressOfPeer);
    }

    private static void displayConnectionList(ChatNode me)
    {
        if (!me.hasPeerConnections())
        {
            System.out.println("\n* No peers connected *\n");
            return;
        }

        System.out.println(
                String.format(
                        "Connected peer handles\n\n%s\n\n",
                        String.join(", ", me.getConnectionHandles())
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
}
