#include "Tag.h"
#include "TagDatabase.h"
#include "Wiegand.h"

#define led 13

enum State {READ, ADD, ENABLE, DISABLE, REPORT_STOLEN, REPORT_FOUND};

WIEGAND wg;

Tag tag1;
TagDatabase tagDB;

State readerState;

void setup() {
    Serial.begin(9600);

	wg.begin(); // start wiegand listener
    tagDB.load(); // load data from Flash/EEPROM

    // Set the reader state
    readerState = READ;

    pinMode(led, OUTPUT);
}

void loop() {
	if(wg.available()) {
        uint16_t id = wg.getCode();
        Tag *tagPointer = tagDB.get(id);
        if (tagPointer != NULL) { // tag id exists in database
            Tag tag(tagPointer);

            if (readerState != READ) {
                switch(readerState) {
                    case ENABLE:
                        tag.enable();
                        break;
                    case DISABLE:
                        tag.disable();
                        break;
                    case REPORT_STOLEN:
                        tag.setStolen(true);
                        break;
                    case REPORT_FOUND:
                        tag.setStolen(false);
                        break;
                    default:
                        break;
                }
                tagDB.update(&tag); // update database
                readerState = READ; // set it back to read mode
            }
            else {
                Serial.println("Tag detected:");

                Serial.print(tag.getID());
                Serial.print(tag.isEnabled() ? ", enabled" : ", disabled");
                Serial.println(tag.isStolen() ? ", stolen." : ", not stolen.");
            }
        }
        else { // tag id does not exist in database
            Tag tag;
            tag.setID(id);
            if (readerState == ADD) {
                tagDB.add(&tag); // add tag into database
                readerState = READ; // set it back to read mode
            }
        }
	}

    if (Serial.available()) {
        char readVal = Serial.read();
        switch (readVal) {
            case 'A':
                readerState = ADD;
                break;
            case 'E':
                readerState = ENABLE;
                break;
            case 'D':
                readerState = DISABLE;
                break;
            case 'S':
                readerState = REPORT_STOLEN;
                break;
            case 'F':
                readerState = REPORT_FOUND;
                break;
        }
    }
}
