import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application2 {
    private JButton bExit;
    private JButton bCalculate;
    private JPanel lagrangePanel;
    private JPanel rootPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel x02aPanel;
    private JPanel x2a2pPanel;
    private JTextField tfLeftU;
    private JTextField tfRightU;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JTextField textField11;
    private JTextField textField12;
    private JPanel a1ImgPanel;
    private JPanel k10ImgPanel;
    private JPanel k11ImgPanel;
    private JPanel a2ImgPanel;
    private JPanel k20ImgPanel;
    private JPanel k21ImgPanel;

    public Application2() {
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
        //To change body of created methods use File | Settings | File Templates.
    }

    public static void main(String[] args) {
        setLAF();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Application2 app = new Application2();
                JFrame frame = new JFrame("Solver by Z. Sergei");
                frame.setContentPane(app.getRootPanel());
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }


    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void createUIComponents() {
        lagrangePanel = new ImagePanel("img/lagrange2.gif");
        x02aPanel = new ImagePanel("img/x02a.gif");
        x2a2pPanel = new ImagePanel("img/x2a2p.gif");
        a1ImgPanel = new ImagePanel("img/A1eq.gif");
        a2ImgPanel = new ImagePanel("img/A2eq.gif");
        k10ImgPanel = new ImagePanel("img/K10eq.gif");
        k11ImgPanel = new ImagePanel("img/K11eq.gif");
        k20ImgPanel = new ImagePanel("img/K20eq.gif");
        k21ImgPanel = new ImagePanel("img/K21eq.gif");
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
