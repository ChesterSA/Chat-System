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
 * @author s6089488
 */
public class PortalTest
{

    Portal a;

    @Test(expected = IllegalArgumentException.class)
    public void connectToEmpty()
    {
        Portal a = new Portal("Test");
        a.connectTo("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToOneBlock()
    {
        Portal a = new Portal("Test");
        a.connectTo("152");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToTwoBlocks()
    {
        Portal a = new Portal("Test");
        a.connectTo("152.105");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToThreeBlocks()
    {
        Portal a = new Portal("Test");
        a.connectTo("152.105.67");
    }

    @Test
    public void connectToFourBlocks()
    {
        Portal a = new Portal("Test");
        a.connectTo("152.105.67.116");
        //If no exception is thrown then IP is valid
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToFiveBlocks()
    {
        Portal a = new Portal("Test");
        a.connectTo("152.105.67.116.123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectToInvalidIp()
    {
        Portal a = new Portal("Test");
        a.connectTo("My Home PC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGroup()
    {
        Portal a = new Portal("Test");
        a.connectTo("152.105..119");
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupAbove255()
    {
        Portal a = new Portal("Test");
        a.connectTo("152.105.67.256");
    }
}
