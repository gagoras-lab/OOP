#ifndef _GSCIMAGE_HPP_
#define _GSCIMAGE_HPP_
#include <sstream>
#include <vector>
#include "image.hpp"
#include "rgbimage.hpp"
#include "gscpixel.hpp"

class GSCImage : public Image {
    private:
        vector<vector<GSCPixel*>> pixels;
	public:
		GSCImage(int width, int height); // Default constructor of GSCImage
		GSCImage(const GSCImage& img): pixels(img.pixels) {} // Copy constructor
		GSCImage(const RGBImage& grayscaled); // Constructor the converts an RGBImage to GSCImage
		GSCImage(std::istream& stream); // Constructor from file
		~GSCImage(); // Deconstructor

		GSCImage& operator = (const GSCImage& img); // Copy constructor through operator '='

		virtual bool isGrayscale() const override;
			
		virtual Image& operator += (int ) override ;
		
		virtual Image& operator *= (double factor) override;
		virtual Image& operator !  () override;
		virtual Image& operator ~  () override;
		virtual Image& operator *  () override;

		virtual Pixel& getPixel(int row, int col) const override;
		virtual void delete_pixels() override;
		friend std::ostream& operator << (std::ostream& out, Image& image);
};

#endif