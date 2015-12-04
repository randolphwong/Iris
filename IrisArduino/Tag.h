#ifndef TAG_H
#define TAG_H

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

class Tag {

public:
    Tag();
    Tag(const uint16_t);
    void setID(const uint16_t);
    void enable();
    void disable();
    void setStolen(const bool stolen);
    uint16_t getID();
    bool isEnabled();
    bool isStolen();

private:
    uint16_t id = 0;
    bool enabled = false;
    bool stolen = false;
};

#endif
