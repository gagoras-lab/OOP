#include "image.hpp"
#include <iostream>

// Print the image contents in NetBPM format
std::ostream &operator<<(std::ostream &out, Image &image) {
	int cols = image.getWidth();
    int rows = image.getHeight();
	
	if (image.isGrayscale()) {
		// Grayscale image
		out << "P2" << std::endl;  // Magic number for PGM format
		out << cols << " " << rows << std::endl;  // Image dimensions
		out << "255" << std::endl;  // Maximum pixel value
		
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
                Pixel pix = image.getPixel(r, c);
				int intensity = static_cast<int>(pix.getLuminosity());
				out << intensity << " ";
			}
			out << std::endl;
		}
	} else if (!image.isGrayscale()) {
		// RGB image
		out << "P3" << std::endl;  // Magic number for PPM format
		out << cols << " " << rows << std::endl;  // Image dimensions
		out << "255" << std::endl;  // Maximum pixel value
		
    	for (int r = 0; r < rows; ++r) {
        	for (int c = 0; c < cols; ++c) {
				Pixel rgb = image.getPixel(r, c);
				int red = static_cast<int>(rgb.getRed());
				int green = static_cast<int>(rgb.getGreen());
				int blue = static_cast<int>(rgb.getBlue());
				out << red << " " << green << " " << blue << " ";
			}
        out << std::endl;
    	}
	} else {
		std::cerr << "Error: Unsupported number of image channels." << std::endl;
	}
	
	return out;
}