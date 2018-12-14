/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import external.Client;
import java.util.Scanner;


/**
 *
 * @author s6089488
 */
public class ClientTest extends Driver
{
    static Client client;

    /**
     *
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("Client Handle?");
        String myHandle = gets();

        //0.0.0.0 would be changed to reflect the company's ip
        client = new Client(myHandle);
        boolean connected = false;

        while (true)
        {
            System.out.println("Client Options:");
            System.out.println("1. Connect to a portal");
            if (connected)
            {
                System.out.println("2. Send Message");
                System.out.println("3. Display Contacts");
            }

            System.out.println("> ");
            final String option = gets();

            switch (option)
            {
                case "1":
                    connectTo();
                    connected = true;
                    break;
                case "2":
                    sendMessage();
                    break;
                case "3":
                    displayContacts();
                    break;

                default:
                    System.err.println("Invalid option.");
            }
        }

    }

    /**
     * Gets string from user.
     *
     * @return Entered string.
     */
    private static String gets()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private static void sendMessage()
    {
        displayContacts();
        System.out.println();
        
        System.out.print("Who is the message to: ");
        String to = gets();

        System.out.print("What is the message content: ");
        String content = gets();

        client.sendMessage(to, content);
    }

    private static void displayContacts()
    {
        System.out.println("Currently connected to: ");
        for (String s : client.getAgent().getContacts())
        {
            System.out.print(s + " ");
        }
    }

    private static void connectTo()
    {
        System.out.print("What is the ip to connect to: " + ipBase);
        String ip = gets();
        client.getAgent().connectTo(ipBase + ip);
    }
}
