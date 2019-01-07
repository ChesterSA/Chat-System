/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.*;

/**
 *
 * @author cswan
 */
public class NewTest
{

    public static void main(String[] args)
    {
        NewPortal p = new NewPortal("p", "0.0.0.0");
        System.out.println("portal created");
        
        NewAgent a1 = new NewAgent("agentone", p);
        NewAgent a2 = new NewAgent("agenttwo", p);
        System.out.println("agent created");
        
        try
        {
            System.out.println("beginning portal");
            p.begin();
            System.out.println("portal begun");
            a1.sendMessage("agenttwo", "hello");
            System.out.println("message sent");
        }
        catch (IOException ex)
        {
            System.out.println("Exception");
            Logger.getLogger(PortalTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
