package com.backend.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RunWith(AndroidJUnit4.class)
public class TranslationApplierTest {

    public static void saveBitmap(Bitmap bitmap, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTranslateImage_appliesTransformation() {
        // Arrange: create a simple 2×2 bitmap with distinct colors
        Bitmap input = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        input.setPixel(0, 0, Color.RED);
        input.setPixel(1, 0, Color.GREEN);
        input.setPixel(0, 1, Color.BLUE);
        input.setPixel(1, 1, Color.YELLOW);

        // Define source points (identity mapping)
        float[] src = new float[]{
                0, 0,
                2, 0,
                0, 2,
                2, 2
        };

        // Apply a translation by modifying the destination points
        float[] dst = new float[]{
                1, 1,
                3, 1,
                1, 3,
                3, 3
        };

        // Spy on getDestinationPoints() by injecting our own matrix
        Bitmap output = TranslationApplier.translateImage(input, src);

        // Assert: output is not null and same size
        Assert.assertNotNull(output);
        Assert.assertEquals(input.getWidth(), output.getWidth());
        Assert.assertEquals(input.getHeight(), output.getHeight());

        // Assert: transformation changed pixel positions
        // After translation, the original top-left pixel should no longer be red
        int outTopLeft = output.getPixel(0, 0);
        Assert.assertNotEquals(Color.RED, outTopLeft);
    }

    @Test
    public void testTranslateImage_returnsBitmapEvenIfMatrixFails() {
        Bitmap input = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);

        // Invalid src array (too small)
        float[] badSrc = new float[]{0, 0};

        Bitmap output = TranslationApplier.translateImage(input, badSrc);

        Assert.assertNotNull(output);
        Assert.assertEquals(10, output.getWidth());
        Assert.assertEquals(10, output.getHeight());
    }

    @Test
    public void testTranslateImage_manualInspection() throws Exception {
        // Load image from test resources
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();

        InputStream inputStream = testContext.getAssets().open("sudoku1.jpg");
        Assert.assertNotNull("Image not found in test/resources!", inputStream);

        Bitmap input = BitmapFactory.decodeStream(inputStream);
        Assert.assertNotNull(input);

        // Example source points (identity)
        float[] src = new float[]{
                0, 0,
                input.getWidth(), 0,
                0, input.getHeight(),
                input.getWidth(), input.getHeight()
        };

        Bitmap output = TranslationApplier.translateImage(input, src);

        // Save output to a file so you can inspect it manually
        File outputFile = new File(
                testContext.getExternalFilesDir(null),
                "translated_output.png"
        );

        System.out.println("Input: " + input.getWidth() + "x" + input.getHeight());
        System.out.println("Output: " + output.getWidth() + "x" + output.getHeight());


        saveBitmap(output, outputFile);

        System.out.println("Saved translated image to: " + outputFile.getAbsolutePath());
    }
}
