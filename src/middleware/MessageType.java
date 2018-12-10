/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    HELLO, HELLOACK, PORTAL, PORTALACK, AGENT, DIR, BROADCAST, STANDARD
}
