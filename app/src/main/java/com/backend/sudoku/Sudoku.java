package com.backend.sudoku;

import androidx.annotation.Nullable;

import java.util.List;

public interface Sudoku {
    void init(int[][] board, @Nullable boolean[] originalList);

    void clearNonOriginals();

    void place(int row, int column, int digit);

    void remove(int row, int column);

    int[][] getAsBoard();

    boolean[] getOriginalList();

    int getDigitAt(int row, int column);

    boolean digitAtIsOriginal(int row, int column);

    boolean canPlaceAt(int row, int column, int digitToTest);

    List<Integer> getCandidates(int row, int column);

    int getRawCandidates(int row, int column);

    boolean isValid();

    boolean isComplete();
}
