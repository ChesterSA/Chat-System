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
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import middleware.Directory;

/**
 *
 * @author s6089488
 */
public final class DirectoryFrame extends MyFrame
{
    Directory dir = new Directory("dir", "0.0.0.0");
            
    public DirectoryFrame() 
    {
        super("Directory");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setBackground(Color.yellow);
        setSize(450, 200);
        setResizable(false);
        
        try
        {
            dir.begin();
        }
        catch (IOException ex)
        {
            Logger.getLogger(DirectoryFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        addButtons();
        
        setVisible(true);
    }   
    
    /**
     * this will show the agents that have been connected to the directory
     *
     * @return this will show a gui with the handle names
     */
    private void displayConnectionList()
    {
        if (!dir.hasConnections())
        {
            JOptionPane.showMessageDialog(null, "No connections", "Connections", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, dir.getConnectionHandles(), "Connections", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addButtons()
    {
        //start of the directory buttons
        JLabel directoryOptions = new JLabel("Directory Options", SwingConstants.CENTER);
        addComponentToGridBag(this, directoryOptions, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        JButton directoryNewConnections = new JButton("Show Connections");
        addComponentToGridBag(this, directoryNewConnections, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        directoryNewConnections.addActionListener((ActionEvent e) ->
        {
            displayConnectionList();
        });
        
        JButton directoryshowConnections = new JButton("Remove Connections");
        addComponentToGridBag(this, directoryshowConnections, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        directoryshowConnections.addActionListener((ActionEvent e) ->
        {
            dir.removeConnections();
        });
        
        JButton direxit = new JButton("Exit");
        addComponentToGridBag(this, direxit, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        direxit.addActionListener((ActionEvent e) ->
        {
            System.exit(0);
        });
    }
}
