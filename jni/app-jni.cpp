#include <string.h>
#include <iostream>
#include <jni.h>
#include <stdio.h>
#include <opencv2/opencv.hpp>
#include <ceres/ceres.h>
#include <glog/logging.h>
#include "Random.hpp"
#include "Utils.hpp"
#include "Detection.hpp"
#include <fstream>

using namespace cv;

const double LDR_MEAN_THRESHOLD = 8;
const int WB_MIN = 30;	//50
const int WB_MAX = 220;	//200

// TODO constantes à mettre en paramètres
const double DEBEVEC_MAX = 1;
const int DEBEVEC_SAMPLES = 140;
const double DEBEVEC_LAMBDA = 20;
const bool DEBEVEC_RANDOM = true;
const double ROBERTSON_MAX = 2;
const int BLUR_SIZE = 20;
const int BLUR_ORDER = 2;
const int POSITIONS_PARTITIONS_SAMPLES = 5;
const int POSITIONS_PARTITIONS_TRIES = 1e6;
const int POSITIONS_RANDOM_SAMPLES = 100;
const int POSITIONS_GRID_K0 = 5;
const int POSITIONS_GRID_INC = 10;
const int PROBLEM_LDR_TRIES = 1e6;


class WbResidualHdr {

private:
    const Vec3d c_;
    const Vec3d l_;

public:
    WbResidualHdr(Vec3d c, Vec3d l) : c_(c), l_(l) {}

    template <typename T> bool operator()(const T * const alpha,
                                          const T * const beta, T * residual) const
    {
        T lp0 = l_[0] * alpha[0] + beta[0];
        T lp1 = T(l_[1]);
        T lp2 = l_[2] * alpha[1] + beta[1];

        T normL = T(sqrt(lp0*lp0 + lp1*lp1 + lp2*lp2));
        T normC = T(sqrt(c_[0]*c_[0] + c_[1]*c_[1] + c_[2]*c_[2]));

        residual[0] = lp0/normL - c_[0]/normC;
        residual[1] = lp2/normL - c_[2]/normC;

        return true;
    }
};


class WbResidualLdr {

private:
    const Vec3d l1_;
    const Vec3d l2_;

public:
    WbResidualLdr(Vec3d l1, Vec3d l2) : l1_(l1), l2_(l2) {}

    template <typename T> bool operator()(const T * const alpha,
                                          const T * const beta, T * residual) const
    {
        T l1p0 = l1_[0] * alpha[0] + beta[0];
        T l1p1 = T(l1_[1]);
        T l1p2 = l1_[2] * alpha[1] + beta[1];

        T l2p0 = l2_[0] * alpha[0] + beta[0];
        T l2p1 = T(l2_[1]);
        T l2p2 = l2_[2] * alpha[1] + beta[1];

        T normL1p = T(sqrt(l1p0*l1p0 + l1p1*l1p1 + l1p2*l1p2));
        T normL2p = T(sqrt(l2p0*l2p0 + l2p1*l2p1 + l2p2*l2p2));

        residual[0] = l1p0/normL1p - l2_[0]/normL2p;
        residual[1] = l1p2/normL1p - l2_[2]/normL2p;

        return true;
    }
};


std::vector<Vec2i> computePositionsGrid(int rows, int cols, int k0, int inc)
{
    std::vector<Vec2i> positions;

    for (int i=k0; i<rows; i+=inc)
        for (int j=k0; j<cols; j+=inc)
            positions.push_back({i,j});

    return positions;
}


std::vector<Vec2i> computePositionsRandom(int rows, int cols, Random & rng, int nbSamples)
{
    std::vector<Vec2i> positions;

    for (int k=0; k<nbSamples; k++)
        positions.push_back({rng(rows), rng(cols)});

    return positions;
}


