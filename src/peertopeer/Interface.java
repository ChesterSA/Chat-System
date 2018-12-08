package peertopeer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.*;

/**
 *
 * @author t7077260
 */
public class Interface
{

    /**
     * @param args the command line arguments
     *
     *
     */
    private static final Insets insetsData = new Insets(2, 2, 2, 2);

    public static void main(String[] args)
    {
        Interface();
    }

    public static void Interface()
    {
        //Initialise ChatNodes
        Portal portal = new Portal("", "0.0.0.0");
        Agent agent = new Agent("", "0.0.0.0");

        //main frame
        final JFrame backing = new JFrame("Start Up");
        backing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        backing.setLayout(new GridBagLayout());

        final JFrame PortalFrame = new JFrame("Portal");
        PortalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        PortalFrame.setLayout(new GridBagLayout());
        PortalFrame.setBackground(Color.yellow);
        PortalFrame.setSize(500, 200);
        PortalFrame.setVisible(false);
        PortalFrame.setResizable(false);

        final JFrame DirectoryFrame = new JFrame("Directory");
        DirectoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        DirectoryFrame.setLayout(new GridBagLayout());
        DirectoryFrame.setBackground(Color.yellow);
        DirectoryFrame.setSize(500, 200);
        DirectoryFrame.setVisible(false);
        DirectoryFrame.setResizable(false);

        final JFrame agentFrame = new JFrame("Portal");
        agentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        agentFrame.setLayout(new GridBagLayout());
        agentFrame.setBackground(Color.yellow);
        agentFrame.setSize(500, 200);
        agentFrame.setVisible(false);
        agentFrame.setResizable(false);

        // start of backing options an buttons
        //adding header
        JLabel choice = new JLabel("ChatNode or Directory ? ", SwingConstants.CENTER);
        addComponentToGridBag(backing, choice, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton Directory = new JButton("Direcotory");
        addComponentToGridBag(backing, Directory, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        Directory.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                backing.dispose();
                // sets the frame to be visible
                DirectoryFrame.setVisible(true);
            }
        });
        JButton exit = new JButton("Exit");
        addComponentToGridBag(backing, exit, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        exit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                backing.dispose();
            }
        });
        JButton agents = new JButton("Agent");
        addComponentToGridBag(backing, agents, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agents.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                backing.dispose();
                // gets handler name
                handler();
                // sets the frame to be visible
                agentFrame.setVisible(true);
            }
        });

        JButton Portal = new JButton("Portal");
        addComponentToGridBag(backing, Portal, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        Portal.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                backing.dispose();
                // gets handler name

                try
                {
                    String myHandle = handler();
                    portal.handle = myHandle;
                    portal.begin();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(PortalTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                // sets the frame to be visible
                PortalFrame.setVisible(true);

            }
        });
        backing.setSize(500, 200);
        backing.setVisible(true);
        //end of backing options an buttons

        // starting if portal
        JLabel portalOptions = new JLabel("Portal Options ", SwingConstants.CENTER);
        addComponentToGridBag(PortalFrame, portalOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton portalNewConnections = new JButton("New Connections");
        addComponentToGridBag(PortalFrame, portalNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalNewConnections.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                newPortalConnection(portal);

            }
        });
        JButton portalshowConnections = new JButton("Show Portals");
        addComponentToGridBag(PortalFrame, portalshowConnections, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalshowConnections.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                displayPortalConnectionList(portal);

            }
        });
        JButton portalConnectdir = new JButton("Connect To directory");
        addComponentToGridBag(PortalFrame, portalConnectdir, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalConnectdir.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                PortalconnectToDir(portal);

            }
        });

        JButton portalRemoveConnections = new JButton("Remove Connections");
        addComponentToGridBag(PortalFrame, portalRemoveConnections, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalRemoveConnections
                .addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {

                        portal.removeConnections();

                    }
                });
        JButton portalShowAgents = new JButton("Show Agents");
        addComponentToGridBag(PortalFrame, portalShowAgents, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalShowAgents.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                displayAgentList(portal);

            }
        });

// end of portal settings
        //start of the directory buttons
        JLabel directoryOptions = new JLabel("Directory Options", SwingConstants.CENTER);
        addComponentToGridBag(DirectoryFrame, directoryOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton directoryNewConnections = new JButton("New Connections");
        addComponentToGridBag(DirectoryFrame, directoryNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton directoryshowConnections = new JButton("Show Connections");
        addComponentToGridBag(DirectoryFrame, directoryshowConnections, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        //end of directory buttons

        // start of agent buttons
        JLabel agantOptions = new JLabel("Agent Options ", SwingConstants.CENTER);
        addComponentToGridBag(agentFrame, agantOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agantNewConnections = new JButton("New Connections");
        addComponentToGridBag(agentFrame, agantNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agantshowPortal = new JButton("Show Portal");
        addComponentToGridBag(agentFrame, agantshowPortal, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agantConnectdir = new JButton("Connect To directory");
        addComponentToGridBag(agentFrame, agantConnectdir, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agantRemoveConnections = new JButton("Remove Connections");
        addComponentToGridBag(agentFrame, agantRemoveConnections, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        // end of agent buttons

    }

    /**
     * Adding components to the gridBag layout.
     *
     */
    private static void addComponentToGridBag(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill)
    {

        GridBagConstraints gridBagConstraints = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, insetsData, 0, 0);
        container.add(component, gridBagConstraints);
    }

    public static String handler()
    {
        String handler = JOptionPane.showInputDialog("Enter handler Name");
        System.out.println("your handler name is: " + handler);
        return handler;
    }

    public static String newConnection()
    {
        String ip = JOptionPane.showInputDialog("Enter IP Adress");
        System.out.println("The IP Adress is: " + ip);
        return ip;
    }

    private static void newPortalConnection(Portal me)
    {
        System.out.println("What is the IP address of the peer to connect to?");
        String ipAddressOfPeer = newConnection();
        me.connectTo(ipAddressOfPeer);
    }

    private static void displayPortalConnectionList(Portal me)
    {
        if (!me.hasPortals())
        {
            JOptionPane.showMessageDialog(null, "No Portal Connections!");
            return;
        }

        List<String> connections = me.getPortalHandles();
        JOptionPane.showMessageDialog(null, connections);
    }

    private static void PortalconnectToDir(ChatNode me)
    {
        me.connectTo("152.105.67.116");
    }

    private static void displayAgentList(Portal me)
    {
        if (!me.hasAgents())
        {
            JOptionPane.showMessageDialog(null, "No Agent Connections!");
            return;
        }

        List<String> connections = me.getAgentHandles();
        JOptionPane.showMessageDialog(null, connections);

    }
}
