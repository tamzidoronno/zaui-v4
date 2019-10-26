/**
 * Handles interuption like:
 *  - Alarm
 *  - Exit button pressed
 *  - Open door from outside pressed.
 */

#include "ExternalInputReader.h";
#include "CodeHandler.h";

#define exitButtonInside PD6
#define exitButtonOutside PD7
#define alarm1 PC2
#define alarm2 PC3

ExternalInputReader::ExternalInputReader(Clock* clock, Logging* logging, CodeHandler* codeHandler) {
	_clock = clock;
	_logging = logging;
	_codeHandler = codeHandler;
	this->_alarmActivated1 = true;
	this->_alarmActivated2 = true;
	this->_exitButtonInsideTriggered = true;
	this->_exitButtonOutsideTriggered = true;
	this->reset();
}

void ExternalInputReader::setup() {
	pinMode(exitButtonInside, INPUT);
	pinMode(exitButtonOutside, INPUT);
	pinMode(alarm1, INPUT);
	pinMode(alarm2, INPUT);

	digitalWrite(alarm1, HIGH);
	digitalWrite(alarm2, HIGH);
	digitalWrite(exitButtonInside, HIGH);
	digitalWrite(exitButtonOutside, HIGH);
}

void ExternalInputReader::checkButtons() {


	// Unlock the door if the inner exit button is pressed
	if (digitalRead(exitButtonInside) == LOW && this->_codeHandler->isLocked() && !this->_exitButtonInsideTriggered) {
		this->_exitButtonInsideTriggered = true;
		this->_codeHandler->unlock(32767);
	}

	// Open the door by automation if the exit button on the outside is pressed and the do
	if (digitalRead(exitButtonOutside) == LOW && !this->_codeHandler->isLocked() && !this->_exitButtonOutsideTriggered) {
		this->_exitButtonOutsideTriggered = true;
		this->_codeHandler->triggerDoorAutomation();
	}

	// Open the door if the button is pressed on the inside and the door is unlocked.
	if (digitalRead(exitButtonInside) == LOW && !this->_codeHandler->isLocked() && !this->_exitButtonInsideTriggered) {
		this->_exitButtonInsideTriggered = true;
		this->_codeHandler->triggerDoorAutomation();
	}


	// Reset states
	if (this->_exitButtonInsideTriggered && digitalRead(exitButtonInside) == HIGH) {
		this->_exitButtonInsideTriggered = false;
		return;
	}

	if (this->_exitButtonOutsideTriggered && digitalRead(exitButtonOutside) == HIGH) {
		this->_exitButtonOutsideTriggered = false;
		return;
	}

}


void ExternalInputReader::checkAlarms() {
	// TODO: Be implemented
}
void ExternalInputReader::reset() {
}
