/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertopeer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author steven
 */
class Connection {
    private String handle;
    private final Socket socket;
    private final InputStream clientSocketInputStream; 
    private final InputStreamReader clientSocketInputStreamReader;
    private final BufferedReader clientSocketBufferedReader;
    PrintWriter clientPrintWriter;
                                            
    
    // Create a partially connected connection.
    // The handle is not yet known.
    Connection(Socket socket) throws IOException {
        this((String)null, socket);
    }
    
    Connection(String handle, Socket socket) throws IOException {
        this.handle = handle;
        this.socket = socket;
        clientSocketInputStream = this.socket.getInputStream();
        clientSocketInputStreamReader = new InputStreamReader(clientSocketInputStream);
        clientSocketBufferedReader = new BufferedReader(clientSocketInputStreamReader);
        clientPrintWriter = new PrintWriter(this.socket.getOutputStream(), true);
        System.out.println("Connection established with " + handle);
    }
    
    public void setHandle(final String handle) {
        if(this.handle == null && handle != null)
            this.handle = handle;
    }
    
    public String getHandle() { return handle; }
    
    public void sendMessage(Message message) {
        clientPrintWriter.println(message.toString());
    }
    
    public Message receiveMessage() throws IOException {
        return Message.parseMessage(clientSocketBufferedReader.readLine());
    }
    
    public boolean hasMessage() throws IOException {
        return clientSocketInputStream.available() > 0;
    }
    
    public boolean hasIpAddress(final String ipAddress) {
        return socket.getInetAddress().getHostAddress().compareTo(ipAddress) == 0;
    }
}
