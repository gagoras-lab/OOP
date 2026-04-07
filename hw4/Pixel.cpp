#include "pixel.hpp"

Pixel::Pixel(unsigned char r, unsigned char g, unsigned char b) { this->red = r; this->green = g; this->blue = b; }
Pixel::Pixel() { this->red = '0'; this->green = '0'; this->blue = '0'; this->luminosity = '0'; }
Pixel::Pixel(unsigned char lum) { this->luminosity = lum; }
unsigned char Pixel::getRed() { return this->red; }
unsigned char Pixel::getGreen() { return this->green; }
unsigned char Pixel::getBlue() { return this->blue; }
unsigned char Pixel::getLuminosity() { return this->luminosity; }
