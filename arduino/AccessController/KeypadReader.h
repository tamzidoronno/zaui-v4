#ifndef KeyPadReader_h
#define KeyPadReader_h

#include "WiegandNG.h"

class KeyPadReader {
	public:
		KeyPadReader();
		static volatile bool _dataAvailable;
		static volatile unsigned char * _codeBuffer[26];
		static volatile unsigned char * _tmpCodeBuffer[26];

		void setupWiegand();
		bool checkWiegand();
		void KeyPadReader::getBuffer(unsigned char* retBuffer);
		boolean KeyPadReader::isAvailable();
		void KeyPadReader::stop();
		void KeyPadReader::start();
		void clear();

	private:

		void KeyPadReader::shiftright();
		String KeyPadReader::printDataToSerial(WiegandNG &tempwg);
		void KeyPadReader::clearBuffer();


};

#endif
