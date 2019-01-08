/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import external.Client;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import middleware.Contactable;
import middleware.Message;

/**
 *
 * @author Group B
 */
public final class ClientFrame extends BaseFrame implements Contactable
{

    /**
     * The Client that this frame uses for connections
     */
    Client agent = new Client(getHandle(), getPortal());

    /**
     * Constructs a swing frame Initialises and starts a new agent Populates the
     * frame with buttons
     */
    public ClientFrame()
    {
        super("Client");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setBackground(Color.yellow);
        setSize(450, 300);
        setResizable(false);

        setTitle(agent.getAgent().getHandle());
        agent.getAgent().setClient(this);

        addButtons();

        // sets the frame to be visible
        setVisible(true);
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
    
    /**
     * Creates a message object Appends the content Calls agents sendMessage
     * method
     *
     * @param to recipient of the message
     * @param content the message contents
     */
    @Override
    public void sendMessage(String to, String content)
    {
        if (to.equals("all"))
        {
            agent.getAgent().sendBroadcast(agent.getAgent().getHandle(), to, content);
            return;
        }
        agent.sendMessage(to, content);
    }

    /**
     * If standard message, get handle input from the user Else handle equals
     * All, to represent a broadcast message
     *
     * @return String handle
     */
    protected String getTo()
    {
        Object[] msgOptions =
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
            for (String c : agent.getAgent().getContacts())
            {
                contacts.add(c);
            }

            while (handle.isEmpty())
            {
                handle = JOptionPane.showInputDialog(null, "Current Contacts\n" + contacts + "\n\nWho would you like to message?", "Send Message");
                if (handle == null)
                {
                    setVisible(false);
                    //Doesn't error now, but immediately exiting program isn't useful
                    System.exit(0);
                }
                if (!handle.matches("^[^\\d\\s]+$"))
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

    /**
     * Shows an agents portal connection handle, error display if null.
     */
    private void displayConnections()
    {
        if (agent == null)
        {
            JOptionPane.showMessageDialog(null, "No Portal Connected", "Portal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        {
            String connection = agent.getAgent().getPortal().getHandle();
            JOptionPane.showMessageDialog(null, connection, "Portal", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Initialises swing buttons Adds them to the frame grid bag. Defines action
     * listeners for each button.
     */
    @Override
    protected void addButtons()
    {
        JLabel agentOptions = new JLabel("Client Options ", SwingConstants.CENTER);
        addComponentToGridBag(this, agentOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton agentSendMessage = new JButton("Send Message");
        addComponentToGridBag(this, agentSendMessage, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentSendMessage.addActionListener((ActionEvent e) ->
        {
            String to = getTo();
            String content = getContent(to);
            sendMessage(to, content);
        });

        JButton agentShowPortal = new JButton("Show Portal");
        addComponentToGridBag(this, agentShowPortal, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentShowPortal.addActionListener((ActionEvent e) ->
        {
            displayConnections();
        });

        JButton agentexit = new JButton("Exit");
        addComponentToGridBag(this, agentexit, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agentexit.addActionListener((ActionEvent e) ->
        {
            System.exit(0);
        });
    }
    
}
