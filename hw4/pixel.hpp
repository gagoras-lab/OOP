#ifndef _PIXEL_HPP_
#define _PIXEL_HPP_

#include <iostream>
using namespace std;

#define MAX_LUMINOCITY 255;

class Pixel {
	protected:
		unsigned char red;
		unsigned char green;
		unsigned char blue;
		unsigned char luminosity;
	public:
		Pixel(unsigned char r, unsigned char g, unsigned char b); // Constructor that initializes the red,green,blue values
		Pixel(); // Default constructor
		Pixel(unsigned char lum); // Constructor that initializes the luminosity value
		virtual ~Pixel(){} // Deconstructor
		unsigned char getRed(); // Method to get the red value of this pixel
		unsigned char getGreen(); // Method to get the green value of this pixel
		unsigned char getBlue(); // Method to get the blue value of this pixel
		unsigned char getLuminosity(); // Method to get the luminosity value of this pixel
};

#endif
