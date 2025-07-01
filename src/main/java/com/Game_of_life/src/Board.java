package main.java.com.Game_of_life.src;
/**
 * The Board class represents a rectangular grid structure with a specific
 * number of rows and columns. The grid is backed by a 2D integer array and
 * allows for values to be manipulated at specific positions. The Board
 * initializes all positions to 0 and provides methods to get and set values
 * at specific locations, retrieve dimensions of the grid, and generate a
 * String representation of the board.
 */
public class Board {
    // instance variables - replace the example below with your own
    int[][] b;

    /**
     * Constructor for objects of class Board
     * An empty board is initialized to 0s.
     *
     * @param rows the number of rows in the Board
     * @param cols the number of columns in the Board
     */
    public Board(int rows, int cols) {
        // initialise instance variables
        b = new int[rows][cols];
    }

    /**
     * The get method returns the value stored at the specified
     * row,col location.
     *
     * @param row The row of the grid
     * @param col The column of the grid
     * @return the int value stored at that row,col
     */
    public int get(int row, int col) {
        return b[row][col];
    }

    /**
     * The set method sets the specified row,col location to
     * the specified value
     *
     * @param row   The row of the grid
     * @param col   The column of the grid
     * @param value The int value to be stored at row,col
     */
    public void set(int row, int col, int value) {
        b[row][col] = value;
    }

    /**
     * The getRows method returns the number of rows (the height)
     * of the grid
     *
     * @return the rows (height) of the grid
     */
    public int getRows() {
        return b.length;
    }

    /**
     * The getCols method returns the number of columns (the width)
     * of the grid
     *
     * @return the columns (width) of the grid
     */
    public int getCols() {
        return b[0].length;
    }

    /**
     * The toString method returns a String that can be printed to
     * display the grid
     *
     * @return a String representing the grid
     */
    public String toString() {
        String result = "";
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                result += String.valueOf(b[row][col]);
            }
            result += "\n";
        }
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - 1);
        }


        return result;

    }


    /**
     * Gets a string representation of the neighboring cells' values in a 3x3 grid.
     * Out of bounds cells are represented as "0".
     *
     * @param row the center cell's row index
     * @param col the center cell's column index
     * @return formatted string containing neighbor values with spaces and newlines
     */
    public int no_of_neighboursReturn(int row, int col) {
        if (b == null) {
            throw new IllegalStateException("Board array is not initialized");
        }

        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < getRows() && newCol >= 0 && newCol < getCols()) {
                    count += b[newRow][newCol];
                }
            }
        }

        return count;
    }
}