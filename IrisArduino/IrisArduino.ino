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
	if(wg.available()) { // RFID tag detected
            uint16_t id = wg.getCode(); // get the tag ID
            Serial.print("Tag detected: ");
            Serial.println(id);
            Tag *tagPointer = tagDB.get(id); // obtain a pointer to the Tag with the specified id from the database
            if (tagPointer != NULL) { // tag id exists in database
                Tag tag(tagPointer); // create a Tag from the pointer
    
                // If the reader is not in read state, then we probably need to update the database.
                if (readerState != READ) { // reader not in read state
                    // TODO: Should add a timer to set the reader back to read state...
                    switch(readerState) {
                        case ENABLE: // user wants to enable a tag
                            tag.enable();
                            Serial.println("Tag enabled.");
                            break;
                        case DISABLE: // user wants to disable a tag
                            tag.disable();
                            Serial.println("Tag disabled.");
                            break;
                        case REPORT_STOLEN: // user wants to report a tag as stolen
                            tag.setStolen(true);
                            Serial.println("Tag set as stolen.");
                            break;
                        case REPORT_FOUND: // user wants to report a tag as found
                            tag.setStolen(false);
                            Serial.println("Tag set as found.");
                            break;
                        default:
                            break;
                    }
                    tagDB.update(&tag); // update database
                    readerState = READ; // set it back to read state
                }
                else { // reader is in read state
                    // TODO: Need to detect the correct number of enabled-and-not-stolen tags to unlock door.
                    // Probably need to do something when a stolen/disabled tag is detected.
                    Serial.print(tag.isEnabled() ? "Tag is enabled" : "Tag is disabled");
                    Serial.println(tag.isStolen() ? " and stolen." : " and not stolen.");
                }
            }
            else { // tag id does not exist in database
                Tag tag; // create a new tag
                tag.setID(id); // set the id
                if (readerState == ADD) { // user wants to add a new tag into the system
                    // TODO: Should add a timer to set the reader back to read state...
                    tagDB.add(&tag); // add tag into database (default state of tag is 'disabled')
                    readerState = READ; // set it back to read state
                    Serial.println("Tag added to system.");
                }
            }
	}

    if (Serial.available()) { // got some signal from the app
        char readVal = Serial.read();// read whatever is sent from the app
        switch (readVal) {
            // TODO: Add a case where user can set the number of tags to detect.
            case 'A': // user wants to add a new tag in the system
                readerState = ADD;
                Serial.println("Add mode.");
                break;
            case 'E': // user wants to enabled a tag
                readerState = ENABLE;
                Serial.println("Enable mode.");
                break;
            case 'D': // user wants to disable a tag
                readerState = DISABLE;
                Serial.println("Disable mode.");
                break;
            case 'S': // user wants to report a tag as stolen
                readerState = REPORT_STOLEN;
                Serial.println("Report stolen mode.");
                break;
            case 'F': // user wants to report a tag as found
                readerState = REPORT_FOUND;
                Serial.println("Report found mode.");
                break;
            case 'R': // user wants to report a tag as found
                Serial.println("Read mode.");
                readerState = READ;
                break;
        }
    }
}
