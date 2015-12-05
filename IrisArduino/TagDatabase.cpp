#include "TagDatabase.h"

TagDatabase::TagDatabase() {}

void TagDatabase::load() {
    // Check if the EEPROM already contains an existing database.
    if (EEPROM.read(0) != SCHEMA) {
        EEPROM.write(0, SCHEMA); // set schema
        EEPROM.write(1, 0); // set size to 0
        return;
    }

    // Get the size of database.
    size = EEPROM.read(1);

    // Allocate memory for the database.
    for (int i = 0; i != size; ++i) {
        Tag tag;
        int index = 2 + i * 4; // start reading only from address 2 onwards

        uint16_t id = (EEPROM.read(index) << 8) + EEPROM.read(index + 1);
        tag.setID(id);

        if (EEPROM.read(index + 2)) tag.enable();

        tag.setStolen(EEPROM.read(index + 3));

        database[i] = &tag;
    }
}

void TagDatabase::add(Tag* tag) {
    store(tag);
}

void TagDatabase::update(Tag* tag) {
    store(tag);
}

void TagDatabase::store(Tag* tag) {
    int writeIndex;
    // Checks if tag is already in the database.
    int index = get(tag);
    if (index != -1) { // tag is in database
        // Check if there is a need to update the database.
        if (database[index]->isStolen() != tag->isStolen())
            database[index]->setStolen(tag->isStolen());

        if (database[index]->isEnabled() != tag->isEnabled()) {
            if (tag->isEnabled())
                database[index]->enable();
            else
                database[index]->disable();
        }
        writeIndex = index;
    }
    else { // tag is not in database
        writeIndex = size;
    }

    // Write to EEPROM.
    writeIndex = 2 + writeIndex * 4; // start writing only from address 2 onwards

    uint8_t lowId = tag->getID();
    uint8_t highId = tag->getID() >> 8;

    EEPROM.write(writeIndex, highId);
    EEPROM.write(writeIndex + 1, lowId);
    EEPROM.write(writeIndex + 2, tag->isEnabled());
    EEPROM.write(writeIndex + 3, tag->isStolen());

    // Update size if necessary.
    if (writeIndex == size) {
        EEPROM.write(1, ++size);
    }
}

bool TagDatabase::contains(Tag* tag) {
    return false;
}

bool TagDatabase::contains(uint16_t id) {
    return false;
}

Tag* TagDatabase::get(uint16_t id) {
    for (int i = 0; i != size; ++i) {
        if (database[i]->getID() == id) {
            return database[i];
        }
    }
    return NULL;
}

int8_t TagDatabase::get(Tag* tag) {
    for (int i = 0; i != size; ++i) {
        if (database[i]->getID() == tag->getID()) {
            return i;
        }
    }
    return -1;
}

