package com.backend.ocr;

import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;


public class SudokuReader {
    private final CharactersReader charactersReader = new CharactersReader();

    public void setImage(InputImage image) {
        this.charactersReader.setImage(image);
    }

    public void readSudoku(Callback callback) {
        Task<Text> task = charactersReader.readText();
        task.addOnSuccessListener(text -> {
            Pair<int[][], Integer> sudokuWithNumberOfIssues = makeSudokuFromText(text);
            callback.onComplete(sudokuWithNumberOfIssues);
        });
    }

    private Pair<int[][], Integer> makeSudokuFromText(Text text) {
        int[][] sudoku = new int[9][9];
        int numberOfInvalidValues = 0;
        int numberOfOverwrites = 0;
        BoundedCharacters boundedCharacters = charactersReader.handleText(text);
        for (BoundedCharacter boundedCharacter : boundedCharacters.characters) {
            int[] indices = SudokuReaderUtil.indexForBoundingBox(boundedCharacters.boundingBox, boundedCharacter.boundingBox);
            if (!SudokuReaderUtil.indicesValid(indices)) {
                continue;
            }
            if (!SudokuReaderUtil.valueValid((boundedCharacter.value))) {
                numberOfInvalidValues++;
                continue;
            }
            if (sudoku[indices[0]][indices[1]] != 0) {
                numberOfOverwrites++;
                continue;
            }

            sudoku[indices[0]][indices[1]] = boundedCharacter.value;
        }

        int numberOfIssues = numberOfOverwrites + numberOfInvalidValues;
        if (sudokuIsEmpty(sudoku)) {
            numberOfIssues++;
        }

        return new Pair(sudoku, numberOfIssues);
    }

    private boolean sudokuIsEmpty(int[][] sudoku) {
        for (int i = 0; i < sudoku.length; i++)
            for (int j = 0; j < sudoku[i].length; j++)
                if (sudoku[i][j] != 0)
                    return false;
        return true;
    }
}
