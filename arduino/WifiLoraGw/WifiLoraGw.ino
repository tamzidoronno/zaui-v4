/*
  WifiLoraGw | Made by Kai Tønder

  This software is acting as a smarthub between the cloud and the 
  smart devices.
*/
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

#include <algorithm> // std::min
#include "DataStorage.h"
#include "WebPages.h"
#include "Errors.h"

#ifndef STASSID
#define SERVER_PORT 4444
#endif

#define BAUD_SERIAL 115200
#define RXBUFFERSIZE 1024

WiFiClient wifiClient;

ESP8266WebServer webServer(80);

////////////////////////////////////////////////////////////

#define STACK_PROTECTOR  512 // bytes

boolean reconnected = false;
char data[1024];
int readBytes = 0;

#define STACK_PROTECTOR  512 // bytes

unsigned long delayUnitil = 0;

void setup() {
  setupErrorLights();
  
  Serial.begin(BAUD_SERIAL);
  setupDatastorage();
  readConfig();
  //resetDevice();
  Serial.setRxBufferSize(RXBUFFERSIZE);

  delay(1000);
  if (!isConfigured()) {
    showScanningWifiLight();
    scanWifi();
    WiFi.softAP("Seros Gateway", "");
    setupWebServer();
  } else {
    WiFi.mode(WIFI_STA);
    WiFi.begin(config.essid, config.password);
  }
}

void setupWebServer() {
  webServer.on("/", std::bind(webPage_home, &webServer));
  webServer.begin();
}

void handleDataFromWifiSocket() {
    if (wifiClient.available()) {
      blinkGreen();
    }
    
    while (wifiClient.available() && Serial.availableForWrite() > 0) {
      Serial.write(wifiClient.read());
    }
}

void handleDataFromUart() {
    //check UART for data
    size_t maxToTcp = wifiClient.availableForWrite();;
    size_t len = std::min((size_t)Serial.available(), maxToTcp);
    len = std::min(len, (size_t)STACK_PROTECTOR);
    
    if (len) {
      uint8_t sbuf[len];
      size_t serial_got = Serial.readBytes(sbuf, len);
    
      if (wifiClient.availableForWrite() >= serial_got) {
          wifiClient.write(sbuf, serial_got);
      }
      blinkGreen();
    }
}

void sendToken() {
  char tokenToSend[200];
  tokenToSend[0] = 't';
  tokenToSend[1] = 'o';
  tokenToSend[2] = 'k';
  tokenToSend[3] = 'e';
  tokenToSend[4] = 'n';
  tokenToSend[5] = ':';

  int len = sizeof(config.token);
  for (int i=0; i<len; i++) {
    tokenToSend[6+i] = config.token[i];  
  }

  tokenToSend[6+len] = '\n';
  wifiClient.write(tokenToSend);
}

void handleNormalOperation() {
    if (delayUnitil > millis()) {
       return;
    }
    checkReset();
  
    while (WiFi.status() != WL_CONNECTED) {
      showWifiNotConnected();
      delayUnitil = millis() + 500;
      return;
    }

    if(!wifiClient.connected() ) {
       if (!wifiClient.connect(config.address, SERVER_PORT)) {
          showNotAbleToConnectToServer();
          delayUnitil = millis() + 500;
          return;
       }
       
       reconnected = true;
    }

    showConnected();
    
    if (reconnected) {
        sendToken();
    }

    handleDataFromWifiSocket();
    handleDataFromUart();
    
    reconnected = false;
}

void loop() {
    if (!isConfigured()) {
      showNotInitializedLight();
      webServer.handleClient();  
    } else {
      handleNormalOperation();
    }

    yield();
}
