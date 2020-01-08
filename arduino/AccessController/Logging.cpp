/*
 * Logging.cpp
 *
 *  Created on: Sep 15, 2019
 *      Author: ktonder
 */




#include "Logging.h"
#include "Arduino.h"

/**
 * We start logging at dataslot 5000.
 */
unsigned int loggingStartsAtSlot = 800;

/**
 * We reserve 100 slots for data storage
 */
unsigned int maxLogSlot = 900;

int timeBetweenEeachCheck = 5000;

/**
 * We reserve 100 slots for data storage
 */
unsigned int logLineLevelStoredAt = 901;

static unsigned long logLineNumber = 0;

//unsigned long nextCheck = 0;

Logging::Logging(DataStorage* dataStorage, Clock* clock) {
	this->_dataStorage = dataStorage;
	this->_clock = clock;
}

/**
 * Meta Data:
*    byte[0] = Reserved for acknowledge checking.
 *     - If byte is 1 the logging will not be ack sent to server
 *     - If byte is 2 the logging will be retransmitted until its acked by server.
 *     - If byte is 3 the logline has been sent and acked by server.
 *   byte[1-3] = Reserved for timestamp.
 *   byte[4-7] = Long byte representation of logline
 */
void Logging::addLog(char* str, int size, bool shouldAck) {
	this->sendDataAsap = true;

	if (logLineNumber > 2000000) {
		logLineNumber = 0;
	}

	logLineNumber++;

	if (size > 16) {
		return;
	}

	unsigned char logMetaData[16] = {
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	unsigned char logLineData[16] = {
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	if (shouldAck) {
		logMetaData[0] = 2;
	} else {
		logMetaData[0] = 1;
	}

	for (int j=0; j < size; j++) {
		logLineData[j] = str[j];
	}

	char timeStamp[4];
	_clock->getTimeChar(timeStamp);

	logMetaData[1] = timeStamp[0];
	logMetaData[2] = timeStamp[1];
	logMetaData[3] = timeStamp[2];
	logMetaData[4] = timeStamp[3];

	logMetaData[5] = logLineNumber & 0xFF; // 0x78
	logMetaData[6] = (logLineNumber >> 8) & 0xFF; // 0x56
	logMetaData[7] = (logLineNumber >> 16) & 0xFF; // 0x34
	logMetaData[8] = (logLineNumber >> 24) & 0xFF; // 0x12

	int pageLogLineSlot = loggingStartsAtSlot + 1 + currentLogSlot;
	_dataStorage->writeCode(loggingStartsAtSlot + currentLogSlot, logMetaData);
	_dataStorage->writeCode(pageLogLineSlot, logLineData);

	currentLogSlot = currentLogSlot + 2;

	if (currentLogSlot > 100) {
		currentLogSlot = 0;
	}

	this->writeLogLineToEeprom();
}

void Logging::shiftAllLogEntries() {
	unsigned char buf[16];
	char tmpbuf[68];

	for (int i=(maxLogSlot-2); i>=loggingStartsAtSlot; i--) {
		int metaDataPos = i;
		int dataPos = i+1;

		int newMetaPos = i+2;
		int newDataPos = i+3;


		_dataStorage->getCode(metaDataPos, buf);
		_dataStorage->writeCode(newMetaPos, buf);

		_dataStorage->getCode(dataPos, buf);
		_dataStorage->writeCode(newDataPos, buf);

		i--;
	}
}

void Logging::writeLogLineToEeprom() {
	char buf[16] = {
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00
		};

	ltoa(logLineNumber, buf, 10);

	buf[15] = 0xAA;

	_dataStorage->writeCode(logLineLevelStoredAt, buf);
}

void Logging::loadLogLineFromEeprom() {

	unsigned char buf[16] = {
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00
	};

	_dataStorage->getCode(logLineLevelStoredAt, buf);
	logLineNumber = atol(buf);
}

void Logging::init() {
	this->loadLogLineFromEeprom();
}

void Logging::runSendCheck(Communication* communication) {
	long timeSinceLastTransmit = millis() - lastTransmitTimestamp;

	unsigned char metaData[16];
	unsigned char logLine[16];

	if (timeSinceLastTransmit <= timeBetweenEeachCheck && !sendDataAsap) {
		return;
	}

	if (sendDataAsap) {
		newDataToSend = true;
		if (timeSinceLastTransmit < 700) {
			return;
		}
	}

	if (!newDataToSend) {
		return;
	}

	bool found = false;
	sendDataAsap = false;
	this->lastTransmitTimestamp = millis();

	for (int i = loggingStartsAtSlot; i < maxLogSlot; i++) {

		for (int j=0; j<16; j++) {
			metaData[j] = 0x00;
			logLine[j] = 0x00;
		}

		_dataStorage->getCode(i, metaData);

		if (metaData[0] == 0x02) {
			_dataStorage->getCode(i+1, logLine);
			found = true;
			this->sendLogLine(communication, metaData, logLine);
			return;
		}

		i++;
	}

	if (!found) {
		newDataToSend = false;
	}
}

void Logging::sendLogLine(Communication* comminucation, unsigned char* meta, unsigned char* logline) {
	char longPackage[26];

	for (int i=0;i<9;i++) {
		longPackage[i] = meta[i];
	}

	for (int i=0; i<16; i++) {
		longPackage[i+9] = logline[i];
	}

	longPackage[25] = 'L';

	comminucation->writeEncrypted(longPackage, 26, true);
}

bool Logging::handleAckMessage(unsigned char* msg) {

	unsigned char metaData[16];

	bool found = false;

	for (int i = loggingStartsAtSlot; i < maxLogSlot; i++) {
		_dataStorage->getCode(i, metaData);

		if (msg[4] == metaData[5] && msg[5] == metaData[6] && msg[6] == metaData[7] && msg[7] == metaData[8]) {
			metaData[0] = 0x03;
			_dataStorage->writeCode(i, metaData);
			found = true;
		}

		i++;
	}

	if (found) {
		this->sendDataAsap = true;
	}

	return found;
}
