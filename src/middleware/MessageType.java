package middleware;

/**
 * Enum for the message type
 *
 * @author Group B
 */
public enum MessageType
{

    /**
     * A message to request a portal-portal connection
     */
    PORTAL,
    /**
     * An acknowledgement to confirm a portal-portal connection
     */
    PORTALACK,
    /**
     * A message to setup an agent-portal connection
     */
    AGENT,
    /**
     * A message sent by a directory containing a formatted list of IPs
     */
    DIR,
    /**
     * A broadcast message, to be sent to every agent on every connected portal
     */
    BROADCAST,
    /**
     * A standard message sent from an agent to another agent
     */
    STANDARD
}
