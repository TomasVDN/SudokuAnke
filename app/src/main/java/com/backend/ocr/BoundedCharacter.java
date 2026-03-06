package com.backend.ocr;

import android.graphics.Rect;

public class BoundedCharacter {
    int value;
    Rect boundingBox;

    public BoundedCharacter(int value, Rect boundingBox) {
        this.value = value;
        this.boundingBox = boundingBox;
    }
    public String toString() {
        return "" + this.value + " - " + this.boundingBox.flattenToString() + "\n";
    }
}
