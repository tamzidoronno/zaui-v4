#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <arpa/inet.h> 

#include <errno.h>
#include <fcntl.h> 
#include <termios.h>


int set_interface_attribs(int fd, int speed)
{
    struct termios tty;

    if (tcgetattr(fd, &tty) < 0) {
        printf("Error from tcgetattr: %s\n", strerror(errno));
        return -1;
    }

    cfsetospeed(&tty, (speed_t)speed);
    cfsetispeed(&tty, (speed_t)speed);

    tty.c_cflag |= (CLOCAL | CREAD);    /* ignore modem controls */
    tty.c_cflag &= ~CSIZE;
    tty.c_cflag |= CS8;         /* 8-bit characters */
    tty.c_cflag &= ~PARENB;     /* no parity bit */
    tty.c_cflag &= ~CSTOPB;     /* only need 1 stop bit */
    tty.c_cflag &= ~CRTSCTS;    /* no hardware flowcontrol */

    /* setup for non-canonical mode */
    tty.c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL | IXON);
    tty.c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
    tty.c_oflag &= ~OPOST;

    /* fetch bytes as they become available */
    tty.c_cc[VMIN] = 1;
    tty.c_cc[VTIME] = 1;

    if (tcsetattr(fd, TCSANOW, &tty) != 0) {
        printf("Error from tcsetattr: %s\n", strerror(errno));
        return -1;
    }
    return 0;
}

void set_mincount(int fd, int mcount)
{
    struct termios tty;

    if (tcgetattr(fd, &tty) < 0) {
        printf("Error tcgetattr: %s\n", strerror(errno));
        return;
    }

    tty.c_cc[VMIN] = mcount ? 1 : 0;
    tty.c_cc[VTIME] = 5;        /* half second timer */

    if (tcsetattr(fd, TCSANOW, &tty) < 0)
        printf("Error tcsetattr: %s\n", strerror(errno));
}

int numPlaces (int n) {
    if (n < 0) return numPlaces ((n == 0) ? 90000: -n);
    if (n < 10) return 1;
    return 1 + numPlaces (n / 10);
}

int checkBuf(unsigned char* buf) {
    for (int i=0; i<256; i++) {

       if (i <= 253 && buf[i] == '+' && buf[i+1] == 'O' && buf[i+2] == 'K') {
	   int totalLength = 5;

	   for (int j=0; j<totalLength; j++) {
               buf[j] = buf[i+j];
	   }
	   printf("Its OK\n");
           return totalLength;
       }

       int maxSize = 252 -7 -4;
       if (i <= maxSize && buf[i] == '+' && buf[i+1] == 'R' && buf[i+2] == 'C' && buf[i+3] == 'V') {
            maxSize = maxSize - 7 - 4;
            unsigned char fromBuf[10];
            memset(fromBuf, 0, sizeof fromBuf);
	    
            for (int j=0; j<7; j++) {
               if (buf[i+j+5] == ',') {
                    break;
                } else {
                    fromBuf[j] = buf[i+j+5];
                }
            }

	    int deviceId = atoi(fromBuf);
	    int numberOfPositions = numPlaces(deviceId);

            if (i > maxSize - numberOfPositions) {
                break;
            }

            memset(fromBuf, 0, sizeof fromBuf);
            for (int j=0; j<7; j++) {
               if (buf[i+j+5+numberOfPositions+1] == ',') {
                    break;
                } else {
                    fromBuf[j] = buf[i+j+5+numberOfPositions+1];
                }
            }

	    int datasize = atoi(fromBuf);
	    int numberOfPositions2 = numPlaces(datasize);

	    int totalLength = numberOfPositions + numberOfPositions2 + datasize + 15 + 1;
            if (i > maxSize - totalLength) {
                break;
            }

	    
	    for (int j=0; j<totalLength; j++) {
                buf[j] = buf[i+j];
	    }

            printf("Total data: %02x %s\n", buf, buf);
            return totalLength;
       }
    }


    return 0;
}

int main(int argc, char *argv[])
{
    char *portname = "/dev/ttyAMA0";
    int fd;
    int wlen;

    fd = open(portname, O_RDWR | O_NOCTTY | O_SYNC);
    if (fd < 0) {
        printf("Error opening %s: %s\n", portname, strerror(errno));
        return -1;
    }
    /*baudrate 115200, 8 bits, no parity, 1 stop bit */
    set_interface_attribs(fd, B115200);
    //set_mincount(fd, 0);                /* set to pure timed read */

    /* simple output */
    wlen = write(fd, "Hello!\n", 7);
    if (wlen != 7) {
        printf("Error from write: %d, %d\n", wlen, errno);
    }
    tcdrain(fd);    /* delay for output */

    int sockfd = 0, n = 0;
    char recvBuff[1024];
    struct sockaddr_in serv_addr; 

    if(argc != 2)
    {
        printf("\n Usage: %s <ip of server> \n",argv[0]);
        return 1;
    } 

    memset(recvBuff, '0',sizeof(recvBuff));
    if((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        printf("\n Error : Could not create socket \n");
        return 1;
    } 

    memset(&serv_addr, '0', sizeof(serv_addr)); 

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(4444); 

    if(inet_pton(AF_INET, argv[1], &serv_addr.sin_addr)<=0)
    {
        printf("\n inet_pton error occured\n");
        return 1;
    } 

    if( connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
       printf("\n Error : Connect Failed \n");
       return 1;
    } 

    write(sockfd, "token:481c9a46-4f34-41c9-bf27-f77896c8062c\r\n", 44 );

    if (fork() == 0) {
        printf("Child should listen for messages from serial port %d\n", 2); 
        unsigned char buf2[256];
        
	do {
            unsigned char buf[80];
            int rdlen;
    
            rdlen = read(fd, buf, 1);
            if (rdlen > 0) {
	        for (int g=0; g<255; g++) {
                    buf2[g] = buf2[g+1];
                }

		buf2[255] = buf[0];
		int totalLength = checkBuf(buf2);
		if (totalLength > 0) {
                    write(sockfd, buf2, totalLength );
                    memset(buf2, 0, sizeof buf2);
                }
            } else if (rdlen < 0) {
                printf("Error from read: %d: %s\n", rdlen, strerror(errno));
            } else {  /* rdlen == 0 */
                printf("Timeout from read\n");
            }               
            /* repeat read to get full message */
        } while (1);

    } else {
        do {
            printf("Parent is lisetening for messages from socket= %d\n", 1); 
            memset(recvBuff, 0, sizeof recvBuff);
            while ( (n = read(sockfd, recvBuff, sizeof(recvBuff)-1)) > 0)
            {
             

	    write(fd, recvBuff, n);
	    printf("Sent command to device, lenght: %i\n", n);
                recvBuff[n] = 0;
                if(fputs(recvBuff, stdout) == EOF)
                {
                    printf("\n Error : Fputs error\n");
                }
            } 

            if(n < 0)
            {
                printf("\n Read error \n");
            } 
        } while(1);
    }

    return 0;
}
