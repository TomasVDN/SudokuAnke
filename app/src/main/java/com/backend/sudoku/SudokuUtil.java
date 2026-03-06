package com.backend.sudoku;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class SudokuUtil {

    private static final String FILENAME = "saveFile";

    public static int[][] fromString(String input) {
        if (input.length() != 81) {
            throw new IllegalArgumentException();
        }

        int[][] board = new int[9][9];

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                // We do an implicit cast to an int here => will throw an error if it is not an int
                // On the plus side: it is always between 0 and 9 :)
                board[row][column] = input.charAt(row * 9 + column) - 48;
            }
        }

        return board;
    }

    public static String toString(int[][] board) {
        StringBuilder result = new StringBuilder();

        for (int[] row : board) {
            for (int value : row) {
                result.append(value);
            }
        }

        return result.toString();
    }

    public static void printToSystemOut(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                System.out.print(board[row][column] + " ");
                if (column % 3 == 2) {
                    System.out.print("  ");
                }
            }
            System.out.println();
            if (row % 3 == 2) {
                System.out.println();
            }
        }
    }

    private static final String SUBDIR = "sudokus";

    private static File getSudokuDir(Context context) {
        File dir = new File(context.getFilesDir(), SUBDIR);
        dir.mkdirs();
        return dir;
    }

    public static boolean saveToDisk(Context context, String name, int[][] board) throws IOException {
        String boardAsString = toString(board);
        if (!checkName(name)) {
            return false;
        }
        File file = new File(getSudokuDir(context), name);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.setLength(0);
            raf.writeBytes(boardAsString);
        }
        return true;
    }

    // TODO[Tomas] handle exception in the callers... For now, the app crashes :D
    public static int[][] getFromDisk(Context context, String name) throws IOException {
        File file = new File(getSudokuDir(context), name);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            return fromString(raf.readLine());
        }
    }

    public static String getFromDiskAsString(Context context, String name) throws IOException {
        return toString(getFromDisk(context, name));
    }

    public static void deleteSave(Context context, String name) throws IOException {
        File file = new File(getSudokuDir(context), name);
        file.delete();
    }

    public static List<String> getSavedNames(Context context) {
        String[] files = getSudokuDir(context).list();
        return files != null ? Arrays.asList(files) : List.of();
    }

    // Check the name under which a sudoku must be saved to avoid illegal chars (probably forgetting some...)
    private static boolean checkName(String name) {
        return name != null && !name.isBlank() && !name.contains("/") && !name.contains("..");
    }
}
