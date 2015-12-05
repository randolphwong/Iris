#include "Tag.h"

Tag::Tag() {}

Tag::Tag(Tag* tag) {
    setID(tag->getID());
    if (tag->isEnabled())
        enable();
    setStolen(tag->isStolen());
}

Tag::Tag(uint16_t id) {
    setID(id);
}

void Tag::setID(uint16_t id) {
    this->id = id;
}

void Tag::enable() {
    this->enabled = true;
}

void Tag::disable() {
    this->enabled = false;
}

void Tag::setStolen(bool stolen) {
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
