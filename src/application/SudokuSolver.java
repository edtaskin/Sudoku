package application;

public class SudokuSolver {

	private static final int GRID_SIZE = 9;
	private static final int BOX_SIZE = 3;
		
	private boolean isNumberInRow(int[][] board, int number, int row) {
		for(int column = 0; column < GRID_SIZE; column++) {
			if(board[row][column] == number) {
				return true;
			}
		}
		return false;
	}
	
	
	private boolean isNumberInColumn(int[][] board, int number, int column) {
		for(int row = 0; row < GRID_SIZE; row++) {
			if(board[row][column] == number) {
				return true;
			}
		}
		return false;
	}
	
	
	private boolean isNumberIn3x3Box(int[][] board, int number, int row, int column) {
		int boxFirstCell_row = row - row % BOX_SIZE;
		int boxFirstCell_column = column - column % BOX_SIZE;
		
		for(int i = boxFirstCell_row; i < boxFirstCell_row + 3; i++) {
			for(int j = boxFirstCell_column; j < boxFirstCell_column + 3; j++) {
				if(board[i][j] == number) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean isValidPlacement(int[][] board, int number, int row, int column) {
		return !(isNumberInRow(board, number, row) || isNumberInColumn(board, number, column) || isNumberIn3x3Box(board, number, row, column));
	}
	
	/*
	 * Returns false if sudoku is unsolvable.
	 */
	public boolean solveSudoku(int[][] board) {
		for(int row = 0; row < GRID_SIZE; row++) {
			for(int column = 0; column < GRID_SIZE; column++) {
				if(board[row][column] == 0) {
					for(int number = 1; number <= GRID_SIZE; number++) {
						if(isValidPlacement(board, number, row, column)) {
							board[row][column] = number;
							
							if(solveSudoku(board)) {
								return true;
							}
							else {
								board[row][column] = 0;
							}
						}
						
					}
					return false;
				}
			}
		}
		return true;
	}
	
	public int[][] getSolvedBoard(int[][] board) {
		if(solveSudoku(board)) {
			return board;
		}
		return null;
	}
	
}

