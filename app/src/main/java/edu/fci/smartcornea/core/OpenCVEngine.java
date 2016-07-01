package edu.fci.smartcornea.core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.List;
import java.util.Random;

import edu.fci.smartcornea.util.Constant;

public class OpenCVEngine {

    private static OpenCVEngine openCVEngineInstance = null;

    private long mNativeDetector = 0;
    private FaceRecognizer faceRecognizer = null;

    private OpenCVEngine() {
    }

    public void init(String cascadeName, int minFaceSize,
                     int radius, int neighbors, int grid_x, int grid_y, double threshold) {
        mNativeDetector = nativeCreateDetector(cascadeName, minFaceSize);
        faceRecognizer = Face.createLBPHFaceRecognizer(radius, neighbors, grid_x, grid_y, threshold);
    }

    public static OpenCVEngine getInstance() {
        if(openCVEngineInstance == null) {
            openCVEngineInstance = new OpenCVEngine();
        }
        return openCVEngineInstance;
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
        faceRecognizer.load(filename);
    }

    public void saveRecognizer(String filename) {
        faceRecognizer.save(filename);
    }

    public int predict(Mat faceGray) {
        Mat temp = new Mat(Constant.LBPH_FaceSize, faceGray.type());
        Imgproc.resize(faceGray, temp, Constant.LBPH_FaceSize);
        faceGray = temp;
        int []label = new int[1];
        double []confidence = new double[1];
        faceRecognizer.predict(faceGray, label, confidence);
        return label[0];
    }

    public String getLabelInfo(int id) {
        return faceRecognizer.getLabelInfo(id);
    }

    public void setLabelInfo(int id, String info) {
        faceRecognizer.setLabelInfo(id, info);
    }

    public int getRandomLabel() {
        Random r = new Random();
        int id;
        do {
            id = r.nextInt();
        }while(!getLabelInfo(id).isEmpty());
        return id;
    }

    @Override
    protected void finalize() {
        releaseDetector();
    }

    // Detector
    private static native long nativeCreateDetector(String cascadeName, int minFaceSize);
    private static native void nativeDestroyDetector(long thiz);
    private static native void nativeStartDetector(long thiz);
    private static native void nativeStopDetector(long thiz);
    private static native void nativeSetDetectorFaceSize(long thiz, int size);
    private static native void nativeDetect(long thiz, long inputImage, long faces);
}
