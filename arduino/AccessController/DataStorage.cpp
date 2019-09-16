/*
 * DataStorage.cpp
 *
 *  Created on: Sep 5, 2019
 *      Author: ktonder
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

//void DataStorage::writeEEPROMPage(long eeAddress, char* data)
//{
//
//  Wire.beginTransmission(eeprom1);
//
//  Wire.write((int)(eeAddress >> 8)); // MSB
//  Wire.write((int)(eeAddress & 0xFF)); // LSB
//  Wire.write(data); //Write the data
//  Wire.endTransmission(); //Send stop condition
//  delay(20);
//}

void DataStorage::writeEEPROMPage(unsigned int eeaddress, unsigned char* data)
{
	int deviceaddress = eeprom1;
  // Uses Page Write for 24LC256
  // Allows for 64 byte page boundary
  // Splits string into max 16 byte writes
  unsigned char i=0, counter=0;
  unsigned int  address;
  unsigned int  page_space;
  unsigned int  page=0;
  unsigned int  num_writes;
  unsigned int  data_len=0;
  unsigned char first_write_size;
  unsigned char last_write_size;
  unsigned char write_size;

  // Calculate length of data
  do{ data_len++; } while(data[data_len]);

  // Calculate space available in first page
  page_space = int(((eeaddress/64) + 1)*64)-eeaddress;

  // Calculate first write size
  if (page_space>16){
     first_write_size=page_space-((page_space/16)*16);
     if (first_write_size==0) first_write_size=16;
  }
  else
     first_write_size=page_space;

  // calculate size of last write
  if (data_len>first_write_size)
     last_write_size = (data_len-first_write_size)%16;

  // Calculate how many writes we need
  if (data_len>first_write_size)
     num_writes = ((data_len-first_write_size)/16)+2;
  else
     num_writes = 1;

  i=0;
  address=eeaddress;
  for(page=0;page<num_writes;page++)
  {
     if(page==0) write_size=first_write_size;
     else if(page==(num_writes-1)) write_size=last_write_size;
     else write_size=16;

     Wire.beginTransmission(deviceaddress);
     Wire.write((int)((address) >> 8));   // MSB
     Wire.write((int)((address) & 0xFF)); // LSB
     counter=0;
     do{
        Wire.write((byte) data[i]);
        i++;
        counter++;
     } while((data[i]) && (counter<write_size));
     Wire.endTransmission();
     address+=write_size;   // Increment address for next write

     delay(6);  // needs 5ms for page write
  }
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

bool DataStorage::handleMessage(unsigned char* msgFromGateWay) {

	if (msgFromGateWay[0] == 'C') {

		unsigned int slot = ((int)(msgFromGateWay[1])-48)*1000 + ((int)(msgFromGateWay[2])-48)*100 + ((int)(msgFromGateWay[3])-48)*10 + ((int)(msgFromGateWay[4]-48));
		unsigned char codeBuf[16];

		codeBuf[0] = msgFromGateWay[5];
		codeBuf[1] = msgFromGateWay[6];
		codeBuf[2] = msgFromGateWay[7];
		codeBuf[3] = msgFromGateWay[8];
		codeBuf[4] = msgFromGateWay[9];
		codeBuf[5] = msgFromGateWay[10];
		codeBuf[6] = msgFromGateWay[11];
		codeBuf[7] = msgFromGateWay[12];
		codeBuf[8] = msgFromGateWay[13];
		codeBuf[9] = msgFromGateWay[14];
		codeBuf[10] = msgFromGateWay[15];
		codeBuf[11] = 0x00;
		codeBuf[12] = 0x00;
		codeBuf[13] = 0x00;
		codeBuf[14] = 0x00;
		codeBuf[15] = 0x00;

		this->writeCode(slot, codeBuf);

		return true;
	}

	return false;
}

void DataStorage::writeCode(unsigned int slot, unsigned char* str_data) {
//	Serial.println("writing");
//	Serial.println((char[])str_data);
//	Serial.println(slot);

	 writeEEPROMPage(slot*pagesize, str_data);
}

