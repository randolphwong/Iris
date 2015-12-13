#include "TagDatabase.h"

TagDatabase::TagDatabase() {
    size = 0;
    threshold = TAG_DEFAULT_THRESHOLD;
}

void TagDatabase::load() {
    // Check if the dueFlashStorage already contains an existing database.
    if (dueFlashStorage.read(0) != SCHEMA) {
        dueFlashStorage.write(0, SCHEMA); // set schema
        dueFlashStorage.write(1, 0); // set size to 0
        return;
    }

    // Get the size of database.
    size = dueFlashStorage.read(1);

    // Allocate memory for the database.
    for (int i = 0; i != size; ++i) {
        Tag tag;
        int index = 2 + i * 4; // start reading only from address 2 onwards

        uint16_t id = (dueFlashStorage.read(index) << 8) + dueFlashStorage.read(index + 1);
        tag.setID(id);

        if (dueFlashStorage.read(index + 2)) tag.enable();

        tag.setStolen(dueFlashStorage.read(index + 3));

        database[i] = tag;
    }
}

void TagDatabase::add(Tag* tag) {
//    Serial.print("Adding tag ");
//    Serial.print(tag->getID());
//    Serial.println(" to system...");
    store(tag);
}

void TagDatabase::update(Tag* tag) {
    store(tag);
}

void TagDatabase::store(Tag* tag) {
    int writeIndex;
    // Checks if tag is already in the database.
    int index = get(tag);
//    Serial.print("Index from TagDatabase.get(tag): ");
//    Serial.println(index);
    if (index != -1) { // tag is in database
        // Check if there is a need to update the database.
        if (database[index].isStolen() != tag->isStolen())
            database[index].setStolen(tag->isStolen());

        if (database[index].isEnabled() != tag->isEnabled()) {
            if (tag->isEnabled())
                database[index].enable();
            else
                database[index].disable();
        }
        writeIndex = index;
    }
    else { // tag is not in database
//        Serial.print("Tag id before adding to database: ");
//        Serial.println(tag->getID());
        database[size] = *tag;
        
//        Serial.print("Tag id after adding to database: ");
//        Serial.println(database[size].getID());
        
        writeIndex = size;
        dueFlashStorage.write(1, size++); // update size of database
//        Serial.print("Tag not in database, adding to address: ");
//        Serial.println(size - 1);
    }

    // Write to dueFlashStorage.
    writeIndex = 2 + writeIndex * 4; // start writing only from address 2 onwards
//    Serial.print("writeIndex: ");
//    Serial.println(writeIndex);
    uint8_t lowId = tag->getID();
    uint8_t highId = tag->getID() >> 8;

    dueFlashStorage.write(writeIndex, highId);
    dueFlashStorage.write(writeIndex + 1, lowId);
    dueFlashStorage.write(writeIndex + 2, tag->isEnabled());
    dueFlashStorage.write(writeIndex + 3, tag->isStolen());
}

bool TagDatabase::contains(Tag* tag) {
    return false;
}

bool TagDatabase::contains(uint16_t id) {
    return false;
}

uint8_t TagDatabase::getThreshold() {
    return threshold;
}

void TagDatabase::setThreshold(uint8_t threshold) {
    this->threshold = threshold;
}

Tag* TagDatabase::get(uint16_t id) {
//    Serial.println("TagDatabase::get(id) being executed:");
//    Serial.print("Size of database: ");
//    Serial.println(size);
    for (int i = 0; i != size; ++i) {
//        Serial.print("database[");
//        Serial.print(i);
//        Serial.print("] = ");
//        Serial.println(database[i].getID());
        if (database[i].getID() == id) {
            return &database[i];
        }
    }
    return NULL;
}

int8_t TagDatabase::get(Tag* tag) {
    for (int i = 0; i != size; ++i) {
        if (database[i].getID() == tag->getID()) {
            return i;
        }
    }
    return -1;
}

