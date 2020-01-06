/*
 * GS8015ActionHandler.h
 *
 *  Created on: Dec 31, 2019
 *      Author: root
 */

#include "ActionHandler.h"

#ifndef GS8015ACTIONHANDLER_H_
#define GS8015ACTIONHANDLER_H_

class GS8015ActionHandler : public ActionHandler {
	public:
		GS8015ActionHandler::GS8015ActionHandler();
		void unlock();
		void lock();
		void setup();
		void triggerDoorAutomation();

	private:

		// Set overall tempo
		long tempo = 5000;
		// Set length of pause between notes
		int pause = 500;
		// Loop variable to increase Rest length
		int rest_count = 100; //<-BLETCHEROUS HACK; See NOTES
		// Initialize core variables
		int tone_ = 0;
		int beat = 0;
		long duration  = 0;

		void playTone();
		int backgroundLightning = 5;
		int buzzer = 4;
};

#endif /* GS8015ACTIONHANDLER_H_ */