std::vector<Vec2i> computePositionsPartitions(const Mat & hdrImage, Random & rng, int nbSamplesPerPartition)
{
    std::vector<Vec2i> positions;

    // compute stats from images
    Scalar meanScalar = mean(hdrImage);
    double meanVal = (meanScalar[0]+meanScalar[1]+meanScalar[2])/3.0;
    double minVal, maxVal;
    minMaxIdx(hdrImage, &minVal, &maxVal);
    std::array<double,4> maxBounds { minVal+0.5*(meanVal-minVal), meanVal, meanVal+0.5*(maxVal-meanVal), maxVal+1.0};

    // display stats
    std::cout << "  - balanceCrf minVal: " << minVal << std::endl;
    std::cout << "  - balanceCrf maxVal: " << maxVal << std::endl;
    std::cout << "  - balanceCrf meanVal: " << meanVal << std::endl;
    std::cout << "  - balanceCrf maxBounds:";

    for (double n : maxBounds)
        std::cout << " " << n;
    std::cout << std::endl;

    // compute positionst
    std::array<int,4> nbSamples = {0, 0, 0, 0};
    int totalSamples = 0;
    int totalTries = 0;

    while (totalSamples < 4*nbSamplesPerPartition)
    {
        Vec2i p {rng(hdrImage.rows), rng(hdrImage.cols)};
        Vec3d L = hdrImage.at<Vec3f>(p);
        double m = (L[0]+L[1]+L[2])/3.0;
        int k=0;

        while (m > maxBounds[k])
            k++;

        if (nbSamples[k] < nbSamplesPerPartition)
        {
            positions.push_back(p);
            nbSamples[k]++;
            totalSamples++;
            //std::cout << p << " " << m << " " << k << std::endl;
        }

        totalTries++;

        if (totalTries > POSITIONS_PARTITIONS_TRIES)
        {
            std::cout << "  - balanceCrf nbSamples:";

            for (int n : nbSamples)
                std::cout << " " << n;
            std::cout << std::endl;
            std::cerr << "error: too much tries (computePositionsPartitions)"
            << std::endl;
            exit(-1);
        }
    }

    return positions;
}


void initWbProblemHdr(ceres::Problem & problem, double alpha[2], double beta[2], const std::vector<Mat> & imagesC, const Mat & hdrImage, Random & rng)
{
    // TODO tester les selections de points pour la WB
    std::vector<Vec2i> positions = computePositionsPartitions(hdrImage, rng, POSITIONS_PARTITIONS_SAMPLES);
    //std::vector<Vec2i> positions = computePositionsRandom(hdrImage.rows, hdrImage.cols, rng, POSITIONS_RANDOM_SAMPLES);
    //std::vector<Vec2i> positions = computePositionsGrid(hdrImage.rows, hdrImage.cols, POSITIONS_GRID_K0, POSITIONS_GRID_INC);

    for (const Vec2i & p : positions)
    {
        Vec3d L = hdrImage.at<Vec3f>(p);
        std::cout << "  - balanceCrf p: " << p << "; L: " << L << "; C:";

        for (const Mat & imageC : imagesC)
        {
            Vec3d C = imageC.at<Vec3b>(p);

            if (C[0]>WB_MIN and C[1]>WB_MIN and C[2]>WB_MIN and C[0]<WB_MAX and C[1]<WB_MAX and C[2]<WB_MAX)
            {
                auto residual = new WbResidualHdr(C, L);
                auto cost = new ceres::AutoDiffCostFunction<WbResidualHdr,2,2,2>(residual);

                // TODO reglage de la detection des points aberrants
                auto loss = new ceres::CauchyLoss(0.5);
                problem.AddResidualBlock(cost, loss, alpha, beta);
                std::cout << ' ' << C;
            }
        }

        std::cout << std::endl;
    }
}


