import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXEnvironment;

import javax.swing.*;

public class EditorTest {

    public static void main(String[] args) {
        setLAF();
        F.initSymbols(null, null, false);
        Application.EVAL_ENGINE = new EvalEngine();
        Application.EVAL = new TimeConstrainedEvaluator(Application.EVAL_ENGINE, false, 360000);
        // for faster initialization of pretty print output we create a dummy
        // instance here:
        new TeXEnvironment(TeXConstants.STYLE_DISPLAY, new DefaultTeXFont(16));
        EquationEditor equationEditor = new EquationEditor("A", "Sin[x]", new EquationEditor.ResultListener() {
            @Override
            public void onOk(String text) {
                JOptionPane.showMessageDialog(null, text, "onOk", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onCancel() {
                JOptionPane.showMessageDialog(null, "onCancel");
            }
        });
        equationEditor.showMe();
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
