/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import middleware.Agent;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author s6089488
 */
public class ChatNodeTest
{
    @Test
    public void validHandle()
    {
        Agent a = new Agent("AgentName");
        String expected = "AgentName";
        String actual = a.getHandle();
        
        assertEquals(expected, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleWithSpaces()
    {
        Agent a = new Agent("Agent name");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleWithNumbers()
    {
        Agent a = new Agent("name1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleEmpty()
    {
        Agent a = new Agent("");
    }
}
