/*
 * KeypadReader.cpp
 *
 *  Created on: Sep 4, 2019
 *      Author: ktonder
 */
#include "WiegandNG.h"
#include "Communication.h"
#include "KeypadReader.h"
#include <avr/io.h>

volatile unsigned char *KeyPadReader::_codeBuffer[26];
volatile unsigned char *KeyPadReader::_tmpCodeBuffer[26];

volatile bool KeyPadReader::_dataAvailable;

WiegandNG wg;


KeyPadReader::KeyPadReader() {
	_dataAvailable = false;
}

void KeyPadReader::clear() {
	wg.clear();
}

void KeyPadReader::shiftright()
{
	int size = 26;

	for (int i=0; i < (size-1); i++)
	{
		_tmpCodeBuffer[i+1] = _codeBuffer[i] ;
	}

	for (int i=0; i < size; i++) {
		_codeBuffer[i] = _tmpCodeBuffer[i];
	}
}

String KeyPadReader::printDataToSerial(WiegandNG &tempwg) {
	volatile unsigned char *buffer=tempwg.getRawData();
	volatile unsigned int bufferSize = tempwg.getBufferSize();
	volatile unsigned int countedBits = tempwg.getBitCounted();

	volatile unsigned int countedBytes = (countedBits/8);
	if ((countedBits % 8)>0) {
	  countedBytes++;
	}
//
	for (unsigned int i=bufferSize-countedBytes; i< bufferSize;i++) {
		// Not sure if we should add 48 to convert it from binary to ascii numbers...
		volatile unsigned char bufByte=buffer[i]+48;

		shiftright();
		_codeBuffer[0] = bufByte;
	}
}


void KeyPadReader::clearBuffer() {
	for (int i=0; i<26; i++)
	{
		_codeBuffer[i] = '\0';
	}
}

void KeyPadReader::setupWiegand() {
	wg.begin(3, 2, 48, 15);
}

void KeyPadReader::getBuffer(unsigned char* retBuffer) {
	int j = 0;

	for (int i=15; i>=0; i--) {
	   retBuffer[j] = _codeBuffer[i];
	   j++;
	}

	//this->clearBuffer();
	_dataAvailable = false;
}

bool KeyPadReader::isAvailable() {
	return _dataAvailable;
}

bool KeyPadReader::checkWiegand() {

	if(wg.available()) {
		wg.pause();				// pause Wiegand pin interrupts
		printDataToSerial(wg);	// display raw data in binary form, raw data inclusive of PARITY
		wg.clear();				// compulsory to call clear() to enable interrupts for subsequent data
		wg.resume();
		KeyPadReader::_dataAvailable = true;

		delay(10);
		return true;
	}

	return false;
}

void KeyPadReader::stop() {
	wg.detachInterrupts(3, 2);
}

void KeyPadReader::start() {
	wg.attachInterupts(3, 2);
}


