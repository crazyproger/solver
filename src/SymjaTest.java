import edu.jas.kern.ComputerThreads;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class SymjaTest {
    public static void main(String[] args) {
//        someExample();
//        integration();
//        solveExpr("D[(10 x^2 - 63 x + 29)/(x^3 - 11 x^2 + 40 x -48),x]");
        solveExpr("D[Sin[pi]+Cos[y],pi]");
//        solveExpr("D[Cos[x]^2,x]");
    }

    private static void someExample() {
        // Static initialization of the MathEclipse engine instead of null
        // you can set a file name to overload the default initial
        // rules. This step should be called only once at program setup:
        F.initSymbols(null);
        EvalUtilities util = new EvalUtilities();

        IExpr result;

        try {
            StringBufferWriter buf = new StringBufferWriter();
            String input = "Expand[(20*x-63)*(x^3-11*x^2+40*x-48)^(-1)-(10*x^2-63*x+29)*(3*x^2-22*x+40)*(x^3-11*x^2+40*x-48)^(-2)]";
            result = util.evaluate(input);
            OutputFormFactory.get().convert(buf, result);
            String output = buf.toString();
            System.out.println("Expanded form for " + input + " is " + output);

            // set some variable values
            input = "A=2;B=4";
            result = util.evaluate(input);

//            buf = new StringBufferWriter();
//            input = "Expand[(A*X^2+B*X)^2]";
//            result = util.evaluate(input);
//            OutputFormFactory.get().convert(buf, result);
//            output = buf.toString();
//            System.out.println("Expanded form for " + input + " is " + output);

            buf = new StringBufferWriter();
            input = "Factor[" + output + "]";
            result = util.evaluate(input);
            OutputFormFactory.get().convert(buf, result);
            output = buf.toString();
            System.out.println("Factored form for " + input + " is " + output);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            // Call terminate() only one time at the end of the program
            ComputerThreads.terminate();
        }
    }

    private static void solveExpr(String expr) {
                // Static initialization of the MathEclipse engine instead of null
        // you can set a file name to overload the default initial
        // rules. This step should be called only once at program setup:
        F.initSymbols(null);
        EvalUtilities util = new EvalUtilities();

        IExpr result;

        try {
            StringBufferWriter buf = new StringBufferWriter();
            String input = expr;
            result = util.evaluate(input);
            OutputFormFactory.get().convert(buf, result);
            String output = buf.toString();
            System.out.println("Differentiate form for " + input + " is " + output);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            // Call terminate() only one time at the end of the program
            ComputerThreads.terminate();
        }
    }

    private static void integration() {
        // Static initialization of the MathEclipse engine instead of null
        // you can set a file name to overload the default initial
        // rules. This step should be called only once at program setup:
        F.initSymbols(null);
        EvalEngine engine = new EvalEngine();
        EvalUtilities util = new EvalUtilities(engine,false);

        IExpr result;

        try {
            StringBufferWriter buf = new StringBufferWriter();
            String input = "Integrate[(10 x^2 - 63 x + 29)/(x^3 - 11 x^2 + 40 x -48),x]";
            result = util.evaluate(input);
            OutputFormFactory.get().convert(buf, result);
            String output = buf.toString();
            System.out.println("Integrated form for " + input + " is " + output);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            // Call terminate() only one time at the end of the program
            ComputerThreads.terminate();
        }

    }
}
