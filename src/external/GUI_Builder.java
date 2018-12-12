/* * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package external;

import drivers.AgentTest;
import drivers.DirectoryTest;
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
import java.util.ArrayList;
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
    Portal portal = new Portal("", "0.0.0.0");
    Agent agent = new Agent("", this);
    String myHandle = "dir";
    Directory dir = new Directory(myHandle, "0.0.0.0");
    final JFrame DirectoryFrame = new JFrame("Directory");
    final JFrame backing = new JFrame("Start Up");
    final JFrame PortalFrame = new JFrame("Portal");
    final JFrame agentFrame = new JFrame("Client");

    public GUI_Builder()
    {

        //main frame
        backing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        backing.setLayout(new GridBagLayout());

        /**
         * Adding components to the gridBag layout.
         */
        PortalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        PortalFrame.setLayout(new GridBagLayout());
        PortalFrame.setBackground(Color.yellow);
        PortalFrame.setSize(450, 300);
        PortalFrame.setVisible(false);
        PortalFrame.setResizable(false);

        DirectoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        DirectoryFrame.setLayout(new GridBagLayout());
        DirectoryFrame.setBackground(Color.yellow);
        DirectoryFrame.setSize(450, 200);
        DirectoryFrame.setVisible(false);
        DirectoryFrame.setResizable(false);

        agentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout to your frame
        agentFrame.setLayout(new GridBagLayout());
        agentFrame.setBackground(Color.yellow);
        agentFrame.setSize(450, 300);
        agentFrame.setVisible(false);
        agentFrame.setResizable(false);

        // start of backing options an buttons
        //adding header
        JLabel choice = new JLabel("Portal, Client, or Directory ? ", SwingConstants.CENTER);
        addComponentToGridBag(backing, choice, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton Directory = new JButton("Directory");
        addComponentToGridBag(backing, Directory, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        Directory.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                backing.dispose();
                try
                {
                    // sets the frame to be visible
                    dir.begin();
                    DirectoryFrame.setVisible(true);

                }
                catch (IOException ex)
                {
                    Logger.getLogger(DirectoryTest.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        JButton agents = new JButton("Client");
        addComponentToGridBag(backing, agents, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agents.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                backing.setVisible(false);
                try
                {
                    String myHandle = getHandle();
                    agent.setHandle(myHandle);
                    agent.begin();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(AgentTest.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                backing.setVisible(false);

                try
                {
                    // gets handler name
                    String myHandle = getHandle();
                    portal.setHandle(myHandle);
                    int nodeMonitor = JOptionPane.showConfirmDialog((Component) null, "Would you like a node monitor on this portal",
                            "alert", JOptionPane.YES_NO_OPTION);

                    if (nodeMonitor == 0)
                    {
                        portal.addNodeMonitor();
                        System.out.println("Node Moniter has started");
                    }
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
                String ip = newConnection();
                connectTo(ip);
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

                connectToDir(portal);

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
        JButton portalShowAgents = new JButton("Show Client");
        addComponentToGridBag(PortalFrame, portalShowAgents, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalShowAgents.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                displayAgentList(portal);

            }
        });

        JButton portalexit = new JButton("Exit");
        addComponentToGridBag(PortalFrame, portalexit, 0, 7, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalexit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        // end of portal settings
        //start of the directory buttons
        JLabel directoryOptions = new JLabel("Directory Options", SwingConstants.CENTER);
        addComponentToGridBag(DirectoryFrame, directoryOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton directoryNewConnections = new JButton("Show Connections");
        addComponentToGridBag(DirectoryFrame, directoryNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        directoryNewConnections.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                DirectorydisplayConnectionList(dir);

            }
        });
        JButton directoryshowConnections = new JButton("Remove Connections");
        addComponentToGridBag(DirectoryFrame, directoryshowConnections, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        directoryshowConnections.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dir.removeConnections();
            }
        });
        JButton direxit = new JButton("Exit");
        addComponentToGridBag(DirectoryFrame, direxit, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        direxit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        //end of directory buttons
        // start of agent buttons
        JLabel agantOptions = new JLabel("Client Options ", SwingConstants.CENTER);
        addComponentToGridBag(agentFrame, agantOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agentNewConnections = new JButton("Connect to Portal");
        addComponentToGridBag(agentFrame, agentNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentNewConnections.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String ip = newConnection();
                connectTo(ip);
            }
        });

        JButton agentSendMessage = new JButton("Send Message");
        addComponentToGridBag(agentFrame, agentSendMessage, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentSendMessage.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String to = getTo();
                String content = getContent(to);
                sendMessage(to, content);
            }
        });

        JButton agentShowPortal = new JButton("Show Portal");
        addComponentToGridBag(agentFrame, agentShowPortal, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentShowPortal.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                displayAgentConnectionList(agent);

            }
        });
        JButton agentRemoveConnections = new JButton("Remove Connections");
        addComponentToGridBag(agentFrame, agentRemoveConnections, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentRemoveConnections.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                agent.removeConnections();
            }
        });
        JButton agentexit = new JButton("Exit");
        addComponentToGridBag(agentFrame, agentexit, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentexit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        // end of agent buttons
    }

    private void addComponentToGridBag(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill)
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, INSETS_DATA, 0, 0);
        container.add(component, gridBagConstraints);
    }

    /**
     * Gets input for a ChatNodes handle.
     *
     * @return handle name for the agents and the portals
     */
    public String getHandle()
    {
        String handle = "";

        while (handle.isEmpty())
        {
            handle = JOptionPane.showInputDialog(null, "Enter handle", "Handle", JOptionPane.QUESTION_MESSAGE);
            if (handle == null)
            {
                backing.setVisible(true);
            }
            if (!handle.matches("^[^\\d\\s]+$"))
            {
                handle = "";
            }

        }
        backing.dispose();

        return handle;
    }

    /**
     * JOptionPane message that asks for the ip
     *
     * @return IP for the the connection to the new directory or the portals
     */
    public String newConnection()
    {
        Pattern ipPattern = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
        boolean find = false;

        do
        {
            String ip = JOptionPane.showInputDialog("Enter IP Address", IP_BASE);
            Matcher matcher = ipPattern.matcher(ip);

            if (matcher.find())
            {
                find = true;
                return ip;
            }
        } while (!find);
        return null;
    }

    /**
     * this out puts a gui window thats get all the handle names that re
     * connected to that portal
     */
    private void displayPortalConnectionList(Portal me)
    {
        if (!me.hasPortals())
        {
            JOptionPane.showMessageDialog(null, "No Portal Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> connections = me.getPortalHandles();
        JOptionPane.showMessageDialog(null, connections);
    }

    /**
     * this will display all the connections the are linked to the agent
     *
     * @return connections or null
     */
    private void displayAgentConnectionList(Agent me)
    {
        if (me.getPortal() == null)
        {
            JOptionPane.showMessageDialog(null, "No Portal Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        {
            String connection = me.getPortal();
            JOptionPane.showMessageDialog(null, connection, "Connections", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println();
    }

    /**
     * this connects a portal to the directory
     */
    private void connectToDir(Portal p)
    {
        p.connectTo(IP_BASE + "116");
    }

    /**
     * this will show the user the connections to the agents
     *
     * @return null or handle names
     */
    private void displayAgentList(Portal me)
    {
        if (!me.hasAgents())
        {
            JOptionPane.showMessageDialog(null, "No Agent Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> connections = me.getAgentHandles();
        JOptionPane.showMessageDialog(null, connections, "Connections", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * this will show the agents that have been connected to the directory
     *
     * @return this will show a gui with the handle names
     */
    private void DirectorydisplayConnectionList(Directory me)
    {
        if (!me.hasConnections())
        {
            JOptionPane.showMessageDialog(null, "No connections", "Connections", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, me.getConnectionHandles(), "Connections", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getTo()
    {
        Object[] msgOptions
                =
                {
                    "Standard", "Broadcast"
                };

        int n = JOptionPane.showOptionDialog(null,
                "What message type are you sending?",
                "Send Message",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null,
                msgOptions, msgOptions[0]);

        String handle = "";

        if (n == 0)
        {
            //System.out.println("Current connections:");
            List<String> contacts = new ArrayList();
            for (String c : agent.getContacts())
            {
                contacts.add(c);
            }

            while (handle.isEmpty())
            {
                handle = JOptionPane.showInputDialog(null, "Current Contacts\n" + contacts + "\n\nWho would you like to message?", "Send Message");
                if (!handle.matches("^[^\\d\\s]+$") || handle.equals("Handle"))
                {
                    handle = "";
                }
            }
        }
        else
        {
            handle = "all";
        }

        return handle;
    }

    private String getContent(String to)
    {
        String content = JOptionPane.showInputDialog(null, "What message would you like to send to " + to, "Send Message");

        return content;
    }

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
    public void sendMessage(String to, String content)
    {
        Message newMessage;

        if (to.equals("all"))
        {
            newMessage = new Message(agent.getHandle(), to, MessageType.BROADCAST);
            newMessage.append(content);
        }
        else
        {
            newMessage = new Message(agent.getHandle(), to, MessageType.STANDARD);
            newMessage.append(content);
        }

        agent.sendMessage(newMessage);
    }

    @Override
    public void connectTo(String ip)
    {
        agent.connectTo(ip);
    }
}
