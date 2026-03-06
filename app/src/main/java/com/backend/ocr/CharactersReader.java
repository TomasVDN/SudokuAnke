package com.backend.ocr;

import android.graphics.Rect;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class CharactersReader {
    InputImage image;

    public void setImage(InputImage image) {
        this.image = image;
    }

    public Task<Text> readText() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image)
            .addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    //Log.d("Recognizer success", text.getText());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.d("Recognizer exception", e.getCause() + e.getMessage());
                }
            })
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    //Log.d("Recognizer canceled", "");
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Text>() {
                @Override
                public void onComplete(@NonNull Task<Text> task) {
                    //Log.d("Recognizer complete", task.toString());
                }
            });
        return result;
    }

    public BoundedCharacters handleText(Text result) {
        BoundedCharacters boundedCharacters = new BoundedCharacters();
        for (Text.TextBlock block : result.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements()) {
                    if (stringIsInt(element.getText())) {
                        int value = stringToInt(element.getText());
                        Rect elementBoundingBox = element.getBoundingBox();
                        boundedCharacters.characters.add(new BoundedCharacter(value, elementBoundingBox));
                        boundedCharacters.boundingBox.union(elementBoundingBox);
                    } else {
                        Log.d("not an int", element.getText());
                    }
                }
            }
        }

        String rep = "";
        for (BoundedCharacter boundedCharacter : boundedCharacters.characters) {
           rep += boundedCharacter.toString();
        }
        //Log.d("Found elements", rep);
        //Log.d("BoundingBox", characters.boundingBox.flattenToString());

        int width = boundedCharacters.boundingBox.width();
        int height = boundedCharacters.boundingBox.height();

        int xOffset = boundedCharacters.boundingBox.left;
        int yOffset = boundedCharacters.boundingBox.top;

        //Log.d("Rect info", "width: " + width + " - height: "+height+" - xOffset: " + xOffset + " - yOffset: "+yOffset);

        return boundedCharacters;
    }

    private int stringToInt(String string) {
        int value;
        try {
            value = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }

    private boolean stringIsInt(String string) {
        boolean success = true;
        try {
            int value = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            success = false;
        }
        return success;
    }
}
