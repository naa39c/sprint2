package solitaire.view;
//imported most libraries i could find lol
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import solitaire.controller.SolitaireController;
import solitaire.model.SolitaireModel;

// Draws the main window, side menus, and the peg board
public class SolitaireView extends JFrame {

	private final SolitaireModel gameModel;
	private SolitaireController gameController;
	private final BoardDisplayPanel boardPanel;
	
	private final JTextField sizeInput;
	private final JRadioButton englishOption;
	private final JRadioButton hexagonOption;
	private final JRadioButton diamondOption;

	// sets up the main game window and all the buttons
	public SolitaireView(SolitaireModel model) {
		this.gameModel = model;
		setTitle("Nicolas's Solitaire - Sprint 2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLayout(new BorderLayout(10, 10));

		// left side menu for board types
		JPanel leftMenu = new JPanel(new BorderLayout());
		JPanel radioGroupPanel = new JPanel(new GridLayout(4, 1));
		radioGroupPanel.add(new JLabel("Board Type:"));

		englishOption = new JRadioButton("English", true);
		hexagonOption = new JRadioButton("Hexagon");
		diamondOption = new JRadioButton("Diamond");
		
		// group them so you can only click one at a time
		ButtonGroup typesGroup = new ButtonGroup();
		typesGroup.add(englishOption);
		typesGroup.add(hexagonOption);
		typesGroup.add(diamondOption);
		
		radioGroupPanel.add(englishOption);
		radioGroupPanel.add(hexagonOption);
		radioGroupPanel.add(diamondOption);
		leftMenu.add(radioGroupPanel, BorderLayout.NORTH);

		// checkbox from sprint 0
		JCheckBox recordToggle = new JCheckBox("Record game");
		leftMenu.add(recordToggle, BorderLayout.SOUTH);
		add(leftMenu, BorderLayout.WEST);

		// top bar for board size and the new game button
		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topBar.add(new JLabel("Board size:"));
		sizeInput = new JTextField("7", 3);
		topBar.add(sizeInput);

		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(event -> handleNewGameClick());
		topBar.add(btnNewGame);
		add(topBar, BorderLayout.NORTH);

		// the actual area where the pegs are drawn
		boardPanel = new BoardDisplayPanel();
		boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(boardPanel, BorderLayout.CENTER);
	}

	// wires up the controller to listen for mouse clicks
	public void setController(SolitaireController controller) {
		this.gameController = controller;
	}

	// forces the screen to update after a move
	public void refreshBoard() {
		boardPanel.repaint();
	}

	// pops up when no valid jumps are left
	public void gameOverMessage() {
		JOptionPane.showMessageDialog(this, "Game over, no remaining moves");
	}

	// reads what the user typed/selected and restarts the game
	private void handleNewGameClick() {
		if (gameController == null) {
			return;
		}
		// default to 7 if they leave it blank
		int requestedSize = 7;
		try {
			requestedSize = Integer.parseInt(sizeInput.getText().trim());
		} catch (NumberFormatException error) {
			// fix it for them if they type a letter
			JOptionPane.showMessageDialog(this, "Invalid board size. Set to 7 instead");
			sizeInput.setText("7");
		}

		SolitaireModel.BoardType selectedType = SolitaireModel.BoardType.ENGLISH;
		if (hexagonOption.isSelected()) {
			selectedType = SolitaireModel.BoardType.HEXAGON;
		} else if (diamondOption.isSelected()) {
			selectedType = SolitaireModel.BoardType.DIAMOND;
		}

		gameController.startNewGame(selectedType, requestedSize);
	}

	// custom panel that handles the math for drawing the circles
	private class BoardDisplayPanel extends JPanel {
		private static final int CELL_WIDTH = 50;
		private static final int PEG_WIDTH = 36;
		private static final int OFFSET = (CELL_WIDTH - PEG_WIDTH) / 2;

		public BoardDisplayPanel() {
			setPreferredSize(new Dimension(350, 350));
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					if (gameController != null) {
						// figure out which grid cell they actually clicked
						int clickedCol = event.getX() / CELL_WIDTH;
						int clickedRow = event.getY() / CELL_WIDTH;
						gameController.handleBoardClick(clickedRow, clickedCol);
					}
				}
			});
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int totalRows = getHeight() / CELL_WIDTH;
			int totalCols = getWidth() / CELL_WIDTH;

			for (int row = 0; row < totalRows; row++) {
				for (int col = 0; col < totalCols; col++) {
					int pegState = gameModel.getCell(row, col);
					
					// don't draw anything in the dead zones
					if (pegState == SolitaireModel.INVALID) {
						continue;
					}
					// calculate pixel positions
					int xPos = (col * CELL_WIDTH) + OFFSET;
					int yPos = (row * CELL_WIDTH) + OFFSET;

					// draw the empty hole background
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(xPos, yPos, PEG_WIDTH, PEG_WIDTH);
					g.setColor(Color.BLACK);
					g.drawOval(xPos, yPos, PEG_WIDTH, PEG_WIDTH);

					//  blue if there is a peg there
					if (pegState == SolitaireModel.PEG) {
						g.setColor(Color.BLUE);
						g.fillOval(xPos, yPos, PEG_WIDTH, PEG_WIDTH);
					}
				}
			}
		}
	}
}
