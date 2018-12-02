To run as peer-to-peer, you will need to run 2 (or more) instances of this app on different computers.

If you have multiple network cards on your computer, you can run as many instances on your computer as network cards, however each instance would have to have the receiveIp address set to 1 of the network cards that has not been used by a previous instance.

The application as-is uses 0.0.0.0 as the receive ip address, which allows connections on any available ip address, this would block both from further use on the given port.

To run from the command line (assumes in the folder where PeerToPeer.jar is located):

java -jar ./PeerToPeer.jar

To use:

1) You need to provide a handle for this "chat node"
2) Select what you want to from the simple list of options!

Note: There is a thread running in the background that is waiting for connections.
