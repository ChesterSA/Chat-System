#!/usr/bin/env python

import socket
import sys
import re # for regular expressions

REMOTE_IP = '192.168.0.2'
PORT = 9090
MESSAGE_REGEX = re.compile("FROM:\#([A-Za-z]+)\#(,TO:\#([A-Za-z]+)\#)?,CONTENT:\#(.+)\#$")
RECV_BUF_READ = 4098

#create socket
def create_socket(ip = REMOTE_IP, port = PORT):
    connection_info = (ip, port)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(connection_info)
    return s

def generate_message(sender, receiver, content):
    message_as_str = "FROM:#{}#,TO:#{}#,CONTENT:#{}#\n".format(sender, receiver, content)
    print("generate_message: " + message_as_str)
    return bytes(message_as_str, 'utf-8')

def receive_all(recv_socket):
    all_data = b''
    part_data = b''
    try:
        while True:
            part_data += recv_socket.recv(RECV_BUF_READ)
            if not part_data:
                # Unreliable
                break
            else:
                all_data += part_data
                if len(part_data) < RECV_BUF_READ:
                    #no more to read
                    break
    except Exception:
        print("Error receiving data from socket" + part_data, file=sys.stderr)
        if all_data:
            pass
        else:
            return (False, None)
    return (all_data.decode('utf-8'), len(all_data))

HANDLE = input("Please enter my handle: ").strip()

if len(HANDLE) > 0:
    chat_socket = create_socket()

    if chat_socket:

        chat_socket.sendall(generate_message(HANDLE, "", "HELLO"))

        while True:
            incoming_message, incoming_message_len = receive_all(chat_socket)

            print(incoming_message, incoming_message)
            
            if incoming_message and incoming_message_len > 0:
                
                message_data = MESSAGE_REGEX.match(incoming_message)

                print("Message from {}: {}".format(message_data[1], message_data[4]))

                #echo the message back
                chat_socket.sendall(generate_message(HANDLE, message_data[1], message_data[4]))
            else:
                print("Unable to receive message from peer", file=sys.stderr)
                break
