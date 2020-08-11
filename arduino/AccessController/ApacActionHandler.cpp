#include "ApacActionHandler.h"
#include "Arduino.h"

ApacActionHandler::ApacActionHandler() {
	this->strikeRelay = 14;
	this->engineRelay = 15;

	this->lightOff();
}

void ApacActionHandler::lock() {
	digitalWrite(strikeRelay, LOW);
	digitalWrite(signalLight, HIGH);

	digitalWrite(strikeRelay, LOW);
	digitalWrite(signalLight, HIGH);

	lightOff();
	digitalWrite(red, HIGH);
}

void ApacActionHandler::setup() {
	pinMode(engineRelay, OUTPUT); // Engine
	pinMode(strikeRelay, OUTPUT); // Strike
	pinMode(signalLight, OUTPUT); // CP LIGHT

	pinMode(red, OUTPUT);
	pinMode(green, OUTPUT);
	pinMode(blue, OUTPUT);


	digitalWrite(PD5, HIGH);
	digitalWrite(engineRelay, LOW);
	digitalWrite(strikeRelay, LOW);

	lightOff();

	digitalWrite(blue, HIGH);
	digitalWrite(red, HIGH);
	digitalWrite(green, HIGH);
}

void ApacActionHandler::lightOff() {
	digitalWrite(blue, LOW);
	digitalWrite(green, LOW);
	digitalWrite(red, LOW);
}

void ApacActionHandler::unlock() {
	digitalWrite(strikeRelay, HIGH);
	digitalWrite(signalLight, LOW);
	lightOff();
	digitalWrite(green, HIGH);
}

void ApacActionHandler::triggerDoorAutomation() {
	delay(200);
	digitalWrite(engineRelay, HIGH);
	delay(100);
	digitalWrite(engineRelay, LOW);
}
