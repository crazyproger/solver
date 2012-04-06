import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {

    private JButton bExit;
    private JButton bCalculate;
    private JTextArea taResult;
    private JTextArea taV;
    private JTextArea taU;
    private JTextArea y2IntegrateDTextArea;
    private JPanel rootPanel;
    private JButton θButton;
    private JButton ψButton;
    private JButton φButton;
    private JButton πButton;
    private JButton ωButton;
    private JButton ρButton;
    private JButton button11;
    private JButton button12;
    private JButton button13;
    private JButton button14;
    private JButton button15;
    private JButton button16;
    private JButton button17;
    private JButton sqrtButton;
    private JButton integralButton;
    private JButton button1;

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
        taResult.setText("");

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
