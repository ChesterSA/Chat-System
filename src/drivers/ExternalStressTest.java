/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import external.Client;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.Portal;

/**
 * Tests the infrastructure with a high Client count
 * @author Group B
 */
public class ExternalStressTest 
{

    /**
     * The portal used on this pc for clients to connect to
     */
    static Portal portal;

    /**
     * The list of clients created on this pc
     */
    static LinkedList<Client> clients = new LinkedList<Client>();

    /**
     * The main driver of the class
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //0.0.0.0 would be changed to reflect the company's ip
        portal = new Portal("p", "0.0.0.0");
        
        
        for (int i = 0; i < 100000; i++) 
        {
            clients.add(new Client("client" + i, portal));
        }
        
        System.out.println("clients made");
        
        try 
        {
            portal.begin();
            for (int i = 0; i < clients.size(); i++)
            {
                Client sender = clients.get(i);
                String receiver = clients.get(clients.size() - i - 1).getName();
                sender.sendMessage(receiver, "message " + i);
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
