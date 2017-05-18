#ifndef DETECTION_HPP_
#define DETECTION_HPP_

#include <opencv2/opencv.hpp>

void detectionSclera(const cv::Mat & image, cv::Point seed, cv::Mat & sclera);
void detectionReflection(const cv::Mat & image, cv::Point seed, cv::Mat & reflection);
void detectionColorchecker(const cv::Mat & image, cv::Point seed, cv::Mat & colorchecker);

#endif
