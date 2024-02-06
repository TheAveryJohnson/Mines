import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>Mines - A mine clearing game</h1>
 * 
 * <h2>Goal -</h2>
 * <b>The goal of the game is to clear the minefield by clicking every cell without clicking any mines.</b>
 * 
 * <h2>Controls -</h2>
 * <b>Click:</b> <i>Clicking an unrevealed cell while NOT holding CTRL or ALT will clear the cell</i> <br><br>
 * <b>CTRL:</b> <i>Holding CTRL and clicking an unrevealed cell will place a flag at the cell</i> <br><br>
 * <b>ALT:</b> <i>Holding ALT and clicking a flagged cell will remove the flag from the cell</i> <br><br>
 * 
 * @version 0.0.0.0
 * @author Avery Johnson
 */
public class Mines {

	/* Window components */
	private JFrame frame = new JFrame("CTRL to place flag - ALT to remove flag");
	private JPanel panel = new JPanel();

	/* Holds each cells JLabel */
	private JLabel[][] labels;

	/* Base Icons */
	private static final ImageIcon HI_ICON			= new ImageIcon("img\\hi.png");
	private static final ImageIcon HI_RED_ICON		= new ImageIcon("img\\hi_red.png");
	private static final ImageIcon HI_GREEN_ICON	= new ImageIcon("img\\hi_green.png");
	private static final ImageIcon LO_ICON			= new ImageIcon("img\\lo.png");
	private static final ImageIcon LO_RED_ICON		= new ImageIcon("img\\lo_red.png");
	private static final ImageIcon LO_GREEN_ICON	= new ImageIcon("img\\lo_green.png");
	
	/* Special Icons */
	private static final ImageIcon FLAG_ICON		= new ImageIcon("img\\flag.png");
	private static final ImageIcon LO_MINE_ICON		= new ImageIcon("img\\lo_mine.png");
	private static final ImageIcon HI_MINE_ICON		= new ImageIcon("img\\hi_mine.png");
	private static final ImageIcon CROWN_ICON		= new ImageIcon("img\\crown.png");
	
	/* Number Icons */
	private static final ImageIcon[] NUMBER_ICONS = {
		null, // There is no 'zero' number icon
		new ImageIcon("img\\one.png"),
		new ImageIcon("img\\two.png"),
		new ImageIcon("img\\three.png"),
		new ImageIcon("img\\four.png"),
		new ImageIcon("img\\five.png"),
		new ImageIcon("img\\six.png"),
		new ImageIcon("img\\seven.png"),
		new ImageIcon("img\\eight.png")
	};

	/* Icon size in pixels [determined by HI_ICON's width]*/
	private static final int ICON_SIZE_PX = HI_ICON.getImage().getWidth(null);

	/* Panel width and height in pixels */
	private final int PANEL_HEIGHT_PX;
	private final int PANEL_WIDTH_PX;

	/* Total number of rows and columns */
	private final int ROWS;
	private final int COLS;

	/* Total number of mines */
	private final int TOTAL_MINES;
	
	/* Total number of flags placed by the player */
	private int totalFlags = 0;

	/* Stores the number of neighboring mines for each cell */
	private int[][] neighborMines;

	/* Stores the mines, flags, and revealed cells */
	private boolean[][] isMined;
	private boolean[][] isFlagged;
	private boolean[][] isRevealed;

	/* Determines whether the game is running or not */
	private boolean isRunning = false;

	/* Used to track time progression */
	private long tInit;

	/**
	 * <h3>Constructs a Mines with the provided dimensions and number of mines</h3>
	 * 
	 * @param rows  : total number of rows
	 * @param cols  : total number of columns
	 * @param mines : total number of mines
	 * 
	 * @throws IllegalArgumentException {@code if rows < 1 || cols < 1 || mines < 1}
	 * @throws IllegalArgumentException {@code if mines > (rows * cols)}
	 */
	public Mines(int rows, int cols, int mines) {

		/* Exception handling */
		if (rows < 1)               throw new IllegalArgumentException("Mines - Mines() : rows < 1 returned true");
		if (cols < 1)               throw new IllegalArgumentException("Mines - Mines() : cols < 1 returned true");
		if (mines < 1)              throw new IllegalArgumentException("Mines - Mines() : mines < 1 returned true");
		if (mines > (rows * cols))  throw new IllegalArgumentException("Mines - Mines() : mines > (rows * cols) returned true [attempting to add more mines than there are available cells]");

		/* Store row, column, and total mine values */
		ROWS = rows;
		COLS = cols;
		TOTAL_MINES = mines;

		/* Calculate and store panel width and height values */
		PANEL_HEIGHT_PX = ROWS * ICON_SIZE_PX;
		PANEL_WIDTH_PX  = COLS * ICON_SIZE_PX;

		/* Initialize flag and revealed arrays */
		isFlagged   = new boolean[ROWS][COLS];
		isRevealed  = new boolean[ROWS][COLS];

		/* Initialize the frame and panel */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setIconImage(FLAG_ICON.getImage());
		panel.setPreferredSize(new Dimension(PANEL_WIDTH_PX, PANEL_HEIGHT_PX));
		panel.setLayout(null);
	}
	
