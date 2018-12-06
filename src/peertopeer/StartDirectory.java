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
                System.out.println("1. Show connections");
                System.out.println("2. Remove connections");
                System.out.println("> ");
                final String option = gets();

                switch (option)
                {
                    case "1":
                        displayConnectionList(dir);
                        break;
                    case "2":
                        dir.removeConnections();
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
}
