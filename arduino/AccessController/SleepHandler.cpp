#include <Arduino.h>
#include "SleepHandler.h"
#include <avr/wdt.h>


void SleepHandler::wakeupLora() {
	if (loraAwake) {
		return;
	}

	pinMode(17, OUTPUT);

	int resetLoraChipPin = PB5;

	pinMode(resetLoraChipPin, OUTPUT);
	delay(500);
	digitalWrite(17, HIGH);
	digitalWrite(resetLoraChipPin, LOW);
	delay(500);
	digitalWrite(17, LOW);
	digitalWrite(resetLoraChipPin, HIGH);

	delay(500);
	digitalWrite(17, HIGH);
	setDeviceIdToLoraChip();
	delay(500);
	digitalWrite(17, LOW);

	String networkId = "AT+NETWORKID=";
	networkId = networkId + getNetworkId();
	networkId = networkId + "\r\n";
	Serial.print(networkId);


	delay(500);
	digitalWrite(17, HIGH);
	Serial.print("AT+MODE=0\r\n");
	delay(500);
	digitalWrite(17, LOW);
	Serial.print("AT+BAND=868500000\r\n");
	delay(500);
	digitalWrite(17, HIGH);
	Serial.print("AT+PARAMETER=10,7,1,7\r\n");
	delay(500);
	digitalWrite(17, LOW);

	wdt_reset();

	loraAwake = true;
}

/**
 * This is not in use as the battery operated devices has been scrapped.
 */
void SleepHandler::delaySleepWithMs(unsigned long tstamp) {}

void SleepHandler::initalize(bool powerSaveMode, DataStorage *dataStorage) {
  this->_dataStorage = dataStorage;
}

/**
 * This is not in use as we dont sleep as the battery operated devices has been scrapped.
 */
bool SleepHandler::checkSleep() {
	return false;
}

/**
 * We store the deviceid to the EEPROM and initialize the chip with it.
 *
 * This is to make sure the device restarts with the correct deviceid.
 */
void SleepHandler::setDeviceIdToLoraChip() {
	String address = "AT+ADDRESS=";
	address = address + getDeviceId();
	address = address + "\r\n";
	Serial.print(address);
}


unsigned int SleepHandler::getDeviceId() {
	unsigned char buf[16];
	this->_dataStorage->getCode(902, buf);

	if (buf[0] != 0x44) {
		return 65000;
	}

	char retVal[6];
	retVal[0] = buf[1];
	retVal[1] = buf[2];
	retVal[2] = buf[3];
	retVal[3] = buf[4];
	retVal[4] = buf[5];
	retVal[5] = buf[6];

	return atoi(retVal);
}

int SleepHandler::getNetworkId() {
	unsigned char buf[16];
	this->_dataStorage->getCode(907, buf);

	if (buf[0] == 0x00 || buf[0] == 0xFF) {
		return 5;
	}

	return buf[0];
}

