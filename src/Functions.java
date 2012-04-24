import com.google.common.base.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IExpr;

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
}
