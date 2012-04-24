import com.google.common.base.Function;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.interfaces.IExpr;

public class ASTUtils {
    @SafeVarargs
    public static final IExpr transforms(IExpr srcExpr, Function<IExpr, IExpr>... functions) {
        IExpr result = srcExpr;
        for (Function<IExpr, IExpr> function : functions) {
            result = transform(result, function);
        }
        return result;
    }

    public static IExpr transform(IExpr srcExpr, Function<IExpr, IExpr> function) {
        IExpr processed = function.apply(srcExpr);
        if (processed.isAST()) {
            AST result = new AST();
            result.set(0, processed.getAt(0));
            for (IExpr expr : (AST) processed) {
                if (expr != null) {
                    IExpr filtered = transform(expr, function);
                    if (filtered != null) {
                        result.add(filtered);
                    }
                }
            }
            return result;
        } else {
            return processed;
        }
    }
}
