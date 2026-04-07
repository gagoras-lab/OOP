#include "gscpixel.hpp"

GSCPixel::GSCPixel(const GSCPixel &p) { *this = p; }
GSCPixel::GSCPixel(unsigned char value) { this->luminosity = value; }
unsigned char GSCPixel::getValue() { return Pixel::getLuminosity(); }
void GSCPixel::setValue(unsigned char value) { luminosity = value; }
GSCPixel::~GSCPixel() { }