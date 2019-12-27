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

static volatile unsigned long lastReceivedTimestamp = 0;

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
int Communication::checkIfTimeIsNewer(unsigned char *buffer) {
	unsigned long timestampInPackage = buffer[11];
	timestampInPackage = timestampInPackage * 256 + buffer[12];
	timestampInPackage = timestampInPackage * 256 + buffer[13];
	timestampInPackage = timestampInPackage * 256 + buffer[14];

	if (timestampInPackage <= lastReceivedTimestamp) {
		return 1;
	}

	unsigned long diffSinceStartup = this->_clock->diffSinceStartup;

	if (diffSinceStartup != 0 && timestampInPackage > this->_clock->getTime()) {
		return 2;
	}

	lastReceivedTimestamp = timestampInPackage;

	return 0;
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

void Communication::check() {
	unsigned char data[34], tmpbuf[34];

	if (Serial.available()) {
		this->dataAvailable = true;
		aes128.setKey(this->key, aes128.keySize());

		char lastRead = 0x00;
		char readBuffer[1];
		unsigned char buffer[16];
		byte decrypted[16];

		while(Serial.available()) {
			Serial.readBytes(readBuffer, 1);

			if (dataCounter < 34) {
				tmpbuf[dataCounter] = (unsigned char)readBuffer[0];
			}

			if (((unsigned char)readBuffer[0] == 0x0A && lastRead == 0x0D)) {
				memcpy(data, tmpbuf, 34);
				memset(tmpbuf, 0, sizeof(tmpbuf));
				dataCounter = 0;
				break;
			}

			lastRead = (unsigned char)readBuffer[0];

			dataCounter++;
		}

		if (data[1] == 'O' && data[2] == 'K') {
			this->dataAvailable = false;
			return;
		}

		for (int i=10; i<26; i++) {
			buffer[(i-10)] = data[i];
		}

		if ((byte)buffer[0] == 'G' && (byte)buffer[1] == 'I' && (byte)buffer[2] == 'D') {
			unsigned long gidfordevice = 1000000001;
			String msgToSend = "AT+SEND=1,14,GID:";
			char atbuf[10];
			ltoa(gidfordevice, atbuf,10);
			msgToSend.concat(atbuf);
			msgToSend += "\r\n";
			Serial.println(msgToSend);
			delay(200);
			return;
		}

		if (!checksum(buffer, data[26])) {
//			this->debugDataArray(buffer, 16);
			this->dataAvailable = false;
			return;
		}

		aes128.decryptBlock(decrypted, buffer);

		int resultOfTimeCheck = checkIfTimeIsNewer(decrypted);

		/*
		if (resultOfTimeCheck == 1) {
			this->writeEncrypted("Old package?", 12);
			this->dataAvailable = false;
			return;
		}

		if (resultOfTimeCheck == 2) {
			this->writeEncrypted("Future package?", 15);
			this->dataAvailable = false;
			return;
		}
		*/

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

    Serial.print("AT+SEND=1,");
   	Serial.print(totalLength);
   	Serial.print(",");

    for (int i=0; i<totalLength;i++) {
    	Serial.print((char)allData[i]);
    }

	Serial.print("\r\n");

}
