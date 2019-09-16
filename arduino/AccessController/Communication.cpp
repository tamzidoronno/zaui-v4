/*
 * Communication.cpp
 *
 *  Created on: Aug 22, 2019
 *      Author: ktonder
 */


#include <Arduino.h>

#include <Crypto.h>
#include <AES.h>
#include "Communication.h";

AES128 aes128;

Communication::Communication(byte* key, byte* ciphertext) {
	this->key = key;
	this->ciphertext = ciphertext;
	this->dataAvailable = false;
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

void Communication::writeEncrypted(volatile unsigned char *msgToSend, volatile unsigned int length) {
	volatile unsigned int gatewayAddress =0;
	char buffer[512];
	sprintf(buffer, "AT+SEND=%u,%u,%s\r\n", gatewayAddress, length, msgToSend);
	Serial.print(buffer);
}
