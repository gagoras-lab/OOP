#include "gscimage.hpp"

GSCImage::GSCImage(int width, int height) {
	vector<vector<GSCPixel*>> pixels(height, vector<GSCPixel*>(width));
	this->pixels = pixels;
	this->width = width;
	this->height = height;
}

GSCImage::GSCImage(const RGBImage &grayscaled) {

	// Copy dimensions from RGBImage
    this->width = grayscaled.getWidth();
	this->height = grayscaled.getHeight();

	vector<vector<GSCPixel*>> newpixels(this->getHeight(), vector<GSCPixel*>(this->getWidth()));
    for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			Pixel rgb_value = grayscaled.getPixel(i,j);
			GSCPixel* newPixel = new GSCPixel();
			newPixel->setValue(0.3*static_cast<int>(rgb_value.getRed()) + 0.59*static_cast<int>(rgb_value.getGreen()) + 0.11*static_cast<int>(rgb_value.getBlue()));
			newpixels[i][j] = newPixel;
		}
	}
	
	this->pixels = newpixels;
}

// Read image data from the input stream
GSCImage::GSCImage(std::istream &stream) {
	std::string line;
	int newWidth, newHeight, max_lum;

	// Get dimensions and max luminosity (Type has already been read in readNetpbmImage method (hw4.c))
	stream >> newWidth >> newHeight;
	this->setWidth(newWidth);
	this->setHeight(newHeight);
	stream >> max_lum;

	vector<vector<GSCPixel*>> pixels(newHeight, vector<GSCPixel*>(newWidth));
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			GSCPixel* newPixel = new GSCPixel();
			int val;
			stream >> val;
			newPixel->setValue(val);
			pixels[i][j] = newPixel;
		}
	}

	this->pixels = pixels;
}

GSCImage::~GSCImage(){
	this->delete_pixels();
}

void GSCImage::delete_pixels() {
	for(int i = 0; i < this->getHeight(); i++) {
		for(int j = 0; j < this->getWidth(); j++) {
			delete this->pixels[i][j]; // Delete each individual GSCPixel
			this->pixels[i][j] = nullptr;
		}
		this->pixels[i].clear();
	}
	this->pixels.clear();
}

GSCImage &GSCImage::operator=(const GSCImage &img) {
    *this = img;

	return *this;
}

bool GSCImage::isGrayscale() const {
    return true;
}

// Operator+= method to rotate the image clockwise or counterclockwise
Image& GSCImage::operator+=(int times) {
	// Check if rotation is necessary
	int numRotations = times % 4;
	if (numRotations == 0) {
		return *this;  // No rotation needed
	}

	int init_height = this->getHeight();
	int init_width = this->getWidth();

	// Create a copy of the pixels vector
	std::vector<std::vector<GSCPixel*>> rotatedPixels = this->pixels;

	// Perform the rotation
    if (times < 0) {
        numRotations = std::abs(times);  // Rotate to the left
    }

	for (int k = 0; k < numRotations; ++k) {
		vector<vector<GSCPixel*>> temp(init_width, vector<GSCPixel*>(init_height));

		for (int i = 0; i < init_height; ++i) {
			for (int j = 0; j < init_width; ++j) {
				if (times >= 0) {
                    temp[j][init_height - 1 - i] = rotatedPixels[i][j];
                } else {
                    temp[init_width - 1 - j][i] = rotatedPixels[i][j];
                }
			}
		}
		
		// Update the rotatedPixels for the next iteration
		rotatedPixels = temp;

		// Swap the height and width for the next iteration
		std::swap(init_height, init_width);
	}

	// Update the original pixels vector with the rotated pixels
	this->setHeight(init_height);
	this->setWidth(init_width);
	this->pixels = rotatedPixels;

	return *this;
}

// Operator*= method to resize the image
Image& GSCImage::operator*=(double factor) {
	// Check if resizing is necessary
	if (factor == 1.0) {
		return *this;  // No resizing needed
	}

	int newWidth = static_cast<int>(this->width * factor);
	int newHeight = static_cast<int>(this->height * factor);

	// Create a copy of the original pixels vector
	vector<vector<GSCPixel*>> newImagePixels(newHeight, vector<GSCPixel*>(newWidth));

	// Perform the resizing
	for (int i = 0; i < newHeight; ++i) {
		for (int j = 0; j < newWidth; ++j) {
			int originalRow = static_cast<int>(i / factor);
			int originalCol = static_cast<int>(j / factor);
			Pixel pix = this->getPixel(originalRow, originalCol);
			GSCPixel* new_pix = new GSCPixel();

			new_pix->setValue(pix.getLuminosity());
			newImagePixels[i][j] = new_pix;
		}
	}

	// Update the image properties
	this->delete_pixels();
	this->setWidth(newWidth);
	this->setHeight(newHeight);

	// Update the original pixels vector with the resized pixels
	this->pixels = newImagePixels;

	return *this;
}

// Operator! method to reverse the colors of the image
Image& GSCImage::operator!() {
	// Reverse the colors of each pixel
    for (int i = 0; i < this->getHeight(); ++i) {
		for (int j = 0; j < this->getWidth(); ++j) {
            this->pixels[i][j]->setValue(255 - this->pixels[i][j]->getValue());
        }
    }

	return *this;
}

// Operator~ method to balance the histogram of the image
Image& GSCImage::operator~() {
	// Calculate histogram
	std::vector<int> histogram(256, 0);
	int totalPixels = 0;

	for (int i = 0; i < this->getHeight(); ++i) {
		for (int j = 0; j < this->getWidth(); ++j) {
            Pixel pix = this->getPixel(i,j);
            int index = static_cast<int>(pix.getLuminosity());
			histogram[index]++;
			totalPixels++;
		}
	}

	// Calculate cumulative distribution function
	std::vector<int> cdf(256, 0);
	int cumulativeSum = 0;

	for (int i = 0; i < 256; ++i) {
		cumulativeSum += histogram[i];
		cdf[i] = cumulativeSum;
	}

	// Normalize the histogram
	std::vector<int> normalizedHistogram(256, 0);

	for (int i = 0; i < 256; ++i) {
		normalizedHistogram[i] = static_cast<int>((cdf[i] / static_cast<double>(totalPixels)) * 255.0);
	}

	// Apply histogram equalization
	for (auto& row : pixels) {
		for (auto& pixel : row) {
            unsigned char value = normalizedHistogram[pixel->getValue()];
            pixel->setValue(value);
		}
	}

	return *this;
}

// Operator* method to flip the image on the vertical axis
Image& GSCImage::operator*() {
    int columns = this->getWidth();
    int rows = this->getHeight();
    
    for (int i = 0; i < rows; i++) {
        // Initialise start and end index
        int start = 0;
        int end = columns - 1;
 
        // Till start < end, swap the element
        // at start and end index
        while (start < end) {
            // Swap the element
			std::swap(this->pixels[i][start], this->pixels[i][end]);
 
            // Increment start and decrement
            // end for next pair of swapping
            start++;
            end--;
        }
    }

    return *this;
}

// Method that returns a reference to the GSCPixel of (row,col) position
Pixel& GSCImage::getPixel(int row, int col) const {
    if (row < 0 || col < 0) {
        throw std::out_of_range("Invalid pixel coordinates");
    }
	
	Pixel* pix = pixels[row][col];
    return *pix;
}