package com.backend;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class SudokuTest {

    private final static int[][] TEST_BOARD = new int[9][9];

    @BeforeClass
    public static void initTestBoard() {
        TEST_BOARD[0][0] = 1;
        TEST_BOARD[0][1] = 2;
        TEST_BOARD[0][2] = 3;

        TEST_BOARD[1][0] = 4;
        TEST_BOARD[1][1] = 5;
        TEST_BOARD[1][2] = 6;

        TEST_BOARD[2][0] = 7;
        TEST_BOARD[2][1] = 8;

        TEST_BOARD[1][3] = 1;
        TEST_BOARD[3][1] = 1;
    }

    @Test
    public void initHappyPath() {
        Sudoku sudoku = new SudokuImpl();
        sudoku.init(TEST_BOARD);

        Assert.assertArrayEquals("Board was not equal", TEST_BOARD, sudoku.getAsBoard());
    }

    @Test
    public void illegalLength() {
        Sudoku sudoku = new SudokuImpl();

        Assert.assertThrows(IllegalArgumentException.class, () -> sudoku.init(new int[9][10]));
    }

    @Test
    public void illegalValue() {
        Sudoku sudoku = new SudokuImpl();

        int[][] illegalBoard = getDeepCopyOfTestBoard();
        illegalBoard[6][6] = 20;

        Assert.assertThrows(IllegalArgumentException.class, () -> sudoku.init(illegalBoard));
    }

    @Test
    public void placingShouldUpdateBoard() {
        Sudoku sudoku = new SudokuImpl();
        sudoku.init(TEST_BOARD);

        int[][] expectedBoard = getDeepCopyOfTestBoard();
        expectedBoard[5][5] = 9;

        sudoku.place(5, 5, 9);

        Assert.assertArrayEquals("Board was not equal", expectedBoard, sudoku.getAsBoard());
    }

    @Test
    public void placingShouldOverride() {
        Sudoku sudoku = new SudokuImpl();
        sudoku.init(TEST_BOARD);

        int[][] expectedBoard = getDeepCopyOfTestBoard();
        expectedBoard[0][0] = 9;

        sudoku.place(0, 0, 9);

        Assert.assertArrayEquals("Board was not equal", expectedBoard, sudoku.getAsBoard());
    }

    @Test
    public void removingShouldUpdateBoard() {
        Sudoku sudoku = new SudokuImpl();
        sudoku.init(TEST_BOARD);

        int[][] expectedBoard = getDeepCopyOfTestBoard();
        expectedBoard[0][0] = 0;

        sudoku.remove(0, 0);

        Assert.assertArrayEquals("Board was not equal", expectedBoard, sudoku.getAsBoard());
        Assert.assertEquals(List.of(1, 9), sudoku.getCandidates(0, 0));
    }

    @Test
    public void canPlaceTest() {
        Sudoku sudoku = new SudokuImpl();
        sudoku.init(TEST_BOARD);

        // Not possible to place 1, as it is already present in the box
        Assert.assertFalse(sudoku.canPlaceAt(2, 2, 1));

        // Not possible to place 1, as it is already present in the row
        Assert.assertFalse(sudoku.canPlaceAt(0, 8, 1));

        // Not possible to place 1, as it is already present in the column
        Assert.assertFalse(sudoku.canPlaceAt(5, 0, 1));

        // Possible to place 9, as it is not present in the box, row or column
        Assert.assertTrue(sudoku.canPlaceAt(2, 2, 9));
    }

    @Test
    public void getCandidatesSimple() {
        Sudoku sudoku = new SudokuImpl();
        sudoku.init(TEST_BOARD);

        List<Integer> expected_0_0 = List.of();
        Assert.assertEquals(expected_0_0, sudoku.getCandidates(0, 0));

        List<Integer> expected_2_2 = List.of(9);
        Assert.assertEquals(expected_2_2, sudoku.getCandidates(2, 2));

        List<Integer> expected_0_3 = List.of(4, 5, 6, 7, 8, 9);
        Assert.assertEquals(expected_0_3, sudoku.getCandidates(0, 3));

        List<Integer> expected_3_3 = List.of(2, 3, 4, 5, 6, 7, 8, 9);
        Assert.assertEquals(expected_3_3, sudoku.getCandidates(3, 3));

        List<Integer> expected_8_8 = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Assert.assertEquals(expected_8_8, sudoku.getCandidates(8, 8));
    }

    @Test
    public void removeAndAddOnCompleteBoard() {
        String boardAsString = "326715849519438267847926135638249571271853496495167382183672954752394618964581723";

        Sudoku sudoku = new SudokuImpl();
        sudoku.init(SudokuUtil.fromString(boardAsString));

        sudoku.remove(0, 0);
        sudoku.place(0, 0, 3);

        Assert.assertTrue(sudoku.isValid());
    }

    // As 0 is the internal representation for an empty field, people might be tempted to place 0.
    // This is a test to test that we handle such a stupid thing (not that we would ever do that...)
    @Test
    public void placeZeroAndAddOnCompleteBoard() {
        String boardAsString = "326715849519438267847926135638249571271853496495167382183672954752394618964581723";

        Sudoku sudoku = new SudokuImpl();
        sudoku.init(SudokuUtil.fromString(boardAsString));

        sudoku.place(0, 0, 0);
        sudoku.place(0, 0, 3);

        Assert.assertTrue(sudoku.isValid());
    }

    // Helper methods
    private static int[][] getDeepCopyOfTestBoard() {
        int[][] board = new int[9][9];

        for (int row = 0; row < 9; row++) {
            System.arraycopy(SudokuTest.TEST_BOARD[row], 0, board[row], 0, 9);
        }

        return board;
    }

}
