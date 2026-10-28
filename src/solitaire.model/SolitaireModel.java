package solitaire.model;

public class SolitaireModel {
	
	// board shapes available for sprint 2
	public enum BoardType {
		ENGLISH, HEXAGON, DIAMOND
	}
	
	// cell states
	public static final int INVALID = -1;
	public static final int EMPTY = 0;
	public static final int PEG = 1;
	
	private int[][] gameBoard;
	private int boardSize;
	private BoardType currentType;

	// constructor just kicks off a new game
	public SolitaireModel(BoardType type, int size) {
		startNewGame(type, size);
	}

	// wipe the board and set up the requested shape
	public void startNewGame(BoardType type, int size) {
		this.currentType = type;
		this.boardSize = size;
		this.gameBoard = new int[size][size];

		if (type == BoardType.ENGLISH) {
			initializeEnglishBoard();
		}
	}

	// builds the standard cross-shaped english board
	private void initializeEnglishBoard() {
		int armLength = boardSize / 3;
		int center = boardSize / 2;

		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				// cut out the corners so it forms a cross
				boolean inCorner = (row < armLength || row >= boardSize - armLength) && 
				                   (col < armLength || col >= boardSize - armLength);

				if (inCorner) {
					gameBoard[row][col] = INVALID;
				} else if (row == center && col == center) {
					// center hole always starts empty
					gameBoard[row][col] = EMPTY;
				} else {
					gameBoard[row][col] = PEG;
				}
			}
		}
	}

	// checks if a jump is actually legal
	public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
		// can't jump out of bounds
		if (isOutOfBounds(fromRow, fromCol) || isOutOfBounds(toRow, toCol)) {
			return false;
		}
		
		// must start on a peg and land in an empty hole
		if (gameBoard[fromRow][fromCol] != PEG || gameBoard[toRow][toCol] != EMPTY) {
			return false;
		}

		int rowDiff = toRow - fromRow;
		int colDiff = toCol - fromCol;

		// check if it's a straight jump exactly two spaces away
		boolean isVertical = Math.abs(rowDiff) == 2 && colDiff == 0;
		boolean isHorizontal = Math.abs(colDiff) == 2 && rowDiff == 0;

		if (isVertical || isHorizontal) {
			// find the peg we are trying to jump over
			int midRow = fromRow + (rowDiff / 2);
			int midCol = fromCol + (colDiff / 2);
			return gameBoard[midRow][midCol] == PEG;
		}
		
		return false;
	}

	// updates the array if the move is good
	public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (isValidMove(fromRow, fromCol, toRow, toCol)) {
			gameBoard[fromRow][fromCol] = EMPTY;
			gameBoard[toRow][toCol] = PEG;
			
			// clear the jumped peg
			int midRow = fromRow + (toRow - fromRow) / 2;
			int midCol = fromCol + (toCol - fromCol) / 2;
			gameBoard[midRow][midCol] = EMPTY;
			
			return true;
		}
		return false;
	}

	// scans the whole board to see if the player is out of moves
	public boolean isGameOver() {
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (gameBoard[row][col] == PEG) {
					// test all four directions around the current peg
					if (isValidMove(row, col, row + 2, col) || 
					    isValidMove(row, col, row - 2, col) || 
					    isValidMove(row, col, row, col + 2) || 
					    isValidMove(row, col, row, col - 2)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// bounds checking so the app doesn't crash
	private boolean isOutOfBounds(int row, int col) {
		return row < 0 || row >= boardSize || col < 0 || col >= boardSize || gameBoard[row][col] == INVALID;
	}

	// gets the cell state, mainly for the view to draw it
	public int getCell(int row, int col) {
		return isOutOfBounds(row, col) ? INVALID : gameBoard[row][col];
	}
}
