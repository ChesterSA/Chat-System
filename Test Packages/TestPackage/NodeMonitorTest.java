/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.NewAgent;
import middleware.NewPortal;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
/**
 *
 * @author s6089488
 */
public class NodeMonitorTest
{

    NewPortal p;

    @Test
    public void senderLogTest()
    {
        NewPortal p = new NewPortal("portal");
        p.addNodeMonitor();

        NewAgent a1 = new NewAgent("agentone", p);
        a1.addNodeMonitor();

        NewAgent a2 = new NewAgent("agenttwo", p);
        a2.addNodeMonitor();

        a1.sendMessage("agenttwo", "test message");
        a2.sendMessage("agentone", "response message");

        String expected = "From: agenttwo\tTo: agentone\t\tType: STANDARD\t\tContent: response message";

        String file = a1.getHandle() + "-LOG.txt";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            String actual = "";
            while ((line = br.readLine()) != null)
            {
                actual = line;
            }
            actual = actual.substring(16);
            
            assertEquals(expected, actual);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(NodeMonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(NodeMonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Test
    public void portalLogTest()
    {
        NewPortal p = new NewPortal("portal");
        p.addNodeMonitor();

        NewAgent a1 = new NewAgent("agentone", p);
        a1.addNodeMonitor();

        NewAgent a2 = new NewAgent("agenttwo", p);
        a2.addNodeMonitor();

        a1.sendMessage("agenttwo", "test message");
        a2.sendMessage("agentone", "response message");

        String expected = "From: agenttwo\tTo: agentone\t\tType: STANDARD\t\tContent: response message";

        String file = p.getHandle() + "-LOG.txt";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            String actual = "";
            while ((line = br.readLine()) != null)
            {
                actual = line;
            }
            actual = actual.substring(16);
            
            assertEquals(expected, actual);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(NodeMonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(NodeMonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Test
    public void receiverLogTest()
    {
        NewPortal p = new NewPortal("portal");
        p.addNodeMonitor();

        NewAgent a1 = new NewAgent("agentone", p);
        a1.addNodeMonitor();

        NewAgent a2 = new NewAgent("agenttwo", p);
        a2.addNodeMonitor();

        a1.sendMessage("agenttwo", "test message");
        a2.sendMessage("agentone", "response message");

        String expected = "From: agentone\tTo: agenttwo\t\tType: STANDARD\t\tContent: test message";

        String file = a2.getHandle() + "-LOG.txt";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            String actual = "";
            while ((line = br.readLine()) != null)
            {
                actual = line;
            }
            actual = actual.substring(16);
            
            System.out.println(actual);
            System.out.println(expected);
            
            assertEquals(expected, actual);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(NodeMonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(NodeMonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
