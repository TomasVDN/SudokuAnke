package com.backend;

import java.util.List;

public interface Sudoku {
    void init(int[][] board);

    void clear();

    void place(int row, int column, int digit);

    void remove(int row, int column);

    int[][] getAsBoard();

    int getDigitAt(int row, int column);

    boolean canPlaceAt(int row, int column, int digitToTest);

    List<Integer> getCandidates(int row, int column);

    int getRawCandidates(int row, int column);

    boolean isValid();

    boolean isComplete();
}
