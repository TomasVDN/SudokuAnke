package com.backend.sudoku;

//TODO[Tomas] Clean this mess up, add comments
public class SudokuSolver {

    // In place mutation for efficiency
    private final Sudoku sudoku;

    public SudokuSolver(Sudoku sudoku) {
        this.sudoku = sudoku;
    }

    // returns true if a solution was found, result will be found in the sudoku instance
    // If no solution can be found, return false. Note: state will be altered in both cases
    public boolean solve() {
        return backtrack();
    }

    // Counts the number of solutions, state of the board is restored when backtracking is finished
    public int countSolutions(int limit) {
        return countBacktrack(limit);
    }

    public boolean hasUniqueSolution() {
        // If we found two, it means we do not have a unique solution => no need to look further
        return countSolutions(2) == 1;
    }

    private boolean backtrack() {
        // Find the empty cell with the fewest candidates (MRV)
        int bestIndex = -1;
        int bestCount = 10; // anything < 9

        for (int i = 0; i < 81; i++) {
            int row = i / 9;
            int column = i % 9;
            if (sudoku.getDigitAt(row, column) != 0) continue;

            int candidates = sudoku.getRawCandidates(row, column);
            int count = Integer.bitCount(candidates);


            if (count == 0) {
                // dead end — no valid digit for this cell
                return false;
            }

            if (count < bestCount) {
                bestCount = count;
                bestIndex = i;
                if (count == 1) {
                    // can't do better than 1 — place it immediately
                    break;
                }
            }
        }

        if (bestIndex == -1) {
            // No empty cell found — puzzle is solved
            return true;
        }

        int row = bestIndex / 9;
        int column = bestIndex % 9;
        int candidates = sudoku.getRawCandidates(row, column);

        while (candidates != 0) {
            int bit = candidates & -candidates;
            int digit = Integer.numberOfTrailingZeros(bit) + 1;

            sudoku.place(row, column, digit);

            if (backtrack()) return true;

            sudoku.remove(row, column);

            candidates &= ~bit;
        }

        // no digit worked — trigger backtrack
        return false;
    }

    private int countBacktrack(int limit) {
        int bestIndex = -1;
        int bestCount = 10;

        for (int i = 0; i < 81; i++) {
            int r = i / 9;
            int c = i % 9;
            if (sudoku.getDigitAt(r, c) != 0) continue;

            int candidates = sudoku.getRawCandidates(r, c);
            int count = Integer.bitCount(candidates);

            if (count == 0) return 0;
            if (count < bestCount) {
                bestCount = count;
                bestIndex = i;
                if (count == 1) break;
            }
        }

        if (bestIndex == -1) {
            // board full — found a solution
            return 1;
        }

        int row = bestIndex / 9;
        int col = bestIndex % 9;
        int candidates = sudoku.getRawCandidates(row, col);
        int total = 0;

        while (candidates != 0) {
            int bit = candidates & -candidates;
            int digit = Integer.numberOfTrailingZeros(bit) + 1;

            sudoku.place(row, col, digit);

            total += countBacktrack(limit - total);

            sudoku.remove(row, col);

            if (total >= limit) {
                // early exit
                return total;
            }

            candidates &= ~bit;
        }

        return total;
    }
}