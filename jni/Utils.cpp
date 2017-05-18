#include "Utils.hpp"
#include <fstream>
#include <iostream>
#include <string>

using namespace std;

const int PRINT_CSV_NB_LINES = 3;

void printImageStats(const cv::Mat & img, const string & name)
{
    cout << "name: " << name << endl;
    cout << "cols: " << img.cols << endl;
    cout << "rows: " << img.rows << endl;
    cout << "channels: " << img.channels() << endl;
    cout << "depth: ";
    switch (img.depth())
    {
        case CV_8U: cout << "CV_8U" << endl; break;
        case CV_8S: cout << "CV_8S" << endl; break;
        case CV_16U: cout << "CV_16U" << endl; break;
        case CV_16S: cout << "CV_16S" << endl; break;
        case CV_32S: cout << "CV_32S" << endl; break;
        case CV_32F: cout << "CV_32F" << endl; break;
        case CV_64F: cout << "CV_64F" << endl; break;
        default: cout << "unknown format" << endl;
    }
    double minVal, maxVal; 
    minMaxIdx(img, &minVal, &maxVal);
    cout << "min: " << minVal << endl;
    cout << "max: " << maxVal << endl;
    cv::Scalar meanScalar = cv::mean(img);
    cout << "mean: " << meanScalar << endl;
    cout << endl;
}

void printInfoColorSpaces()
{
    cout << "rgb : 0 < r < 1; 0 < g < 1; 0 < b < 1" << endl;
    cout << "lab : 0 < l < 100; -127 < a < 127; -127 < b < 127" << endl;
    cout << "luv : 0 < l < 100; -134 < u < 220; -134 < v < 220" << endl;
    cout << "hsv : 0 < h < 360; 0 < s < 1; 0 < v < 1" << endl;
}

void smoothCrf(cv::Mat & crf, int blurOrder, int blurSize)
{
    for (int k=0; k<blurOrder; k++) 
        cv::GaussianBlur(crf, crf, cv::Size(0, 0), blurSize); 
}

void logSmoothCrf(cv::Mat & crf, int blurOrder, int blurSize)
{
    cv::log(crf, crf);
    smoothCrf(crf, blurOrder, blurSize);
    cv::exp(crf, crf);
}

void saveCrf(const cv::Mat & crf, const string & filename,const string & dir)
{
    std::string file = dir + filename;
    ofstream ofs(file);
    ofs << cv::format(crf, cv::Formatter::FMT_CSV);
    cout << "  - " << filename << endl;
}

/*
cv::Mat readCSV(const string & filename)
{

    // read csv
    cv::Mat data;
    ifstream ifs(filename);
    string line;
    while (getline(ifs, line))
    {
        vector<float> vals;
        stringstream ifsline(line);
        string val;
        while (getline(ifsline, val, ','))
            vals.push_back(stof(val));
        cv::Mat mline(vals, true);
        cv::transpose(mline, mline);
        data.push_back(mline);
    }
    // display info
    printImageStats(data, filename);
    // display first and last lines
    for (int i=0; i<PRINT_CSV_NB_LINES; i++)
    {
        cout << "  ";
        int lastJ = data.cols - 1;
        for (int j=0; j<lastJ; j++)
            cout << data.at<float>(i, j) << ", ";
        cout << data.at<float>(i, lastJ);
        cout << endl;
    }
    cout << "  ..." << endl;
    for (int i=data.rows-PRINT_CSV_NB_LINES; i<data.rows; i++)
    {
        cout << "  ";
        int lastJ = data.cols - 1;
        for (int j=0; j<lastJ; j++)
            cout << data.at<float>(i, j) << ", ";
        cout << data.at<float>(i, lastJ);
        cout << endl;
    }
    cout << endl;
    return data;
}

cv::Mat loadCrf(const string & filename,const string & dir)
{
    std::string file = dir + filename;

    // load csv file
    cv::Mat crf = readCSV(file);
    // reshape to 256x1x3
    cv::Mat crfLut(crf.rows, 1, CV_32FC3);
    for (int i=0; i<crfLut.rows; i++)
        crfLut.at<cv::Vec3f>(i) = crf.at<cv::Vec3f>(i);
    return crfLut;
}
*/
void saveHdr(const cv::Mat & img, const string & filename, int normMax, const string & dir)
{
    std::string file = dir + filename;

    cv::Mat normImg = img.clone();
    cv::normalize(normImg, normImg, 0, normMax, cv::NORM_MINMAX);
    cv::imwrite(file, normImg);
    cout << "  - " << filename << endl;
}

cv::Mat loadHdr(const string & filename, const string & dir)
{
    std::string file = dir + filename;

    // open image
    cv::Mat imageFromFile = cv::imread(file,
            cv::IMREAD_ANYDEPTH | cv::IMREAD_ANYCOLOR);
    if (not imageFromFile.data)
    {
        std::cout << "error: unable to open " << filename << std::endl;
        exit(-1);
    }
    printImageStats(imageFromFile, filename);
    // convert to CV_32F and normalize
    cv::Mat imageToAnalyze; 
    switch (imageFromFile.depth())
    {
        case CV_8U:
            imageFromFile.convertTo(imageToAnalyze, CV_32F);
            imageToAnalyze *= 1.0/255.0;
            break;
        case CV_32F:
            cv::normalize(imageFromFile, imageToAnalyze, 0, 1, 
                    cv::NORM_MINMAX, CV_32F);
            break;
        default:
            std::cout << "image format not supported" << std::endl;
            exit(-1);
    }
    printImageStats(imageToAnalyze, filename+" (HDR)");
    return imageToAnalyze;
}

cv::Mat loadHdrQuiet(const string & filename, const string & dir)
{
    std::string file = dir + filename;

    // open image
    cv::Mat imageFromFile = cv::imread(file,
            cv::IMREAD_ANYDEPTH | cv::IMREAD_ANYCOLOR);
    if (not imageFromFile.data)
    {
        std::cout << "error: unable to open " << filename << std::endl;
        exit(-1);
    }
    // convert to CV_32F and normalize
    cv::Mat imageToAnalyze; 
    switch (imageFromFile.depth())
    {
        case CV_8U:
            imageFromFile.convertTo(imageToAnalyze, CV_32F);
            imageToAnalyze *= 1.0/255.0;
            break;
        case CV_32F:
            cv::normalize(imageFromFile, imageToAnalyze, 0, 1, 
                    cv::NORM_MINMAX, CV_32F);
            break;
        default:
            std::cout << "image format not supported" << std::endl;
            exit(-1);
    }
    return imageToAnalyze;
}
