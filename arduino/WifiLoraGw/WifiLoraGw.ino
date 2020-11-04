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

char data[1024];
int readBytes = 0;
bool tokenSent = false;
bool wrongToken = false;
unsigned long timeOut = 1000*60*3;

#define STACK_PROTECTOR  512 // bytes

unsigned long delayUnitil = 0;
unsigned long lastReceivedMessageFromServer = 0;

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
    int availableAccessPoints = 0;
    int n = WiFi.scanNetworks();
    int networks[256];
    
    for (int i = 0; i < n; ++i) {
      if (WiFi.SSID(i) == config.essid) {
        networks[availableAccessPoints] = i;
        availableAccessPoints++;
      }
    }

    int networkToUse = random(0, availableAccessPoints);
    WiFi.mode(WIFI_STA);  
    WiFi.persistent(false);
    WiFi.disconnect(true);
    delay(100);
    WiFi.begin(config.essid, config.password, WiFi.channel(networks[networkToUse]), WiFi.BSSID(networks[networkToUse]));     
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
      String line = wifiClient.readStringUntil('\n');
      if (line.indexOf("WRONG_TOKEN") > -1) {
        off();
        showWrongToken();
        wrongToken = true;
      } else {
        Serial.println(line);
      }

      lastReceivedMessageFromServer = millis();
      blinkGreen();
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

void checkConnectivity() {
  unsigned long millisSinceLastReceived = millis() - lastReceivedMessageFromServer;
  
  if (millisSinceLastReceived > timeOut) {
    ESP.reset();
  }
}

void handleNormalOperation() {
    checkConnectivity();
    
    if (delayUnitil > millis()) {
       delay(1);
       return;
    }

    /**
     * This will reset set device if the 
     * reset button has been pressed for more then
     * two seconds.
     */
    checkReset();

    if (wrongToken) {
      showWrongToken();
      delay(100);
      return;
    }

    if (WiFi.status() != WL_CONNECTED) {
      showWifiNotConnected();
      delayUnitil = millis() + 500;
      return;
    }

    if(!wifiClient.connected()) {
       tokenSent = false;
       wifiClient.connect(config.address, SERVER_PORT);
       showNotAbleToConnectToServer();
       delayUnitil = millis() + 500;
       return;      
    }
     
    if(wifiClient.connected() && !tokenSent) {
      lastReceivedMessageFromServer = millis();
      tokenSent = true;
      sendToken();
      showConnected();
    } 

    handleDataFromWifiSocket();
    handleDataFromUart();
    blinkAlive();
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
