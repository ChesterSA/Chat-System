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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import middleware.MetaAgent;
import middleware.Contactable;
import middleware.Message;
import middleware.Portal;

/**
 * The frame that all other frames in the program extend 
 * @author Group B
 */
public abstract class BaseFrame extends JFrame
{

    /**
     * Insets data
     */
    final Insets INSETS_DATA = new Insets(2, 2, 2, 2);

    /**
     * The local IP base, used to make entering IP's less work
     */
    final String IP_BASE = "152.105.67.";

    /**
     * The portal that every frame on the machine uses
     */
    static Portal portal;

    /**
     * Constructor for the frame
     *
     * @param title
     */
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
        } while (!MetaAgent.checkIp(ip));

        return ip;
    }

    /**
     *
     * @param container
     * @param component
     * @param gridx
     * @param gridy
     * @param gridwidth
     * @param gridheight
     * @param anchor
     * @param fill
     */
    protected void addComponentToGridBag(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill)
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, INSETS_DATA, 0, 0);
        container.add(component, gridBagConstraints);
    }

    /**
     * out puts a gui that is used for for getting the content of the message
     *
     * @param to
     * @return content gui input box
     */
    protected String getContent(String to)
    {
        String content = JOptionPane.showInputDialog(null, "What message would you like to send to " + to, "Send Message");
        return content;
    }

    /**
     * Gets input for a MetaAgent's handle.
     *
     * @return handle name for the agents and the portals
     */
    public String getHandle()
    {
        String handle = "";

        while (!MetaAgent.checkHandle(handle))
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
     * Initialises swing buttons Adds them to the frame grid bag. Defines action
     * listeners for each button.
     */
    protected abstract void addButtons();

    /**
     * Set the portal that all frames should connect to
     * @param portal the portal reference for all local frames to use
     */
    public void setPortal(Portal portal)
    {
        BaseFrame.portal = portal;
    }

    /**
     * Gets the portal value
     * @return the portal
     */
    public Portal getPortal()
    {
        return portal;
    }

}
