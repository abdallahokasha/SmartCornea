package edu.fci.smartcornea;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

public abstract class Constant {
    // Face Detector
    public static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final Scalar FACE_TEXT_COLOR = new Scalar(0, 0, 255, 255);
    // Face Recognizers
    public static final Integer LBPH_RADIUS = 3;
    public static final Integer LBPH_NEIGHBORS = 8;
    public static final Integer LBPH_GRID_X = 8;
    public static final Integer LBPH_GRID_Y = 8;
    public static final Double LBPH_THRESHOLD = 180.0;
    public static final Size LBPH_FaceSize = new Size(150, 150);
}
