/*
 * Communication.cpp
 *
 *  Created on: Aug 22, 2019
 *      Author: ktonder
 *
 */


#include <Arduino.h>

#include <Crypto.h>
#include <AES.h>
#include "Communication.h";

AES128 aes128;


volatile unsigned char	*Communication::readData;		// buffer for data retention

Communication::Communication(Clock* clock, DataStorage* dataStorage, SleepHandler* sleepHandler) {
	this->dataAvailable = false;
	this->_sleepHandler = sleepHandler;
	this->_dataStorage = dataStorage;
	_clock = clock;
	readData = new unsigned char [16];
	this->resetSerialBuffer();
}

void Communication::setup() {
	this->initializeEncryption();
}

void Communication::initializeEncryption() {
	unsigned char buffer[16];
	this->_dataStorage->getCode(906, buffer);
	bool useStoredKey = buffer[0] == 0x44;

	if (useStoredKey) {
		this->_dataStorage->getCode(905, this->key);
	}

	aes128.setKey(this->key, aes128.keySize());
}

/**
 * We check the timestamp so its not possible to just
 * re-transmit encrypted packages to get access to old codes etc.
 */
void Communication::adjustClock(unsigned char *buffer) {
	unsigned long timestampInPackage = buffer[11];
	timestampInPackage = timestampInPackage * 256 + buffer[12];
	timestampInPackage = timestampInPackage * 256 + buffer[13];
	timestampInPackage = timestampInPackage * 256 + buffer[14];

	_clock->adjustClock(timestampInPackage);
}

/**
 * We check the checksum to make sure that the decrypted
 * package is a valid package.
 */
bool Communication::checksum(unsigned char *buffer, unsigned char checksum) {
	unsigned long total = 0;

	for (int i=0; i<16; i++) {
		total += buffer[i];
	}

	int y = total / 256;
	int max = y * 256;
	int diff = total - max;

	bool ret = checksum == diff;

	return ret;
}


void Communication::resetSerialBuffer() {
	memset((unsigned char *)serialDataBuffer, 0, sizeof(serialDataBuffer));
	while(Serial.available()) {
		Serial.read();
	}
}

void Communication::shiftSerialBufferLeft() {
	for (int i=0; i<26; i++) {
		this->serialDataBuffer[i] = this->serialDataBuffer[i+1];
	}
}

void Communication::checkSerialBuffer() {
	if ((byte)this->serialDataBuffer[24] == 'G' && (byte)this->serialDataBuffer[25] == 'I' && (byte)this->serialDataBuffer[26] == 'D') {
		this->sendGid();
		this->resetSerialBuffer();
	}

	if ((byte)this->serialDataBuffer[0] == '+' && (byte)this->serialDataBuffer[1] == 'R' && (byte)this->serialDataBuffer[2] == 'C' && (byte)this->serialDataBuffer[3] == 'V') {
		this->decryptData();
		this->resetSerialBuffer();
	}
}

void Communication::decryptData() {
	unsigned char buffer[16];
	unsigned char decrypted[16];

	for (int i=10; i<26; i++) {
		buffer[(i-10)] = this->serialDataBuffer[i];
	}

	if (!checksum(buffer, this->serialDataBuffer[26])) {

		this->dataAvailable = false;
		return;
	}

	aes128.decryptBlock(decrypted, buffer);

	adjustClock(decrypted);


//	if (resultOfTimeCheck == 1) {
//		this->writeEncrypted("Old package?", 12);
//		this->dataAvailable = false;
//		return;
//	}
//
//	if (resultOfTimeCheck == 2) {
//		this->writeEncrypted("Future package?", 15);
//		this->dataAvailable = false;
//		return;
//	}




	for (int i=0; i<16; i++) {
		this->readData[i] = decrypted[i];
	}

	this->dataAvailable = true;

	this->_sleepHandler->delaySleepWithMs(10000);
}

void Communication::sendGid() {
	unsigned char gidBuffer[16];
	this->_dataStorage->getCode(904, gidBuffer);

	unsigned long gidfordevice = 1000000000;

	if (gidBuffer[0] == 0x43) {
		unsigned long gidfordevice = gidBuffer[4];
		gidfordevice = gidfordevice * 256 + gidBuffer[5];
		gidfordevice = gidfordevice * 256 + gidBuffer[6];
		gidfordevice = gidfordevice * 256 + gidBuffer[7];
	}

	_sleepHandler->wakeupLora();

	String msgToSend = "AT+SEND=1,14,GID:";
	char atbuf[10];
	ltoa(gidfordevice, atbuf,10);
	msgToSend.concat(atbuf);
	msgToSend += "\r\n";
	Serial.println(msgToSend);
	delay(200);
	return;
}

void Communication::check() {
	char readBuffer[1];

	while (Serial.available()) {
		Serial.readBytes(readBuffer, 1);
		this->shiftSerialBufferLeft();
		this->serialDataBuffer[26] = readBuffer[0];
		this->checkSerialBuffer();
	}
}

void Communication::debugDataArray(unsigned char* data, int size) {
	Serial.print("Start: ");

	for (int i=0; i<size; i++) {
		Serial.print(data[i], HEX);
		Serial.print(' ');
	}

	Serial.print(" DONE");
	delay(200);

	while(Serial.available()) {
		Serial.read();
	}
}

bool Communication::isDataAvailable() {
	return this->dataAvailable;
}

/**
 * The new encryptionkey is sent in two messages.
 *
 * When the part two has arrived we are ready to say that we can use the new encryption code.
 */
bool Communication::setEncryptionKey(unsigned char* comData) {
	int start = comData[2] == 0x02 ? 8 : 0;

	unsigned char keyToSet[16];
	this->_dataStorage->getCode(906, keyToSet);

	// We already have an encryption set.
	if (keyToSet[0] == 0x44) {
		return false;
	}

	this->_dataStorage->getCode(905, keyToSet);

	int g = 3;
	for (int i=start; i < (start+8); i++) {
		keyToSet[i] = comData[g];
		g++;
	}

	this->_dataStorage->writeCode(905, keyToSet);

	if (start == 8) {
		keyToSet[0] = 0x44;
		this->_dataStorage->writeCode(906, keyToSet);
	}

	return true;
}

void Communication::getData(unsigned char* buffer) {
	this->dataAvailable = false;

	for (int i=0; i<16; i++) {
		buffer[i] = this->readData[i];
	};
}

void Communication::writeEncrypted(char *msgToSend, volatile unsigned int length, bool encrypt) {

	unsigned char encrypted[16], buf[16];
    int packages = (length / 16);
    if ((length % 16) != 0) {
    	packages = packages + 1;
    }
    int totalLength = packages*16;

    unsigned char allData[totalLength];

    for (int i=0; i<packages;i++) {
    	for (int j=0; j<16;j++) {
    		int k = j + (16*i);
    		if (k > (length-1)) {
    			buf[j] = 0xff;
    		} else {
    			buf[j] = msgToSend[k];
    		}
    	}

    	aes128.encryptBlock(encrypted, buf);

		volatile unsigned int gatewayAddress = 1;

		for (int g=0; g<16; g++) {
			if (encrypt) {
				allData[g + (i*16)] = encrypted[g];
			} else {
				allData[g + (i*16)] = buf[g];
			}
		}
    }

    _sleepHandler->wakeupLora();

    Serial.print("AT+SEND=1,");
   	Serial.print(totalLength);
   	Serial.print(",");

    for (int i=0; i<totalLength;i++) {
    	Serial.print((char)allData[i]);
    }

	Serial.print("\r\n");

}
