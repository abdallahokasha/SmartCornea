package edu.fci.smartcornea;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.List;

public class OpenCVEngine {

    private long mNativeDetector = 0;
    private FaceRecognizer faceRecognizer = null;

    public OpenCVEngine(String cascadeName, int minFaceSize,
                        int radius, int neighbors, int grid_x, int grid_y, double threshold) {
        mNativeDetector = nativeCreateDetector(cascadeName, minFaceSize);
        faceRecognizer = Face.createLBPHFaceRecognizer(radius, neighbors, grid_x, grid_y, threshold);
    }

    // Detector
    public void startDetector() {
        nativeStartDetector(mNativeDetector);
    }

    public void stopDetector() {
        nativeStopDetector(mNativeDetector);
    }

    public void setDetectorMinFaceSize(int size) {
        nativeSetDetectorFaceSize(mNativeDetector, size);
    }

    public void detect(Mat imageGray, MatOfRect faces) {
        nativeDetect(mNativeDetector, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
    }

    public void releaseDetector() {
        nativeDestroyDetector(mNativeDetector);
        mNativeDetector = 0;
    }

    // Recognizer

    public void trainRecognizer(List<Mat> faces, List<Integer> labels) {
        for(int i = 0; i < faces.size(); ++i) {
            Mat temp = new Mat(Constant.LBPH_FaceSize, faces.get(i).type());
            Imgproc.resize(faces.get(i), temp, Constant.LBPH_FaceSize);
            faces.set(i, temp);
        }
        Mat labels_mat = Converters.vector_int_to_Mat(labels);
        faceRecognizer.train(faces, labels_mat);
    }

    public void updateRecognizer(List<Mat> faces, List<Integer> labels) {
        for(int i = 0; i < faces.size(); ++i) {
            Mat temp = new Mat(Constant.LBPH_FaceSize, faces.get(i).type());
            Imgproc.resize(faces.get(i), temp, Constant.LBPH_FaceSize);
            faces.set(i, temp);
        }
        Mat labels_mat = Converters.vector_int_to_Mat(labels);
        faceRecognizer.update(faces, labels_mat);
    }

    public void loadRecognizer(String filename) {
    }

    public void saveRecognizer(String filename) {
    }

    public int predict(Mat faceGray) {
        int []label = new int[1];
        double []confidence = new double[1];
        faceRecognizer.predict(faceGray, label, confidence);
        return label[0];
    }

    @Override
    protected void finalize() {
        nativeDestroyDetector(mNativeDetector);
    }

    // Detector
    private static native long nativeCreateDetector(String cascadeName, int minFaceSize);
    private static native void nativeDestroyDetector(long thiz);
    private static native void nativeStartDetector(long thiz);
    private static native void nativeStopDetector(long thiz);
    private static native void nativeSetDetectorFaceSize(long thiz, int size);
    private static native void nativeDetect(long thiz, long inputImage, long faces);
}
