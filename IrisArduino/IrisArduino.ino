#include "Tag.h"
#include "Wiegand.h"

#define led 13

WIEGAND wg;

Tag tag1;

void setup() {
    Serial.begin(9600);
	wg.begin();

    pinMode(led, OUTPUT);

    tag1.setID(12345);
    tag1.enable();
}

void loop() {
	if(wg.available()) {
        if (tag1.getID() == wg.getCode()) {
            Serial.println("tag1 detected.");
        }
	}

    if (Serial.available()) {
        char readVal = Serial.read();
        if (readVal == 'H') {
            digitalWrite(led, HIGH);
            Serial.println("Hello, my LED is switched on!");
        }
        else if (readVal == 'L') {
            digitalWrite(led, LOW);
            Serial.println("Hello, my LED is switched off!");
        }
    }
}
