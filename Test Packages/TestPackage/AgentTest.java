/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import static junit.framework.Assert.assertEquals;
import middleware.Agent;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author s6089488
 */
public class AgentTest
{
    Agent a;

    @Test(expected = IllegalArgumentException.class)
    public void connectToEmpty()
    {
        Agent a = new Agent("Test");
        a.connectTo("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToOneBlock()
    {
        Agent a = new Agent("Test");
        a.connectTo("152");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToTwoBlocks()
    {
        Agent a = new Agent("Test");
        a.connectTo("152.105");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToThreeBlocks()
    {
        Agent a = new Agent("Test");
        a.connectTo("152.105.67");
    }

    @Test
    public void connectToFourBlocks()
    {
        Agent a = new Agent("Test");
        a.connectTo("152.105.67.116");
        //If no exception is thrown then IP is valid
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToFiveBlocks()
    {
        Agent a = new Agent("Test");
        a.connectTo("152.105.67.116.123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToInvalidIp()
    {
        Agent a = new Agent("Test");
        a.connectTo("My Home PC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGroup()
    {
        Agent a = new Agent("Test");
        a.connectTo("152.105..119");
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupAbove255()
    {
        Agent a = new Agent("Test");
        a.connectTo("152.105.67.256");
    }
}
