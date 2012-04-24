import com.google.common.base.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import java.util.Map;

public class Functions {
    public static final Function<IExpr, IExpr> B_FIRST_DERIV = new Function<IExpr, IExpr>() {
        @Override
        public IExpr apply(IExpr iExpr) {
            if (iExpr.isSymbol()) {
                if (iExpr.isSame(new Symbol("bzzvi"))) {
                    return new Symbol(Application.DDTB_VI);
                } else if (iExpr.isSame(new Symbol("bzzui"))) {
                    return new Symbol(Application.DDTB_UI);
                }
            }
            return iExpr;
        }
    };
    public static final Function<IExpr, IExpr> I_TO_J_FUNCTION = new Function<IExpr, IExpr>() {
        @Override
        public IExpr apply(IExpr iExpr) {
            if (iExpr.isSymbol()) {
                Symbol symbol = (Symbol) iExpr;
                String str = symbol.getSymbol();
                if (str.equals("i")) {
                    return new Symbol("j");
                } else if (str.matches("\\w+zz\\w*i\\w*")) {
                    return new Symbol(str.replaceAll("i", "j"));
                }
            }
            return iExpr;
        }
    };
    public static final Function<IExpr, IExpr> REMOVE_SUM_FUNCTION = new Function<IExpr, IExpr>() {
        @Override
        public IExpr apply(IExpr iExpr) {
            if (F.Sum.isSame(iExpr.head())) {
                return iExpr.getAt(1);
            }
            return iExpr;
        }
    };

    public static class ChangeIntegralIntervalFunction implements Function<IExpr, IExpr> {
        private final IExpr leftLimit;
        private final IExpr rightLimit;

        public ChangeIntegralIntervalFunction(IExpr leftLimit, IExpr rightLimit) {
            this.leftLimit = leftLimit;
            this.rightLimit = rightLimit;
        }

        @Override
        public IExpr apply(IExpr iExpr) {
            if (F.Integrate.isSame(iExpr.head()) && F.List.isSame(iExpr.getAt(2).head())) {
                IAST integralParams = F.List(iExpr.getAt(2).getAt(1), leftLimit, rightLimit);
                return F.Integrate(iExpr.getAt(1), integralParams);
            }
            return iExpr;
        }
    }

    public static class MemoryReplaceFunction implements Function<IExpr, IExpr> {
        private final Map<IExpr, IExpr> memory;

        public MemoryReplaceFunction(Map<IExpr, IExpr> memory) {
            this.memory = memory;
        }

        @Override
        public IExpr apply(IExpr iExpr) {
            if (memory.containsKey(iExpr)) {
                return memory.get(iExpr);
            }
            return iExpr;
        }
    }

    public static class FunctionToSymbol implements Function<IExpr, IExpr> {
        private final Symbol diffSubject;

        public FunctionToSymbol(Symbol diffSubject) {
            this.diffSubject = diffSubject;
        }

        @Override
        public IExpr apply(IExpr iExpr) {
            if (iExpr.isAST()) {
                if ((Application.U.isSame(iExpr.head()) || Application.V.isSame(iExpr.head()) || Application.DDT_U.isSame(iExpr.head()) || Application.DDT_V.isSame(iExpr.head())) && diffSubject.isSame(iExpr.getAt(1))) {
                    return iExpr.head();
                }
            }
            return iExpr;
        }
    }

    public static class SymbolToFunction implements Function<IExpr, IExpr> {

        private final Symbol diffSubject;

        public SymbolToFunction(Symbol diffSubject) {
            this.diffSubject = diffSubject;
        }

        @Override
        public IExpr apply(IExpr iExpr) {
            if (iExpr.isSymbol()) {
                if (Application.U.isSame(iExpr) || Application.V.isSame(iExpr) || Application.DDT_U.isSame(iExpr) || Application.DDT_V.isSame(iExpr)) {
                    IAST ast = F.ast(iExpr);
                    ast.add(diffSubject);
                    return ast;
                }
            }
            return iExpr;
        }
    }
}
