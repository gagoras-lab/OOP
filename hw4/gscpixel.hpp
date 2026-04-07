#ifndef _GSCPIXEL_HPP_
#define _GSCPIXEL_HPP_
#include "pixel.hpp"

class GSCPixel : public Pixel {
	public:
		GSCPixel() = default; // Default constructor of GSCPixel
		GSCPixel(const GSCPixel& p); // Copy constructor
		GSCPixel(unsigned char value); // Constructor that sets the luminosity equal to value
		unsigned char getValue(); // Method to get the GSCPixel's luminosity
		void setValue(unsigned char value); // Method to set the GSCPixel's luminosity
		~GSCPixel(); // Deconstructor
};

#endif