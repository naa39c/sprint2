package solitaire.controller;

import solitaire.model.SolitaireModel;
import solitaire.view.SolitaireView;

//view to logic
public class SolitaireController {

	private final SolitaireModel model;
	private final SolitaireView view;
	private int selectedRow = -1;
	private int selectedCol = -1;

	//constructs controller and sets to view
	public SolitaireController(SolitaireModel model, SolitaireView view) {
		this.model = model;
		this.view = view;
		this.view.setController(this);
	}

	//mouse click to set peg/move
	public void handleBoardClick(int row, int col) {
		if (model.getCell(row, col) == SolitaireModel.INVALID) {
			return;
		}

		if (selectedRow == -1 && selectedCol == -1) {
			if (model.getCell(row, col) == SolitaireModel.PEG) {
				selectedRow = row;
				selectedCol = col;
			}
		} else {
			if (model.makeMove(selectedRow, selectedCol, row, col)) {
				if (model.isGameOver()) {
					view.gameOverMessage();
				}
			}
			selectedRow = -1;
			selectedCol = -1;
		}
		view.refreshBoard();
	}

	//resets game and forces new view
	public void startNewGame(SolitaireModel.BoardType type, int size) {
		model.startNewGame(type, size);
		selectedRow = -1;
		selectedCol = -1;
		view.refreshBoard();
	}
}
