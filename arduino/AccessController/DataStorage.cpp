/*
 * DataStorage.cpp
 *
 *  Created on: Sep 5, 2019
 *      Author: ktonder
 *
 *
 *  Following datapages are used:
 *  Codes:
 *  0 <= 799 : Access Codes
 *
 *  Logging:
 *  800 <= 900 : Logs
 *
 *  Other:
 *  901 = Loglinenumber
 *  902 = Device id
 *  903 = AutoClosingTime
 *  904 = Gid.
 *  905 = EncryptionKey.
 *  906 = Is encryptioncode set.
 *
 */

#include <Arduino.h>
#include <Wire.h>
#include "DataStorage.h"
#include <avr/wdt.h>

#define eeprom1 0x54
#define WRITE_CNT 5

int pagesize = 16;

void(* restartAfterResteCompleted) (void) = 0;//declare reset function at address 0

// Contstructor
DataStorage::DataStorage() {
}

void DataStorage::writeEEPROMPage(unsigned int eeaddress, unsigned char* data)
{
	Wire.beginTransmission(eeprom1);

	Wire.write((int)((eeaddress) >> 8));   // MSB
	Wire.write((int)((eeaddress) & 0xFF)); // LSB

	for (int i=0; i<16; i++) {
		Wire.write((byte) data[i]);
	}

	Wire.endTransmission();
	delay(11);  // needs 5ms for page write
}

void DataStorage::setupDataStorageBus() {
	Wire.begin();
	Wire.setClock(400000);
 // this->resetAll();
}

void DataStorage::readEEPROM(int deviceaddress, unsigned int eeaddress, unsigned char* data, unsigned int num_chars) {
  unsigned char i=0;
  Wire.beginTransmission(deviceaddress);
  Wire.write((int)(eeaddress >> 8));   // MSB
  Wire.write((int)(eeaddress & 0xFF)); // LSB
  Wire.endTransmission();

  Wire.requestFrom(deviceaddress,num_chars);

  while(Wire.available()) data[i++] = Wire.read();
}

void DataStorage::getCode(unsigned int slot, unsigned char* buffer) {
   	 this->readEEPROM(eeprom1, slot*pagesize, buffer, pagesize);
}

unsigned int DataStorage::handleCodeMessage(unsigned char* msgFromGateWay) {

	if (msgFromGateWay[0] == 'C') {


		unsigned int slot = msgFromGateWay[1];
		slot = slot * 256 + msgFromGateWay[2];

		unsigned char codeBuf[16];

		codeBuf[0] = msgFromGateWay[3];
		codeBuf[1] = msgFromGateWay[4];
		codeBuf[2] = msgFromGateWay[5];
		codeBuf[3] = msgFromGateWay[6];
		codeBuf[4] = msgFromGateWay[7];
		codeBuf[5] = msgFromGateWay[8];
		codeBuf[6] = msgFromGateWay[9];
		codeBuf[7] = msgFromGateWay[10];
		codeBuf[8] = msgFromGateWay[11];
		codeBuf[9] = msgFromGateWay[12];
		codeBuf[10] = msgFromGateWay[13];
		codeBuf[11] = 0xFF;
		codeBuf[12] = 0xFF;
		codeBuf[13] = 0xFF;
		codeBuf[14] = 0xFF;
		codeBuf[15] = 0xFF;

		this->writeCode(slot, codeBuf);

		return slot;
	}

	return 0;
}

void DataStorage::writeCode(unsigned int slot, unsigned char* str_data) {
	 writeEEPROMPage(slot*pagesize, str_data);
}

void DataStorage::resetAll() {

	unsigned char blank[16] = {
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	// Device Id
	writeCode(901, blank);
	writeCode(902, blank);
	writeCode(903, blank);

	// Should we delete this or not, gid encryptionkey etc.. security risk?
	writeCode(904, blank);
	writeCode(905, blank);
	writeCode(906, blank);


	// Autoclose millis
	writeCode(5501, blank);

	deleteAllCodes();

	deleteAllLogs();

	unsigned char defaultCode[16] = {
		0x01, 0x02, 0x03, 0x04,
		0x05, 0x06, 0x07, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	writeCode(1, defaultCode);

	delay(1000);

	restartAfterResteCompleted();
}

void DataStorage::deleteAllLogs() {
	unsigned char blank[16] = {
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00
	};

	for (unsigned int i=800; i<=901; i++) {
		writeCode(i, blank);
		wdt_reset();
	}
}

void DataStorage::deleteAllCodes() {
	unsigned char blank[16] = {
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	for (unsigned int i=0; i<=800; i++) {
		char buf[4];
		itoa(i, buf, 10);

		blank[1] = buf[0];
		blank[2] = buf[1];
		blank[3] = buf[2];
		blank[4] = buf[3];
		writeCode(i, blank);
		wdt_reset();
	}
}
