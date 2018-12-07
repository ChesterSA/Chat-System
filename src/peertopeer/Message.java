/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author steven
 */
public class Message
{

    private final String from;
    private final ArrayList<String> to;
    private String content = "";

    
    
    public Message(String from)
    {
        this(from, null);
    }

    public Message(String from, String to)
    {
        this.from = from;
        this.to = new ArrayList<>();
        if (to != null && to.length() > 0)
        {
            this.to.add(to);
        }
    }

    public void append(String appendWith)
    {
        if (appendWith != null && appendWith.length() > 0)
        {
            content = content + appendWith;
        }
    }

    public List<String> getTo()
    {
        return Collections.unmodifiableList(to);
    }

    public String getFrom()
    {
        return from;
    }

    public String getContent()
    {
        return content;
    }

    public boolean isBroadcast()
    {
        return to.isEmpty();
    }

    public boolean isHelloMessage()
    {
        return to.isEmpty() && content.compareTo("HELLO") == 0;
    }
    
    public boolean isAgentsMessage()
    {
        return to.isEmpty() && content.compareTo("AGENT") == 0;
    }
    
    public boolean isDirMessage()
    {
        Pattern ipPattern = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3},)+");
        Matcher m = ipPattern.matcher(content);
        return m.matches();
    }

    public boolean isHelloAckMessage()
    {
        return content.compareTo("HELLOACK") == 0;
    }
    
    public boolean isGetMessage()
    {
        return content.compareTo("GET") == 0;
    }

    @Override
    public String toString()
    {
        //
        //Very simple message format for transmission...
        //
        return String.format("FROM:#%s#,TO:#%s#,CONTENT:#%s#",
                from,
                String.join(",", to),
                content);
    }
    static final Pattern MESSAGE_REGEX_PATTERN = Pattern.compile("^FROM:#([A-Za-z]+)#(,TO:#([A-Za-z]*)#)?,CONTENT:#(.+)#$");

    
    public static Message parseMessage(String rawMessage)
    {
        System.out.println("RAW MESSAGE: " + rawMessage);
        Message newMessage = null;

        Matcher matcher = MESSAGE_REGEX_PATTERN.matcher(rawMessage);

        if (matcher.matches())
        {
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(3));
//            System.out.println(matcher.group(4));
            newMessage = new Message(matcher.group(1), matcher.group(3));
            newMessage.append(matcher.group(4));
        }

        //append the content
        return newMessage;
    }

    public static Message createHelloMessage(final String from)
    {
        Message helloMessage = new Message(from);
        helloMessage.content = "HELLO";
        return helloMessage;
    }

    public static Message createAgentMessage(final String from)
    {
        Message agentMessage = new Message(from);
        agentMessage.content = "AGENT";
        return agentMessage;
    }
    
    public static Message createHelloAckMessage(final String from, final String to)
    {
        Message helloAckMessage = new Message(from, to);
        helloAckMessage.content = "HELLOACK";
        return helloAckMessage;
    }
}
