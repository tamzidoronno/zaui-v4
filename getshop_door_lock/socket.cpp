/* A simple server in the internet domain using TCP.
myServer.c
D. Thiebaut
Adapted from http://www.cs.rpi.edu/~moorthy/Courses/os98/Pgms/socket.html
The port number used in 51717.
This code is compiled and run on the Raspberry as follows:
   
    g++ -o socket socket.cpp wiegand.cpp -lboost_system -std=c++11 -pthread -lpigpio -lrt ; ./socket
    ./socket

The server waits for a connection request from a client.
The server assumes the client will send positive integers, which it sends back multiplied by 2.
If the server receives -1 it closes the socket with the client.
If the server receives -2, it exits.
*/

#include <stdio.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <thread>


#include <iostream>
#include <unistd.h>
#include <pigpio.h>
#include "wiegand.hpp"

int client_socket[30], client_socket2[30], max_clients = 30;

#define TRUE   1
#define FALSE  0

int createMasterSocket(sockaddr_in address, int port) {

    int opt = TRUE;
    int master_socket;

    if ((master_socket = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    //set master socket to allow multiple connections , this is just a good habit, it will work without this
    if (setsockopt(master_socket, SOL_SOCKET, SO_REUSEADDR, (char *) &opt, sizeof (opt)) < 0) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    //type of socket created
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(port);

    //bind the socket to localhost port 8888
    if (bind(master_socket, (struct sockaddr *) &address, sizeof (address)) < 0) {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }
    printf("Listener on port %d \n", port);

    //try to specify maximum of 3 pending connections for the master socket
    if (listen(master_socket, 3) < 0) {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    return master_socket;
}

void createNewSocket(int master_socket, sockaddr_in address, int addrlen, int max_clients, int *client_socket) {
    int new_socket;

    if ((new_socket = accept(master_socket, (struct sockaddr *) &address, (socklen_t*) & addrlen)) < 0) {
        perror("accept");
        exit(EXIT_FAILURE);
    }

    // inform user of socket number - used in send and receive commands
    //  printf("New connection , socket fd is %d , ip is : %s , port : %d \n" , new_socket , inet_ntoa(address.sin_addr) , ntohs(address.sin_port));

    //send new connection greeting message
//    if (send(new_socket, message, strlen(message), 0) != strlen(message)) {
//        perror("send");
//    }

    puts("Welcome message sent successfully");
    int i;
    
    //add new socket to array of sockets
    for (i = 0; i < max_clients; i++) {
        //if position is empty
        if (client_socket[i] == 0) {
            client_socket[i] = new_socket;
            printf("Adding to list of sockets as %d\n", i);

            break;
        }
    }
}

void handleSocket(sockaddr_in address, int addrlen, int sd, int *client_socket, int i) {
    //Check if it was for closing , and also read the incoming message
    char buffer[1025]; //data buffer of 1K
    int valread;
    
    if ((valread = read(sd, buffer, 1024)) == 0) {
        //Somebody disconnected , get his details and print
        getpeername(sd, (struct sockaddr*) &address, (socklen_t*) & addrlen);
        //printf("Host disconnected , ip %s , port %d \n" , inet_ntoa(address.sin_addr) , ntohs(address.sin_port));

        //Close the socket and mark as 0 in list for reuse
        close(sd);
        client_socket[i] = 0;
    }
        //Echo back the message that came in
    else {
        //set the string terminating NULL byte on the end of the data read
        buffer[valread] = '\0';
        send(sd, buffer, strlen(buffer), 0);
    }
}


void sendData( int sockfd, int x ) {
  int n;

  char buffer[32];
  sprintf( buffer, "%d", x );

  if (write( sockfd, buffer, strlen(buffer) ) < 0) {
     close(sockfd);
  }

  //buffer[n] = '\0';
}

void callback(int bits, uint32_t value) {
   for (int i=0; i<max_clients;i++) {
      sendData( client_socket[i], value );
   }
}

void callback2(int bits, uint32_t value) {
   for (int i=0; i<max_clients;i++) {
      sendData( client_socket2[i], value );
   }
}

int main(int argc, char *argv[]) {
    if (gpioInitialise() < 0) return 1;
     
    Wiegand dec1(20, 21, callback);
    Wiegand dec2(19, 26, callback2);

    fd_set readfds;

    
    int master_socket, addrlen;
    int master_socket2, addrlen2;
    
    struct sockaddr_in address_51517;
    struct sockaddr_in address_51518;

    

    //initialise all client_socket[] to 0 so not checked
    for (int i = 0; i < max_clients; i++) {
        client_socket[i] = 0;
        client_socket2[i] = 0;
    }

    master_socket = createMasterSocket(address_51517, 51517);
    master_socket2 = createMasterSocket(address_51518, 51518);
    
    //accept the incoming connection
    addrlen = sizeof (address_51517);
    addrlen2 = sizeof (address_51518);
    puts("Waiting for connections ...");

    while (TRUE) {
        
        int max_sd;
        
        //clear the socket set
        FD_ZERO(&readfds);

        //add master socket to set
        FD_SET(master_socket, &readfds);
        FD_SET(master_socket2, &readfds);
        
        if (master_socket > master_socket2) {
            max_sd = master_socket;
        } else {
            max_sd = master_socket2;
        }
        

        //add child sockets to set
        for (int i = 0; i < max_clients; i++) {
            //socket descriptor
            int sd = client_socket[i];

            //if valid socket descriptor then add to read list
            if (sd > 0)
                FD_SET(sd, &readfds);

            //highest file descriptor number, need it for the select function
            if (sd > max_sd)
                max_sd = sd;
        }
        
        for (int i = 0; i < max_clients; i++) {
            //socket descriptor
            int sd = client_socket2[i];

            //if valid socket descriptor then add to read list
            if (sd > 0)
                FD_SET(sd, &readfds);

            //highest file descriptor number, need it for the select function
            if (sd > max_sd)
                max_sd = sd;
        }

        //wait for an activity on one of the sockets , timeout is NULL , so wait indefinitely
        int activity = select(max_sd + 1, &readfds, NULL, NULL, NULL);
        
        if ((activity < 0) && (errno != EINTR)) {
            printf("select error");
        }

        //If something happened on the master socket , then its an incoming connection
        if (FD_ISSET(master_socket, &readfds)) {
            createNewSocket(master_socket, address_51517, addrlen, max_clients, client_socket);
        }
        
        if (FD_ISSET(master_socket2, &readfds)) {
            createNewSocket(master_socket2, address_51518, addrlen2, max_clients, client_socket2);
        }

        //else its some IO operation on some other socket :)
        for (int i = 0; i < max_clients; i++) {
            int sd = client_socket[i];

            if (FD_ISSET(sd, &readfds)) {
                // Handle incoming message
                handleSocket(address_51517, addrlen, sd, client_socket, i);
            }
        }
        
        for (int i = 0; i < max_clients; i++) {
            int sd = client_socket2[i];

            if (FD_ISSET(sd, &readfds)) {
                // Handle incoming message
                handleSocket(address_51518, addrlen2, sd, client_socket2, i);
            }
        }
    }

    return 0;

}

