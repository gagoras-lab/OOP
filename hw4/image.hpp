#ifndef _IMAGE_HPP_
#define _IMAGE_HPP_

#include <iostream>
#include <vector>
#include "pixel.hpp"
#include "rgbpixel.hpp"
#include "gscpixel.hpp"
using namespace std;

#define CLIP(X) ( (X) > 255 ? 255 : (X) < 0 ? 0 : X)

// RGB -> YUV
#define RGB2Y(R, G, B) CLIP(( (  66 * (R) + 129 * (G) +  25 * (B) + 128) >> 8) +  16)
#define RGB2U(R, G, B) CLIP(( ( -38 * (R) -  74 * (G) + 112 * (B) + 128) >> 8) + 128)
#define RGB2V(R, G, B) CLIP(( ( 112 * (R) -  94 * (G) -  18 * (B) + 128) >> 8) + 128)

// YUV -> RGB
#define C(Y) ( (Y) - 16  )
#define D(U) ( (U) - 128 )
#define E(V) ( (V) - 128 )

#define YUV2R(Y, U, V) CLIP(( 298 * C(Y)              + 409 * E(V) + 128) >> 8)
#define YUV2G(Y, U, V) CLIP(( 298 * C(Y) - 100 * D(U) - 208 * E(V) + 128) >> 8)
#define YUV2B(Y, U, V) CLIP(( 298 * C(Y) + 516 * D(U)              + 128) >> 8)

class Image {
  protected:
    int width;
    int height;
    int max_luminocity;
  public:
    int getWidth() const { return width; }
    int getHeight() const { return height; }
    int getMaxLuminocity() const { return max_luminocity;  }
    void setWidth(int width) { this->width = width; }
    void setHeight(int height) { this->height = height; }
    void setMaxLuminocity(int lum) { this->max_luminocity = lum; }

    virtual Image& operator += (int times) = 0; // Operator+= method to rotate the image clockwise or counterclockwise
    virtual Image& operator *= (double factor) = 0; // Operator*= method to resize the image
    virtual Image& operator !() = 0; // Operator! method to reverse the colors of the image
    virtual Image& operator ~() = 0; // Operator~ method to balance the histogram of the image
    virtual Image& operator *() = 0; // Operator* method to flip the image on the vertical axis
    virtual Pixel& getPixel(int row, int col) const = 0; // Method that returns the pixel of the image in (row,col) position
    virtual bool isGrayscale() const = 0; // Constant method that indicates if the image is of type 'GSCImage' or 'RGBImage'
    virtual ~Image(){} // Deconstructor
    virtual void delete_pixels() = 0; // Method that deletes the pixels of the image, used by the deconstructor

    friend std::ostream& operator << (std::ostream& out, Image& image); // Method to save an image in NetBPM format to a file
};

#endif
