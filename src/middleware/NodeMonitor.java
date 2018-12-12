/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author v8269590
 */
public class NodeMonitor {
    
    String portalHandle;
    PrintWriter nodeMonitor;
    
    NodeMonitor(String pH)
    {
        portalHandle = pH;
        
    }    
        
    public void handleMessage(Message m)
    {
        File monitor = new File(portalHandle + "-Log.txt");
        try
        {
            FileWriter fw = new FileWriter(monitor, true);
            nodeMonitor = new PrintWriter(fw);
        }
        catch(FileNotFoundException e)
        {
            System.err.println("There has been an error finding file");
        }
        catch(IOException ex)
        {
            System.err.println("There has been an input/output error");
        }
        
        nodeMonitor.print("From: " + m.getFrom() + "\t");
        nodeMonitor.print("To: " + m.getTo() + "\t");
        nodeMonitor.print("Type: " + m.getType() + "\t");
        nodeMonitor.print("Content: " + m.getContent());
        nodeMonitor.println();
        
        nodeMonitor.close();
    }
}
