#ifndef _RGBIMAGE_HPP_
#define _RGBIMAGE_HPP_
#include <sstream>
#include <vector>
#include <array>
#include "image.hpp"
#include "rgbpixel.hpp"

class RGBImage : public Image {
	protected:
        vector<vector<RGBPixel*>> pixels;
	public:
		RGBImage(int width, int height);  // Default constructor of RGBImage
		RGBImage(const RGBImage& img): pixels(img.pixels) {}; // Copy constructor
		RGBImage(std::istream& stream); // Constructor from file
		~RGBImage(); // Deconstructor
		RGBImage& operator = (const RGBImage& img);
   
	virtual Image& operator += (int ) override ;

	virtual bool isGrayscale() const override;
  
	virtual Image& operator *= (double factor) override;
	virtual Image& operator !() override;
	virtual Image& operator ~() override;
	virtual Image& operator *() override;

    virtual Pixel& getPixel(int row, int col) const override;
	virtual void delete_pixels() override;
	friend std::ostream& operator << (std::ostream& out, Image& image);
};

#endif