package middleware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The message object that is passed along the network by ChatNodes
 *
 * @Author Group B
 */
public class Message
{

    /**
     * The node that is sending the message
     */
    private final String from;

    /**
     * the node that is receiving the message
     */
    private final String to;

    /**
     * the content of the message
     */
    private String content;

    /**
     * the type of the message
     */
    private MessageType type;

    /**
     * The constructor of an empty message
     *
     * @param from who is sending the message
     * @param type the type of message
     */
    public Message(String from, MessageType type)
    {
        this(from, null, type);
    }

    /**
     * Constructor of a standup message
     *
     * @param from who is sending the message
     */
    public Message(String from)
    {
        this(from, null, MessageType.STANDARD);
    }

    /**
     * Constructor of a standard message with a receiver
     *
     * @param from this is the sender of the message
     * @param to this is the destination of the message
     */
    public Message(String from, String to)
    {

        this(from, to, MessageType.STANDARD);
    }

    /**
     * Constructor that performs validation checks
     *
     * @param from this is the sender of the message
     * @param to this is the destination of the message
     * @param type type of message that is being sent
     */
    public Message(String from, String to, MessageType type)
    {
        if (from == null || MetaAgent.checkHandle(from))
        {
            this.from = from;
        }
        else
        {
            throw new IllegalArgumentException("Invalid Sender");
        }

        if (to == null || MetaAgent.checkHandle(to))
        {
            this.to = to;
        }
        else
        {
            throw new IllegalArgumentException("Invalid Receiver");
        }

        this.type = type;
    }

    /**
     * this adds to the end of the contents of the message
     *
     * @param appendWith
     */
     public void append(String appendWith)
    {
        if (appendWith != null && appendWith.length() > 0)
        {
            if (content == null)
                content = appendWith;
            else
                content += appendWith;
        }
    }

    /**
     * gets the destination of the message
     *
     * @return the destination of the message
     */
    public String getTo()
    {
        return to;
    }

    /**
     * this gets the sender of the message
     *
     * @return the handle that sent the message
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * gets the contents of the message
     *
     * @return the message
     */
    public String getContent()
    {
        return content;
    }

    /**
     * gets the type of message that is sent
     *
     * @return the type that is being sent
     */
    public MessageType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        //Simple message format for transmission...
        return String.format("FROM:#%s#,TO:#%s#,CONTENT:#%s#,TYPE:#%s#",
                from,
                to,
                content,
                type);
    }

    /**
     * A regex to check messages are the correct format
     */
    static final Pattern MESSAGE_REGEX_PATTERN = Pattern.compile("^FROM:#([A-Za-z]+)#(,TO:#([A-Za-z]*)#)?,CONTENT:#(.*)#,TYPE:#(.*)#$");

    /**
     * Parse a message to check it is a valid format
     *
     * @param rawMessage
     * @return the message that is going to be sent
     */
    public static Message parseMessage(String rawMessage)
    {
        Message newMessage = null;

        Matcher matcher = MESSAGE_REGEX_PATTERN.matcher(rawMessage);

        if (matcher.matches())
        {
            newMessage = new Message(matcher.group(1), matcher.group(3), MessageType.valueOf(matcher.group(5))); 

            String content = (matcher.group(4).equals("")) ? "N/A" : matcher.group(4);
            newMessage.append(content);
        }
        
        return newMessage;
    }

}
