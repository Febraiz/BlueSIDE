#ifndef RANDOM_HPP_
#define RANDOM_HPP_

#include <random>

class Random 
{
	private:
		std::mt19937_64 _engine;
        std::uniform_real_distribution<double> _dist;
    public:
        Random();
        Random(const Random &) = delete;
        double operator()();
        int operator()(int n);
};

#endif

