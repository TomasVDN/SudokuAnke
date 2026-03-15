package com.backend.ocr;

import android.util.Pair;

public interface Callback {
    void onComplete(Pair<int[][], Integer> sudoku);
}
