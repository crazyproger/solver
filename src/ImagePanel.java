import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel(String pathname) {
        try {
            image = ImageIO.read(new File(pathname));
            setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
        } catch (IOException ex) {
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        g.drawImage(image, 0, 0, null);
    }
}