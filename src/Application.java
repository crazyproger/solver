import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {

    private JButton bExit;
    private JButton bCalculate;
    private JTextArea taResult;
    private JTextArea textArea2;
    private JTextArea textArea3;
    private JTextArea textArea4;
    private JPanel rootPanel;

    public Application() {
        bExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
    }

    private void calculate() {
        // todo make
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLAF();
                Application app = new Application();
                JFrame frame = new JFrame("Solver by Z. Sergei");
                frame.setContentPane(app.getRootPanel());
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private static void setLAF() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(255);
        }
    }
}
