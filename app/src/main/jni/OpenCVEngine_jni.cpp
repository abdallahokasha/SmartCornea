#include <OpenCVEngine_jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>
#include "cv.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "Native/OpenCVEngine"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

// START Face Detection

inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat) {
    mat = Mat(v_rect, true);
}

class CascadeDetectorAdapter: public DetectionBasedTracker::IDetector {
public:
    CascadeDetectorAdapter(Ptr<CascadeClassifier> detector): IDetector(), Detector(detector) {
        LOGD("CascadeDetectorAdapter::Detect::Detect");
        CV_Assert(detector);
    }

    void detect(const Mat &Image, std::vector<Rect> &objects) {
        LOGD("CascadeDetectorAdapter::Detect: begin");
        LOGD("CascadeDetectorAdapter::Detect: scaleFactor=%.2f, minNeighbours=%d, minObjSize=(%dx%d), maxObjSize=(%dx%d)", scaleFactor, minNeighbours, minObjSize.width, minObjSize.height, maxObjSize.width, maxObjSize.height);
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
        LOGD("CascadeDetectorAdapter::Detect: end");
    }

    virtual ~CascadeDetectorAdapter() {
        LOGD("CascadeDetectorAdapter::Detect::~Detect");
    }

private:
    CascadeDetectorAdapter();
    Ptr<CascadeClassifier> Detector;
};

struct DetectorAgregator {
    Ptr<CascadeDetectorAdapter> mainDetector;
    Ptr<CascadeDetectorAdapter> trackingDetector;

    Ptr<DetectionBasedTracker> tracker;
    DetectorAgregator(Ptr<CascadeDetectorAdapter>& _mainDetector, Ptr<CascadeDetectorAdapter>& _trackingDetector): mainDetector(_mainDetector), trackingDetector(_trackingDetector) {
        CV_Assert(_mainDetector);
        CV_Assert(_trackingDetector);

        DetectionBasedTracker::Parameters DetectorParams;
        tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
    }
};

JNIEXPORT jlong JNICALL Java_edu_fci_smartcornea_core_OpenCVEngine_nativeCreateDetector (JNIEnv * jenv, jclass, jstring jFileName, jint faceSize) {
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeCreateDetector enter");
    const char* jnamestr = jenv->GetStringUTFChars(jFileName, NULL);
    string stdFileName(jnamestr);
    jlong result = 0;

    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeCreateDetector");

    try {
        Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(makePtr<CascadeClassifier>(stdFileName));
        Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(makePtr<CascadeClassifier>(stdFileName));
        result = (jlong)new DetectorAgregator(mainDetector, trackingDetector);
        if (faceSize > 0) {
            mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }catch(Exception& e) {
        LOGD("nativeCreateDetector caught Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...) {
        LOGD("nativeCreateDetector caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of OpenCVEngine.nativeCreateDetector()");
        return 0;
    }
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeCreateDetector exit");
    return result;
}

JNIEXPORT void JNICALL Java_edu_fci_smartcornea_core_OpenCVEngine_nativeDestroyDetector(JNIEnv * jenv, jclass, jlong thiz) {
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeDestroyDetector");
    try {
        if(thiz != 0) {
            ((DetectorAgregator*)thiz)->tracker->stop();
            delete (DetectorAgregator*)thiz;
        }
    }catch(Exception& e) {
        LOGD("nativeestroyObject caught Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...) {
        LOGD("nativeDestroyDetector caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of OpenCVEngine.nativeDestroyDetector()");
    }
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeDestroyDetector exit");
}

JNIEXPORT void JNICALL Java_edu_fci_smartcornea_core_OpenCVEngine_nativeStartDetector(JNIEnv * jenv, jclass, jlong thiz) {
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeStartDetector");
    try {
        ((DetectorAgregator*)thiz)->tracker->run();
    }catch(Exception& e) {
        LOGD("nativeStartDetector caught Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...) {
        LOGD("nativeStartDetector caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of OpenCVEngine.nativeStartDetector()");
    }
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeStartDetector exit");
}

JNIEXPORT void JNICALL Java_edu_fci_smartcornea_core_OpenCVEngine_nativeStopDetector(JNIEnv * jenv, jclass, jlong thiz) {
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeStopDetector");
    try {
        ((DetectorAgregator*)thiz)->tracker->stop();
    }catch(Exception& e) {
        LOGD("nativeStopDetector caught Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...) {
        LOGD("nativeStopDetector caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of OpenCVEngine.nativeStopDetector()");
    }
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeStopDetector exit");
}

JNIEXPORT void JNICALL Java_edu_fci_smartcornea_core_OpenCVEngine_nativeSetDetectorFaceSize(JNIEnv * jenv, jclass, jlong thiz, jint faceSize) {
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeSetDetectorFaceSize -- BEGIN");
    try {
        if (faceSize > 0) {
            ((DetectorAgregator*)thiz)->mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //((DetectorAgregator*)thiz)->trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }catch(Exception& e) {
        LOGD("nativeSetDetectorFaceSize caught Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...) {
        LOGD("nativeSetDetectorFaceSize caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of OpenCVEngine.nativeSetDetectorFaceSize()");
    }
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeSetDetectorFaceSize -- END");
}


JNIEXPORT void JNICALL Java_edu_fci_smartcornea_core_OpenCVEngine_nativeDetect(JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces) {
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeDetect");
    try {
        vector<Rect> RectFaces;
        ((DetectorAgregator*)thiz)->tracker->process(*((Mat*)imageGray));
        ((DetectorAgregator*)thiz)->tracker->getObjects(RectFaces);
        *((Mat*)faces) = Mat(RectFaces, true);
    }catch(Exception& e) {
        LOGD("nativeDetect caught Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...) {
        LOGD("nativeDetect caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code OpenCVEngine.nativeDetect()");
    }
    LOGD("Java_edu_fci_smartcornea_core_OpenCVEngine_nativeDetect END");
}

// END Face Detection