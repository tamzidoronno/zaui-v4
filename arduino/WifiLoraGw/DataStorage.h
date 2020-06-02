/**
 * The datastorage is keeping track of the following
 * 
 *  - ESSID
 *  - Wifi Password
 *  - Token
 *  - Address to getshop system
 */
 
#ifndef DataStorage_H
#define DataStorage_H

#include <EEPROM.h>
#include <Arduino.h>  // for type definitions

int addrEE = 0x0100;
unsigned long resetTimer = 0;

struct {
  byte configured = NULL;
  char token[37] = "";
  char essid[50] = "";
  char password[50] = "";
  char address[250] = "";
} config;

void setupDatastorage() {
   pinMode(4, INPUT);
   digitalWrite(4, HIGH);
   EEPROM.begin(512);
}
  
void readConfig() {
  EEPROM.get(0, config);
}

void saveConfig() {
  EEPROM.put(0, config);
  EEPROM.commit();  
}

void resetDevice() {
  memset(config.essid, 0x00, sizeof(config.essid));
  memset(config.password, 0x00, sizeof(config.password));
  config.configured = 0x00;
  saveConfig();
}

boolean isConfigured() {
  return config.configured == 0x01;
}

void checkReset() {
  if (digitalRead(4) == LOW && resetTimer == 0) {
    resetTimer = millis() + 2000;
  }

  if (resetTimer == 0) {
    return;
  }

  unsigned long now = millis();
  if (digitalRead(4) == HIGH) {
    resetTimer = 0;
  }

  if (millis() > resetTimer) {
    resetDevice();
    ESP.restart();
  }
}

#endif
