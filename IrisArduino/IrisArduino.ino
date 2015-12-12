#include "Tag.h"
#include "TagDatabase.h"
#include "Wiegand.h"
#include "Servo.h"

#define TAG_READ_TIME_THRESHOLD 20000 // Allow tag reads to persist for 20 seconds
#define TAG_COUNT_MAX_THRESHOLD 10
#define led 13
#define STATE_TIME_LIMIT 20000 // Allow non-read state to persist for 20 seconds

enum State {READ, ADD, ENABLE, DISABLE, REPORT_STOLEN, REPORT_FOUND, SET_THRESHOLD};

const String STATE_STRING[] = {String("ADDTAG"),
                               String("ENABLETAG"),
                               String("DELETETAG"),
                               String("DISABLETAG"),
                               String("LOSETAG"),
                               String("FOUNDTAG"),
                               String("SETTHRESHOLD")};

unsigned long stateTimeStamp = 0;

char serialBuffer[64];
uint8_t bufferIndex = 0;
bool isAppMsg = false;

Servo servo1;
int pos1 = 0;

WIEGAND wg;

TagDatabase tagDB;

State readerState;

struct TagTime {
    uint16_t id = 0;
    unsigned long time = 0;
};

struct TagTime tagTimeArray[TAG_COUNT_MAX_THRESHOLD];
uint8_t ttArraySize = 0;

void clearSerialBuffer() {
    for (int i = 0; i != bufferIndex; ++i)
        serialBuffer[i] = 0;
    bufferIndex = 0;
}

