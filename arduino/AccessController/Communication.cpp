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

static long lastReceivedTimestamp = 0;

volatile unsigned char	*Communication::readData;		// buffer for data retention

Communication::Communication(byte* key, byte* ciphertext, Clock* clock) {
	this->key = key;
	this->ciphertext = ciphertext;
	this->dataAvailable = false;
	_clock = clock;
	readData = new unsigned char [16];
}

/**
 * We check the timestamp so its not possible to just
 * re-transmit encrypted packages to get access to old codes etc.
 */
bool Communication::checkIfTimeIsNewer(unsigned char *buffer) {
	unsigned long timestampInPackage = buffer[11];
	timestampInPackage = timestampInPackage * 256 + buffer[12];
	timestampInPackage = timestampInPackage * 256 + buffer[13];
	timestampInPackage = timestampInPackage * 256 + buffer[14];

	if (timestampInPackage <= lastReceivedTimestamp) {
		return false;
	}

	lastReceivedTimestamp = timestampInPackage;

	return true;
}

/**
 * We check the checksum to make sure that the decrypted
 * package is a valid package.
 */
bool Communication::checksum(unsigned char *buffer) {
	unsigned long total = 0;

	for (int i=0; i<15; i++) {
		total += buffer[i];
	}

	int y = total / 256;
	int max = y * 256;
	int diff = total - max;

	return (int)buffer[15] == diff;
}

void Communication::check() {

	if (Serial.available()) {
		this->dataAvailable = true;
		aes128.setKey(this->key, aes128.keySize());
		unsigned char data[34];
		unsigned char buffer[16];
		byte decrypted[16];

		Serial.readBytesUntil('\n', data, 34);

		if (data[1] == 'O' && data[2] == 'K') {
			this->dataAvailable = false;
			return;
		}

		for (int i=10; i<26; i++) {
			buffer[(i-10)] = data[i];
		}

		aes128.decryptBlock(decrypted, buffer);

		if (!checksum(decrypted)) {
			this->dataAvailable = false;
			return;
		}

		if (!checkIfTimeIsNewer(decrypted)) {
			this->writeEncrypted("Old package?", 12);
			this->dataAvailable = false;
			return;
		}

		_clock->adjustClock(lastReceivedTimestamp);

		for (int i=0; i<16; i++) {
			this->readData[i] = decrypted[i];
		}

		this->dataAvailable = true;

		while(Serial.available()) {
			Serial.read();
		}

		delay(10);
	}
}

bool Communication::isDataAvailable() {
	return this->dataAvailable;
}

void Communication::getData(unsigned char* buffer) {
	this->dataAvailable = false;

	for (int i=0; i<16; i++) {
		buffer[i] = this->readData[i];
	};
}

void Communication::writeEncrypted(char *msgToSend, volatile unsigned int length) {
	volatile unsigned int gatewayAddress =0;
//	char buffer[512];
//	sprintf(buffer, "AT+SEND=%u,%u,%s", gatewayAddress, length, msgToSend);
//	Serial.print(buffer);

	Serial.print("AT+SEND=0,");
	Serial.print(length);
	Serial.print(",");;

	for (int i=0; i<length; i++) {
		Serial.print(msgToSend[i]);
	}
	Serial.print("\r\n");

}
