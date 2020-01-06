#ifndef KeyPadReader_h
#define KeyPadReader_h

#include "CodeHandler.h"
#include "WiegandNG.h"
#include "CodeReader.h"
#include "Clock.h"

class KeyPadReader: public CodeReader {
	public:
		KeyPadReader(Clock* clock);
		static volatile bool _dataAvailable;
		static volatile unsigned char * _codeBuffer[26];
		static volatile unsigned char * _tmpCodeBuffer[26];

		void setup();
		bool check();
		void KeyPadReader::getBuffer(unsigned char* retBuffer);
		boolean KeyPadReader::isAvailable();
		void KeyPadReader::stop();
		void KeyPadReader::start();
		void clear();
		void KeyPadReader::clearBuffer();
		void setCodeHandler(CodeHandler* codeHandler);

	private:
		void checkExternalButtons();
		bool checkWiegand();
		Clock* _clock;
		CodeHandler* _codeHandler;
		void KeyPadReader::shiftright();
		void setupWiegand();
		String KeyPadReader::printDataToSerial(WiegandNG &tempwg);

		// Inputs
		int _innerButtonPin = 6;
		int _outerButtonOutside = 4;

};

#endif
