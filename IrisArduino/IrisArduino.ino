#include "Tag.h"
#include "TagDatabase.h"
#include "Wiegand.h"

#define TAG_COUNT_THRESHOLD 3
#define TAG_READ_TIME_THRESHOLD 20000 // Allow tag reads to persist for 20 seconds
#define led 13
#define STATE_TIME_LIMIT 20000 // Allow non-read state to persist for 20 seconds

enum State {READ, ADD, ENABLE, DISABLE, REPORT_STOLEN, REPORT_FOUND};

unsigned long stateTimeStamp = 0;

WIEGAND wg;

TagDatabase tagDB;

State readerState;

struct TagTime {
    uint16_t id = 0;
    unsigned long time = 0;
};

struct TagTime tagTimeArray[TAG_COUNT_THRESHOLD];
uint8_t ttArraySize = 0;

void updateTagTimeArray(Tag *tag) {
    bool toInsertTag = true;
    unsigned long currentTime = millis(); // get the current time in ms
    Serial.print("Current time is ");
    Serial.println(currentTime);
    // If tag is already in the array, then we just update the time

    Serial.println("Checking if tag is already in array...");
    for (int i = 0; i != ttArraySize; ++i) {
        if (tagTimeArray[i].id == tag->getID()) {
            Serial.println("Yup, it is.");
            Serial.print("Tag time stamp updated from ");
            Serial.print(tagTimeArray[i].time);
            tagTimeArray[i].time = currentTime; // update the time stamp in the array
            Serial.print(" to ");
            Serial.println(tagTimeArray[i].time);
            toInsertTag = false; // tag is already in the array
        }
    }

    // Remove any tag whose time stamp has expired
    Serial.println("Removing any tag whose time stamp has expired...");
    for (int i = ttArraySize - 1; i >= 0; --i) {
        if ((currentTime - tagTimeArray[i].time) > TAG_READ_TIME_THRESHOLD) { // tag i time stamp expired
            Serial.print("time stamp of tag to be removed: ");
            Serial.println(tagTimeArray[i].time);
            Serial.print("Time difference: ");
            Serial.println(tagTimeArray[i].time - currentTime);
            Serial.print("Tag ");
            Serial.print(tagTimeArray[i].id);
            Serial.println(" removed.");
            for (int j = i; j != ttArraySize - 1; ++j)
                tagTimeArray[j] = tagTimeArray[j + 1];
            ttArraySize--; // reduce size by 1
        }
    }

    if (toInsertTag && (ttArraySize < TAG_COUNT_THRESHOLD)) {
        Serial.println("Adding it to the array...");
        tagTimeArray[ttArraySize].id = tag->getID();
        tagTimeArray[ttArraySize].time = currentTime;
        ttArraySize++;
    }
    Serial.print("Array Updated. New size is: ");
    Serial.println(ttArraySize);
}

void setup() {
    Serial.begin(9600);

    wg.begin(); // start wiegand listener
    tagDB.load(); // load data from Flash/EEPROM

    // Set the reader state
    readerState = READ;

    pinMode(led, OUTPUT);
}

bool withinTimeLimit() {
    unsigned long currentTime = millis(); // get the current time in ms
    bool inTime = false;
    if ((currentTime - stateTimeStamp) < STATE_TIME_LIMIT) // it is within the time limit
        inTime = true;
    stateTimeStamp = 0;
    return inTime;
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
                if (readerState != READ && withinTimeLimit()) { // reader not in read state
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
                    if (tag.isEnabled() && !tag.isStolen()) {
                        updateTagTimeArray(&tag);
                        if (ttArraySize == TAG_COUNT_THRESHOLD) // enough tags detected to unlock door
                            Serial.println("OPEN SESAME!!!!");
                        else // not enough tags detected to unlock door
                            Serial.println("Need more tags!!!");
                    }
                }
            }
            else { // tag id does not exist in database
                Tag tag; // create a new tag
                tag.setID(id); // set the id
                tag.enable(); // automatically enable it
                if (readerState == ADD) { // user wants to add a new tag into the system
                    tagDB.add(&tag); // add tag into database (default state of tag is 'disabled')
                    readerState = READ; // set it back to read state
                    Serial.println("Tag added to system.");
                }
            }
	}

    if (Serial.available()) { // got some signal from the app
        char readVal = Serial.read();// read whatever is sent from the app
        if (readVal == 'R') { // default state
            readerState = READ;
            stateTimeStamp = 0; // reset state time stamp
            Serial.println("Read mode.");
        }
        else {
            switch (readVal) {
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
            }
            stateTimeStamp = millis();
        }
    }
}
