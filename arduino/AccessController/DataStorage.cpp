/*
 * DataStorage.cpp
 *
 *  Created on: Sep 5, 2019
 *      Author: ktonder
 *
 *
 *  Following datapages are used:
 *  Codes:
 *  0 <= 2000 : Access Codes
 *
 *  Logging:
 *  5000 <= 5100 : Logs
 *  5050 = Loglinenumber
 *
 *  Other:
 *  5500 = Device id
 *
 */

#include <Arduino.h>
#include <Wire.h>
#include "DataStorage.h"

#define eeprom1 0x54
#define WRITE_CNT 5

int pagesize = 16;

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
	digitalWrite(PB5, LOW);

	unsigned char blank[16] = {
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	unsigned int deviceIdSlot = 5500;
	writeCode(deviceIdSlot, blank);

	for (unsigned int i=0; i<=2000; i++) {
		writeCode(i, blank);
	}

	for (unsigned int i=5000; i<=5100; i++) {
		writeCode(i, blank);
	}

	writeCode(5050, blank);

	delay(1000);

	digitalWrite(PB5, HIGH);

}

