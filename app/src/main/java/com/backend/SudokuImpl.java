package com.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class SudokuImpl implements Sudoku {

    private final int[] valuesInOrder = new int[81];
    private final int[] rowValues = new int[9];
    private final int[] columnValues = new int[9];
    private final int[] boxValues = new int[9];

    public SudokuImpl() {
    }

    @Override
    public void init(int[][] board) {
        performSanityChecks(board);

        clear();

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board[row][column] != 0) {
                    place(row, column, board[row][column]);
                }
            }
        }
    }

    public void clear() {
        // Reset all the arrays
        Arrays.fill(valuesInOrder, 0);
        Arrays.fill(rowValues, 0);
        Arrays.fill(columnValues, 0);
        Arrays.fill(boxValues, 0);
    }

    private static void performSanityChecks(int[][] board) {
        Predicate<Integer> predicate = (a) -> a >= 0 && a <= 9;

        if (board.length != 9) {
            throw new IllegalArgumentException("Input board is not a valid board.");
        }

        for (int[] row : board) {
            if (row.length != 9) {
                throw new IllegalArgumentException("Input board is not a valid board.");
            }

            for (int value : row) {
                if (!predicate.test(value)) {
                    throw new IllegalArgumentException("Invalid value provided: " + value);
                }
            }

            //TODO[Tomas] add a check for the values in the board (1-9) so that they do not appear twice
        }
    }

    @Override
    public void place(int row, int column, int digit) {
        // As 0 is the internal representation for an empty field, people might be tempted to place 0.
        // If someone attempts to do that, perform a remove operation instead
        if (digit == 0) {
            remove(row, column);
        }

        int bit = 1 << (digit - 1);

        rowValues[row] ^= bit;
        columnValues[column] ^= bit;

        int box = (row / 3) * 3 + (column / 3);
        boxValues[box] ^= bit;

        valuesInOrder[row * 9 + column] = digit;
    }

    @Override
    public void remove(int row, int column) {
        int digit = getDigitAt(row, column);

        if (digit == 0) {
            // Nothing to do, return early
            return;
        }

        // Shhh, this is a small trick - don't tell Lene <3
        place(row, column, digit);
        valuesInOrder[row * 9 + column] = 0;
    }

    @Override
    public int[][] getAsBoard() {
        int[][] board = new int[9][9];

        for (int row = 0; row < 9; row++) {
            System.arraycopy(valuesInOrder, row * 9, board[row], 0, 9);
        }

        return board;
    }

    @Override
    public int getDigitAt(int row, int column) {
        return valuesInOrder[row * 9 + column];
    }

    @Override
    public boolean canPlaceAt(int row, int column, int digitToTest) {
        int box = (row / 3) * 3 + (column / 3);
        int bitToTest = 1 << (digitToTest - 1);

        // The OR's combine to provide the "used" digits
        int used = rowValues[row] | columnValues[column] | boxValues[box];

        // If the bit is used, the AND operation would return 1
        return (used & bitToTest) == 0;
    }

    @Override
    public List<Integer> getCandidates(int row, int column) {
        return getCandidates(row, column, false);
    }

    private List<Integer> getCandidates(int row, int column, boolean isValidation) {
        // If there is already a value assigned, there are no candidates
        if (!isValidation && valuesInOrder[row * 9 + column] != 0) {
            return List.of();
        }

        int candidates = getRawCandidates(row, column);

        List<Integer> result = new ArrayList<>();
        while (candidates != 0) {
            // Get the lowest set bit, and get corresponding digit
            int bit = candidates & -candidates;
            int digit = Integer.numberOfTrailingZeros(bit) + 1;
            result.add(digit);

            // clear bit
            candidates &= ~bit;
        }
        return result;
    }

    /*
     * This method should only be used for internal actions. It is not safe, as it does not take into
     * account whether there already is a value on that location.
     */
    @Override
    public int getRawCandidates(int row, int column) {
        int box = (row / 3) * 3 + (column / 3);

        // The OR's combine to provide the "used" digits
        // The negation provides the "free" digits
        // The 0x1FF truncates this to only the nine last digits
        return ~(rowValues[row] | columnValues[column] | boxValues[box]) & 0x1FF;
    }

    @Override
    public boolean isValid() {
        int[] rowCheck = new int[9];
        int[] colCheck = new int[9];
        int[] boxCheck = new int[9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int digit = valuesInOrder[row * 9 + col];
                if (digit == 0) continue;

                int bit = 1 << (digit - 1);
                int box = (row / 3) * 3 + (col / 3);

                if ((rowCheck[row] & bit) != 0) return false;
                if ((colCheck[col] & bit) != 0) return false;
                if ((boxCheck[box] & bit) != 0) return false;

                rowCheck[row] |= bit;
                colCheck[col]  |= bit;
                boxCheck[box]  |= bit;
            }
        }
        return true;
    }

    @Override
    public boolean isComplete() {
        for (int i : valuesInOrder) {
            if (i == 0) {
                return false;
            }
        }
        return true;
    }
}