void initWbProblemLdr(ceres::Problem & problem, double alpha[2], double beta[2], const std::vector<Mat> & imagesC, const std::vector<float> & times, const Mat & crf, Random & rng)
{
    assert(imagesC.size() == times.size());
    assert(imagesC.size() > 1);
    int rows = imagesC.front().rows;
    int cols = imagesC.front().cols;
    int nbImages = imagesC.size();

    // inverse crf for each ldr images
    std::vector<Mat> imagesL;

    for (int i=0; i<nbImages; i++)
    {
        Mat imageL;
        LUT(imagesC[i], crf, imageL);
        imagesL.push_back(imageL);
    }

#ifdef DEBUG
    printImageStats(imagesC.back(), "initWbProblemLdr, imagesC");
    printImageStats(imagesL.back(), "initWbProblemLdr, imagesL");
#endif

    // compute problem (add residual blocks)
    // TODO nbSamplesMax in parameter ?
    int nbSamplesMax = 40;
    int nbSamples = 0;
    int totalTries = 0;

    while (nbSamples < nbSamplesMax)
    {
        // choose a random point
        Vec2i p {rng(rows), rng(cols)};

        // choose 2 images randomly
        int ind1 = rng(nbImages);
        int ind2 = ind1;
        while (ind2 == ind1)
            ind2 = rng(nbImages);

        // TODO verifie/forcer la repartition des points
        // TODO implementer un choix des points/images qui converge plus vite
        // add residual block (if in [50, 200])
        Vec3d C1 = imagesC[ind1].at<Vec3b>(p);
        Vec3d C2 = imagesC[ind2].at<Vec3b>(p);
        if (C1[0]>WB_MIN and C1[1]>WB_MIN and C1[2]>WB_MIN
            and C1[0]<WB_MAX and C1[1]<WB_MAX and C1[2]<WB_MAX
            and C2[0]>WB_MIN and C2[1]>WB_MIN and C2[2]>WB_MIN
            and C2[0]<WB_MAX and C2[1]<WB_MAX and C2[2]<WB_MAX)
        {
            Vec3d L1 = imagesL[ind1].at<Vec3f>(p);
            Vec3d L2 = imagesL[ind2].at<Vec3f>(p);
            auto residual = new WbResidualLdr(L1, L2);
            auto cost = new ceres::AutoDiffCostFunction<WbResidualLdr,2,2,2>(residual);
            // TODO reglage de la detection des points aberrants
            auto loss = new ceres::CauchyLoss(0.5);
            problem.AddResidualBlock(cost, loss, alpha, beta);
            nbSamples++;
            // TODO TOTO
            std::cout << "  - balanceCrf: p=" << p
            << " C1=" << C1 << " C2=" << C2
            << " L1=" << L1 << " L2=" << L2
            << " i1=" << ind1 << " i2=" << ind2 << std::endl;
        }

        totalTries++;

        if (totalTries > PROBLEM_LDR_TRIES)
        {
            std::cout << "  - balanceCrf nbSamples: " << nbSamples << std::endl;
            std::cerr << "error: too much tries (initWbProblemLdr)" << std::endl;
            exit(-1);
        }

    }

    std::cout << "  - balanceCrf totalTries: " << totalTries << std::endl;
}


void balanceCrf(Mat & crf, const std::vector<Mat> & images, const std::vector<float> & times, const Mat & hdrImage, Random & rng)
{
    assert(hdrImage.depth() == CV_32F);
    assert(not images.empty());
    assert(images.front().depth() == CV_8U);
    assert(hdrImage.rows == images.front().rows);
    assert(hdrImage.cols == images.front().cols);

    double alpha[2] = {1.0, 1.0};
    double beta[2] = {0.0, 0.0};

    // choose positions for estimating WB
    // and construct problem (residual blocks)
    // TODO WB selon RGB ou Lab/Luv/HSV ?
    ceres::Problem problem;
    //initWbProblemHdr(problem, alpha, beta, images, hdrImage, rng);
    initWbProblemLdr(problem, alpha, beta, images, times, crf, rng);
    std::cout << "  - balanceCrf nbBlocks: " << problem.NumResidualBlocks() << std::endl;

    // solve problem
#ifdef DEBUG
    ceres::Solver::Options options;
    options.linear_solver_type = ceres::DENSE_NORMAL_CHOLESKY;
    options.minimizer_progress_to_stdout = true;
    ceres::Solver::Summary summary;
    ceres::Solve(options, &problem, &summary);
    std::cout << "  - " << summary.FullReport() << std::endl;
#else
    ceres::Solver::Options options;
    options.linear_solver_type = ceres::DENSE_NORMAL_CHOLESKY;
    ceres::Solver::Summary summary;
    ceres::Solve(options, &problem, &summary);
    std::cout << "  - " << summary.BriefReport() << std::endl;
#endif

    // display found solution
    std::cout << "  - alpha0 : " << alpha[0] << std::endl;
    std::cout << "  - alpha1 : " << alpha[1] << std::endl;
    std::cout << "  - beta0 : " << beta[0] << std::endl;
    std::cout << "  - beta1 : " << beta[1] << std::endl;

    // update CRF
    std::vector<Mat> splittedCrfs(3);
    split(crf, splittedCrfs);
    splittedCrfs[0] *= alpha[0];
    splittedCrfs[0] += beta[0];
    splittedCrfs[2] *= alpha[1];
    splittedCrfs[2] += beta[1];
    merge(splittedCrfs, crf);
}

