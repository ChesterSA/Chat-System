/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JOptionPane;
import static middleware.MetaAgent.DEFAULT_RECV_IP_ADDRESS;

/**
 *
 * @author cswan
 */
public class NewAgent
{
    String handle;
    NewPortal portal;

    public NewAgent(String handle, NewPortal portal)
    {
        this.handle = handle;
        this.portal = portal;
        portal.addAgent(this);
    }
    
    public void sendMessage(String to, String from, String content, MessageType type)
    {
        Message m = new Message(from, to, type);
        m.append(content);
        
        portal.enqueue(m);
        portal.sendMessage();
    }
    
    public void receiveMessage(Message m)
    {
        System.out.println(handle + " has received a message");
        System.out.println("To: " + m.getTo());
        System.out.println("From: " + m.getFrom());
        System.out.println("Content: " + m.getContent());
    }

    public String getHandle()
    {
        return handle;
    }
    
    
}

