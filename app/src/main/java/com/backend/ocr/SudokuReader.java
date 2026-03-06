package com.backend.ocr;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;

import java.lang.Character;


public class SudokuReader {
    private final CharactersReader charactersReader = new CharactersReader();

    public void setImage(InputImage image) {
        this.charactersReader.setImage(image);
    }

    public void readSudoku(Callback callback) {
        Task<Text> task = charactersReader.readText();
        task.addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                int[][] sudoku = makeSudokuFromText(text);
                callback.onComplete(sudoku);
            }
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

            sudoku[indices[0]][indices[1]] = boundedCharacter.value;
        }
        return sudoku;
    }
}
