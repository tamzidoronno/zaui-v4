/*
  WiFiTelnetToSerial - Example Transparent UART to Telnet Server for esp8266

  Copyright (c) 2015 Hristo Gochkov. All rights reserved.
  This file is part of the ESP8266WiFi library for Arduino environment.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
#include <ESP8266WiFi.h>

#include <algorithm> // std::min

#ifndef STASSID
#define STASSID "Altibox877070"
#define STAPSK  "uvfDuRQd"
#define SERVER_ADDR "192.168.10.128"
#define SERVER_PORT 4444
#endif

/*
    SWAP_PINS:
   0: use Serial1 for logging (legacy example)
   1: configure Hardware Serial port on RX:GPIO13 TX:GPIO15
      and use SoftwareSerial for logging on
      standard Serial pins RX:GPIO3 and TX:GPIO1
*/

#define BAUD_SERIAL 115200
#define BAUD_LOGGER 115200
#define RXBUFFERSIZE 1024

WiFiClient wifiClient;

////////////////////////////////////////////////////////////


#define logger (&Serial1)

#define STACK_PROTECTOR  512 // bytes

const char* ssid = STASSID;
const char* password = STAPSK;
boolean reconnected = false;
char data[1024];
int readBytes = 0;

#define STACK_PROTECTOR  512 // bytes


void setup() {

  Serial.begin(BAUD_SERIAL);
  Serial.setRxBufferSize(RXBUFFERSIZE);

  logger->begin(BAUD_LOGGER);
  logger->println("\n\nUsing Serial1 for logging");
  logger->println(ESP.getFullVersion());
  logger->printf("Serial baud: %d (8n1: %d KB/s)\n", BAUD_SERIAL, BAUD_SERIAL * 8 / 10 / 1024);
  logger->printf("Serial receive buffer size: %d bytes\n", RXBUFFERSIZE);


  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  logger->print("\nConnecting to ");
  logger->println(ssid);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print('.');
    delay(500);
  }
  
  Serial.println();
  Serial.print("connected, address=");
  Serial.println(WiFi.localIP());

  //start server
  
  logger->print("Ready! Use 'telnet ");
  logger->print(WiFi.localIP());
}

void loop() {
    if(!wifiClient.connected() ) {
       if (!wifiClient.connect(SERVER_ADDR, SERVER_PORT)) {
          delay(5000);
          return;
       }
       reconnected = true;
    }

    if (reconnected) {
        wifiClient.write("token:cd0f1acd-17b3-49ec-beca-811839f9a08b\n");
    }

    // Send data from socket to serial.
    while (wifiClient.available() && Serial.availableForWrite() > 0) {
      // working char by char is not very efficient
      Serial.write(wifiClient.read());
    }
    
    //check UART for data
    size_t maxToTcp = wifiClient.availableForWrite();;
    size_t len = std::min((size_t)Serial.available(), maxToTcp);
    len = std::min(len, (size_t)STACK_PROTECTOR);
    
    if (len) {
      uint8_t sbuf[len];
      size_t serial_got = Serial.readBytes(sbuf, len);
      
      
      // if client.availableForWrite() was 0 (congested)
      // and increased since then,
      // ensure write space is sufficient:
      if (wifiClient.availableForWrite() >= serial_got) {
          size_t tcp_sent = wifiClient.write(sbuf, serial_got);
          if (tcp_sent != len) {
            logger->printf("len mismatch: available:%zd serial-read:%zd tcp-write:%zd\n", len, serial_got, tcp_sent);
          }
      }
  }
    reconnected = false;
    yield();
}
