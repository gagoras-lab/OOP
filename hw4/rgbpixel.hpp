#ifndef _RGBPIXEL_HPP_
#define _RGBPIXEL_HPP_
#include "pixel.hpp"

class RGBPixel : public Pixel {
	public:
		RGBPixel() = default; // Default constructor of RGBPixel
		RGBPixel(const RGBPixel& p); // Copy constructor
		RGBPixel(unsigned char r, unsigned char g, unsigned char b); // Constructor that initializes the red,green,blue values
		unsigned char getRed() const; // Method to get the RGBPixel's red
		unsigned char getGreen() const; // Method to get the RGBPixel's green
		unsigned char getBlue() const; // Method to get the RGBPixel's blue
		void setRed(int r); // Method to set the RGBPixel's red
		void setGreen(int g); // Method to set the RGBPixel's green
		void setBlue(int b); // Method to set the RGBPixel's blue
		~RGBPixel(); // Deconstructor
};

#endif