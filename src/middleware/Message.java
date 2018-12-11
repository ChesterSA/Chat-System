package middleware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The message object that is passed along the network by ChatNodes
 * @Author Group B
 */
public class Message
{

    private final String from;
    private final String to;
    private String content = "";
    private MessageType type;

    /**
     *
     * @param from who is sending the message
     * @param type the type of message
     */
    public Message(String from, MessageType type)
    {
        this(from, null, type);
    }

    /**
     *
     * @param from this is the sender of the message
     * @param to this is the destination of the message
     * @param type type of message that is being sent
     */
    public Message(String from, String to, MessageType type)
    {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    /**
     * this adds to the end of the contents of the message
     * @param appendWith 
     */
    public void append(String appendWith)
    {
        if (appendWith != null && appendWith.length() > 0)
        {
            content = content + appendWith;
        }
    }

    /**
     * gets the destination of the message
     * @return  the destination of the message
     */
    public String getTo()
    {
        return to;
    }

    /**
     * this gets the sender of the message
     * @return the handle that sent the message
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * gets the contents of the message 
     * @return the message
     */
    public String getContent()
    {
        return content;
    }

    /**
     * gets the type of message that is sent
     * @return the type that is being sent
     */
    public MessageType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        //
        //Very simple message format for transmission...
        //
        return String.format("FROM:#%s#,TO:#%s#,CONTENT:#%s#,TYPE:#%s#",
                from,
                to,
                content,
                type);
    }

    static final Pattern MESSAGE_REGEX_PATTERN = Pattern.compile("^FROM:#([A-Za-z]+)#(,TO:#([A-Za-z]*)#)?,CONTENT:#(.*)#,TYPE:#(.*)#$");

    /**
     *
     * @param rawMessage
     * @return the message that is going to be sent
     */
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

}
