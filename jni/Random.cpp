#include "Random.hpp"

Random::Random() : 
    _engine(std::random_device{}()), _dist(0, 1) 
{}

double Random::operator()() 
{ 
    return _dist(_engine); 
}

int Random::operator()(int n) 
{ 
    return std::min(n-1, int(n*_dist(_engine))); 
}

