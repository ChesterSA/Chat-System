/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

/**
 *
 * @author v8269590
 */
public interface Connectable {

    public void connectTo(final String remoteIpAddress);
    
    public abstract void connectTo(final String remoteIpAddress, final int remotePort);
}
