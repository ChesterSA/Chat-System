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
 * @author v8269590
 */
public class NodeMonitor
{

    String fileLocation;
    PrintWriter nodeMonitor;

    NodeMonitor(String fL)
    {
        fileLocation = fL;

    }

    public void handleMessage(Message m)
    {
        File monitor = new File(fileLocation);
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

        System.out.println("Node monitor writing to file");
        String datetime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        nodeMonitor.print(datetime + "  ");
        nodeMonitor.print("From: " + m.getFrom() + "  ");
        nodeMonitor.print("To: " + m.getTo() + "  ");
        nodeMonitor.print("Content: " + m.getContent() + "  ");
        nodeMonitor.print("Type: " + m.getType());
        nodeMonitor.println();

        nodeMonitor.close();
    }
}
