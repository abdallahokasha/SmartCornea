package edu.fci.smartcornea.util;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

public abstract class Constant {

    public static final String SMARTCORNEA_SERVER_URL = "http://192.168.1.3:3000/";
    public static final String USER_ID = "user_id";
    public static final String DOMAIN_ID = "domain_id";
    // Face Detector
    public static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final Scalar FACE_TEXT_COLOR = new Scalar(0, 0, 255, 255);
    // Face Recognizers
    public static final Integer LBPH_RADIUS = 3;
    public static final Integer LBPH_NEIGHBORS = 8;
    public static final Integer LBPH_GRID_X = 8;
    public static final Integer LBPH_GRID_Y = 8;
    public static final Double LBPH_THRESHOLD = 150.0;
    public static final Size LBPH_FaceSize = new Size(92, 112);
}
