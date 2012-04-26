import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private Icon icon;

    public ImagePanel(String pathname) {
        try {
            image = ImageIO.read(new File(FilenameUtils.separatorsToSystem(pathname)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setSize();
    }

    public ImagePanel(Icon icon) {
        this.icon = icon;
        setSize();
    }

    public ImagePanel(BufferedImage image) {
        this.image = image;
        setSize();
    }

    private void setSize() {
        if (image != null) {
            setMinimumSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
            setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
            setMaximumSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
        } else {
            setMinimumSize(new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight()));
            setPreferredSize(new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight()));
            setMaximumSize(new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight()));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        } else {
            icon.paintIcon(this, g, 0, 0);
        }
    }
}