import edu.jas.kern.ComputerThreads;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {

    private JButton bExit;
    private JButton bCalculate;
    private JTextArea taResult;
    private JTextArea taV;
    private JTextArea taU;
    private JTextArea taT;
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

        String uText = taU.getText();
        String vText = taV.getText();
        String master = taT.getText();
        String sumStr = "Sum(i,1,N)";
        boolean wasSum = false;
        if (uText.startsWith(sumStr)) {
            wasSum = true;
            uText = uText.substring(sumStr.length());
            vText = vText.substring(sumStr.length());
        }
        String firstDU = solveExpr("D["+uText+",t]");
        System.out.println("firstDU = " + firstDU);
        String firstDV = solveExpr("D["+uText+",t]");
        System.out.println("firstDV = " + firstDV);

        System.out.println("master before replace = " + master);
        master = master.replaceAll("D\\[V,t\\]","("+firstDV+")");
        master = master.replaceAll("D\\[U,t\\]","("+firstDU+")");
        System.out.println("master after D replace = " + master);
        master = master.replaceAll("V","("+vText+")");
        master = master.replaceAll("U","("+uText+")" );
        System.out.println("master after all replace = " + master);
        String result = solveExpr(master);
        result = result.replaceAll("D\\[b\\[t\\],t\\]", "b`");
        taResult.setText(result);
    }
    private static String  solveExpr(String input) {
                    // Static initialization of the MathEclipse engine instead of null
            // you can set a file name to overload the default initial
            // rules. This step should be called only once at program setup:
            F.initSymbols(null);
            EvalUtilities util = new EvalUtilities();

            IExpr result;

            try {
                StringBufferWriter buf = new StringBufferWriter();
                result = util.evaluate(input);
                OutputFormFactory.get().convert(buf, result);
                String output = buf.toString();
                System.out.println("Differentiate form for " + input + " is " + output);
                return output;
            } catch (final Exception e) {
                e.printStackTrace();
                return "ERROR";
            } finally {
                // Call terminate() only one time at the end of the program
                ComputerThreads.terminate();
            }
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
