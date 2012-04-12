import edu.jas.kern.ComputerThreads;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {

    private void calculate() {
//        taResult.setText("");

        String uText = "";//taU.getText();
        String vText = "";//taV.getText();
        String master = "";//taT.getText();
        String sumStr = "Sum(i,1,N)";
        boolean wasSum = false;
        if (uText.startsWith(sumStr)) {
            wasSum = true;
            uText = uText.substring(sumStr.length());
            vText = vText.substring(sumStr.length());
        }
        String firstDU = solveExpr("D[" + uText + ",t]");
        addToResult("dU/dt", wasSum ? sumStr + firstDU : firstDU);
        System.out.println("firstDU = " + firstDU);
        String firstDV = solveExpr("D[" + uText + ",t]");
        addToResult("dV/dt", wasSum ? sumStr + firstDV : firstDV);
        System.out.println("firstDV = " + firstDV);

        System.out.println("master before replace = " + master);
        master = master.replaceAll("D\\[V,t\\]", "(" + firstDV + ")");
        master = master.replaceAll("D\\[U,t\\]", "(" + firstDU + ")");
        System.out.println("master after D replace = " + master);
        master = master.replaceAll("V", "(" + vText + ")");
        master = master.replaceAll("U", "(" + uText + ")");
        System.out.println("master after all replace = " + master);
        String tResult = solveExpr(master);
        tResult = tResult.replaceAll("D\\[b\\[t\\],t\\]", "b`");

        String result = tResult;
        if (wasSum) {
            result = tResult.replaceAll("b", sumStr + "b");
        }
        addToResult("Взят интеграл от T", result);
    }

    private void addToResult(String title, String result) {
    }

    private static String solveExpr(String input) {
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
}
