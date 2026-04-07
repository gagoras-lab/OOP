#include "rgbpixel.hpp"

RGBPixel::RGBPixel(const RGBPixel& p) { *this = p; }
RGBPixel::RGBPixel(unsigned char r, unsigned char g, unsigned char b) { Pixel(r,g,b); }
unsigned char RGBPixel::getRed() const { return this->red; }
unsigned char RGBPixel::getGreen() const { return this->green; }
unsigned char RGBPixel::getBlue() const { return this->blue; }
void RGBPixel::setRed(int r) { this->red = r; }
void RGBPixel::setGreen(int g) { this->green = g; }
void RGBPixel::setBlue(int b) { this->blue = b; }
RGBPixel::~RGBPixel() { }