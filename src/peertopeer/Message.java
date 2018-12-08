/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author steven
 */
public class Message
{

    private final String from;
    private final String to;
    private String content = "";
    private MessageType type;

    public Message(String from, MessageType type)
    {
        this(from, null, type);
    }

    public Message(String from, String to, MessageType type)
    {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public void append(String appendWith)
    {
        if (appendWith != null && appendWith.length() > 0)
        {
            content = content + appendWith;
        }
    }

    public String getTo()
    {
        return to;
    }

    public String getFrom()
    {
        return from;
    }

    public String getContent()
    {
        return content;
    }

    @Override
    public String toString()
    {
        //
        //Very simple message format for transmission...
        //
        return String.format("FROM:#%s#,TO:#%s#,CONTENT:#%s#,TYPE:#%s#",
                from,
                String.join(",", to),
                content,
                type);
    }

    static final Pattern MESSAGE_REGEX_PATTERN = Pattern.compile("^FROM:#([A-Za-z]+)#(,TO:#([A-Za-z]*)#)?,CONTENT:#(.*)#,TYPE:#(.*)#$");

    public static Message parseMessage(String rawMessage)
    {
        System.out.println("Raw Message - " + rawMessage);
        Message newMessage = null;

        Matcher matcher = MESSAGE_REGEX_PATTERN.matcher(rawMessage);

        if (matcher.matches())
        {
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(3));
//            System.out.println(matcher.group(4));
//            System.out.println(matcher.group(5));
            newMessage = new Message(matcher.group(1), matcher.group(3), MessageType.valueOf(matcher.group(5)));
            newMessage.append(matcher.group(4));
        }

        //append the content
        return newMessage;
    }

      public MessageType getType()
      {
          return type;
      }
}
