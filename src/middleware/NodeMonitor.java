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
 * Reads every message that pass through a chat node and writes to a filehandle
 * @author Group B
 */
public class NodeMonitor
{
    /**
     * the handle of the agent that this NodeMonitor is monitoring
     */
    String handle;
    
    /**
     * The output writer used for file access
     */
    PrintWriter writer;

    /**
     * A constructor for the node monitor, sets the handle
     * @param h 
     */
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
            writer = new PrintWriter(fw);
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

        writer.print(currentTime + "\t");
        writer.print("From: " + m.getFrom() + "\t");
        writer.print("To: " + m.getTo() + "\t\t");
        writer.print("Type: " + m.getType() + "\t\t");
        writer.print("Content: " + m.getContent());
        writer.print("\r\n");

        writer.close();
    }
}
