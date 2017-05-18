#include "Detection.hpp"
#include <iostream>
#include <stdio.h>
#include <fstream>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

cv::Vec3b color_black = cv::Vec3b(0, 0, 0);
cv::Vec3b color_white = cv::Vec3b(255, 255, 255);

void detectionSclera(const cv::Mat & image, cv::Point seed, cv::Mat & sclera){

	cv::Mat hsv;
	cv::cvtColor(image, hsv, CV_BGR2HSV);

	cv::Mat mask = hsv.clone();
	cv::Scalar lower = cv::Scalar(80, 50, 50);
	cv::Scalar upper = cv::Scalar(80, 50, 50);

	std::cout << "floodfill" << std::endl;
	cv::floodFill(mask, seed, cv::Scalar(255, 255, 255), 0, lower, upper, FLOODFILL_FIXED_RANGE);

	//cv::imwrite("floodfill_white.png", mask);

	cv::Mat mask2 = mask.clone();

	for (int i = 0; i < mask.rows; i++) {
		for (int j = 0; j < mask.cols; j++) {

			if (mask.at<cv::Vec3b>(i, j) != color_white)
				mask2.at<cv::Vec3b>(i, j) = color_black;			
		}
	}

	//cv::imwrite("mask_floodfill.png", mask2);

	cv::cvtColor(mask2, mask2, CV_BGR2GRAY);

	//detect, draw and fill contours
	cv::Mat maskContours = mask2.clone();

	std::cout << "find and draw contours" << std::endl;
	std::vector<std::vector<cv::Point> > contours;
	cv::findContours(maskContours, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

	//find index in the vector of the contour containing the seed point (most of the time it will be 0)
	int index = -1;

	for (int i = 0; i < contours.size(); i++) {

		int minX = image.rows;
		int minY = image.cols;
		int maxX = 0;
		int maxY = 0;

		for (int j = 0; j < contours[i].size(); j++) {
			if (contours[i].at(j).x < minX) minX = contours[i].at(j).x;
			if (contours[i].at(j).y < minY) minY = contours[i].at(j).y;
			if (contours[i].at(j).x > maxX) maxX = contours[i].at(j).x;
			if (contours[i].at(j).y > maxY) maxY = contours[i].at(j).y;
		}
		if ((seed.x >= minX) && (seed.x <= maxX) && (seed.y >= minY) && (seed.y <= maxY)) {
			index = i;
			std::cout << "index: " << index << std::endl;
		}
		
	}


	cv::drawContours(maskContours, contours, index, CV_RGB(255, 255, 255), CV_FILLED);

	//cv::imwrite("maskContours.png", maskContours);
	
	cv::Mat maskFinal = maskContours.clone();
	
	//erode + dilate
	cv::erode(maskFinal, maskFinal, cv::getStructuringElement(MORPH_ELLIPSE, Size(5, 5)));
	std::cout << "erode " << std::endl;
	cv::dilate(maskFinal, maskFinal, cv::getStructuringElement(MORPH_ELLIPSE, Size(5, 5)));
	std::cout << "dilate " << std::endl;

	//dilate + erode
	cv::dilate(maskFinal, maskFinal, cv::getStructuringElement(MORPH_ELLIPSE, Size(20, 20)));
	std::cout << "dilate " << std::endl;
	cv::erode(maskFinal, maskFinal, cv::getStructuringElement(MORPH_ELLIPSE, Size(20, 20)));
	std::cout << "erode " << std::endl;

	//cv::imwrite("mask_dilate_erode.png", maskFinal);

	cv::cvtColor(maskFinal, maskFinal, CV_GRAY2BGR);

	sclera = image.clone();

	cv::cvtColor(maskContours, maskContours, CV_GRAY2BGR);
	cv::cvtColor(mask2, mask2, CV_GRAY2BGR);

	cv::Mat maskReflection = maskContours - mask2;	
	
	cv::Mat maskSclera = maskFinal - maskReflection;

	for (int i = 0; i < maskSclera.rows; i++) {
		for (int j = 0; j < maskSclera.cols; j++) {

			if (maskSclera.at<cv::Vec3b>(i, j) != color_white)
				sclera.at<cv::Vec3b>(i, j) = color_black;
		}
	}

	std::cout << "result sclera" << std::endl;
	

}


void detectionReflection(const cv::Mat & image, cv::Point seed, cv::Mat & reflection){

	cv::Mat hsv;
	cv::cvtColor(image, hsv, CV_BGR2HSV);

	cv::Mat mask = hsv.clone();
	cv::Scalar lower = cv::Scalar(80, 50, 25);	//25 better than 50 for under exposure
	cv::Scalar upper = cv::Scalar(80, 50, 50);

	std::cout << "floodfill" << std::endl;
	cv::floodFill(mask, seed, cv::Scalar(255, 255, 255), 0, lower, upper, FLOODFILL_FIXED_RANGE);

	//cv::imwrite("floodfill_reflection.png", mask);

	cv::Mat mask2 = mask.clone();

	for (int i = 0; i < mask.rows; i++) {
		for (int j = 0; j < mask.cols; j++) {

			if (mask.at<cv::Vec3b>(i, j) != color_white) 	
				mask2.at<cv::Vec3b>(i, j) = color_black;			
		}
	}

	//cv::imwrite("mask_floodfill.png", mask2);

	cv::cvtColor(mask2, mask2, CV_BGR2GRAY);

	//detect, draw and fill contours
	cv::Mat maskContours = mask2.clone();

	std::cout << "find and draw contours" << std::endl;
	std::vector<std::vector<cv::Point> > contours;
	cv::findContours(maskContours, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

	//find index in the vector of the contour containing the seed point (most of the time it will be 0)
	int index = -1;

	for (int i = 0; i < contours.size(); i++) {

		int minX = image.rows;
		int minY = image.cols;
		int maxX = 0;
		int maxY = 0;

		for (int j = 0; j < contours[i].size(); j++) {
			if (contours[i].at(j).x < minX) minX = contours[i].at(j).x;
			if (contours[i].at(j).y < minY) minY = contours[i].at(j).y;
			if (contours[i].at(j).x > maxX) maxX = contours[i].at(j).x;
			if (contours[i].at(j).y > maxY) maxY = contours[i].at(j).y;
		}
		if ((seed.x >= minX) && (seed.x <= maxX) && (seed.y >= minY) && (seed.y <= maxY)) {
			index = i;
			std::cout << "index: " << index << std::endl;
		}
		
	}


	cv::drawContours(maskContours, contours, index, CV_RGB(255, 255, 255), CV_FILLED);

	//cv::imwrite("maskContours.png", maskContours);
	
	cv::cvtColor(maskContours, maskContours, CV_GRAY2BGR);
	cv::cvtColor(mask2, mask2, CV_GRAY2BGR);

	cv::Mat maskReflection = maskContours - mask2;
	
	/*		
	//keep the important part of the reflection and not the isolated pixels (which can be wrong detection)
	cv::cvtColor(maskReflection, maskReflection, CV_BGR2GRAY);

	std::vector<std::vector<cv::Point>> contours_reflection;
	cv::findContours(maskReflection, contours_reflection, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

	int nb_points = 0;
	int c = -1;

	for (int i = 0; i < contours_reflection.size(); i++) {

		if (contours_reflection[i].size() > nb_points) {
			nb_points = contours_reflection[i].size();
			c = i;
		}

	}

	cv::drawContours(maskReflection, contours_reflection, c, CV_RGB(255, 255, 255), CV_FILLED);
	cv::imwrite("maskReflection.png", maskReflection);

	cv::cvtColor(maskReflection, maskReflection, CV_GRAY2BGR);*/
	
	reflection = image.clone();
	
	for (int i = 0; i < maskReflection.rows; i++) {
		for (int j = 0; j < maskReflection.cols; j++) {

			if (maskReflection.at<cv::Vec3b>(i, j) != color_white)
				reflection.at<cv::Vec3b>(i, j) = color_black;

		}
	}

	std::cout << "result reflection" << std::endl;

}

void detectionColorchecker(const cv::Mat & image, cv::Point seed, cv::Mat & colorchecker){

	cv::Mat hsv;
	cv::cvtColor(image, hsv, CV_BGR2HSV);

	cv::Mat mask = hsv.clone();
	cv::Scalar lower = cv::Scalar(80, 50, 50);
	cv::Scalar upper = cv::Scalar(80, 50, 50);

	std::cout << "floodfill" << std::endl;
	cv::floodFill(mask, seed, cv::Scalar(255, 255, 255), 0, lower, upper, FLOODFILL_FIXED_RANGE);

	//cv::imwrite("floodfill_colorchecker.png", mask);

	cv::Mat mask2 = mask.clone();

	for (int i = 0; i < mask.rows; i++) {
		for (int j = 0; j < mask.cols; j++) {

			if (mask.at<cv::Vec3b>(i, j) != color_white) {	

				mask2.at<cv::Vec3b>(i, j) = color_black;			
			}
		}
	}

	//cv::imwrite("mask_floodfill.png", mask2);

	cv::cvtColor(mask2, mask2, CV_BGR2GRAY);

	//detect, draw and fill contours
	cv::Mat maskContours = mask2.clone();

	std::cout << "find and draw contours" << std::endl;
	std::vector<std::vector<cv::Point> > contours;
	cv::findContours(maskContours, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);

	//find index in the vector of the contour containing the seed point (most of the time it will be 0)
	int index = -1;

	for (int i = 0; i < contours.size(); i++) {

		int minX = image.rows;
		int minY = image.cols;
		int maxX = 0;
		int maxY = 0;

		for (int j = 0; j < contours[i].size(); j++) {
			if (contours[i].at(j).x < minX) minX = contours[i].at(j).x;
			if (contours[i].at(j).y < minY) minY = contours[i].at(j).y;
			if (contours[i].at(j).x > maxX) maxX = contours[i].at(j).x;
			if (contours[i].at(j).y > maxY) maxY = contours[i].at(j).y;
		}
		if ((seed.x >= minX) && (seed.x <= maxX) && (seed.y >= minY) && (seed.y <= maxY)) {
			index = i;
			std::cout << "index: " << index << std::endl;
		}
		
	}


	cv::drawContours(maskContours, contours, index, CV_RGB(255, 255, 255), CV_FILLED);

	//cv::imwrite("maskContours.png", maskContours);
	
	cv::cvtColor(maskContours, maskContours, CV_GRAY2BGR);
	
	colorchecker = image.clone();

	for (int i = 0; i < maskContours.rows; i++) {
		for (int j = 0; j < maskContours.cols; j++) {

			if (maskContours.at<cv::Vec3b>(i, j) != color_white) 
				colorchecker.at<cv::Vec3b>(i, j) = color_black;

		}
	}

	std::cout << "result colorchecker" << std::endl;


}

