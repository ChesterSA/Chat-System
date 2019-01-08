/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

/**
 *
 * @author cswan
 */
public class Node
{
        /**
     * The handle of the agent, used for identification
     */
    String handle;
    
        /**
     * Monitor which when set will keep track of all messages through the portal
     */
    protected NodeMonitor nodeMonitor;
    
    /**
     * Adds new node monitor to portal
     */
    public void addNodeMonitor()
    {
        nodeMonitor = new NodeMonitor(this.handle);
    }

    /**
     * Removes node monitor from portal
     */
    public void removeNodeMonitor()
    {
        nodeMonitor = null;
    }
    
    /**
     * Gets the handle of the node
     *
     * @return the handle of the node
     */
    public String getHandle()
    {
        return handle;
    }
}
