#include "pixel.hpp"
#include "rgbpixel.hpp"
#include "gscpixel.hpp"
#include "image.hpp"
#include "rgbimage.hpp"
#include "gscimage.hpp"
#include "token.hpp"
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <algorithm>
#include <sys/stat.h>
#include <unistd.h>
#include <experimental/filesystem>

#define MAX_LEN 6 // define the MAX_LEN string
namespace fs = std::experimental::filesystem;

std::string input_strings[MAX_LEN];

// length of the string  
int len(string str) {  
    int length = 0;  
    for (int i = 0; str[i] != '\0'; i++)  
    {  
        length++;  
          
    }  
    return length;     
}  
  
// create custom split() function  
void split (string str, char seperator)  
{  
    int currIndex = 0, i = 0;  
    int startIndex = 0, endIndex = 0;  
    while (i <= len(str))  
    {  
        if (str[i] == seperator || i == len(str)) // Seperate the i-th string
        {  
            endIndex = i;  
            string subStr = "";  
            subStr.append(str, startIndex, endIndex - startIndex);  
            input_strings[currIndex] = subStr;  
            currIndex += 1;  
            startIndex = endIndex + 1;  
        }  
        i++;  
    }     
}

// Method to check and read NetPBM image
Image* readNetpbmImage(const char* filename) {
    ifstream f(filename);
    if(!f.is_open()) {
        std::cout << "[ERROR] Unable to open " << filename << std::endl;
    }
    Image* img_ptr = nullptr;
    string type;

    if(f.good() && !f.eof())
        f >> type;
    if(!type.compare("P3")) {
        img_ptr = new RGBImage(f);
    }
    else if(!type.compare("P2")) {
        img_ptr = new GSCImage(f);
    }
    else if(f.is_open()) {
        std::cout << "[ERROR] Invalid file format" << std::endl;
    }
    return img_ptr;
}

// Method used to export an image to a new file
void createExportFile(vector<Token*>& database, vector<string>& token_ids,const std::string& filename,const std::string& token_name) {
    std::ifstream file(filename);
    
    if (file) {
        std::cout << "[ERROR] File exists" << std::endl;
    } else {
        std::ofstream newFile;
        newFile.open(filename);
        if(newFile.fail()) {
            std::cout << "[ERROR] Unable to create file" << std::endl;
        }
        else {
            auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
            if ( token_ids.end() != find_token ) { // Token exists
                int index = find_token - token_ids.begin();
                Token* entry = database.at(index);
                Image* out_image = entry->getPtr();
                newFile<<(*out_image);
                std::cout << "[OK] Export " << token_name <<std::endl;
                newFile.close();
            }
            else {
                std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
            }
            newFile.close();
        }
    }
}

// Method to delete entry from a database of tokens
void delete_entry(vector<Token*>& db, const string& name) {
    for (auto it = db.begin(); it != db.end(); ++it) {
        int index = it - db.begin();
        Token* entry = db.at(index);
        
        if (entry->getName() == name) { // Correct token found
            Image* img = entry->getPtr();
            delete img; // Delete the image saved to the token's pointer
            db.erase(it); // Delete the element from the database
            delete entry; // Delete the entry itself
            break;
        }
    }
}

// Clear all elements of the token database
void clear_contents(vector<Token*>& database) {
    for (Token* entry: database) {
        Image* img = entry->getPtr();
        delete img; // Delete the image saved to the token's pointer
        delete entry; // Delete the entry itself
    }
    database.clear(); // Delete the database
}

// MAIN METHOD //
int main() {
    vector<Token*> database; // Database of tokens
    vector<string> token_ids; // Database that holds the token names (used for search purposes only)
    int index;
    Token* entry;
    char seperator = ' '; // space

    while (true) {
        std::string input, filename, token_name;
        char choice; // Input choice
        int rotation_factor; // Used in 'r' option
        double scaling_factor; // Uesd in 's' option

        std::getline(std::cin, input); // Get option line from standard input
        
        split(input, seperator); // Split input line to seperate strings
        choice = input_strings[0][0];

        switch(choice) {

            // IMPORT //
            case 'i': {
                filename = input_strings[1];
                const char * f_ptr = filename.c_str();

                token_name = input_strings[3];
                Image* new_img = readNetpbmImage(f_ptr);

                // Check if token already exists
                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() == find_token ) { // Token doesn't already exist
                    Token* new_entry = new Token(filename, new_img); // Construct new token/entry

                    // Initialize the name and image pointer of the new token
                    new_entry->setName(token_name);
                    new_entry->setPtr(new_img);

                    // Put the new token in the end of both vectors
                    database.push_back(new_entry);
                    token_ids.push_back(token_name);

                    std::cout << "[OK] Import " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " exists" << std::endl;
                }
                break;
            }

            // EXPORT //
            case 'e': {
                token_name = input_strings[1];
                filename = input_strings[3];
                
                createExportFile(database, token_ids, filename, token_name);

                break;
            }

            // DELETE //
            case 'd': {
                token_name = input_strings[1];

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    delete_entry(database, token_name);
                    token_ids.erase(find_token);

                    std::cout << "[OK] Delete " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }
                
                break;
            }

            // COLOR INVERSION //
            case 'n': {
                token_name = input_strings[1];

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    Image* init_img = entry->getPtr();
                    !(*init_img);

                    std::cout << "[OK] Color Inversion " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }

                break;
            }

            // EQUALIZE //
            case 'z': {
                token_name = input_strings[1];

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    Image* init_img = entry->getPtr();
                    ~(*init_img);

                    std::cout << "[OK] Equalize " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }

                break;
            }
            
            // MIRROR //
            case 'm': {
                token_name = input_strings[1];

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    Image* init_img = entry->getPtr();
                    *(*init_img);

                    std::cout << "[OK] Mirror " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }

                break;
            }

            // GRAYSCALE //
            case 'g': {
                token_name = input_strings[1];

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    Image* img_entry = entry->getPtr();

                    // Check if the image is already grayscaled
                    if(img_entry->isGrayscale()) {
                        std::cout << "[NOP] Already grayscale " << token_name <<std::endl;
                    }
                    else {
                        RGBImage* rgb_img = (RGBImage*) img_entry;
                        GSCImage* grayscaled = new GSCImage(*rgb_img);
                        entry->setPtr(grayscaled);
                        delete rgb_img;
                        std::cout << "[OK] Grayscale " << token_name <<std::endl;
                    }
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }

                break;
            }

            // SCALE //
            case 's': {
                token_name = input_strings[1];
                scaling_factor = stod(input_strings[3]);

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    Image* init_img = entry->getPtr();
                    (*init_img)*=scaling_factor;

                    std::cout << "[OK] Scale " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }

                break;
            }

            // ROTATE //
            case 'r': {
                token_name = input_strings[1];
                rotation_factor = stoi(input_strings[3]);

                auto find_token = std::find(token_ids.begin(), token_ids.end(), token_name);
                if ( token_ids.end() != find_token ) { // Token exists
                    index = find_token - token_ids.begin();
                    entry = database.at(index);
                    Image* init_img = entry->getPtr();
                    (*init_img)+=rotation_factor;

                    std::cout << "[OK] Rotate " << token_name <<std::endl;
                }
                else {
                    std::cout << "[ERROR] Token " << token_name << " not found!" << std::endl;
                }
                
                break;
            }

            // QUIT //
            case 'q': {
                clear_contents(database);
                return 0;
            }

            default: {
                std::cerr << "\n-- Invalid command! --" << std::endl;
                break;
            }
        }
    }
}