void update(String str) {
    uint16_t val = str.toInt();

    if (readerState == SET_THRESHOLD) {
        tagDB.setThreshold(val);
        Serial.print("Updated threshold. Now requires ");
        Serial.print(val);
        Serial.println(" tags to unlock system.");
        return;
    }

    Tag *tagPointer = tagDB.get(val); // obtain a pointer to the Tag with the specified id from the database

    if (tagPointer == NULL) { // tag id exists in database
        Serial.println("Tag doesn't exist in database.");
        return;
    }

    Tag tag(tagPointer); // create a Tag from the pointer

    switch(readerState) {
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

void updateTagTimeArray(Tag *tag) {
    bool toInsertTag = true;
    unsigned long currentTime = millis(); // get the current time in ms
    //Serial.print("Current time is ");
    //Serial.println(currentTime);
    // If tag is already in the array, then we just update the time

    //Serial.println("Checking if tag is already in array...");
    for (int i = 0; i != ttArraySize; ++i) {
        if (tagTimeArray[i].id == tag->getID()) {
            //Serial.println("Yup, it is.");
            //Serial.print("Tag time stamp updated from ");
            //Serial.print(tagTimeArray[i].time);
            tagTimeArray[i].time = currentTime; // update the time stamp in the array
            //Serial.print(" to ");
            //Serial.println(tagTimeArray[i].time);
            toInsertTag = false; // tag is already in the array
        }
    }

    // Remove any tag whose time stamp has expired
    //Serial.println("Removing any tag whose time stamp has expired...");
    for (int i = ttArraySize - 1; i >= 0; --i) {
        if ((currentTime - tagTimeArray[i].time) > TAG_READ_TIME_THRESHOLD) { // tag i time stamp expired
            //Serial.print("time stamp of tag to be removed: ");
            //Serial.println(tagTimeArray[i].time);
            //Serial.print("Time difference: ");
            //Serial.println(tagTimeArray[i].time - currentTime);
            //Serial.print("Tag ");
            //Serial.print(tagTimeArray[i].id);
            //Serial.println(" removed.");
            for (int j = i; j != ttArraySize - 1; ++j)
                tagTimeArray[j] = tagTimeArray[j + 1];
            ttArraySize--; // reduce size by 1
        }
    }

    if (toInsertTag && (ttArraySize < tagDB.getThreshold())) {
        //Serial.println("Adding it to the array...");
        tagTimeArray[ttArraySize].id = tag->getID();
        tagTimeArray[ttArraySize].time = currentTime;
        ttArraySize++;
    }
    //Serial.print("Array Updated. New size is: ");
    //Serial.println(ttArraySize);
}

void updateTagTimeArray() {
    unsigned long currentTime = millis(); // get the current time in ms

    // Remove any tag whose time stamp has expired
    //Serial.println("Removing any tag whose time stamp has expired...");
    for (int i = ttArraySize - 1; i >= 0; --i) {
        if ((currentTime - tagTimeArray[i].time) > TAG_READ_TIME_THRESHOLD) { // tag i time stamp expired
            for (int j = i; j != ttArraySize - 1; ++j)
                tagTimeArray[j] = tagTimeArray[j + 1];
            ttArraySize--; // reduce size by 1
        }
    }
}

void setup() {
    Serial.begin(9600); // communication between PC
    Serial1.begin(9600); // communication between android

    wg.begin(); // start wiegand listener
    tagDB.load(); // load data from Flash/EEPROM

    servo1.attach(9);

    // Set the reader state
    readerState = READ;
    
    clearSerialBuffer();

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
            Serial.print(id);
            Tag *tagPointer = tagDB.get(id); // obtain a pointer to the Tag with the specified id from the database
            if (tagPointer != NULL) { // tag id exists in database
                Tag tag(tagPointer); // create a Tag from the pointer
    
                // If the reader is not in read state, then we probably need to update the database.
                if (readerState == READ) { // reader is in read state
                    Serial.print(tag.isEnabled() ? ", it is enabled" : ", it is disabled");
                    Serial.println(tag.isStolen() ? " and stolen." : " and not stolen.");
                    if (tag.isEnabled() && !tag.isStolen()) {
                        updateTagTimeArray(&tag);
                        if (ttArraySize == tagDB.getThreshold()) { // enough tags detected to unlock door
                            Serial.println("OPEN SESAME!!!!");
                            digitalWrite(led, HIGH);
                            for(pos1=0;pos1 < 180; pos1 += 1)
                            {
                                servo1.write(pos1);
                                delay(10);
                            }
                        }
                        else // not enough tags detected to unlock door
                            Serial.println("Need more tags!!!");
                    }
                }
            }
            else { // tag id does not exist in database
                Serial.println();
                Tag tag; // create a new tag
                tag.setID(id); // set the id
                tag.enable(); // automatically enable it
                if (readerState == ADD) { // user wants to add a new tag into the system
                    tagDB.add(&tag); // add tag into database (default state of tag is 'disabled')
                    readerState = READ; // set it back to read state
                    Serial1.print(String(id) + "!");
                    Serial.println("Tag added to system.");
                }
            }
	}
    else { // no tags detected
        updateTagTimeArray();
        if (ttArraySize != tagDB.getThreshold()) { // enough tags detected to unlock door
            digitalWrite(led, LOW);
        }
    }
    
    if (Serial1.available()) {
        char byteRead = Serial1.read();
        if (byteRead == '-') {
            if (!isAppMsg)
                isAppMsg = true;
            else {
                isAppMsg = false;
                String stringRead(serialBuffer);
                int state = -1;
                for (int i = 0; i != bufferIndex; ++i) {
                    if (stringRead.equals(STATE_STRING[i]))
                        state = i;
                }

                switch(state) {
                    case 0: // user wants to enable a tag
                        readerState = ADD;
                        Serial.println("Add mode.");
                        break;
                    case 1: // user wants to disable a tag
                        readerState = ENABLE;
                        Serial.println("Enable mode.");
                        break;
                    case 2: // user wants to report a tag as stolen
                        readerState = DISABLE;
                        Serial.println("Delete mode.");
                        break;
                    case 3: // user wants to report a tag as found
                        readerState = DISABLE;
                        Serial.println("Disable mode.");
                        break;
                    case 4: // user wants to disable a tag
                        readerState = REPORT_STOLEN;
                        Serial.println("Report lost mode.");
                        break;
                    case 5: // user wants to report a tag as stolen
                        readerState = REPORT_FOUND;
                        Serial.println("Report found mode.");
                        break;
                    case 6: // user wants to report a tag as stolen
                        readerState = SET_THRESHOLD;
                        Serial.println("Set tag threshold mode.");
                        break;
                    default:
                        update(stringRead);
                        break;
                }
                clearSerialBuffer();
            }
        }
        if (isAppMsg && (byteRead != '-')) {
            serialBuffer[bufferIndex++] = byteRead;
        }
    }
}