	/**
	 * <h3>Starts Mines by initializing components and setting up the window</h3>
	 */
	public void start() {

		/* Generate new random mines */
		newRandomMines(TOTAL_MINES);

		/* Calculate the neighboring mines based off mines */
		countAllNeighborMines();

		/* Generate new JLabels */
		newLabels();

		/* Add the new icons to the panel */
		addLabelsToPanel();

		/* Add the mouse listener */
		panel.addMouseListener(mouseAdapter);

		/* Add the panel to the frame, pack, center, and set visible */
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		/* Start the game */
		isRunning = true;

		/* Initialize the timer */
		tInit = System.currentTimeMillis();
	}

	/**
	 * <h3>MouseAdapter to handle mouse input</h3>
	 */
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {

			/* If the game is not running and we clicked the screen, reset and return */
			if (!isRunning) {
				reset();
				return;
			}

			/* Calculate the row and column based on the click position */
			int row = (int) e.getPoint().getY() / ICON_SIZE_PX;
			int col = (int) e.getPoint().getX() / ICON_SIZE_PX;

			/* If we clicked a cell that has already been revealed, do nothing and return */
			if (isRevealed[row][col]) {
				return;
			}

			/* Store modifiers */
			int mod = e.getModifiersEx();

			/* If the user was holding ctrl as they selected this cell [they want to place a flag] */
			if (ctrlPressed(mod)) {
				placeFlag(row, col);
			}

			/* If the user was holding alt as they selected this cell [they want to remove a flag] */
			else if (altPressed(mod)) {
				removeFlag(row, col);
			}

			/* If the user was holding no mask keys as they selected this cell [they want to clear this cell] */
			else {
				selectCell(row, col);
			}
		}
	};

	/**
	 * <h3>Places a flag at the specified cell if the cell doesn't already contain a flag</h3>
	 * 
	 * @param row : The row coordinate of the cell
	 * @param col : The column coordinate of the cell
	 */
	private void placeFlag(int row, int col) {

		/* If the selected cell already contains a flag, do nothing and return */
		if (isFlagged[row][col]) {
			return;
		}

		/* Place a flag at this cell */
		isFlagged[row][col] = true;
		labels[row][col].setIcon(FLAG_ICON);
		++totalFlags;
		frame.setTitle(midgameTitle());
	}

	/**
	 * <h3>Removes a flag from the specified cell if the cell contains a flag</h3>
	 * 
	 * @param row : The row coordinate of the cell
	 * @param col : The column coordinate of the cell
	 */
	private void removeFlag(int row, int col) {

		/* If the selected cell does not contain a flag, do nothing and return */
		if (!isFlagged[row][col]) {
			return;
		}

		/* Remove the flag at this cell */
		isFlagged[row][col] = false;
		labels[row][col].setIcon(HI_ICON);
		--totalFlags;
		frame.setTitle(midgameTitle());
	}

	/**
	 * <h3>Handles the selection of a cell</h3>
	 * 
	 * @param row : The row coordinate of the cell
	 * @param col : The column coordinate of the cell
	 */
	private void selectCell(int row, int col) {

		/* If the selected cell contains a flag, do nothing and return */
		if (isFlagged[row][col]) {
			return;
		}

		/* If the selected cell contains a mine, reveal and gameOver */
		if (isMined[row][col]) {
			isRevealed[row][col] = true;
			gameOver();
		}

		/* If the selected cell has at least one neighbor, reveal and check for victory */
		else if (neighborMines[row][col] != 0) {
			labels[row][col].setIcon(NUMBER_ICONS[neighborMines[row][col]]);
			isRevealed[row][col] = true;
			checkForVictory();
		}

		/* If the selected cell has no neighbors, flood fill and check for victory */
		else {
			floodFill(row, col);
			checkForVictory();
		}
	}

	/**
	 * <h3>Performs the flood fill algorithm starting from the provided cell</h3>
	 * 
	 * @param row : The row coordinate of the starting cell
	 * @param col : The column coordinate of the starting cell
	 */
	private void floodFill(int row, int col) {

		/* If the selected cell is flagged, do nothing and return */
		if (isFlagged[row][col]) {
			return;
		}

		/* If the selected cell has no neighboring mines, reveal the cell and clear */
		if (neighborMines[row][col] == 0) {
			labels[row][col].setIcon(LO_ICON);
			isRevealed[row][col] = true;
			clear(row, col);

		/* If the selected cell as at least one neighboring mines, reveal the cell */
		} else {
			labels[row][col].setIcon(NUMBER_ICONS[neighborMines[row][col]]);
			isRevealed[row][col] = true;
		}
	}

	/**
	 * <h3>Clears neighboring cells recursively, starting from the given cell</h3>
	 * 
	 * @param r : The row coordinate of the starting cell
	 * @param c : The column coordinate of the starting cell
	 */
	private void clear(int r, int c) {

		/* For every cell */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {

				/* If this cell is not a mine, has not been revealed, and is a neighbor of the passed cell, floodfill */
				if (!isMined[row][col] && !isRevealed[row][col] && isNeighbor(row, col, r, c)) {
					floodFill(row, col);
				}
			}
		}
	}

	/**
	 * <h3>Checks for victory by verifying if every non-mine cell has veen revealed</h3>
	 */
	private void checkForVictory() {

		/* For every cell */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {

				/* If this cell is not a mine and has not been revealed we have not won, do nothing and return */
				if (!isMined[row][col] && !isRevealed[row][col]) {
					return;
				}
			}
		}

		/* If we have revealed every non-mine cell, declare victory */
		victory();
	}

	/**
	 * <h3>Handles the victory scenario by stopping the game, updating the window and revealing the state of each cell</h3>
	 */
	private void victory() {

		/* Stop game from running */
		isRunning = false;

		/* Update the window title */
		frame.setTitle("You Win - " + ((float)getDurationMS() / 1000) + "s - Click to restart");
		
		/* For every cell */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {

				/* If this cell is a mine, set to crown icon */
				if (isMined[row][col]) {
					labels[row][col].setIcon(CROWN_ICON);

				/* If this cell is revealed, set to low green icon */
				} else if (isRevealed[row][col]) {
					labels[row][col].setIcon(LO_GREEN_ICON);
				
				/* If this cell is not a mine and not revealed, set to high green icon */
				} else {
					labels[row][col].setIcon(HI_GREEN_ICON);
				}
			}
		}
	}

	/**
	 * <h3>Handles the game over scenario by stopping the game, updating the window and revealing the state of each cell</h3>
	 */
	private void gameOver() {

		/* Stop game from running */
		isRunning = false;

		/* Update the window title */
		frame.setTitle("Game Over - " + ((float)getDurationMS() / 1000) + "s - Click to restart");
		
		/* For every cell */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {

				/* If this cell is a mine */
				if (isMined[row][col]) {

					/* If this cell is revealed, set to low mine icon */
					if (isRevealed[row][col]) {
						labels[row][col].setIcon(LO_MINE_ICON);
					}

					/* If this cell is not revealed, set to high mine icon */
					else {
						labels[row][col].setIcon(HI_MINE_ICON);
					}
				
				/* If this cell is revealed, set to low red icon */
				} else if (isRevealed[row][col]) {
					labels[row][col].setIcon(LO_RED_ICON);
				
				/* If this cell is not revealed, set to high red icon */
				} else {
					labels[row][col].setIcon(HI_RED_ICON);
				}
			}
		}
	}

	/**
	 * <h3>Resets Mines to its initial state</h3>
	 */
	private void reset() {

		/* Clear flaged and revealed cells */
		isFlagged = new boolean[ROWS][COLS];
		isRevealed = new boolean[ROWS][COLS];
		
		/* Reset the total number of flags */
		totalFlags = 0;

		/* Generate new random mines */
		newRandomMines(TOTAL_MINES);

		/* Count all the neighboring mines */
		countAllNeighborMines();

		/* For every cell, set the icon back to the high icon */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {
				labels[row][col].setIcon(HI_ICON);
			}
		}

		/* Update the window title */
		frame.setTitle("CTRL to place flag - ALT to remove flag");

		/* Start the game again */
		isRunning = true;

		/* Start the timer again */
		tInit = System.currentTimeMillis();
	}

	/**
	 * <h3>Randomly places a defined number of mines on the game board</h3>
	 * 
	 * @param mines : The total number of mines to be placed
	 */
	private void newRandomMines(int mines) {

		/* Clear any previous mines */
		isMined = new boolean[ROWS][COLS];

		/* Tracks the number of mines we have placed */
		int placedMines = 0;

		/* Tracks our coordinates */
		int row;
		int col;

		/* Continuously loop until we have met the total number of mines */
		while (placedMines < TOTAL_MINES) {

			/* Pick a random coordinate */
			row = (int) (Math.random() * ROWS);
			col = (int) (Math.random() * COLS);

			/* If we do not already have a mine in that position, place here */
			if (!isMined[row][col]) {
				isMined[row][col] = true;
				++placedMines;
			}
		}
	}

	/**
	 * <h3>Counts and updates the number of neighboring mines for each cell</h3>
	 */
	private void countAllNeighborMines() {

		/* Clear any previous neighbor values */
		neighborMines = new int[ROWS][COLS];

		/* For every cell */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {

				/* If we have found a mine */
				if (isMined[row][col]) {

					/* Assign the neighbor value to 9 */
					neighborMines[row][col] = 9; // A value of 9 denotes a mine within neighborMines
				
				/* If we are searching in a non-mine cell */
				} else {

					/* Calculate the number of neighboring mines and store into the array */
					neighborMines[row][col] = countNeighborMines(row, col);
				}
			}
		}
	}

	/**
	 * <h3>Counts the number of neighboring mines for a given cell</h3>
	 * 
	 * @param row : The row coordinate of the cell
	 * @param col : The column coordinate of the cell
	 * 
	 * @return The number of neighboring mines
	 */
	private int countNeighborMines(int row, int col) {

		/* Tracks the number of neighboring mines found */
		int neighborMines = 0;

		/* For every cell */
		for (int r = 0;r < isMined.length;++r) {
			for (int c = 0;c < isMined[row].length;++c) {

				/* If this cell is a neighbor and is a mine, increment the counter */
				if (isNeighbor(r, c, row, col) && isMined[r][c]) {
					++neighborMines;
				}
			}
		}

		/* Return the total */
		return neighborMines;
	}

	/**
	 * <h3>Checks if two cells are neighbors</h3>
	 * 
	 * @param row1 : The row coordinate of the first cell
	 * @param col1 : The column coordinate of the first cell
	 * @param row2 : The row coordinate of the first cell
	 * @param col2 : The column coordinate of the first cell
	 * 
	 * @return {@code true} if the cells are neighbors, {@code false} otherwise
	 */
	private static boolean isNeighbor(int row1, int col1, int row2, int col2) {

		/* If the first and second rows are both within 1 of eachother (and is not itself), they are neighbors */
		return (Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1) && (row1 != row2 || col1 != col2);
	}

	/**
	 * <h3>Generates the midgame title with information about the ramaining flags</h3>
	 * 
	 * @return The midgame title
	 */
	private String midgameTitle() {
		return "Mines - Flags placed: " + totalFlags + " Total mines: " + TOTAL_MINES;
	}

	/**
	 * <h3>Calculates the duration of the game in milliseconds</h3>
	 * 
	 * @return The duration of the game in milliseconds
	 */
	private long getDurationMS() {
		return System.currentTimeMillis() - tInit;
	}

	/**
	 * <h3>Initializes the JLabels for each cell</h3>
	 */
	private void newLabels() {

		/* Initialize the label array */
		labels = new JLabel[ROWS][COLS];

		/* For every cell */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {

				/* Initialize new JLabels and set bounds  */
				labels[row][col] = new JLabel(HI_ICON);
				labels[row][col].setBounds(col * ICON_SIZE_PX, row * ICON_SIZE_PX, ICON_SIZE_PX, ICON_SIZE_PX);
			}
		}
	}

	/**
	 * <h3>Adds the labels representing cells to the panel</h3>
	 */
	private void addLabelsToPanel() {

		/* For every cell, add its label to panel */
		for (int row = 0;row < ROWS;++row) {
			for (int col = 0;col < COLS;++col) {
				panel.add(labels[row][col]);
			}
		}
	}
	
	/**
	 * <h3>Checks if the CTRL key is pressed based on the given modifier value</h3>
	 * 
	 * @param mod : The modifier value
	 * 
	 * @return {@code true} if the CTRL key is pressed, {@code false} otherwise
	 */
	private static boolean ctrlPressed(int mod) {
		return (mod & MouseEvent.CTRL_DOWN_MASK) != 0;
	}

	/**
	 * <h3>Checks if the ALT key is pressed based on the given modifier value</h3>
	 * 
	 * @param mod : The modifier value
	 * 
	 * @return {@code true} if the CTRL key is pressed, {@code false} otherwise
	 */
	private static boolean altPressed(int mod) {
		return (mod & MouseEvent.ALT_DOWN_MASK) != 0;
	}

}
