#include "ApacActionHandler.h"
#include "Arduino.h"

ApacActionHandler::ApacActionHandler() {
	this->strikeRelay = 14;
	this->engineRelay = 15;
}

void ApacActionHandler::lock() {
	digitalWrite(strikeRelay, LOW);
	digitalWrite(signalLight, HIGH);
}

void ApacActionHandler::setup() {
	pinMode(engineRelay, OUTPUT); // Engine
	pinMode(strikeRelay, OUTPUT); // Strike
	pinMode(signalLight, OUTPUT); // CP LIGHT

	digitalWrite(PD5, HIGH);
	digitalWrite(engineRelay, LOW);
	digitalWrite(strikeRelay, LOW);
}

void ApacActionHandler::unlock() {
	digitalWrite(strikeRelay, HIGH);
	digitalWrite(signalLight, LOW);
}

void ApacActionHandler::triggerDoorAutomation() {
	delay(200);
	digitalWrite(engineRelay, HIGH);
	delay(100);
	digitalWrite(engineRelay, LOW);
}
