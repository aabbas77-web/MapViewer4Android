//
// Created by AZUS on 10/25/2021.
//

#include "tcp_client.h"

#include<stdio.h>
#include<string.h>	//strlen
#include<sys/socket.h>
#include<arpa/inet.h>	//inet_addr

#include "tcp_lib.h"

static bool terminated = false;

tcp_client::tcp_client(){

}

tcp_client::~tcp_client(){

}

int tcp_client::connect(const char *ip, int port)
{
    try{
        int socket_desc;
        struct sockaddr_in server;
        char *message , server_reply[2000];
        terminated = false;

        //Create socket
        socket_desc = socket(AF_INET , SOCK_STREAM , 0);
        if (socket_desc == -1)
        {
            addError("Could not create socket");
        }

        server.sin_addr.s_addr = inet_addr(ip);
        server.sin_family = AF_INET;
        server.sin_port = htons( port );

        //Connect to remote server
        if (::connect(socket_desc , (struct sockaddr *)&server , sizeof(server)) < 0)
        {
            addError("connect error");
            return 1;
        }

        addError("Connected\n");

        //Send some data
        message = "GET / HTTP/1.1\r\n\r\n";
        if( send(socket_desc , message , strlen(message) , 0) < 0)
        {
            addError("Send failed");
            return 1;
        }
        addError("Data Send\n");

        //Receive a reply from the server
        if( recv(socket_desc, server_reply , 2000 , 0) < 0)
        {
            addError("recv failed");
        }
        addError("Reply received\n");
        addError(server_reply);
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
    return 0;
}

int tcp_client::stop(){
    terminated = true;
}
