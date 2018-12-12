/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import middleware.Message;

/**
 *
 * @author s6089488
 */
public final class MainFrame extends MyFrame
{


    /**
     * Initialising empty middleware nodes
     */
    DirectoryFrame directoryFrame;
    PortalFrame portalFrame;
    ClientFrame agentFrame;

    public MainFrame()
    {
        super("Main");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        this.setSize(500, 200);
        
        addButtons();
        this.setVisible(true);
    }
    
    private void addButtons()
    {
        JLabel choice = new JLabel("Portal, Client, or Directory ? ", SwingConstants.CENTER);
        addComponentToGridBag(this, choice, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton Portal = new JButton("Portal");
        addComponentToGridBag(this, Portal, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        Portal.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                setVisible(false);
                portalFrame = new PortalFrame();
            }
        });
        
        JButton agents = new JButton("Client");
        addComponentToGridBag(this, agents, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        agents.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                agentFrame = new ClientFrame();
            }
        });
        
        JButton Directory = new JButton("Directory");
        addComponentToGridBag(this, Directory, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        Directory.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                directoryFrame = new DirectoryFrame();
            }
        });
        
        JButton exit = new JButton("Exit");
        addComponentToGridBag(this, exit, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        exit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // close first Frame
                dispose();
            }
        });
    }
}
