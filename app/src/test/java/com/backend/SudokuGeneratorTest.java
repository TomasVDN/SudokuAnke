package com.backend;

import com.backend.sudoku.Sudoku;
import com.backend.sudoku.SudokuGenerator;
import com.backend.sudoku.SudokuImpl;
import com.backend.sudoku.SudokuSolver;

import org.junit.Assert;
import org.junit.Test;

public class SudokuGeneratorTest {
    @Test
    public void test() {
        SudokuGenerator generator = new SudokuGenerator();

        int[][] puzzle = generator.generate(SudokuGenerator.Difficulty.MEDIUM);

        Sudoku sudoku = new SudokuImpl();
        sudoku.init(puzzle);
        SudokuSolver solver = new SudokuSolver(sudoku);
        solver.solve();

        Assert.assertTrue(sudoku.isValid());

        int[][] puzzleHard = generator.generate(SudokuGenerator.Difficulty.EVIL);

        sudoku.init(puzzleHard);
        solver = new SudokuSolver(sudoku);
        solver.solve();

        Assert.assertTrue(sudoku.isValid());
    }
}
