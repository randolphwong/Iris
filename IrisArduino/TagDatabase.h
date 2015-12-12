#ifndef TAGDATABASE_H
#define TAGDATABASE_H

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

#include <stdlib.h>
//#include <EEPROM.h>
#include "DueFlashStorage.h"
#include "Tag.h"

#define SCHEMA 123
#define DATABASE_SIZE 8
#define TAG_DEFAULT_THRESHOLD 2

class TagDatabase {

public:
    TagDatabase();
    void load();
    void clear();
    void add(Tag*);
    void update(Tag*);
    bool contains(Tag*);
    bool contains(uint16_t);
    uint8_t getThreshold();
    void setThreshold(uint8_t);
    Tag* get(uint16_t);

private:
    DueFlashStorage dueFlashStorage;
    void store(Tag*);
    int8_t get(Tag*);

    int8_t size;
    uint8_t threshold;
    Tag database[8];
};

#endif
