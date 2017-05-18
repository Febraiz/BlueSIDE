#ifndef UTILS_HPP_
#define UTILS_HPP_

#include <opencv2/opencv.hpp>

void printImageStats(const cv::Mat & img, const std::string & name);
void printInfoColorSpaces();

void smoothCrf(cv::Mat & crf, int blurOrder, int blurSize);
void logSmoothCrf(cv::Mat & crf, int blurOrder, int blurSize);
void saveCrf(const cv::Mat & crf, const std::string & filename, const std::string & dir);
cv::Mat loadCrf(const std::string & filename, const std::string & dir);
cv::Mat readCSV(const std::string & filename, const std::string & dir);

void saveHdr(const cv::Mat & img, const std::string & filename, int normMax, const std::string & dir);
cv::Mat loadHdr(const std::string & filename, const std::string & dir);
cv::Mat loadHdrQuiet(const std::string & filename, const std::string & dir);

#endif
