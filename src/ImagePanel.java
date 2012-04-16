import org.apache.commons.io.FilenameUtils;

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
            image = ImageIO.read(new File(FilenameUtils.separatorsToSystem(pathname)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSize();
    }

    public ImagePanel(BufferedImage image) {
        this.image = image;
        setSize();
    }

    private void setSize() {
        setMinimumSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
        setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
        setMaximumSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}