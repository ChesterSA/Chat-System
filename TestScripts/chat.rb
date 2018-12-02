#!/usr/bin/env ruby

require 'socket'

def create_socket(ip = REMOTE_IP, port = PORT)
  TCPSocket.new ip, port
end

def generate_message(from,to,content)
  "FROM:\##{from}\#,TO:\##{to}\#,CONTENT:\##{content}\#"
end

REMOTE_IP = '192.168.0.2'
PORT = 9090
MESSAGE_REGEX = Regexp.new("FROM:\#([A-Za-z]+)\#(,TO:\#([A-Za-z]+)\#)?,CONTENT:\#(.+)\#$")

puts "Please enter my handle: "

HANDLE = gets.strip

remote_socket = create_socket

remote_socket.puts generate_message(HANDLE,"","HELLO")

while true
  while line = remote_socket.gets # Read lines from socket
    puts line         # and print them

    message_data = MESSAGE_REGEX.match(line)

    puts "Message received from #{message_data[1]} was: #{message_data[4]}"
  
    #echo back what they've said
    outbound_message = generate_message(HANDLE,message_data[1],message_data[4])

    #puts "SENDING: #{outbound_message}"

    remote_socket.puts outbound_message
  end
end

remote_socket.close

