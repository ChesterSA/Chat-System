package drivers;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.Agent;
import middleware.Message;
import middleware.MessageType;

/**
 * Driver to create an agent class for testing
 * @author Group B
 */
public class AgentTest extends Driver
{
    static Agent a;
    /**
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        
        System.out.println("Agent Handle?");
        String myHandle = gets();

        //0.0.0.0 would be changed to reflect the company's ip
        a = new Agent(myHandle, "0.0.0.0");

        try
        {
            a.begin();

            while (true)
            {
                System.out.println("Agent Options:");
                System.out.println("1. New Connection");
                System.out.println("2. Send Message");
                System.out.println("3. Show Portal");
                System.out.println("4. Remove Portal");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        newConnection();
                        break;
                    case "2":
                        sendMessage();
                        break;
                    case "3":
                        displayConnectionList();
                        break;
                    case "4":
                        a.removeConnections();
                        break;
                    default:
                        System.err.println("Invalid option.");
                }
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(AgentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays to the user the steps needed to send a message to a connection
     * in the system.
     * @param me The agent sending the message.
     */
    private static void sendMessage()
    {
        System.out.println("Current connections:");
        for (String c : a.getContacts())
        {
            System.out.print(c + " ");
            System.out.println();
        }
        System.out.println("Who would you like to send a message to?");
        final String handle = gets();

        System.out.println("What message would you like to send to " + handle + "?");

        Message newMessage;
        if (handle.equals("all"))
        {
            newMessage = new Message(a.getHandle(), handle, MessageType.BROADCAST);
        }
        else
        {
            newMessage = new Message(a.getHandle(), handle, MessageType.STANDARD);
        }

        newMessage.append(gets());

        a.sendMessage(newMessage);
    }

    /**
     * Displays to the user what is needed to add a new connection to the agent.
     * @param me Agent to be added to.
     */
    private static void newConnection()
    {
        System.out.println("What is the IP address of the portal to connect to?");
        System.out.print(ipBase);
        String ipAddressOfPeer = gets();
        a.connectTo(ipBase + ipAddressOfPeer);
    }

    /**
     * Displays current portal handle of agent.
     * @param me Agent to be searched.
     */
    private static void displayConnectionList()
    {
        System.out.println();
        if (a.getPortal() == null)
        {
            System.out.println("No Portal Connected");
        }
        else
        {
            System.out.println("Current Portal handle is: " + a.getPortal());
        }
        System.out.println();
    }

    /**
     * Gets string from user.
     * @return Entered string.
     */
    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

}
