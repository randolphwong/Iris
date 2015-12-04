#include "Tag.h"

Tag::Tag() {}

Tag::Tag(const uint16_t id) {
    setID(id);
}

void Tag::setID(const uint16_t id) {
    this->id = id;
}

void Tag::enable() {
    this->enabled = true;
}

void Tag::disable() {
    this->enabled = false;
}

void Tag::setStolen(const bool stolen) {
    this->stolen = stolen;
}

uint16_t Tag::getID() {
    return id;
}

bool Tag::isEnabled() {
    return enabled;
}

bool Tag::isStolen() {
    return stolen;
}
