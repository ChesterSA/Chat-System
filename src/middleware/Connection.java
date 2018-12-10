/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author v8269590
 */
class Connection
{

    /**
     * Identifying string of connection.
     */
    private String handle;
    /**
     * Socket used by connection.
     */
    public final Socket socket;
    /**
     * Stream of data (in bytes) to be processed through connection.
     */
    private final InputStream clientSocketInputStream;
    /**
     * Reader object to parse InputStream byte data into character data.
     */
    private final InputStreamReader clientSocketInputStreamReader;
    /**
     * Reader object wrapping InputStreamReader, buffering the data being read
     * by the reader, increasing efficiency of the reading process.
     */
    private final BufferedReader clientSocketBufferedReader;
    /**
     * Sends data to stream of text, used to send messages through connection.
     */
    private final PrintWriter clientPrintWriter;

    /**
     * Creates partial connection, where handle is not known.
     * @param socket Socket of connection.
     * @throws IOException Handles errors of input-output.
     */
    Connection(Socket socket) throws IOException
    {
        this((String) null, socket);
    }

    /**
     * Creates connection, giving an identifying string and socket of connection.
     * @param handle String handle of connection
     * @param socket Socket of connection.
     * @throws IOException Handles errors of input-output.
     */
    Connection(String handle, Socket socket) throws IOException
    {
        this.handle = handle;
        this.socket = socket;
        clientSocketInputStream = this.socket.getInputStream();
        clientSocketInputStreamReader = new InputStreamReader(clientSocketInputStream);
        clientSocketBufferedReader = new BufferedReader(clientSocketInputStreamReader);
        clientPrintWriter = new PrintWriter(this.socket.getOutputStream(), true);
        //System.out.println("Connection established with " + handle);
    }

    /**
     * Sets handle of connection, when the current handle is null and new handle
     * is given.
     * @param handle New handle of connection.
     */
    public void setHandle(final String handle)
    {
        if (this.handle == null && handle != null)
        {
            this.handle = handle;
        }
    }

    /**
     * Gets handle of connection.
     * @return String handle of connection.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Sends message through connection by sending message through connection's
     * PrintWriter object.
     * @param message Message being sent.
     */
    public void sendMessage(Message message)
    {
        //System.out.println("---connection is sending message From:" + message.getFrom() + " To:" + message.getTo() + " Content:" + message.getContent() + " Type:" + message.getType());
        //System.out.println("---connection details: " + this.socket.toString());
        clientPrintWriter.println(message.toString());
    }

    /**
     * Recieves message through connection by reading data from connection's
     * BufferedReader object.
     * @return Message recieved.
     * @throws IOException Handles errors of input-output.
     */
    public Message receiveMessage() throws IOException
    {
        Message m = Message.parseMessage(clientSocketBufferedReader.readLine());
        //System.out.println("---" + m.getType());
        return m;
    }

    /**
     * Checks whether the connection has a message to be processed.
     * @return If message is present.
     * @throws IOException Handles errors of input-output.
     */
    public boolean hasMessage() throws IOException
    {
        return clientSocketInputStream.available() > 0;
    }

    /**
     * Checks whether connection has specified IP address as host address in
     * socket.
     * @param ipAddress IP to be checked.
     * @return If connection has IP address.
     */
    public boolean hasIpAddress(final String ipAddress)
    {
        return socket.getInetAddress().getHostAddress().compareTo(ipAddress) == 0;
    }
}
