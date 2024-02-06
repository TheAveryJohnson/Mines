import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>MinesOptions - Options menu for Mines</h1>
 * 
 * <h2>Provides difficulty selection functionality</h2>
 * 
 * <h3>Beginner:</h3>
 * <b>8 rows x 8 columns x 10 mines</b>
 * <h3>Intermediate:</h3>
 * <b>16 rows x 16 columns x 40 mines</b>
 * <h3>Advanced:</h3>
 * <b>16 rows x 24 columns x 60 mines</b>
 * <h3>Expert:</h3>
 * <b>24 rows x 48 columns x 180 mines</b>
 * 
 * @version 0.0.0.0
 * @author Avery Johnson
 */
public class MinesOptions {

	/* Window components */
	private JFrame frame = new JFrame("Mines - A mine clearing game");
	private ImagePanel panel = new ImagePanel("img\\options.png");

	/* Window Icon */
	private static final ImageIcon FLAG_ICON = new ImageIcon("img\\flag.png");

	/* Option Icons */
	private static final ImageIcon BEGINNER_ICON        = new ImageIcon("img\\beginner.png");
	private static final ImageIcon INTERMEDIATE_ICON    = new ImageIcon("img\\intermediate.png");
	private static final ImageIcon ADVANCED_ICON        = new ImageIcon("img\\advanced.png");
	private static final ImageIcon EXPERT_ICON          = new ImageIcon("img\\expert.png");

	/* Option Labels */
	private static JLabel BEGINNER_LABEL;
	private static JLabel INTERMEDIATE_LABEL;
	private static JLabel ADVANCED_LABEL;
	private static JLabel EXPERT_LABEL;

	/* Option Labels Bounds */
	private static final int OPTIONS_FIRST_ROW = 32;
	private static final int OPTIONS_FIRST_COL = 36;
	private static final int OPTIONS_SECOND_ROW = 80;
	private static final int OPTIONS_SECOND_COL = 200;

	/* Mines difficulty options */
	private Mines beginner		= new Mines(8, 8, 10);
	private Mines intermediate	= new Mines(16, 16, 40);
	private Mines advanced		= new Mines(16, 24, 60);
	private Mines expert		= new Mines(24, 48, 180);

	/**
	 * <h3>Constructs a MinesOptions</h3>
	 */
	public MinesOptions() {

		/* Initialize labels for different difficulties */
		BEGINNER_LABEL      = new JLabel(BEGINNER_ICON);
		INTERMEDIATE_LABEL  = new JLabel(INTERMEDIATE_ICON);
		ADVANCED_LABEL      = new JLabel(ADVANCED_ICON);
		EXPERT_LABEL        = new JLabel(EXPERT_ICON);
		
		/* Set label bounds */
		BEGINNER_LABEL      .setBounds(OPTIONS_FIRST_COL, OPTIONS_FIRST_ROW, BEGINNER_ICON.getImage().getWidth(null), BEGINNER_ICON.getImage().getHeight(null));
		INTERMEDIATE_LABEL  .setBounds(OPTIONS_FIRST_COL, OPTIONS_SECOND_ROW, INTERMEDIATE_ICON.getImage().getWidth(null), INTERMEDIATE_ICON.getImage().getHeight(null));
		ADVANCED_LABEL      .setBounds(OPTIONS_SECOND_COL, OPTIONS_FIRST_ROW, ADVANCED_ICON.getImage().getWidth(null), BEGINNER_ICON.getImage().getHeight(null));
		EXPERT_LABEL        .setBounds(OPTIONS_SECOND_COL, OPTIONS_SECOND_ROW, EXPERT_ICON.getImage().getWidth(null), BEGINNER_ICON.getImage().getHeight(null));

		/* Add labels to the panel */
		panel.add(BEGINNER_LABEL);
		panel.add(INTERMEDIATE_LABEL);
		panel.add(ADVANCED_LABEL);
		panel.add(EXPERT_LABEL);

		/* Add mouse listener */
		panel.addMouseListener(mouseAdapter);

		/* Configure the frame */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setIconImage(FLAG_ICON.getImage());
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * <h3>MouseAdapter to handle mouse input</h3>
	 */
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {

			/* Determine the position of the mouse when pressed */
			int row = (int) e.getPoint().getY();
			int col = (int) e.getPoint().getX();
			
			/* Determine which rows and columns have been selected */
			boolean clickedFirstRow     = (row > OPTIONS_FIRST_ROW && row < OPTIONS_FIRST_ROW + BEGINNER_ICON.getImage().getHeight(null));
			boolean clickedSecondRow    = (row > OPTIONS_SECOND_ROW && row < OPTIONS_SECOND_ROW + EXPERT_ICON.getImage().getHeight(null));
			boolean clickedFirstCol     = (col > OPTIONS_FIRST_COL && col < OPTIONS_FIRST_COL + BEGINNER_ICON.getImage().getWidth(null));
			boolean clickedSecondCol    = (col > OPTIONS_SECOND_COL && col < OPTIONS_SECOND_COL + EXPERT_ICON.getImage().getWidth(null));

			/* If the user has selected the first row */
			if (clickedFirstRow) {

				/* If the user has selected the first column */
				if (clickedFirstCol) { // top left / beginner
					frame.setVisible(false);
					frame			= null;
					panel			= null;
					intermediate	= null;
					advanced		= null;
					expert			= null;
					beginner		.start();
				}

				/* If the user has selected the second column */
				else if (clickedSecondCol) { // top right / advanced
					frame.setVisible(false);
					frame			= null;
					panel			= null;
					beginner		= null;
					intermediate	= null;
					expert			= null;
					advanced.start();
				}
			}
			
			/* If the user has selected the second row */
			else if (clickedSecondRow) {

				/* If the user has selected the first column */
				if (clickedFirstCol) { // bottom left / intermediate
					frame.setVisible(false);
					frame			= null;
					panel			= null;
					beginner		= null;
					advanced		= null;
					expert			= null;
					intermediate.start();
				}

				/* If the user has slected the second column */
				else if (clickedSecondCol) { // bottom right / expert
					frame.setVisible(false);
					frame			= null;
					panel			= null;
					beginner		= null;
					intermediate	= null;
					advanced		= null;
					expert.start();
				}
			}
		}
	};

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MinesOptions minesOptions = new MinesOptions();
	}
	
}
