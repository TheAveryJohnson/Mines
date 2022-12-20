import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

/**
 * <h1>Mines - A mine clearing game</h1>
 *
 * <h2>Goal</h2>
 * <b>The goal of the game is to clear the minefield by placing flags on each mine. You only have access to the same number of flags as there are mines so place carefully.</b><br><br>
 *
 * <h2>Controls</h2>
 *
 * <b>Click : </b> <i>Clicking an unrevealed cell while NOT holding CTRL or ALT will clear the clicked cell</i><br><br>
 * <b>CTRL : </b> <i>Holding CTRL and clicking an unrevealed cell will place a flag at the clicked cell</i><br><br>
 * <b>ALT : </b> <i>Holding ALT and clicking a flagged cell will remove the flag from the clicked cell</i><br><br>
 *
 * @since 2022-12-19
 * @version 0.0.0.1
 * @author Avery Johnson
 **/
public class Mines {

    /* private static final Strings */
    private static final String INSTRUCTIONS = "Mines - CTRL to place flag - ALT to remove flag";
    private static final String VICTORY = "Mines - You Win - ";
    private static final String FAILURE = "Mines - Game Over - ";

    /* public static final ImageIcons */
    public static final ImageIcon HI_ICON = new ImageIcon("img/hi.png");
    public static final ImageIcon HI_RED_ICON = new ImageIcon("img/hi_red.png");
    public static final ImageIcon HI_GREEN_ICON = new ImageIcon("img/hi_green.png");
    public static final ImageIcon LO_ICON = new ImageIcon("img/lo.png");
    public static final ImageIcon LO_RED_ICON = new ImageIcon("img/lo_red.png");
    public static final ImageIcon LO_GREEN_ICON = new ImageIcon("img/lo_green.png");
    public static final ImageIcon FLAG_ICON = new ImageIcon("img/flag.png");
    public static final ImageIcon MINE_ICON = new ImageIcon("img/mine.png");
    public static final ImageIcon CROWN_ICON = new ImageIcon("img/crown.png");
    public static final ImageIcon[] NUMBER_ICONS = { null, 
    		new ImageIcon("img/one.png"), new ImageIcon("img/two.png"), new ImageIcon("img/three.png"), 
    		new ImageIcon("img/four.png"), new ImageIcon("img/five.png"), new ImageIcon("img/six.png"), 
    		new ImageIcon("img/seven.png"), new ImageIcon("img/eight.png")
    };

    /* private static final ints */
    private static final int ICON_SIZE_PX = HI_ICON.getImage().getWidth(null);
    private static final int IS_MINE = 9; // UNNECESSARY

    /* private final ints */
    private final int ROWS;
    private final int COLS;
    private final int NUM_MINES;
    private final int PANEL_WIDTH_PX;
    private final int PANEL_HEIGHT_PX;

    /* private swing components */
    private final JFrame frame = new JFrame(INSTRUCTIONS);
    private final JPanel panel = new JPanel();
    private JLabel[][] icons;

    /* used to track time progression */
    private long tInit;

    /* board values */
    private int[][] neighbors;
    private boolean[][] mines;
    private boolean[][] flags;
    private boolean[][] revealed;

    /* game values */
    private boolean isRunning;
    private int numberOfFlags = 0;

    /**
     * <h3>Constructs a Mines with provided dimensions and number of mines</h3>
     * @param ROWS : int rows of game board
     * @param COLS : int cols of game board
     * @param NUM_MINES : int number of mines on board
     * @precondition ROWS & COLS must be at least 2 or greater
     * @throws IllegalArgumentException : if ROWS or COLS is less than 2
     * @throws IllegalArgumentException : if NUM_MINES is more than total number of cells
     */
    public Mines(int ROWS, int COLS, int NUM_MINES) {
    	if (ROWS < 2 || COLS < 2) { throw new IllegalArgumentException("Mines(rows, cols, num_mines) : Trying to assign an illegal value to ROW or COL"); }
    	if (NUM_MINES > (ROWS * COLS)) { throw new IllegalArgumentException("Mines(rows, cols, num_mines) : Trying to place more mines than there are available cells"); }
        this.ROWS = ROWS;
        this.COLS = COLS;
        this.NUM_MINES = NUM_MINES;
        this.PANEL_WIDTH_PX = COLS * ICON_SIZE_PX;
        this.PANEL_HEIGHT_PX = ROWS * ICON_SIZE_PX;
    }

