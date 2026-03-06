package com.backend.ocr;

import android.graphics.Rect;

public class SudokuReaderUtil {
    public static int[] indexForBoundingBox(Rect totalBoundingBox, Rect characterBoundingBox) {
        int width = totalBoundingBox.width();
        int height = totalBoundingBox.height();
        if (width <= 0 || height <= 0){
            return new int[]{-1, -1};
        }

        int xOffset = totalBoundingBox.left;
        int yOffset = totalBoundingBox.top;

        int relativeXPosition = 9*(characterBoundingBox.centerX() - xOffset) / width;
        int relativeYPosition = 9*(characterBoundingBox.centerY() - yOffset) / height;

        return new int[] {relativeXPosition, relativeYPosition};
    }

    public static boolean indicesValid(int[] indices) {
        return indices.length == 2
                && indices[0] >= 0 && indices[0] < 9
                && indices[1] >= 0 && indices[1] < 9;
    }

    public static String prettyPrintSudoku(int[][] sudoku) {
        String string = "";
        for(int i=0; i<sudoku.length; i++){
            for (int j=0;j<sudoku[i].length;j++) {
                string += sudoku[j][i] + " ";
            }
            string += "\n";
        }
        return string;
    }
}
