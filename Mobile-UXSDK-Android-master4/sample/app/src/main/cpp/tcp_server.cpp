//
// Created by AZUS on 10/25/2021.
//

#include "tcp_server.h"

#include<cstdio>
#include<cstring>	//strlen
#include<cstdlib>	//strlen
#include<sys/socket.h>
#include<arpa/inet.h>	//inet_addr
#include<unistd.h>	//write

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>

#define _GNU_SOURCE     /* To get defns of NI_MAXSERV and NI_MAXHOST */
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <ifaddrs.h>
#include <unistd.h>
#include <linux/if_link.h>

#include<pthread.h> //for threading , link with lpthread

#include "tcp_lib.h"

static bool terminated = false;
void *server_handler(void *);
void *connection_handler(void *);
static char text[1024];

tcp_server::tcp_server(){

}

tcp_server::~tcp_server(){

}

int tcp_server::listen(int port)
{
    try{
        this->port = port;
        terminated = false;

        pthread_t server_thread;
        if( pthread_create( &server_thread , nullptr ,  server_handler , &port) != 0) {
            addError("could not create server thread");
            return 1;
        }
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
    return 0;
}

int tcp_server::stop(){
    terminated = true;
}

char *get_ip_address(){
    int fd;
    struct ifreq ifr{};

    fd = socket(AF_INET, SOCK_DGRAM, 0);

    /* I want to get an IPv4 IP address */
    ifr.ifr_addr.sa_family = AF_INET;

    /* I want IP address attached to "eth0" */
    strncpy(ifr.ifr_name, "eth0", IFNAMSIZ-1);

    ioctl(fd, SIOCGIFADDR, &ifr);

    close(fd);

    /* display result */
    return inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr);
}

int get_system_interfaces(){
#if __ANDROID_API__ >= 24
    struct ifaddrs *ifaddr;
    int family, s;
    char host[NI_MAXHOST];

    if (getifaddrs(&ifaddr) == -1) {
        addError("getifaddrs");
        exit(EXIT_FAILURE);
    }

    /* Walk through linked list, maintaining head pointer so we
       can free list later. */

    for (struct ifaddrs *ifa = ifaddr; ifa != NULL;
         ifa = ifa->ifa_next) {
        if (ifa->ifa_addr == NULL)
            continue;

        family = ifa->ifa_addr->sa_family;

        /* Display interface name and family (including symbolic
           form of the latter for the common families). */

        sprintf(text, "%-8s %s (%d)\n",
               ifa->ifa_name,
               (family == AF_PACKET) ? "AF_PACKET" :
               (family == AF_INET) ? "AF_INET" :
               (family == AF_INET6) ? "AF_INET6" : "???",
               family);
        addError(text);

        /* For an AF_INET* interface address, display the address. */

        if (family == AF_INET || family == AF_INET6) {
            s = getnameinfo(ifa->ifa_addr,
                            (family == AF_INET) ? sizeof(struct sockaddr_in) :
                            sizeof(struct sockaddr_in6),
                            host, NI_MAXHOST,
                            NULL, 0, NI_NUMERICHOST);
            if (s != 0) {
                sprintf(text, "getnameinfo() failed: %s\n", gai_strerror(s));
                addError(text);
                exit(EXIT_FAILURE);
            }

            sprintf(text, "\t\taddress: <%s>\n", host);
            addError(text);
        } else if (family == AF_PACKET && ifa->ifa_data != NULL) {
            struct rtnl_link_stats *stats = static_cast<rtnl_link_stats *>(ifa->ifa_data);

            sprintf(text, "\t\ttx_packets = %10u; rx_packets = %10u\n"
                   "\t\ttx_bytes   = %10u; rx_bytes   = %10u\n",
                   stats->tx_packets, stats->rx_packets,
                   stats->tx_bytes, stats->rx_bytes);
            addError(text);
        }
    }

    freeifaddrs(ifaddr);
    exit(EXIT_SUCCESS);
#endif /* __ANDROID_API__ >= 24 */
}

/*
 * This will handle connection for this server
 * */
void *server_handler(void *port0)
{
    try {
        int new_socket, c;
        int socket_desc , *new_sock;
        struct sockaddr_in server{} , client{};
        char *message;
        int port = *((int *)port0);

        //Create socket
        socket_desc = socket(AF_INET , SOCK_STREAM , 0);
        if (socket_desc == -1)
        {
            addError("Could not create socket");
        }

//        int enable = 1;
//        if (setsockopt(socket_desc, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int)) < 0){
//            addError("setsockopt(SO_REUSEADDR) failed");
//        }

        //Prepare the sockaddr_in structure
        server.sin_family = AF_INET;
        server.sin_addr.s_addr = INADDR_ANY;
        server.sin_port = htons(port);

        //Bind
        if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
        {
            addError("bind failed");
            return nullptr;
        }
        addError("bind done");
        addError(get_ip_address());
        get_system_interfaces();

        //Listen
        listen(socket_desc , 3);

        //Accept and incoming connection
        addError("Waiting for incoming connections...");
        c = sizeof(struct sockaddr_in);
        while( (new_socket = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c)) )
        {
            addError("Connection accepted");

            //Reply to the client
            message = "Hello Client , I have received your connection. And now I will assign a handler for you\n";
            write(new_socket , message , strlen(message));

            pthread_t sniffer_thread;
            new_sock = static_cast<int *>(malloc(1));
            *new_sock = new_socket;

            if( pthread_create( &sniffer_thread , NULL ,  connection_handler , (void*) new_sock) != 0)
            {
                addError("could not create client thread");
                return nullptr;
            }

            //Now join the thread , so that we dont terminate before the thread
            //pthread_join( sniffer_thread , NULL);
            addError("Handler assigned");

            if(terminated)  break;
        }

        if (new_socket<0)
        {
            addError("accept failed");
            return nullptr;
        }
    } catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
    return nullptr;
}

/*
 * This will handle connection for each client
 * */
void *connection_handler(void *socket_desc)
{
    try{
        //Get the socket descriptor
        int sock = *((int *)socket_desc);
        int read_size;
        char *message , client_message[2000];

        //Send some messages to the client
        message = "Greetings! I am your connection handler\n";
        write(sock , message , strlen(message));

        message = "Now type something and i shall repeat what you type \n";
        write(sock , message , strlen(message));

        //Receive a message from client
        while( (read_size = recv(sock , client_message , 2000 , 0)) > 0 )
        {
            //Send the message back to client
            write(sock , client_message , strlen(client_message));

            if(terminated)  break;
        }

        if(read_size == 0)
        {
            addError("Client disconnected");
            fflush(stdout);
        }
        else if(read_size == -1)
        {
            addError("recv failed");
        }

        //Free the socket pointer
        free(socket_desc);
    }catch(...) {
        rethrow_cpp_exception_as_java_exception();
    }
    return nullptr;
}
