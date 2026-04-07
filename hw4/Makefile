CPPFLAGS = -Wall -g -fsanitize=address
OBJ = hw4.o Token.o RGBImage.o GSCImage.o Image.o RGBPixel.o GSCPixel.o Pixel.o

#top-level goal
all: $(OBJ)
	g++ $(OBJ) -fsanitize=address -o hw4

hw4.o: hw4.cpp Token.cpp token.hpp Pixel.cpp pixel.hpp Image.cpp image.hpp GSCPixel.cpp gscpixel.hpp RGBPixel.cpp rgbpixel.hpp GSCImage.cpp gscimage.hpp RGBImage.cpp rgbimage.hpp
	g++ $(CPPFLAGS) -c hw4.cpp

Token.o: Token.cpp token.hpp
	g++ $(CPPFLAGS) -c Token.cpp

#Image objects
RGBImage.o: RGBImage.cpp RGBPixel.cpp rgbpixel.hpp Image.cpp image.hpp rgbimage.hpp
	g++ $(CPPFLAGS) -c RGBImage.cpp

GSCImage.o: GSCImage.cpp GSCPixel.cpp gscpixel.hpp Image.cpp image.hpp RGBImage.cpp rgbimage.hpp gscimage.hpp
	g++ $(CPPFLAGS) -c GSCImage.cpp

Image.o: Image.cpp Pixel.cpp pixel.hpp image.hpp
	g++ $(CPPFLAGS) -c Image.cpp

#Pixel objects
RGBPixel.o: RGBPixel.cpp rgbpixel.hpp Pixel.cpp pixel.hpp
	g++ $(CPPFLAGS) -c RGBPixel.cpp

GSCPixel.o: GSCPixel.cpp gscpixel.hpp Pixel.cpp pixel.hpp
	g++ $(CPPFLAGS) -c GSCPixel.cpp

Pixel.o: Pixel.cpp pixel.hpp
	g++ $(CPPFLAGS) -c Pixel.cpp

.PHONY: clean
clean:
	rm *.o
