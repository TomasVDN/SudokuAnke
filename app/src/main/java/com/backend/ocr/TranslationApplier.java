package com.backend.ocr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class TranslationApplier {
    static private int size = 450;

    static private float[] getDestinationPoints(int width, int height) {
        return new float[]{0, 0, height, 0, height, width, 0, width};
    }
    static public Bitmap translateImage(Bitmap inputBitmap, float[] dots) {
        Matrix matrix = new Matrix();
        float[] destination = getDestinationPoints(inputBitmap.getWidth(), inputBitmap.getHeight());
        boolean success = matrix.setPolyToPoly(dots, 0, destination, 0, 4);
        if (success) {
            Log.e("TranslationApplier", matrix.toString());
        } else {
            Log.e("TranslationApplier", "Unsuccessful");
        }
        System.out.println("Matrix: " + matrix.toShortString());

        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(inputBitmap, matrix, null);

        return outputBitmap;
    }
}
