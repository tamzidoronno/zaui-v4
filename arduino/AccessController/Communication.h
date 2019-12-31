#ifndef Communication_h
#define Communication_h

#include "Clock.h";
#include "Arduino.h";
#include "DataStorage.h"

class Communication {
	public:
		void Communication::sendEncrypted(String msgToSend);
		Communication(Clock* clock, DataStorage* dataStorage);
		bool Communication::isDataAvailable();
		void Communication::writeEncrypted(char* msgToSend, unsigned int size, bool encrypt);
		void Communication::getData(unsigned char* buffer);
		bool Communication::setEncryptionKey(unsigned char* comData);
		void Communication::initializeEncryption();
		void Communication::check();
		void Communication::setup();

	private:
		Clock* _clock;
		DataStorage* _dataStorage;
		bool Communication::checksum(unsigned char* buffer, unsigned char checksum);
		void Communication::debugDataArray(unsigned char* data, int size);
		void Communication::printByteArray(byte inArray[], bool lineFeed);
		void Communication::encryptAndSend(byte* msgToSend);
		int Communication::checkIfTimeIsNewer(unsigned char *buffer);

		void Communication::resetSerialBuffer();
		void Communication::shiftSerialBufferLeft();
		void Communication::checkSerialBuffer();
		void Communication::decryptData();
		void Communication::sendGid();

		bool useDefaultEncryptionKey = true;
		byte key[16] = {0x54, 0xA1, 0xB7, 0xCD, 0x00, 0xF1, 0xF0, 0xA0, 0x18, 0x9A, 0xCF, 0x2B, 0xC1, 0xAA, 0x0E, 0x9F};
		byte* ciphertext;
		static volatile unsigned char *readData;
		volatile bool dataAvailable;
		volatile int dataCounter = 0;
		volatile unsigned char data[17];
		volatile unsigned char buffer[16];

		volatile unsigned char serialDataBuffer[27];

//		volatile byte decrypted[16];
};

#endif
