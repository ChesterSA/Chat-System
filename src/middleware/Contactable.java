/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

/**
 *
 * @author s6089488
 */
public interface Contactable
{
    public void handleMessage(Message m);
    
    public void sendMessage(String to, String content);
}
