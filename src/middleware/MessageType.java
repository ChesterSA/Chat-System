package middleware;

/**
 * Enum for the message type
 * Hello - Standard handshake initiator
 * HelloAck - Acknowledgement of the handshake
 * Portal - Portal handshake initiator
 * PortalAck - Acknowledgement of the portal handshake
 * Agent - Agent's handshake initiator
 * Dir - Directory message, contains list of ips to connect to
 * Broadcast - Message to be sent to every agent and portal
 * Standard - Standard message from a node to another node
* @author Group B
 */
public enum MessageType
{

    /**
     * A Hello message to request a standard connection
     */
    HELLO,

    /**
     * An acknowledgement of the hello message to confirm a connection
     */
    HELLOACK,

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
