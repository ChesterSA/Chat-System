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
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Group B
 */
public class NodeMonitor
{
    /**
     * the handle of the portal that this NodeMonitor is monitoring
     */
    String handle;
    
    /**
     * 
     */
    PrintWriter nodeMonitor;

    NodeMonitor(String h)
    {
        handle = h;
    }

    /**
     * Logs to a file when a message passes through a portal
     *
     * @param m message being passed monitored
     */
    public void handleMessage(Message m)
    {
        File monitor = new File(handle + "-Log.txt");
        try
        {
            FileWriter fw = new FileWriter(monitor, true);
            nodeMonitor = new PrintWriter(fw);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("There has been an error finding file");
        }
        catch (IOException ex)
        {
            System.err.println("There has been an input/output error");
        }

        String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        nodeMonitor.print(currentTime + "\t");
        nodeMonitor.print("From: " + m.getFrom() + "\t");
        nodeMonitor.print("To: " + m.getTo() + "\t\t");
        nodeMonitor.print("Type: " + m.getType() + "\t\t");
        nodeMonitor.print("Content: " + m.getContent());
        nodeMonitor.println();

        nodeMonitor.close();
    }
}
