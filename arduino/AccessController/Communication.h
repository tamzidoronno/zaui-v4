#ifndef Communication_h
#define Communication_h

#include "Clock.h";
#include "Arduino.h";

class Communication {
	public:
		void Communication::sendEncrypted(String msgToSend);
		Communication(byte* key, byte* ciphertext, Clock* clock);
		bool Communication::isDataAvailable();
		void Communication::writeEncrypted(char* msgToSend, unsigned int size, bool encrypt);
		void Communication::getData(unsigned char* buffer);
		void Communication::check();

	private:
		Clock* _clock;
		bool Communication::checksum(unsigned char* buffer, unsigned char checksum);
		void Communication::printByteArray(byte inArray[], bool lineFeed);
		void Communication::encryptAndSend(byte* msgToSend);
		int Communication::checkIfTimeIsNewer(unsigned char *buffer);
		byte* key;
		byte* ciphertext;
		static volatile unsigned char *readData;
		volatile bool dataAvailable;
		volatile unsigned char data[17];
		volatile unsigned char buffer[16];
		volatile byte decrypted[16];
};

#endif
