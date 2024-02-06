import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Image; 
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * <h3>ImagePanel - An extension of JPanel that allows the user to set a background image</h3>
 * 
 * <p>When constructed, this class sets the preferred size of this panel to be the same dimensions of the provided image</p>
 * 
 * @version 0.0.0.0
 * @author Avery Johnson
 */
public class ImagePanel extends JPanel {

	/* Holds the Image of this ImagePanel */
	private Image image;

	/**
	 * <h3>Constructs an ImagePanel from the provided filename</h3>
     * 
	 * @param filename : The address of the image
	 */
	public ImagePanel(String filename) {
		this(new ImageIcon(filename).getImage());
	}

	/**
	 * <h3>Constructs an ImagePanel from the provided image</h3>
     * 
	 * @param image : The provided image
	 */
	public ImagePanel(Image image) {
		setLayout(null);
		setImage(image);
	}

	/**
	 * <h3>Sets the background image as well as adjusts the size of this ImagePanel</h3>
     * 
	 * @param image : The provided image
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
