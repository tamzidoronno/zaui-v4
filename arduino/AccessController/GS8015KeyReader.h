/*
 * GS8015KeyReader.h
 *
 *  Created on: Dec 31, 2019
 *      Author: root
 */


#include "CodeHandler.h"
#include "CodeReader.h"
#include "Clock.h"

#ifndef GS8015KeyReader_h
#define GS8015KeyReader_h

class GS8015KeyReader : public CodeReader {
	public:
		GS8015KeyReader(Clock* clock);
		void setup();
		void getBuffer(unsigned char* retBuffer);
		void clearBuffer();

		void checkKeyCombo();
		bool isAvailable();
		bool check();

		void setCodeHandler(CodeHandler* codeHandler);

	private:
		CodeHandler* codeHandler;
		volatile bool _dataAvailable;
		volatile unsigned char * _codeBuffer[26];
		volatile unsigned char * _tmpCodeBuffer[26];
		void showInitialized();
		void shiftright();
		void changeBackgroundColor();

		// Blue
		int backgroundLightning = 5;
		int buzzer = 4;
		unsigned long lastTimeKeyPresse = 0;
		bool keyHoldingIn = false;

		int currentBackGroundColor = 0;

};

#endif
