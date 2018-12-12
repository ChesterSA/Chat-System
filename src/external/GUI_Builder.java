/* * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import GUI.*;
import drivers.PortalTest;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import middleware.Agent;
import middleware.Contactable;
import middleware.Directory;
import middleware.Message;
import middleware.MessageType;
import middleware.Portal;

/**
 * A GUI used to create directories, agents, or portals and use their actions
 *
 * @author Group B
 */
public class GUI_Builder implements Contactable
{

    /**
     * The base ip used for autofilling forms
     */
    final String IP_BASE = "152.105.67.";

    /**
     * Gridlayout feature
     */
    final Insets INSETS_DATA = new Insets(2, 2, 2, 2);

    /**
     * Initialising empty middleware nodes
     */
    //Portal portal = new Portal("", "0.0.0.0");
    //Agent agent = new Agent("", this);
    //String myHandle = "dir";
    //Directory dir = new Directory(myHandle, "0.0.0.0");
    DirectoryFrame directoryFrame;
    final JFrame backing = new JFrame("Start Up");
    PortalFrame portalFrame;
    ClientFrame agentFrame;

    public GUI_Builder()
    {

        //main frame
        backing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        backing.setLayout(new GridBagLayout());

        /**
         * Adding components to the gridBag layout.
         */
        // start of backing options an buttons
        //adding header
        

        backing.setSize(500, 200);
        backing.setVisible(true);
        //end of backing options an buttons
    }
//
    
    protected void addComponentToGridBag(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill)
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, INSETS_DATA, 0, 0);
        container.add(component, gridBagConstraints);
    }

    @Override
    public void handleMessage(Message m)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(String to, String content)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connectTo(String ip)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}



