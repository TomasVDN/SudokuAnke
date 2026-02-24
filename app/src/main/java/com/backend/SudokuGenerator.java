package com.backend;

import java.util.Random;

//TODO[Tomas] Clean this mess up, add comments
public class SudokuGenerator {

    public enum Difficulty {
        EASY(36, 45),
        MEDIUM(27, 35),
        HARD(22, 26),

        EVIL(18, 20);

        private final int minClues;
        private final int maxClues;

        Difficulty(int minClues, int maxClues) {
            this.minClues = minClues;
            this.maxClues = maxClues;
        }
    }

    private final Random random;

    public SudokuGenerator() {
        this(new Random());
    }

    public SudokuGenerator(Random random) {
        this.random = random;
    }

    public int[][] generate(Difficulty difficulty) {
        Sudoku sudoku = new SudokuImpl();

        // Step 1: Generate a fully solved board
        fillRandomly(sudoku);

        // Step 2: Pick a target clue count within the difficulty range
        int targetClues = difficulty.minClues
                + random.nextInt(difficulty.maxClues - difficulty.minClues + 1);

        // Step 3: Remove cells symmetrically while preserving uniqueness
        removeClues(sudoku, targetClues);

        return sudoku.getAsBoard();
    }

    private boolean fillRandomly(Sudoku sudoku) {
        // Find the first empty cell with MRV
        int bestIndex = -1;
        int bestCount = 10;

        for (int i = 0; i < 81; i++) {
            int row = i / 9;
            int column = i % 9;
            if (sudoku.getDigitAt(row, column) != 0) {
                continue;
            }

            int candidates = sudoku.getRawCandidates(row, column);
            int count = Integer.bitCount(candidates);

            if (count == 0) {
                // No candidates, return early
                return false;
            }
            if (count < bestCount) {
                bestCount = count;
                bestIndex = i;
                if (count == 1) break;
            }
        }

        if (bestIndex == -1) {
            // board is full
            return true;
        }

        int row = bestIndex / 9;
        int col = bestIndex % 9;
        int candidates = sudoku.getRawCandidates(row, col);

        // Extract candidates into an array so we can shuffle them
        int[] digits = extractAndShuffle(candidates);

        for (int digit : digits) {
            sudoku.place(row, col, digit);
            if (fillRandomly(sudoku)) return true;
            sudoku.remove(row, col);
        }

        return false;
    }

    private int[] extractAndShuffle(int candidates) {
        int count = Integer.bitCount(candidates);
        int[] digits = new int[count];
        int idx = 0;

        while (candidates != 0) {
            int bit = candidates & -candidates;
            digits[idx++] = Integer.numberOfTrailingZeros(bit) + 1;
            candidates &= ~bit;
        }

        // Fisher-Yates shuffle
        for (int i = count - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = digits[i];
            digits[i] = digits[j];
            digits[j] = tmp;
        }

        return digits;
    }

    private void removeClues(Sudoku sudoku, int targetClues) {
        // Build a shuffled list of cell indices to try removing.
        // Only include one cell per symmetric pair (i.e., indices 0..40).
        // Index 40 is the center cell (4,4), which is its own mirror.
        int[] indices = new int[41];
        for (int i = 0; i <= 40; i++) {
            indices[i] = i;
        }
        shuffle(indices);

        int currentClues = 81;

        for (int idx : indices) {
            if (currentClues <= targetClues) {
                break;
            }

            int row1 = idx / 9;
            int column1 = idx % 9;
            int row2 = 8 - row1;
            int column2 = 8 - column1;

            boolean isCenter = (row1 == row2 && column1 == column2);

            // Skip if already removed
            int digit1 = sudoku.getDigitAt(row1, column1);
            int digit2 = isCenter ? 0 : sudoku.getDigitAt(row2, column2);
            if (digit1 == 0){
                continue;
            }

            // Tentatively remove
            sudoku.remove(row1, column1);
            if (!isCenter) sudoku.remove(row2, column2);

            int cluesToRemove = isCenter ? 1 : 2;

            // Check if we'd go below target
            if (currentClues - cluesToRemove < targetClues) {
                // Restore — would overshoot
                sudoku.place(row1, column1, digit1);
                if (!isCenter) {
                    sudoku.place(row2, column2, digit2);
                }
                continue;
            }

            // Check uniqueness
            SudokuSolver solver = new SudokuSolver(sudoku);
            if (solver.hasUniqueSolution()) {
                currentClues -= cluesToRemove;
            } else {
                // Restore — would create multiple solutions
                sudoku.place(row1, column1, digit1);
                if (!isCenter) {
                    sudoku.place(row2, column2, digit2);
                }
            }
        }
    }

    private void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
}