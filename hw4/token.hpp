#ifndef _TOKEN_HPP_
#define _TOKEN_HPP_
#include "image.hpp"
#include "gscimage.hpp"
#include "rgbimage.hpp"

class Token {
    private:
        string name;
        Image* ptr;
    public:
        Token(const string& ="",Image* = nullptr); // Default constructor
        ~Token(); // Deconstructor
        string getName() const; // Method to get the Token's name
        Image* getPtr() const; // Method to get the Token's Image pointer
        void setName(const string& ); // Method to set the Token's name
        void setPtr(Image* ptr); // Method to set the Token's Image pointer
};

#endif