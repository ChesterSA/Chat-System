/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import middleware.Portal;
import org.junit.Test;

/**
 *
 * @author Group B
 */
public class PortalTest
{

    Portal p;

    @Test(expected = IllegalArgumentException.class)
    public void connectToEmpty()
    {
        p = new Portal("Test");
        p.connectTo("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToOneBlock()
    {
        p = new Portal("Test");
        p.connectTo("152");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToTwoBlocks()
    {
        p = new Portal("Test");
        p.connectTo("152.105");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToThreeBlocks()
    {
        p = new Portal("Test");
        p.connectTo("152.105.67");
    }

    @Test
    public void connectToFourBlocks()
    {
        p = new Portal("Test");
        p.connectTo("152.105.67.116");
        //If no exception is thrown then IP is valid
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToFiveBlocks()
    {
        p = new Portal("Test");
        p.connectTo("152.105.67.116.123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToInvalidIp()
    {
        p = new Portal("Test");
        p.connectTo("My Home PC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGroup()
    {
        p = new Portal("Test");
        p.connectTo("152.105..119");
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupAbove255()
    {
        p = new Portal("Test");
        p.connectTo("152.105.67.256");
    }
}