    /**
     * <h3>Initializes visual components then starts the game</h3>
     */
    public void start() {
        flags = new boolean[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        mines = newRandomMines(ROWS, COLS, NUM_MINES);
        neighbors = minesGridToNeighborsGrid(mines);
        icons = newJLabelGrid(ROWS, COLS);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setIconImage(FLAG_ICON.getImage());
        panel.setPreferredSize(new Dimension(PANEL_WIDTH_PX, PANEL_HEIGHT_PX));
        panel.setLayout(null);
        addJLabelsToPanel();
        panel.addMouseListener(mouseAdapter);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        isRunning = true;
        tInit = System.currentTimeMillis();
    }

    /** Controls mouse interactions */
    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!isRunning) { reset(); return; }
            Point point = e.getPoint();
            int row = (int) point.getY() / ICON_SIZE_PX;
            int col = (int) point.getX() / ICON_SIZE_PX;
            int mod = e.getModifiersEx();
            if (revealed[row][col]) { return; }
            if (isCtrlPressed(mod)) {
                if (flags[row][col]) { return; }
                if (numberOfFlags >= NUM_MINES) { return; }
                flags[row][col] = true;
                icons[row][col].setIcon(FLAG_ICON);
                ++numberOfFlags;
                frame.setTitle(currentTitleString());
                if (isEveryMineFlagged()) {
            		isRunning = false;
            		showVictory();
                }
            } else if (isAltPressed(mod)) {
                if (!flags[row][col]) { return; }
                flags[row][col] = false;
                icons[row][col].setIcon(HI_ICON);
                --numberOfFlags;
                frame.setTitle(currentTitleString());
            } else {
                if (flags[row][col]) { return; }
                if (mines[row][col]) {
                	isRunning = false;
                    showFailure();
                } else if (neighbors[row][col] != 0) {
                    icons[row][col].setIcon(NUMBER_ICONS[neighbors[row][col]]);
                    revealed[row][col] = true;
                } else {
                    floodFill(row, col);
                }
            }
        }
    };

    /** The flood fill algorithm */
    private void floodFill(int row, int col) {
        if (neighbors[row][col] == 0) {
            icons[row][col].setIcon(LO_ICON);
            revealed[row][col] = true;
            clear(row, col);
        } else {
            icons[row][col].setIcon(NUMBER_ICONS[neighbors[row][col]]);
            revealed[row][col] = true;
        }
    }

    /** Helper method to flood fill algorithm */
    private void clear(int r, int c) {
        for (int row = 0;row < ROWS;++row) {
            for (int col = 0;col < COLS;++col) {
                if (isNeighbor(row, col, r, c) &&
                	!revealed[row][col] &&
                    !mines[row][col] &&
                    !flags[row][col]) {
                    floodFill(row, col);
                }
            }
        }
    }
    
    /** Returns true if every mine has been flagged */
    private boolean isEveryMineFlagged() {
    	 int flagsOnMines = 0;
         for (int row = 0;row < ROWS;++row) {
             for (int col = 0;col < COLS;++col) {
                 if (mines[row][col] && flags[row][col]) { ++flagsOnMines; }
             }
         }
         return flagsOnMines == NUM_MINES;
    }

    /** Stops game from running, updates title, and sets Icons to green */
    private void showVictory() {
        frame.setTitle(VICTORY + ((float)currentDurationMS() / 1000) + "s");
        for (int row = 0;row < ROWS;++row) {
            for (int col = 0;col < COLS;++col) {
                if (mines[row][col]) {
                    icons[row][col].setIcon(CROWN_ICON);
                } else if (revealed[row][col]) {
                    icons[row][col].setIcon(LO_GREEN_ICON);
                } else {
                    icons[row][col].setIcon(HI_GREEN_ICON);
                }
            }
        }
    }

    /** Stops game from running, updates title, and sets Icons to red */
    private void showFailure() {
        frame.setTitle(FAILURE + ((float)currentDurationMS() / 1000) + "s");
        for (int row = 0;row < ROWS;++row) {
            for (int col = 0;col < COLS;++col) {
                if (mines[row][col]) {
                    icons[row][col].setIcon(MINE_ICON);
                } else if (revealed[row][col]) {
                    icons[row][col].setIcon(LO_RED_ICON);
                } else {
                    icons[row][col].setIcon(HI_RED_ICON);
                }
            }
        }
    }

    /** Resets all board values, updates title, updates timer and starts running */
    private void reset() {
        flags = new boolean[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        mines = newRandomMines(ROWS, COLS, NUM_MINES);
        neighbors = minesGridToNeighborsGrid(mines);
        for (int row = 0;row < ROWS;++row) {
            for (int col = 0;col < COLS;++col) {
                icons[row][col].setIcon(HI_ICON);
            }
        }
        numberOfFlags = 0;
        frame.setTitle(INSTRUCTIONS);
        isRunning = true;
        tInit = System.currentTimeMillis();
    }

    /** Adds all icons to the panel */
    private void addJLabelsToPanel() {
        for (int row = 0;row < ROWS;++row) {
            for (int col = 0;col < COLS;++col) {
                panel.add(icons[row][col]);
            }
        }
    }

    /** Generates a new 2D array of JLabels using the provided dimensions */
    private static JLabel[][] newJLabelGrid(int rows, int cols) {
        JLabel[][] icons = new JLabel[rows][cols];
        for (int row = 0;row < rows;++row) {
            for (int col = 0;col < cols;++col) {
                icons[row][col] = new JLabel(HI_ICON);
                icons[row][col].setBounds(col * ICON_SIZE_PX, row * ICON_SIZE_PX, ICON_SIZE_PX, ICON_SIZE_PX);
            }
        }
        return icons;
    }

    /** Generates a new 2D array of booleans representing mines using the provided dimensions and number of mines */
    private static boolean[][] newRandomMines(int rows, int cols, int numMines) {
        int row, col;
        int placedMines = 0;
        boolean[][] mines = new boolean[rows][cols];
        while (placedMines < numMines) {
            row = (int) (Math.random() * rows);
            col = (int) (Math.random() * cols);
            if (!mines[row][col]) {
                mines[row][col] = true;
                ++placedMines;
            }
        }
        return mines;
    }

    /** Generates a new 2D array of ints representing the count of neighboring mines per cell provided a boolean[][] of mines */
    private static int[][] minesGridToNeighborsGrid(boolean[][] mines) {
        int[][] neighbors = new int[mines.length][mines[0].length];
        for (int row = 0;row < mines.length;++row) {
            for (int col = 0;col < mines[row].length;++col) {
                if (mines[row][col]) {
                    neighbors[row][col] = IS_MINE; // UNNECESSARY
                } else {
                    neighbors[row][col] = numberOfNeighboringMines(row, col, mines);
                }
            }
        }
        return neighbors;
    }

    /** Returns the int number of neighboring mines at a position within a boolean[][] of mines */
    private static int numberOfNeighboringMines(int row, int col, boolean[][] mines) {
        int neighbors = 0;
        for (int r = 0;r < mines.length;++r) {
            for (int c = 0;c < mines[row].length;++c) {
                if (isNeighbor(r, c, row, col) && mines[r][c]) {
                    ++neighbors;
                }
            }
        }
        return neighbors;
    }
    
    /** Returns the current title */
    private String currentTitleString() {
        return "Mines - Flags remaining: " + (NUM_MINES - numberOfFlags);
    }

    /** Returns the current duration of the game in milliseconds */
    private long currentDurationMS() {
        return System.currentTimeMillis() - tInit;
    }

    /** Returns boolean if provided indices are neighbors */
    private static boolean isNeighbor(int row1, int col1, int row2, int col2) {
        return (Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1) && (row1 != row2 || col1 != col2);
    }

    /** Returns boolean if provided mod matches CTRL mask */
    private static boolean isCtrlPressed(int mod) {
    	return (mod & InputEvent.CTRL_DOWN_MASK) != 0;
    }

    /** Returns boolean if provided mod matches ALT mask */
    private static boolean isAltPressed(int mod) {
    	return (mod & InputEvent.ALT_DOWN_MASK) != 0;
    }

}
