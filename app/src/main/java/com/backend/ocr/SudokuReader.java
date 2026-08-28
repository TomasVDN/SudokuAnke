package com.backend.ocr;

import android.graphics.Rect;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;


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
            ArrayList<Pair<int[], Integer>> array_indices_with_value = getValuesWithIndices(boundedCharacter, boundedCharacters.boundingBox);
            for (Pair<int[], Integer> indices_with_value : array_indices_with_value) {
                int[] indices = indices_with_value.first;
                int value = indices_with_value.second;

                if (!SudokuReaderUtil.indicesValid(indices)) {
                    continue;
                }

                if (sudoku[indices[0]][indices[1]] != 0) {
                    numberOfOverwrites++;
                    continue;
                }

                sudoku[indices[0]][indices[1]] = value;
            }
        }

        int numberOfIssues = numberOfOverwrites + numberOfInvalidValues;
        if (sudokuIsEmpty(sudoku)) {
            numberOfIssues++;
        }

        return new Pair(sudoku, numberOfIssues);
    }

    private ArrayList<Pair<int[], Integer>> getValuesWithIndices(BoundedCharacter boundedCharacter, Rect totalBoundingBox) {
        ArrayList<Pair<int[], Integer>> all_indices_with_values = new ArrayList<>();
        int[] indices = SudokuReaderUtil.indexForBoundingBox(totalBoundingBox, boundedCharacter.boundingBox);
        int index_y = indices[0];
        int index_x_center = indices[1];

        int value = boundedCharacter.value;
        int number_of_characters = (int) Math.log10(value) + 1;

        int index_x_left = index_x_center - number_of_characters / 2;

        for (int i = 0; i < number_of_characters; i++) {
            int digit = (value / (int) Math.pow(10, i)) % 10;
            int index_x_digit = index_x_left + number_of_characters - i - 1;

            Pair<int[], Integer> indices_with_value = new Pair<>(new int[]{index_y, index_x_digit}, digit);
            all_indices_with_values.add(indices_with_value);
        }

        return all_indices_with_values;
    }

    private boolean sudokuIsEmpty(int[][] sudoku) {
        for (int i = 0; i < sudoku.length; i++)
            for (int j = 0; j < sudoku[i].length; j++)
                if (sudoku[i][j] != 0)
                    return false;
        return true;
    }
}
