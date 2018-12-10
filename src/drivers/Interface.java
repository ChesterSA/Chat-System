package drivers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import middleware.Agent;
import middleware.ChatNode;
import middleware.Directory;
import middleware.Message;
import middleware.MessageType;
import middleware.Portal;

/**
 *
 * @author t7077260
 */
public class Interface {

    /**
     * @param args the command line arguments
     *
     *
     */
    private static final Insets insetsData = new Insets(2, 2, 2, 2);

    public static void main(String[] args) {
        Interface();
    }

    public static void Interface() {
        //Initialise ChatNodes
        Portal portal = new Portal("", "0.0.0.0");
        Agent agent = new Agent("", "0.0.0.0");
        String myHandle = "dir";
        Directory dir = new Directory(myHandle, "0.0.0.0");
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
        Directory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // close first Frame
                backing.dispose();
                try 
                {
                    // sets the frame to be visible
                    dir.begin();
                    DirectoryFrame.setVisible(true);
                
                } catch (IOException ex) {
                    Logger.getLogger(DirectoryTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton exit = new JButton("Exit");
        addComponentToGridBag(backing, exit, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // close first Frame
                backing.dispose();
            }
        });
        JButton agents = new JButton("Agent");
        addComponentToGridBag(backing, agents, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // close first Frame
                backing.dispose();
                try {
                    String myHandle = handler();
                    agent.setHandle(myHandle);
                    agent.begin();
                } catch (IOException ex) {
                    Logger.getLogger(AgentTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                // sets the frame to be visible
                agentFrame.setVisible(true);
            }
        });

        JButton Portal = new JButton("Portal");
        addComponentToGridBag(backing, Portal, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        Portal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // close first Frame
                backing.dispose();

                try {
                    // gets handler name
                    String myHandle = handler();
                    portal.setHandle(myHandle);
                    portal.begin();
                } catch (IOException ex) {
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
        portalNewConnections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                newPortalConnection(portal);

            }
        });
        JButton portalshowConnections = new JButton("Show Portals");
        addComponentToGridBag(PortalFrame, portalshowConnections, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalshowConnections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                displayPortalConnectionList(portal);

            }
        });
        JButton portalConnectdir = new JButton("Connect To directory");
        addComponentToGridBag(PortalFrame, portalConnectdir, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalConnectdir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                connectToDir(portal);

            }
        });

        JButton portalRemoveConnections = new JButton("Remove Connections");
        addComponentToGridBag(PortalFrame, portalRemoveConnections, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalRemoveConnections
                .addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        portal.removeConnections();

                    }
                });
        JButton portalShowAgents = new JButton("Show Agents");
        addComponentToGridBag(PortalFrame, portalShowAgents, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        portalShowAgents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                displayAgentList(portal);

            }
        });

        // end of portal settings
        //start of the directory buttons
        JLabel directoryOptions = new JLabel("Directory Options", SwingConstants.CENTER);
        addComponentToGridBag(DirectoryFrame, directoryOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton directoryNewConnections = new JButton("Show Connections");
        addComponentToGridBag(DirectoryFrame, directoryNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        directoryNewConnections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                DirectorydisplayConnectionList(dir);

            }
        });
        JButton directoryshowConnections = new JButton("Remove Connections");
        addComponentToGridBag(DirectoryFrame, directoryshowConnections, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        directoryshowConnections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dir.removeConnections();
            }
        });

        //end of directory buttons
        // start of agent buttons
        JLabel agantOptions = new JLabel("Agent Options ", SwingConstants.CENTER);
        addComponentToGridBag(agentFrame, agantOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agentNewConnections = new JButton("New Connections");
        addComponentToGridBag(agentFrame, agentNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentNewConnections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                newAgentConnection(agent);

            }
        });

        JButton agentSendMessage = new JButton("Send Message");
        addComponentToGridBag(agentFrame, agentSendMessage, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentSendMessage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                agentSendMessage(agent);

            }
        });

        JButton agentShowPortal = new JButton("Show Portal");
        addComponentToGridBag(agentFrame, agentShowPortal, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentShowPortal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                displayAgentConnectionList(agent);

            }
        });

        JButton agentConnectDir = new JButton("Connect To directory");
        addComponentToGridBag(agentFrame, agentConnectDir, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentConnectDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                connectToDir(agent);
            }
        });

        JButton agentRemoveConnections = new JButton("Remove Connections");
        addComponentToGridBag(agentFrame, agentRemoveConnections, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentRemoveConnections.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                agent.removeConnections();
            }
        });
        // end of agent buttons

    }

    /**
     * Adding components to the gridBag layout.
     *
     */
    private static void addComponentToGridBag(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill) {

        GridBagConstraints gridBagConstraints = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor, fill, insetsData, 0, 0);
        container.add(component, gridBagConstraints);
    }

    public static String handler() {
        String handle = JOptionPane.showInputDialog("Enter handle Name", "Handle");
        System.out.println("your handler name is: " + handle);
        return handle;
    }

    public static String newConnection() {
        String ip = JOptionPane.showInputDialog("Enter IP Adress", "IP Address");
        System.out.println("The IP Adress is: " + ip);
        return ip;
    }

    private static void newPortalConnection(Portal me) {
        String ipAddressOfPeer = newConnection();
        me.connectTo(ipAddressOfPeer);
    }

    private static void newAgentConnection(Agent me) {
        String ipAddressOfPeer = newConnection();
        me.connectTo(ipAddressOfPeer);
    }

    private static void displayPortalConnectionList(Portal me) {
        if (!me.hasPortals()) {
            JOptionPane.showMessageDialog(null, "No Portal Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> connections = me.getPortalHandles();
        JOptionPane.showMessageDialog(null, connections);
    }

    private static void displayAgentConnectionList(Agent me) {
        if (me.getPortal() == null) {
            JOptionPane.showMessageDialog(null, "No Portal Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            String connection = me.getPortal();
            JOptionPane.showMessageDialog(null, connection, "Connections", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println();
    }

    private static void connectToDir(ChatNode me) {
        me.connectTo("152.105.67.116");
    }

    private static void displayAgentList(Portal me) {
        if (!me.hasAgents()) {
            JOptionPane.showMessageDialog(null, "No Agent Connections!", "Connections", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> connections = me.getAgentHandles();
        JOptionPane.showMessageDialog(null, connections, "Connections", JOptionPane.INFORMATION_MESSAGE);

    }

    private static void agentSendMessage(Agent me) {
        Object[] msgOptions = {"Standard", "Broadcast"};

        int n = JOptionPane.showOptionDialog(null,
                "What message type are you sending?",
                "Send Message",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null,
                msgOptions, msgOptions[0]);

        String handle;

        if (n == 0) {
            //System.out.println("Current connections:");
            List<String> contacts = new ArrayList();
            for (String c : me.getContacts()) {
                contacts.add(c);
            }
            handle = JOptionPane.showInputDialog(null, "Current Contacts\n" + contacts + "\n\nWho would you like to message?", "Send Message");
        } else {
            handle = "all";
        }

        String messageContent = JOptionPane.showInputDialog(null, "What message would you like to send to " + handle, "Send Message");

        Message newMessage;

        if (handle.equals("all")) {
            newMessage = new Message(me.getHandle(), handle, MessageType.BROADCAST);
            newMessage.append(messageContent);
        } else {
            newMessage = new Message(me.getHandle(), handle, MessageType.STANDARD);
            newMessage.append(messageContent);
        }

        me.sendMessage(newMessage);
    }

    private static void DirectorydisplayConnectionList(Directory me) {
        if (!me.hasConnections()) {
            System.out.println("\n* No portals connected *\n");
            return;
        }

        
        
        
        JOptionPane.showMessageDialog(null, me.getConnectionHandles(), "Connections", JOptionPane.INFORMATION_MESSAGE);
     
    }
}