extern "C" {

    JNIEXPORT jint JNICALL Java_com_example_isit_1mp3c_projet_camera_CameraActivity_imageHDR(JNIEnv *env, jobject obj, jstring dirIMG, jobjectArray  images, jfloatArray  times) {

        const char *dir = env->GetStringUTFChars(dirIMG,0);

        jsize length = env->GetArrayLength(times);
        jfloat *exposureTimes = env->GetFloatArrayElements(times,0);

        google::InitGoogleLogging(0);

        std::vector<Mat> list_img;
        std::vector<float>time;

        for(int i=0;i<length;i++) {

            jstring name = (jstring) env->GetObjectArrayElement(images, i);
            const char *nameImg = env->GetStringUTFChars(name, 0);
            cv::Mat image = cv::imread(nameImg);

            cv::Scalar meanScalar = cv::mean(image);
            std::cout << " mean=" << meanScalar << std::endl;
            if (sum(meanScalar)[0] > 3 * LDR_MEAN_THRESHOLD) {
                list_img.push_back(image);
                time.push_back(exposureTimes[i]);
                std::cout << " -> added" << std::endl;
            }
            else {
                std::cout << " -> skipped" << std::endl;


            }
        }

        std::vector<Mat> img = list_img;
        cv::Ptr<cv::AlignMTB> align = cv::createAlignMTB(5,4,false);
        align->process(list_img, img);

        Random rng;

        /****************************ROBERTSON********************************/

        //CRF Robertson
        cv::Mat crfRobertson;
        cv::Ptr<cv::CalibrateRobertson> calibrateRobertson = cv::createCalibrateRobertson();
        calibrateRobertson->process(img, crfRobertson, time);
        saveCrf(crfRobertson,"/crfRobertson.csv",dir);

        //HDR Robertson
        cv::Mat hdrRobertson;
        cv::Ptr<cv::MergeRobertson> mergeRobertson = cv::createMergeRobertson();
        mergeRobertson->process(img, hdrRobertson, time, crfRobertson);
        saveHdr(hdrRobertson,"/hdrRobertson.exr",ROBERTSON_MAX,dir);

        /*
        //Tonemap Robertson
        cv::Mat ldrRobertson;
        cv::Ptr<cv::Tonemap> tonemapRobertson = cv::createTonemap(2.2f);
        tonemapRobertson->process(hdrRobertson, ldrRobertson);
         */

        //Smooth CRF Robertson
        cv::Mat crfRobertsonSmooth = crfRobertson.clone();
        smoothCrf(crfRobertsonSmooth, BLUR_ORDER,BLUR_SIZE);
        saveCrf(crfRobertsonSmooth,"/crfRobertsonSmooth.csv",dir);

        //HDR Robertson Smooth
        cv::Mat hdrRobertsonSmooth;
        mergeRobertson->process(img, hdrRobertsonSmooth, time, crfRobertsonSmooth);
        saveHdr(hdrRobertsonSmooth,"/hdrRobertsonSmooth.exr",ROBERTSON_MAX,dir);

        //Smooth CRF Robertson WB
        cv::Mat crfRobertsonSmoothWB = crfRobertsonSmooth.clone();
        balanceCrf(crfRobertsonSmoothWB, img, time, hdrRobertsonSmooth, rng);
        saveCrf(crfRobertsonSmoothWB,"/crfRobertsonSmoothWB.csv",dir);

        //HDR Robertson Smooth WB
        cv::Mat hdrRobertsonSmoothWB;
        mergeRobertson->process(img, hdrRobertsonSmoothWB, time, crfRobertsonSmoothWB);
        saveHdr(hdrRobertsonSmoothWB,"/hdrRobertsonSmoothWB.exr",ROBERTSON_MAX,dir);

        //CRF Robertson WB
        cv::Mat crfRobertsonWB = crfRobertson.clone();
        balanceCrf(crfRobertsonWB,img,time,hdrRobertson,rng);
        saveCrf(crfRobertsonWB,"/crfRobertsonWB.csv",dir);

        //HDR Robertson WB
        cv::Mat hdrRobertsonWB;
        mergeRobertson->process(img, hdrRobertsonWB, time, crfRobertsonWB);
        saveHdr(hdrRobertsonWB,"/hdrRobertsonWB.exr",ROBERTSON_MAX,dir);


        /****************************DEBEVEC********************************/

        cv::Mat crfDebevec;
        Ptr<CalibrateDebevec> calibrateDebevec = createCalibrateDebevec(DEBEVEC_SAMPLES,DEBEVEC_LAMBDA,DEBEVEC_RANDOM);
        calibrateDebevec->process(img, crfDebevec, time);
        saveCrf(crfDebevec,"/crfDebevec.csv",dir);

        cv::Mat hdrDebevec;
        Ptr<MergeDebevec> mergeDebevec = createMergeDebevec();
        mergeDebevec->process(img, hdrDebevec, (std::vector<float>) time, crfDebevec);
        saveHdr(hdrDebevec,"/hdrDebevec.exr", DEBEVEC_MAX,dir);

        /*
        cv::Mat ldrDebevec;
        cv::Ptr<cv::Tonemap> tonemapDebevec = cv::createTonemap(2.2f);
        tonemapDebevec->process(hdrDebevec, ldrDebevec);*/

        /*
        cv::Mat crfDebevecWB = crfDebevec.clone();
        balanceCrf(crfDebevecWB, img, time, hdrDebevec, rng);
        saveCrf(crfDebevecWB,"/crfDebevecWB.csv",dir);

        cv::Mat hdrDebevecWB;
        mergeDebevec->process(img, hdrDebevecWB, (std::vector<float>) time, crfDebevecWB);
        saveHdr( hdrDebevecWB, "/hdrDebevecWB.exr", DEBEVEC_MAX,dir);*/

        env->ReleaseStringUTFChars(dirIMG, dir);
        env->ReleaseFloatArrayElements(times,exposureTimes,0);

        return 0;
    }

    JNIEXPORT jint JNICALL Java_com_example_isit_1mp3c_projet_camera_CameraActivity_eyeDetection(JNIEnv *env, jobject obj, jstring nameIMG, jstring dirIMG, jint x, jint y) {

        const char *name_image = env->GetStringUTFChars(nameIMG,0);
        const char *dir = env->GetStringUTFChars(dirIMG,0);

        char name[150];
        strcpy(name,dir);
        strcat(name,name_image);

        cv::Mat img = cv::imread(name);
        cv::Point pt = cv::Point(x,y);
        cv::Mat result;

        if (img.empty()) {
            return -1;
        }

        detectionSclera(img,pt,result);

        char nameSclera[150];
        strcpy(nameSclera,dir);
        strcat(nameSclera,"/sclera.png");
        cv::imwrite(nameSclera, result);

        env->ReleaseStringUTFChars(nameIMG, name_image);
        env->ReleaseStringUTFChars(dirIMG, dir);
        return 0;
    }

    JNIEXPORT jint JNICALL Java_com_example_isit_1mp3c_projet_camera_CameraActivity_analysisLDR(JNIEnv *env, jobject obj, jstring name1, jint i1, jint j1,
                                                                                                jstring name2, jint i2, jint j2, jint size, jstring dirIMG) {

        const char *name_sclera = env->GetStringUTFChars(name1,0);
        const char *name_ref = env->GetStringUTFChars(name2,0);
        const char *dir = env->GetStringUTFChars(dirIMG,0);

        //////////////////////////////////////////////////////////////////////
        // process data
        //////////////////////////////////////////////////////////////////////

        // open LDR images

        char nameS[150];
        strcpy(nameS,dir);
        strcat(nameS,name_sclera);

        cv::Mat sclera = imread(nameS);
        if (sclera.depth() != CV_8U) return -1;

        char nameR[150];
        strcpy(nameR,dir);
        strcat(nameR,name_ref);

        cv::Mat ref = imread(nameR);
        if (ref.depth() != CV_8U) return -1;

        int size_window = size;
        int iSclera = i1;
        int jSclera = j1;
        int iRef = i2;
        int jRef = j2;

        // compute CRF-corrected image
        Rect rectSclera(jSclera, iSclera, size_window, size_window);
        Mat matScleraLDR = sclera(rectSclera);
        Scalar scleraLDR = mean(matScleraLDR);

        Rect rectRef(jRef, iRef, size_window, size_window);
        Mat matRefLDR = ref(rectRef);
        Scalar refLDR = mean(matRefLDR);

        //////////////////////////////////////////////////////////////////////
        // analyze color
        //////////////////////////////////////////////////////////////////////

        // sclère
        // bgr
        Mat matScleraBGR = sclera(rectSclera);
        Scalar colorSclera = mean(matScleraBGR);
        Scalar colorScleraBgrBar;
        double colorScleraBgrSum = sum(colorSclera)[0];
        divide(colorSclera,Scalar::all(colorScleraBgrSum),colorScleraBgrBar);


        // reflet
        // bgr
        Mat matRefBGR = ref(rectRef);
        Scalar colorRef = mean(matRefBGR);
        Scalar colorRefBgrBar;
        double colorRefBgrSum = sum(colorRef)[0];
        divide(colorRef,Scalar::all(colorRefBgrSum),colorRefBgrBar);

        // ratio (couleur réelle de la sclère, sans l'illuminant)
        // bgr
        Scalar ratioBGR;
        divide(colorSclera, colorRef, ratioBGR);
        Scalar ratioBgrBar;
        double ratioBgrSum = sum(ratioBGR)[0];
        divide(ratioBGR, Scalar::all(ratioBgrSum), ratioBgrBar);


        //////////////////////////////////////////////////////////////////////
        // display results
        //////////////////////////////////////////////////////////////////////

        // compute image to display considered rect
        char debugFilename[150];
        strcpy(debugFilename,dir);
        strcat(debugFilename,"/debug.png");

        Scalar color = Scalar(0,255,0);
        Scalar colorS = Scalar(127, 255, 255);
        Scalar colorR = Scalar(255, 127, 255);

        Mat debugImgSclera = sclera.clone();
        rectangle(debugImgSclera, rectSclera, colorS, 1);
        rectangle(debugImgSclera, rectRef, colorR, 1);
        cv::imwrite(debugFilename, debugImgSclera);

        // display color analysis
        //std::cout << std::setprecision(3);
        std::cout << "sclera : " << std::endl;
        std::cout << "  - bgr = " << colorSclera << std::endl;
        std::cout << "  - avg = " << colorScleraBgrSum/3.0 << std::endl;
        std::cout << "  - bgrBar = " << colorScleraBgrBar << std::endl;
        std::cout << "ref : " << std::endl;
        std::cout << "  - bgr = " << colorRef << std::endl;
        std::cout << "  - avg = " << colorRefBgrSum/3.0 << std::endl;
        std::cout << "  - bgrBar = " << colorRefBgrBar << std::endl;
        std::cout << "ratio : " << std::endl;
        std::cout << "  - bgr = " << ratioBGR << std::endl;
        std::cout << "  - avg = " << ratioBgrSum/3.0 << std::endl;
        std::cout << "  - bgrBar = " << ratioBgrBar << std::endl;
        std::cout << std::endl;

        // validation
        std::cout << "validation: " << std::endl;
        std::cout << "  - scleraLDR = " << scleraLDR << std::endl;
        std::cout << "  - refLDR = " << refLDR << std::endl;

        char resultFile[150];
        strcpy(resultFile,dir);
        strcat(resultFile,"/analysis_ldr_ratio.txt");

        std::fstream fs;
        fs.open (resultFile, std::fstream::in | std::fstream::out | std::fstream::app);
        fs << nameS;
        fs << "\n";
        fs << nameR;
        fs << "\n";
        fs << ratioBgrBar;
        fs << "\n";
        fs.close();

        return 0;

    }


}
