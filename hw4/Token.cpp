#include "token.hpp"

Token::Token(const string &, Image *) { }
Token::~Token(){ }
string Token::getName() const { return this->name; }
Image* Token::getPtr() const { return this->ptr; }
void Token::setName(const string& newName) { this->name = newName; }
void Token::setPtr(Image* newPtr) { this->ptr = newPtr; }