/*
 * GS8015KeyReader.cpp
 *
 *  Created on: Dec 31, 2019
 *      Author: root
 */

#include "GS8015KeyReader.h"

#include <Arduino.h>

//Constructor
GS8015KeyReader::GS8015KeyReader(Clock* clock, SleepHandler* sleepHandler) {
	this->sleepHandler = sleepHandler;
}

void GS8015KeyReader::setCodeHandler(CodeHandler* codeHandler) {

}

void GS8015KeyReader::setup() {
  // Sleep pin reader
//	  pinMode(17, OUTPUT); digitalWrite(17, LOW);

	pinMode(this->backgroundLightning, OUTPUT);
	digitalWrite(this->backgroundLightning, LOW);

	pinMode(this->buzzer, OUTPUT);

	pinMode(12, OUTPUT); digitalWrite(12, LOW);
	pinMode(13, OUTPUT); digitalWrite(13, LOW);
	pinMode(14, OUTPUT); digitalWrite(14, LOW);

  // Sleep pin
  pinMode(17, OUTPUT); digitalWrite(17, LOW);

	digitalWrite(2, HIGH);
	digitalWrite(9, HIGH);
	digitalWrite(10, HIGH);
	digitalWrite(11, HIGH);

	this->showInitialized();
}

void GS8015KeyReader::checkKeyCombo() {
	digitalWrite(this->backgroundLightning, HIGH);
	delay(400);
	digitalWrite(this->backgroundLightning, LOW);
	delay(400);
}

void GS8015KeyReader::showInitialized() {

//	digitalWrite(this->buzzer, HIGH);

	for (int i=0; i<3; i++) {
		delay(100);
		digitalWrite(this->backgroundLightning, HIGH);

		delay(100);
		digitalWrite(this->backgroundLightning, LOW);

	}

//	digitalWrite(this->buzzer, LOW);
}

void GS8015KeyReader::getBuffer(unsigned char* retBuffer) {
	int j = 0;

	for (int i=15; i>=0; i--) {
	   retBuffer[j] = _codeBuffer[i];
	   j++;
	}

	_dataAvailable = false;
}

void GS8015KeyReader::clearBuffer() {
	for (int i=0; i<26; i++)
	{
		_codeBuffer[i] = '\0';
	}
}

void GS8015KeyReader::shiftright()
{
	int size = 26;

	for (int i=0; i < (size-1); i++)
	{
		_tmpCodeBuffer[i+1] = _codeBuffer[i] ;
	}

	for (int i=0; i < size; i++) {
		_codeBuffer[i] = _tmpCodeBuffer[i];
	}
}

void GS8015KeyReader::changeBackgroundColor() {
	if (currentBackGroundColor == 0) {
		digitalWrite(5, LOW);
		digitalWrite(15, LOW);
		digitalWrite(16, LOW);
	}

	if (currentBackGroundColor == 1) {
		digitalWrite(5, HIGH);
		digitalWrite(15, LOW);
		digitalWrite(16,  LOW);
	}
}

bool GS8015KeyReader::check() {

	digitalWrite(2, HIGH);
	digitalWrite(9, HIGH);
	digitalWrite(10, HIGH);
	digitalWrite(11, HIGH);

	unsigned long timePassedSinceLastPressed = millis() - lastTimeKeyPresse;
	if (timePassedSinceLastPressed > 5000 && this->currentBackGroundColor != 0) {
		this->currentBackGroundColor = 0;
		this->changeBackgroundColor();
	}

	int keyPressed = 0;

	if (timePassedSinceLastPressed < 100) {
		keyHoldingIn = true;
	}

	for (int i=1; i<=3; i++) {
		digitalWrite(12, HIGH);
		digitalWrite(13, HIGH);
		digitalWrite(14, HIGH);

		digitalWrite(i + 11, LOW);

		for (int j=1; j<=4; j++) {
			int line = j+7;

			if (line == 8) {
				line = 2;
			}

			bool found = digitalRead(line) == LOW;
			if (found && keyHoldingIn)
				return false;

			if (found) {
				this->shiftright();

				keyPressed = ((j-1)*3)+(4-i);

				digitalWrite(5, LOW);
				digitalWrite(this->buzzer, HIGH);
				delay(100);
				digitalWrite(this->buzzer, LOW);
				digitalWrite(5, HIGH);

				lastTimeKeyPresse = millis();
				keyHoldingIn = true;

				_codeBuffer[0] = keyPressed == 11 ? keyPressed == 0x00 : (byte)keyPressed;
				_dataAvailable = true;
				this->currentBackGroundColor = 1;

				this->sleepHandler->delaySleepWithMs(6000);
				return true;
			}


		}
	}

	keyHoldingIn = false;

	return false;
}

bool GS8015KeyReader::isAvailable() {
	return _dataAvailable;
}
