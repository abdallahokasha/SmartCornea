LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED

include $(OPENCV_SDK_PATH)/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := OpenCVEngine_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := opencv_engine

include $(BUILD_SHARED_LIBRARY)
