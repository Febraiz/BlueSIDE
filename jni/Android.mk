LOCAL_PATH := $(call my-dir)
OPENCV_PATH := C:/OpenCV-android-sdk/sdk/native/jni
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
CERES_PATH := C:/ceres/ceres-solver-1.11.0

include $(CLEAR_VARS)
LOCAL_MODULE    := ceres
LOCAL_SRC_FILES := libceres.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES  := on
include $(OPENCV_PATH)/OpenCV.mk

LOCAL_C_INCLUDES += $(EIGEN_PATH)
LOCAL_C_INCLUDES +=  $(CERES_PATH)/config
LOCAL_C_INCLUDES +=  $(CERES_PATH)/include
LOCAL_C_INCLUDES +=  $(CERES_PATH)/internal/ceres/miniglog

LOCAL_MODULE    := app-jni
LOCAL_CFLAGS += -std=gnu++11
LOCAL_SRC_FILES := app-jni.cpp Utils.cpp Random.cpp Detection.cpp
LOCAL_STATIC_LIBRARIES  :=  ceres
LOCAL_LDLIBS +=  -llog -ldl
include $(BUILD_SHARED_LIBRARY)