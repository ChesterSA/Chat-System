/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import middleware.Agent;
import middleware.Portal;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Group B
 */
public class AgentTest
{
    //The that the agents are all connected to
    Portal p;
    
    @Before
    public void initialisePortal()
    {
        p = new Portal("portal");
    }
    
    @Test
    public void validHandle()
    {
        Agent a = new Agent("AgentName", p);
        String expected = "AgentName";
        String actual = a.getHandle();
        
        assertEquals(expected, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleWithSpaces()
    {
        Agent a = new Agent("Agent name", p);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleWithNumbers()
    {
        Agent a = new Agent("name1", p);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleEmpty()
    {
        Agent a = new Agent("", p);
    }
}
