package com.frontend;

import androidx.annotation.Nullable;

import java.util.Stack;

public class Undoer {

    public static class PositionAndDigit {
        private final int row;
        private final int column;
        private final int previousDigit;
        private final boolean wasValid;

        public PositionAndDigit(int row, int column, int previousDigit, boolean wasValid) {
            this.row = row;
            this.column = column;
            this.previousDigit = previousDigit;
            this.wasValid = wasValid;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public int getPreviousDigit() {
            return previousDigit;
        }

        public boolean wasValid() {
            return wasValid;
        }
    }

    private final Stack<PositionAndDigit> stack = new Stack<>();

    public void addPlay(int row, int column, int previousDigit, boolean wasValid) {
        stack.push(new PositionAndDigit(row, column, previousDigit, wasValid));
    }

    @Nullable
    public PositionAndDigit getPreviousPlay() {
        if (stack.empty()) {
            return null;
        }

        return stack.pop();
    }

}
