/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author u0012604
 */
public class PeerToPeerTest
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Handle?");
        String myHandle = gets();

        // TODO code application logic here
        //Change 0.0.0.0 to a more specific ip address range or
        //specific ip address.
        ChatNode thisChatNode = new ChatNode(myHandle, "0.0.0.0");

        try
        {
            thisChatNode.begin();

            while (true)
            {

                System.out.println("Options:");
                System.out.println("1. New Connection");
                System.out.println("2. Send message to existing connection");
                System.out.println("3. Show connections");
                System.out.println("4. Connect to dir");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        newConnection(thisChatNode);
                        break;
                    case "2":
                        sendMessage(thisChatNode);
                        break;
                    case "3":
                        displayConnectionList(thisChatNode);
                        break;
                    case "4":
                        connectToDir(thisChatNode);
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

    private static void newConnection(ChatNode me)
    {
        System.out.println("What is the IP address of the peer to connect to?");
        String ipAddressOfPeer = gets();
        me.connectTo(ipAddressOfPeer);
    }

    private static void sendMessage(ChatNode me)
    {
        System.out.println("Who would you like to send a message to?");
        final String peerHandle = gets();

        System.out.println("What message would you like to send to " + peerHandle + "?");

        Message newMessage = new Message(me.getHandle(), peerHandle);

        newMessage.append(gets());

        me.sendMessage(newMessage);
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
        me.connectTo("152.106.67.116");
    }

}
