package com.backend.ocr;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;


public class SudokuReader {
    private final CharactersReader charactersReader = new CharactersReader();
    private float[] dots = new float[8];
    private InputImage inputImage;

    public void setImage(InputImage inputImage) {
        this.inputImage = inputImage;
    }

    public void setDots(float[] dots) {
        if (dots.length == 8) {
            this.dots = dots;
        } else {
            throw new IllegalArgumentException("You donkey :)");
        }
    }

    public void readSudoku(Callback callback) {
        charactersReader.setImage(inputImage);
        Task<Text> task = charactersReader.readText();
        task.addOnSuccessListener(text -> {
            int[][] sudoku = makeSudokuFromText(text);
            callback.onComplete(sudoku);
        });
    }

    private int[][] makeSudokuFromText(Text text) {
        int[][] sudoku = new int[9][9];
        BoundedCharacters boundedCharacters = charactersReader.handleText(text);
        for (BoundedCharacter boundedCharacter : boundedCharacters.characters) {
            int[] indices = SudokuReaderUtil.indexForBoundingBox(boundedCharacters.boundingBox, boundedCharacter.boundingBox);
            if (!SudokuReaderUtil.indicesValid(indices)) {
                continue;
            }
            if (!SudokuReaderUtil.valueValid((boundedCharacter.value))) {
                continue;
            }

            sudoku[indices[0]][indices[1]] = boundedCharacter.value;
        }
        return sudoku;
    }
}
