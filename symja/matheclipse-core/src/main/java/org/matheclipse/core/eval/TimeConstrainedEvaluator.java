package org.matheclipse.core.eval;

import com.google.common.base.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.TimeExceeded;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

import javax.annotation.Nullable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Run the evaluation of a given math formula <code>String</code> in a time
 * limited thread
 */
public class TimeConstrainedEvaluator extends EvalUtilities implements Runnable {

    protected IExpr fEvaluationResult;

    protected Throwable fException;

    protected IExpr fParsedExpression;

    private long fMilliSeconds;

    private final boolean fRelaxedSyntax;

    public boolean fTraceEvaluation;

    public TimeConstrainedEvaluator(final EvalEngine evalEngine, final boolean msie, final long milliSeconds) {
        this(evalEngine, msie, milliSeconds, false);

    }

    public TimeConstrainedEvaluator(final EvalEngine evalEngine, final boolean msie, final long milliSeconds, boolean relaxedSyntax) {
        super(evalEngine, msie);
        fMilliSeconds = milliSeconds;
        fRelaxedSyntax = relaxedSyntax;
        fTraceEvaluation = false;
    }

    public void run() {
        try {
            startRequest();
            if (fTraceEvaluation) {
                Predicate<IExpr> matcher = new Predicate<IExpr>() {
                    private List<IExpr> badSymbols = Arrays.asList(new IExpr[]{F.Derivative, F.Function, F.Expand,
                            F.ExpandAll, F.FreeQ, F.Less, F.LessEqual, F.Greater, F.GreaterEqual, F.True, F.False});

                    @Override
                    public boolean apply(@Nullable IExpr iExpr) {
                        if (iExpr == null) {
                            return false;
                        }
                        if (iExpr.isAST()) {
                            AST expr = (AST) iExpr;
                            for (int i = 0; i < expr.size(); i++) {
                                IExpr part = expr.get(i);
                                if (part != null && part.isSymbol()) {
                                    Symbol symbol = ((Symbol) part);
                                    if (badSymbols.contains(symbol)) {
                                        return false;
                                    }
                                }
                                if (part.isAST()) {
                                    if (!apply(part)) {
                                        return false;
                                    }
                                }
                            }

                        }
                        return true;
                    }
                };
                fEvaluationResult = evalTrace(fParsedExpression, matcher, F.List());
                fEvaluationResult = removeSmallLists(fEvaluationResult);
            } else {
                fEvaluationResult = evaluate(fParsedExpression);
            }
        } catch (final Exception e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
            fException = e;
        } catch (final OutOfMemoryError e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
            fEvaluationResult = F.stringx("OutOfMemoryError");
            // fException = e;
        } catch (final StackOverflowError e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }
            fEvaluationResult = F.stringx("StackOverflowError");
            // fException = e;
        }
    }

    /**
     * Удаляем списки с одной конструкцией(они не имеют смысла в выводе)
     *
     * @param list
     * @return
     */
    private IExpr removeSmallLists(IExpr list) {
        AST result = new AST();
        if (!list.isList()) {
            return list;
        }
        AST ast = (AST) list;
        ArrayList<Integer> toDelete = new ArrayList<>();
        for (int i = 0; i < ast.size(); i++) {
            IExpr iExpr = ast.get(i);
            if (iExpr.isAST()) {
                AST el = (AST) iExpr;
                if (el.size() == 2 && el.head().equals(F.List) && !el.get(1).isList()) {
                    toDelete.add(i);
                } else if (el.isList()) {
                    addToAST(result, removeSmallLists(el));
                } else {
                    addToAST(result, iExpr);
                }
            } else {
                addToAST(result, iExpr);
            }
        }
        return result;
    }

    private boolean addToAST(AST result, IExpr el) {
        if (result.get(0) == null) {
            result.set(0, el);
            return true;
        }
        return result.add(el);
    }

    /**
     * Runs the evaluation of the given math formula <code>String</code> in a time
     * limited thread
     *
     * @param traceEvaluation
     */
    public IExpr constrainedEval(final Writer writer, final String inputString, boolean traceEvaluation) throws Exception {

        fEvaluationResult = null;
        fException = null;
        fParsedExpression = null;
        fEvalEngine.setStopRequested(false);
        fTraceEvaluation = traceEvaluation;

        try {
            EvalEngine.set(fEvalEngine);
            fParsedExpression = fEvalEngine.parse(inputString);
        } catch (final RuntimeException e) {
            throw e;
        }

        return constrainedEval(writer, fParsedExpression);

    }

    /**
     * Runs the evaluation of the given math expression in a time limited thread
     */
    public IExpr constrainedEval(final Writer writer, final IExpr inputExpression) throws Exception {

        fEvaluationResult = null;
        fException = null;
        fParsedExpression = inputExpression;
        fEvalEngine.setStopRequested(false);

        try {
            final Thread thread = new Thread(this, "TimeConstrainedEvaluator");// EvaluationRunnable();
            thread.start();
            thread.join(fMilliSeconds);
            if (thread.isAlive()) {
                thread.interrupt();
                fEvalEngine.stopRequest();
                // wait a bit, so the thread can stop by itself
                Thread.sleep(Config.TIME_CONSTRAINED_SLEEP_MILLISECONDS);
                if (thread.isAlive()) {
                    // call the deprecated method as last possible exit
                    thread.stop();
                    throw new TimeExceeded();
                }
            }
            if (fException != null) {
                writer.write(fException.getMessage() != null ? fException.getMessage() : "Exception: " + fException.getClass().getName());
                writer.write('\n');
            }

            if ((fEvaluationResult != null) && !fEvaluationResult.equals(F.Null)) {
                OutputFormFactory.get(fRelaxedSyntax).convert(writer, fEvaluationResult);
            }
            return fEvaluationResult;
        } catch (final Exception e) {
            throw e;
        }
    }

    /**
     * Get the parsed expression after calling the <code>constrainedEval()</code>
     * method
     *
     * @return the parsed expression; may return <ode>null</code>
     */
    public IExpr getParsedExpression() {
        return fParsedExpression;
    }

}