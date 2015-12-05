#ifndef TAGDATABASE_H
#define TAGDATABASE_H

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

#include "Tag.h"

class TagDatabase {

public:
    TagDatabase();
    void load();
    void store(Tag*);
    bool contains(Tag*);
    bool contains(uint16_t);
    Tag* get(uint16_t);

private:
    uint8_t size;
    Tag** database;
};

#endif
