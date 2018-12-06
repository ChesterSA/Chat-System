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
 * @author s6089488
 */
public class StartDirectory
{
    public static void main(String[] args)
    {
        System.out.println("Handle");
        String myHandle = gets();

        // TODO code application logic here
        //Change 0.0.0.0 to a more specific ip address range or
        //specific ip address.
        Directory dir = new Directory(myHandle, "0.0.0.0");

        try
        {
           dir.begin();

            while (true)
            {

                System.out.println("Options:");
                System.out.println("1. Send message to existing connection");
                System.out.println("2. Show connections");
                System.out.println("3. Send dir message");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        sendMessage(dir);
                        break;
                    case "2":
                        displayConnectionList(dir);
                        break;
                    case "3":
                        sendDirMessage(dir);
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

    private static void sendMessage(Directory dir)
    {
        System.out.println("Who would you like to send a message to?");
        final String peerHandle = gets();

        System.out.println("What message would you like to send to " + peerHandle + "?");

        Message newMessage = new Message(dir.getHandle(), peerHandle);

        newMessage.append(gets());

        dir.sendMessage(newMessage);
    }

    private static void displayConnectionList(Directory me)
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

    private static void sendDirMessage(Directory dir)
    {
        System.out.println("Who would you like to send the directory to");
        final String peerHandle = gets();

        Message newMessage = new Message(dir.getHandle(), peerHandle);

        newMessage.append(dir.getAddresses());

        dir.sendMessage(newMessage);
    }
}
