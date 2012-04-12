import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SolvingPanel extends JPanel {

    public static final Color BACKGROUND = Color.WHITE;
    private JPanel currentRow;
    private GridLayout gridLayout;

    public SolvingPanel() {
        setupUI();
    }

    public void setupUI() {
        gridLayout = new GridLayout(1, 1, 1, 5);
        setLayout(gridLayout);
        addRow();
    }

    public void addImage(BufferedImage image) {
        ImagePanel panel = new ImagePanel(image);
        panel.setBackground(BACKGROUND);
        currentRow.add(panel, LEFT_ALIGNMENT);
    }

    public void addRow() {
        gridLayout.setRows(gridLayout.getRows()+1);
        currentRow = new JPanel();
        currentRow.setBackground(BACKGROUND);
        BoxLayout layout = new BoxLayout(currentRow, BoxLayout.X_AXIS);
        currentRow.setLayout(layout);
        add(currentRow, LEFT_ALIGNMENT);
    }

    public void addConsequence(){
        try {
            addImage(ImageIO.read(new FileInputStream("img/eqArrow.gif")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
