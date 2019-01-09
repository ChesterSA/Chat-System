/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

/**
 * Interface for nodes that need to be able to connect to other nodes
 *
 * @author Group B
 */
public interface Connectable
{

    /**
     * Connects to the specified ip
     *
     * @param remoteIpAddress the ip to connect to
     */
    public void connectTo(final String remoteIpAddress);

    /**
     * Connects to the specified ip and port
     *
     * @param remoteIpAddress the ip to connect to
     * @param remotePort the port to connect to
     */
    public void connectTo(final String remoteIpAddress, final int remotePort);
}
