import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * <h1>BackgroundPanel</h1>
 * <h3>An extension of JPanel that allows the user to set a background image</h3>
 *
 * <h2>Constructors</h2>
 * <b>BackgroundPanel(String filename) : </b><i>Constructs an BackgroundPanel using the file at the provided filename</i><br><br>
 * <b>BackgroundPanel(Image image) : </b><i>Constructs an BackgroundPanel using the provided Image</i><br><br>
 * <b>Important! : </b><i>When constructed, this class sets the preferred size of this panel to be the same dimensions of the image</i><br><br>
 *
 * @since 2022-12-19
 * @version 0.0.0.1
 * @author Avery Johnson
 */
public class BackgroundPanel extends JPanel {

    /** Holds the Image of this BackgroundPanel */
    private Image image;

    /**
     * <h3>Constructs an BackgroundPanel using the provided filename</h3>
     * @param filename : The String address of the file
     */
    public BackgroundPanel(String filename) {
        this(new ImageIcon(filename).getImage());
    }

    /**
     * <h3>Constructs an BackgroundPanel using the provided image</h3>
     * @param image : The Image provided
     */
    public BackgroundPanel(Image image) {
        setLayout(null);
        setImage(image);
    }

    /**
     * <h3>Sets the background image as well as adjusts the size of this BackgroundPanel</h3>
     * @param image : The Image provided
     */
    public void setImage(Image image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

}