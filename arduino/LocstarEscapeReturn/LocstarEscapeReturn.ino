#include <avr/sleep.h>
#include <avr/interrupt.h>

const int switchPin                     = 2;
const int unlockPin                     = 3;
const int lockPin                       = 4;

void setup() {
    pinMode(switchPin, INPUT);
    
}

void sleep() {
    digitalWrite(switchPin, HIGH);
    
    GIMSK |= _BV(PCIE);                     // Enable Pin Change Interrupts
    PCMSK |= _BV(switchPin);                // Use PB3 as interrupt pin
    ADCSRA &= ~_BV(ADEN);                   // ADC off
    set_sleep_mode(SLEEP_MODE_PWR_DOWN);    // replaces above statement

    sleep_enable();                         // Sets the Sleep Enable bit in the MCUCR Register (SE BIT)
    sei();                                  // Enable interrupts
    sleep_cpu();                            // sleep
    
    cli();                                  // Disable interrupts
    PCMSK &= ~_BV(switchPin);                  // Turn off PB3 as interrupt pin
    sleep_disable();                        // Clear SE bit
    ADCSRA |= _BV(ADEN);                    // ADC on
    sei();                                  // Enable interrupts
} // sleep

ISR(PCINT0_vect) {
    // This is called when the interrupt occurs, but I don't need to do anything in it
}

void unlock() {
    pinMode(unlockPin, OUTPUT);
    digitalWrite(unlockPin, HIGH);
    delay(300);
    digitalWrite(unlockPin, LOW);
    pinMode(unlockPin, INPUT);
}

void lock() {
    pinMode(lockPin, OUTPUT);
    digitalWrite(lockPin, HIGH);
    delay(300);
    digitalWrite(lockPin, LOW);
    pinMode(lockPin, INPUT);
}

void loop() {
    sleep();
    
    pinMode(switchPin, INPUT);
    delay(50);
    
    int interruptPin = digitalRead(switchPin);

    if (interruptPin == HIGH) {
       return;
    }
    
    unlock();
    boolean found = false;
    int i = 0;
    for (i=0; i <= 2000; i++) {
       int val = digitalRead(lockPin);
       if (val == HIGH) {
          found = true;
          break;
       }
       delay(10);
    }

    if (!found) {
      lock();
    }
}
