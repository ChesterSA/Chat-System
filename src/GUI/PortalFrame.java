/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import middleware.Portal;

/**
 *
 * @author Group B
 */
public final class PortalFrame extends BaseFrame
{ 
   private final String DIR_IP = "152.105.67.123";
    /**
     * Constructs a swing frame
     * Initialises and starts a new portal
     * Populates the frame with buttons
     */
    public PortalFrame()
    {
        super("Portal");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setBackground(Color.yellow);
        setSize(450, 300);
        setResizable(false);

        try
        {
            setPortal(new Portal("Default", "0.0.0.0"));
            String myHandle = getHandle();
            portal.setHandle(myHandle);
            int result = JOptionPane.showConfirmDialog(null, "Do you want a NodeMonitor on this portal?", "Node Monitor?", JOptionPane.YES_NO_OPTION);
            
            //if yes option
            if (result == 0)
            {
                portal.addNodeMonitor();
            }
            
            setTitle(myHandle);
            portal.begin();
        }
        catch (IOException ex)
        {
            Logger.getLogger(PortalFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        addButtons();
        setVisible(true);
    }
    
    /**
     * Portal object calls connectTo 
     * @param ip validated ip from
     */
    @Override
    public void connectTo(String ip)
    {
        portal.connectTo(ip);
    }
    
    private void displayPortalConnectionList()
    {
        if (!portal.hasPortals())
        {
            JOptionPane.showMessageDialog(null, "No Portal Connections", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> connections = portal.getPortalHandles();
        JOptionPane.showMessageDialog(null, connections);
    }
    
    /**
     * this will show the user the connections to the agents
     *
     * @return null or handle names
     */
    private void displayAgentList()
    {
        if (!portal.hasAgents())
        {
            JOptionPane.showMessageDialog(null, "No Agent Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> connections = portal.getAgentHandles();
        JOptionPane.showMessageDialog(null, connections, "Connections", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Initialises swing buttons
     * Adds them to the frame grid bag.
     * Defines action listeners for each button.
     */
    @Override
    protected void addButtons()
    {
        JLabel portalOptions = new JLabel("Portal Options ", SwingConstants.CENTER);
        addComponentToGridBag(this, portalOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton portalNewConnections = new JButton("New Connections");
        addComponentToGridBag(this, portalNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalNewConnections.addActionListener((ActionEvent e) ->
        {
            String ip = getIpAddress();
            connectTo(ip);
        });
        
        JButton portalshowConnections = new JButton("Show Portals");
        addComponentToGridBag(this, portalshowConnections, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalshowConnections.addActionListener((ActionEvent e) ->
        {
            displayPortalConnectionList();
        });
        
        JButton portalConnectdir = new JButton("Connect to Directory");
        addComponentToGridBag(this, portalConnectdir, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalConnectdir.addActionListener((ActionEvent e) ->
        {
            connectTo(DIR_IP);
        });

        JButton portalRemoveConnections = new JButton("Remove Connections");
        addComponentToGridBag(this, portalRemoveConnections, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalRemoveConnections
                .addActionListener((ActionEvent e) ->
        {
            portal.removeConnections();
        });
        
        JButton portalShowAgents = new JButton("Show Client");
        addComponentToGridBag(this, portalShowAgents, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalShowAgents.addActionListener((ActionEvent e) ->
        {
            displayAgentList();
        });

        JButton portalexit = new JButton("Exit");
        addComponentToGridBag(this, portalexit, 0, 7, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalexit.addActionListener((ActionEvent e) ->
        {
            System.exit(0);
        });
    }
    
}
