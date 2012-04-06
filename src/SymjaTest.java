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
//        solveExpr("Sum[D[(b[t]/i)*Cos[i*Q],t],{i,1,N}]");
//        solveExpr("D[(b[t]/i)*Cos[i*Q],t]");
//        solveExpr("D[Cos[x]^2,x]");
        solveExpr("Simplify[1/2*Y*(-4*M*i^(-1)*p*Cos[Q*i]*YY*Sin[Q*i]*YYY+4*M^2*p*r*Sin[Q*i]*YYY+4*M*p*YY*Sin[Q*i]^2*YYY+4*M*p*r*YY*Sin[Q*i]+2*M^2*p*Sin[Q*i]^2*YYY^2+2*M^2*i^(-2)*p*Cos[Q*i]^2*YYY^2+4*p*YY^2*Sin[Q*i]^2+2*M^2*p*r^2)]");
//        solveExpr("2*M^2*YYY^2*p*Sin[Q*i]^2+4*M*YY*YYY*p*Sin[Q*i]^2+4*YY^2*p*Sin[Q*i]^2+2*M^2*YYY^2*i^(-2)*p*Cos[Q*i]^2+2*M^2*p*r^2)");
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
            System.out.println("SolveExpr form for " + input + " is " + output);
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
//            String input = "Integrate[(10 x^2 - 63 x + 29)/(x^3 - 11 x^2 + 40 x -48),{x, p,2*p}]";
            String input = "(Y/2)* Integrate[((D[b[t],t] +M*r+M*d[t])^2+(D[d[t],t]-M*b[t])^2),{O, 0 ,2*p}]";
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
