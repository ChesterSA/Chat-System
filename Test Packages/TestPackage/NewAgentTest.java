/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import static junit.framework.Assert.assertEquals;
import middleware.NewAgent;
import middleware.NewPortal;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author cswan
 */
public class NewAgentTest
{
    NewPortal p;
    
    @Before
    public void initialisePortal()
    {
        p = new NewPortal("portal");
    }
    
    @Test
    public void validHandle()
    {
        NewAgent a = new NewAgent("AgentName", p);
        String expected = "AgentName";
        String actual = a.getHandle();
        
        assertEquals(expected, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleWithSpaces()
    {
        NewAgent a = new NewAgent("Agent name", p);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleWithNumbers()
    {
        NewAgent a = new NewAgent("name1", p);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void handleEmpty()
    {
        NewAgent a = new NewAgent("", p);
    }
}
