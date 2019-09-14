#ifndef Communication_h
#define Communication_h

#include "KeypadReader.h";

class Communication {
	public:
		Communication(byte* key, byte* ciphertext);
		void Communication::sendEncrypted(String msgToSend);
		bool Communication::isDataAvailable();
		void Communication::writeEncrypted(volatile unsigned char* msgToSend, unsigned int size);
		void Communication::getData(unsigned char* buffer);
		void Communication::check();


	private:
		void Communication::printByteArray(byte inArray[], bool lineFeed);
		void Communication::encryptAndSend(byte* msgToSend);
		KeyPadReader keypadReader;
		byte* key;
		byte* ciphertext;
		volatile unsigned char *readData[16];
		volatile bool dataAvailable;
		volatile unsigned char data[17];
		volatile unsigned char buffer[16];
		volatile byte decrypted[16];
};

#endif
