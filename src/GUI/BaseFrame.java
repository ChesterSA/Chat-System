/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import middleware.ChatNode;
import middleware.Contactable;
import middleware.Message;

/**
 *
 * @author s6089488
 */
public abstract class BaseFrame extends JFrame implements Contactable
{

    final Insets INSETS_DATA = new Insets(2, 2, 2, 2);
    final String IP_BASE = "152.105.67.";

    public BaseFrame(String title)
    {
        super(title);
    }

    /**
     * JOptionPane message that asks for the ip
     *
     * @return IP for the the connection to the new directory or the portals
     */
    protected String getIpAddress()
    {
        String ip = "";
        do
        {
            ip = JOptionPane.showInputDialog("Enter IP Address", IP_BASE);
        } while (!ChatNode.checkIp(ip));

        return ip;
    }

    protected void addComponentToGridBag(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill)
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, INSETS_DATA, 0, 0);
        container.add(component, gridBagConstraints);
    }

    protected String getContent(String to)
    {
        String content = JOptionPane.showInputDialog(null, "What message would you like to send to " + to, "Send Message");
        return content;
    }

    /**
     * Gets input for a ChatNode's handle.
     *
     * @return handle name for the agents and the portals
     */
    public String getHandle()
    {
        String handle = "";

        while (!ChatNode.checkHandle(handle))
        {
            handle = JOptionPane.showInputDialog(null, "Enter handle", "Handle", JOptionPane.QUESTION_MESSAGE);
            if (handle == null)
            {
                setVisible(false);

                //Doesn't error now, but immediately exiting program isn't useful
                System.exit(0);
            }
        }

        dispose();

        return handle;
    }

    /**
     * Check the message information and react accordingly
     *
     * @param m the message being handled
     */
    @Override
    public void handleMessage(Message m)
    {
        String content = m.getContent();
        if (content.isEmpty())
        {
            content = "N/A";
        }

        JOptionPane.showMessageDialog(null, "From: " + m.getFrom() + "\n"
                + "To: " + m.getTo() + "\n"
                + "Content: " + m.getContent() + "\n"
                + "Type: " + m.getType().toString(),
                "Message Notification",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void connectTo(String ip)
    {

    }

    /**
     * Send a message from this class to any other agent on the network
     *
     * @param to the handle of the agent being sent to
     * @param content the content of the message to send
     */
    @Override
    public void sendMessage(String to, String content)
    {

    }

    protected abstract void addButtons();

}