package middleware;

/**
 * Interface used by clients to ensure they can be contacted by the network items
 * @author Group B
 */
public interface Contactable
{
    /**
     * Check the message information and react accordingly
     * @param m the message being handled
     */
    public void handleMessage(Message m);
    
    /**
     * Send a message from this class to any other agent on the network
     * @param to the handle of the agent being sent to
     * @param content the content of the message to send
     */
    public void sendMessage(String to, String content);
    
    public void connectTo(String ip);
}
