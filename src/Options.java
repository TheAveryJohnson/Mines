import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * <h1>Options</h1>
 * <h3>An options screen made to function alongside Mines to allow the user to choose their preferred difficulty</h3>
 *
 * @since 2022-12-19
 * @version 0.0.0.1
 * @author Avery Johnson
 **/
public class Options {

    /* private static final Strings */
    private static final String OPTIONS = "Mines - Options";
    private static final String BEGINNER_BUTTON_TEXT = "Beginner";
    private static final String INTERMEDIATE_BUTTON_TEXT = "Intermediate";
    private static final String ADVANCED_BUTTON_TEXT = "Advanced";
    private static final String EXTREME_BUTTON_TEXT = "Extreme!";

    /* private static final ints */
    private static final int PANEL_WIDTH_PX = 384;
    private static final int PANEL_HEIGHT_PX = 144;
    private static final int BUTTON_WIDTH_PX = 156;
    private static final int BUTTON_HEIGHT_PX = 36;
    private static final int BUTTON_FIRST_ROW_PX = 30;
    private static final int BUTTON_SECOND_ROW_PX = 78;
    private static final int BUTTON_FIRST_COL_PX = 30;
    private static final int BUTTON_SECOND_COL_PX = 196;

    /* private static final button params */
    private static final Font FONT = new Font("Dialog", Font.BOLD, 20);
    private static final BevelBorder BORDER = new BevelBorder(BevelBorder.RAISED);

    /* private static final Colors */
    private static final Color BEGINNER_BUTTON_COLOR = new Color(115, 115, 115);
    private static final Color INTERMEDIATE_BUTTON_COLOR = new Color(44, 155, 63);
    private static final Color ADVANCED_BUTTON_COLOR = new Color(194, 100, 100);
    private static final Color EXTREME_BUTTON_COLOR = new Color(200, 50, 50);

    /* private swing components */
    private final JFrame frame = new JFrame(OPTIONS);
    private final BackgroundPanel panel = new BackgroundPanel("img/options.png");
    private final JButton beginnerButton = new JButton(BEGINNER_BUTTON_TEXT);
    private final JButton intermediateButton = new JButton(INTERMEDIATE_BUTTON_TEXT);
    private final JButton advancedButton = new JButton(ADVANCED_BUTTON_TEXT);
    private final JButton extremeButton = new JButton(EXTREME_BUTTON_TEXT);

    /* private Mines difficulty options */
    private Mines beginner = new Mines(8, 8, 12);
    private Mines intermediate = new Mines(16, 16, 50);
    private Mines advanced = new Mines(16, 24, 72);
    private Mines extreme = new Mines(24, 48, 200);

    /**
     * <h3>Constructs an Options window for Mines and displays it to the screen</h3>
     */
    public Options() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setIconImage(Mines.FLAG_ICON.getImage());
        panel.setPreferredSize(new Dimension(PANEL_WIDTH_PX, PANEL_HEIGHT_PX));
        setButton(beginnerButton, BUTTON_FIRST_ROW_PX, BUTTON_FIRST_COL_PX, BEGINNER_BUTTON_COLOR);
        setButton(intermediateButton, BUTTON_FIRST_ROW_PX, BUTTON_SECOND_COL_PX, INTERMEDIATE_BUTTON_COLOR);
        setButton(advancedButton, BUTTON_SECOND_ROW_PX, BUTTON_FIRST_COL_PX, ADVANCED_BUTTON_COLOR);
        setButton(extremeButton, BUTTON_SECOND_ROW_PX, BUTTON_SECOND_COL_PX, EXTREME_BUTTON_COLOR);
        panel.add(beginnerButton);
        panel.add(intermediateButton);
        panel.add(advancedButton);
        panel.add(extremeButton);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** Sets the provided buttons position, size, and color */
    private void setButton(JButton button, int row, int col, Color color) {
        button.setFocusable(false);
        button.setBorder(BORDER);
        button.setFont(FONT);
        button.setForeground(Color.BLACK);
        button.setBackground(color);
        button.addActionListener(actionListener);
        button.setBounds(col, row, Options.BUTTON_WIDTH_PX, Options.BUTTON_HEIGHT_PX);
    }

    /** The ActionListener used by each button */
    private final ActionListener actionListener = e -> {
        frame.setVisible(false);
        JButton source = (JButton) e.getSource();
        switch (source.getText()) {
            case BEGINNER_BUTTON_TEXT -> {
                beginner.start();
                intermediate = null;
                advanced = null;
                extreme = null;
            }
            case INTERMEDIATE_BUTTON_TEXT -> {
                beginner = null;
                intermediate.start();
                advanced = null;
                extreme = null;
            }
            case ADVANCED_BUTTON_TEXT -> {
                beginner = null;
                intermediate = null;
                advanced.start();
                extreme = null;
            }
            case EXTREME_BUTTON_TEXT -> {
                beginner = null;
                intermediate = null;
                advanced = null;
                extreme.start();
            }
        }
    };

    public static void main(String[] args) {
        new Options();
    }

}