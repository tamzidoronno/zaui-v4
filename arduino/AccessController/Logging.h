/*
 * Logging.h
 *
 *  Created on: Sep 15, 2019
 *      Author: ktonder
 */

#ifndef LOGGING_H_
#define LOGGING_H_

#include "DataStorage.h"
#include "Communication.h"
#include "Clock.h"

class Logging {
	public:
		Logging(DataStorage* dataStorage, Clock* clock);
		void addLog(char*  logData, int size, bool shouldAck);
		void Logging::init();
		void runSendCheck(Communication* communication);
		bool handleAckMessage(unsigned char* msg);

	private:
		Clock* _clock;
		DataStorage* _dataStorage;
		void shiftAllLogEntries();
		void writeLogLineToEeprom();
		void loadLogLineFromEeprom();
		void sendLogLine(Communication* comminucation, unsigned char* meta, unsigned char* logline);
};


#endif /* LOGGING_H_ */
