/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackage;

import static junit.framework.Assert.assertEquals;
import middleware.Message;
import middleware.MessageType;
import org.junit.Test;

/**
 *
 * @author s6089488
 */
public class MessageTest
{
    @Test
    public void validMessage()
    {
        Message m = new Message("me", "you", MessageType.STANDARD);
        m.append("messageContent");
        
        String expected = "messageContent";
        String actual = m.getContent();
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void EmptyContent()
    {
        Message m = new Message("me", "you", MessageType.STANDARD);
        
        String expected = "";
        String actual = m.getContent();
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void messagewithNumbers()
    {
        Message m = new Message("me", "you", MessageType.STANDARD);
        m.append("m3ssag3");
        
        String expected = "m3ssag3";
        String actual = m.getContent();
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void messagewithSpacess()
    {
        Message m = new Message("me", "you", MessageType.STANDARD);
        m.append("test message");
        
        String expected = "test message";
        String actual = m.getContent();
        
        assertEquals(expected, actual);
    }
}
