import com.google.common.base.Function;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.util.WriterOutputStream;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXEnvironment;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Application {

    public static final String DDTB_UI = "ddtbzzui";
    public static final String DDTB_VI = "ddtbzzvi";
    public static final Symbol DDTB_UJ = new Symbol("ddtbzzuj");
    public static final Symbol DDTB_VJ = new Symbol("ddtbzzvj");

    public static final Symbol B_UI = new Symbol("bzzui");
    public static final Symbol B_VI = new Symbol("bzzvi");

    public static final Symbol U = new Symbol("U");
    public static final Symbol V = new Symbol("V");
    public static final Symbol DDT_U = new Symbol("ddtUzz");
    public static final Symbol DDT_V = new Symbol("ddtVzz");

    public static final Symbol THETA = new Symbol("THeta");
    public static final Symbol ALPHA = new Symbol("alpha");

    private JButton bExit;
    private JButton bCalculate;
    private JPanel lagrangePanel;
    private JPanel rootPanel;
    private JTextField tfT;
    private JTextField tfP;
    private JPanel x02aPanel;
    private JPanel x2a2pPanel;
    private JTextField tfLeftU;
    private JTextField tfRightU;
    private JTextField tfRightV;
    private JTextField tfA2;
    private JTextField tfK20;
    private JTextField tfK21;
    private JTextField tfK11;
    private JTextField tfK10;
    private JTextField tfA1;
    private JTextField tfLeftV;
    private JPanel a1ImgPanel;
    private JPanel k10ImgPanel;
    private JPanel k11ImgPanel;
    private JPanel a2ImgPanel;
    private JPanel k20ImgPanel;
    private JPanel k21ImgPanel;
    private JPanel pLeftOutput;
    private JPanel pRightOutput;
    private JPanel pCenterOutput;
    private JPanel pLeft;
    private JPanel pRight;
    private JPanel qLeftImgPanel;
    private JPanel qRightImgPanel;
    private JTextField tfQLeft;
    private JTextField tfQRight;
    private JTextField tfTest;
    private SolvingPanel spLeft;
    private SolvingPanel spRight;
    private SolvingPanel spCenter;
    private static JFrame frame;
    private IndexedFocusTraversalPolicy traversalPolicy;
    public static EvalEngine EVAL_ENGINE;
    private TimeConstrainedEvaluator EVAL;
    public static final float FONT_SIZE_TEX = 24;

    public static Map<String, String> inputReplacements = new LinkedHashMap<>();
    public static Map<String, String> texReplacements = new LinkedHashMap<>();
    public Map<String, IExpr> globalMemory = new HashMap<>();


    public Application() {
        $$$setupUI$$$();
        bExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    calculate();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        setupTraversalPolicy();

        initEngine();

        inputReplacements.put("b_ui", "bzzui");
        inputReplacements.put("b_ui", "bzzui");
        inputReplacements.put("b_vi", "bzzvi");
        inputReplacements.put("b_vi", "bzzvi");
        inputReplacements.put("D\\[V,t\\]", "ddtVzz");
        inputReplacements.put("D\\[U,t\\]", "ddtUzz");
        inputReplacements.put("Q", "THeta");

        texReplacements.put("ddt(\\wzz\\w*)", "\\\\dot{$1}");
        texReplacements.put("Uzz", "U");
        texReplacements.put("Vzz", "V");
        texReplacements.put("bzzvi", "b_\\{vi\\}");
        texReplacements.put("bzzui", "b_\\{ui\\}");
        texReplacements.put("bzzvj", "b_\\{vj\\}");
        texReplacements.put("bzzuj", "b_\\{uj\\}");
        texReplacements.put("OM", "\\\\Omega");
        texReplacements.put("pi", "\\\\pi");
        texReplacements.put("PSI", "\\\\Psi");
        texReplacements.put("nu", "\\\\nu");
        texReplacements.put("THeta", "\\\\Theta");

        try {
            calculate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEngine() {
        F.initSymbols(null, null, false);
        EVAL_ENGINE = new EvalEngine();
        EVAL = new TimeConstrainedEvaluator(EVAL_ENGINE, false, 360000);
        // for faster initialization of pretty print output we create a dummy
        // instance here:
        new TeXEnvironment(TeXConstants.STYLE_DISPLAY, new DefaultTeXFont(16));
    }

    private void setupTraversalPolicy() {
        traversalPolicy = new IndexedFocusTraversalPolicy();
        traversalPolicy.addIndexedComponent(tfT);
        traversalPolicy.addIndexedComponent(tfP);
        traversalPolicy.addIndexedComponent(tfLeftU);
        traversalPolicy.addIndexedComponent(tfLeftV);
        traversalPolicy.addIndexedComponent(tfA1);
        traversalPolicy.addIndexedComponent(tfK10);
        traversalPolicy.addIndexedComponent(tfK11);
        traversalPolicy.addIndexedComponent(tfRightU);
        traversalPolicy.addIndexedComponent(tfRightV);
        traversalPolicy.addIndexedComponent(tfA2);
        traversalPolicy.addIndexedComponent(tfK20);
        traversalPolicy.addIndexedComponent(tfK21);
        traversalPolicy.addIndexedComponent(bCalculate);
        traversalPolicy.addIndexedComponent(bExit);
    }

    private void calculate() throws Exception {

        spCenter.removeAll();
        spLeft.removeAll();
        spRight.removeAll();

        final StringBufferWriter printBuffer = new StringBufferWriter();
        final PrintStream pout = new PrintStream(new WriterOutputStream(printBuffer));
        EVAL_ENGINE.setOutPrintStream(pout);
        final StringBufferWriter buf0 = new StringBufferWriter();

        processLeftPanel();
        processRightPanel();

        // use evalTrace method
//        String testExpr = tfTest.getText();
//        final IExpr expr = EVAL.constrainedEval(buf0, testExpr, true);
//        render(spCenter, expr);

        System.out.println(buf0.toString());
    }

    private void processLeftPanel() {
        EvalEngine.set(EVAL_ENGINE);
        Map<IExpr, IExpr> memory = new HashMap<>();

        IExpr uExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfLeftU.getText()));
        memory.put(U, uExpr);
        TeXIcon teXIcon = TexUtils.getIcon("U=", uExpr);
        spLeft.addIconRow(teXIcon);
        IExpr vExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfLeftV.getText()));
        memory.put(V, vExpr);
        teXIcon = TexUtils.getIcon("V=", vExpr);
        spLeft.addIconRow(teXIcon);

        // differentiations
        IExpr ddtV = ASTUtils.transform(vExpr, Functions.B_FIRST_DERIV);
        teXIcon = TexUtils.getIcon("ddtVzz=", ddtV);
        memory.put(DDT_V, ddtV);
        spLeft.addIconRow(teXIcon);

        IExpr ddtU = ASTUtils.transform(uExpr, Functions.B_FIRST_DERIV);
        teXIcon = TexUtils.getIcon("ddtUzz=", ddtU);
        memory.put(DDT_U, ddtU);
        spLeft.addIconRow(teXIcon);

        // partial differentiations
        IExpr ddtUieqj = ASTUtils.transforms(ddtU, Functions.REMOVE_SUM_FUNCTION, Functions.I_TO_J_FUNCTION);
        IExpr ddtbujddtU = F.eval(F.D, ddtUieqj, DDTB_UJ);
        memory.put(F.D(DDT_U, DDTB_UJ), ddtbujddtU);
        IExpr ddtbvjddtU = F.eval(F.D, ddtUieqj, DDTB_VJ);
        memory.put(F.D(DDT_U, DDTB_VJ), ddtbvjddtU);
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{ddtUzz}}{\\partial{ddtbzzuj}}=", ddtbujddtU));
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{ddtUzz}}{\\partial{ddtbzzvj}}=", ddtbvjddtU));

        IExpr ddtVieqj = ASTUtils.transforms(ddtV, Functions.REMOVE_SUM_FUNCTION, Functions.I_TO_J_FUNCTION);
        IExpr ddtbujddtV = F.eval(F.D, ddtVieqj, DDTB_UJ);
        memory.put(F.D(DDT_V, DDTB_UJ), ddtbujddtV);
        IExpr ddtbvjddtV = F.eval(F.D, ddtVieqj, DDTB_VJ);
        memory.put(F.D(DDT_V, DDTB_VJ), ddtbvjddtV);
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{ddtVzz}}{\\partial{ddtbzzuj}}=", ddtbujddtV));
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{ddtVzz}}{\\partial{ddtbzzvj}}=", ddtbvjddtV));

        IExpr Uieqj = ASTUtils.transforms(uExpr, Functions.REMOVE_SUM_FUNCTION, Functions.I_TO_J_FUNCTION);
        IExpr ddtbujUzz = F.eval(F.D, Uieqj, DDTB_UJ);
        memory.put(F.D(U, DDTB_UJ), ddtbujUzz);
        IExpr ddtbvjUzz = F.eval(F.D, Uieqj, DDTB_VJ);
        memory.put(F.D(U, DDTB_VJ), ddtbvjUzz);
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{Uzz}}{\\partial{ddtbzzuj}}=", ddtbujUzz));
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{Uzz}}{\\partial{ddtbzzvj}}=", ddtbvjUzz));

        IExpr Vieqj = ASTUtils.transforms(vExpr, Functions.REMOVE_SUM_FUNCTION, Functions.I_TO_J_FUNCTION);
        IExpr ddtbujVzz = F.eval(F.D, Vieqj, DDTB_UJ);
        memory.put(F.D(V, DDTB_UJ), ddtbujVzz);
        IExpr ddtbvjVzz = F.eval(F.D, Vieqj, DDTB_VJ);
        memory.put(F.D(V, DDTB_VJ), ddtbvjVzz);
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{Vzz}}{\\partial{ddtbzzuj}}=", ddtbujVzz));
        spLeft.addIconRow(TexUtils.getIcon("\\frac{\\partial{Vzz}}{\\partial{ddtbzzvj}}=", ddtbvjVzz));

        ///////////////////////////////

        IExpr tExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfT.getText()));
        spCenter.addIconRow(TexUtils.getIcon("T=", tExpr));

        IExpr omega = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfQLeft.getText()));
        memory.put(THETA, omega);

        processIntegral("T", tExpr, memory, F.C0, F.Times(IntegerSym.valueOf(2), ALPHA), DDTB_UJ);

    }

    private static IAST ast(IExpr first, IExpr... exprs) {
        IAST ast = F.ast(first);
        Collections.addAll(ast, exprs);
        return ast;
    }

    private IExpr processIntegral(String key, IExpr expr, final Map<IExpr, IExpr> memory, final IExpr leftLimit, final IExpr rightLimit, final Symbol diffSubject) {

        IExpr differentiated = ASTUtils.transform(expr, new Function<IExpr, IExpr>() {
            @Override
            public IExpr apply(IExpr iExpr) {
                if (F.Integrate.isSame(iExpr.head()) && F.List.isSame(iExpr.getAt(2).head())) {
                    IExpr subIntegrPart = iExpr.getAt(1);
                    IExpr prepared = ASTUtils.transform(subIntegrPart, new Functions.SymbolToFunction(diffSubject));
                    IExpr differentiated = F.eval(F.D(prepared, diffSubject));
                    IExpr untransformed = ASTUtils.transform(differentiated, new Functions.FunctionToSymbol(diffSubject));
                    return F.Integrate(untransformed, iExpr.getAt(2));
                }
                return iExpr;
            }
        });

        spCenter.addIconRow(TexUtils.getIcon("\\frac{\\partial{" + key + "}}{\\partial{" + diffSubject + "}} =", differentiated));

        IExpr fullSubstituted = ASTUtils.transforms(differentiated, new Functions.MemoryReplaceFunction(memory), new Functions.ChangeIntegralIntervalFunction(leftLimit, rightLimit)
        );
        spCenter.addIconRow(TexUtils.getIcon("\\frac{\\partial{" + key + "}}{\\partial{" + diffSubject + "}} =", fullSubstituted));

//        IExpr debugEval = null; todo раскомментировать для замены на универсальный метод
//        try {
//            EVAL.fTraceEvaluation = true;
//            debugEval = EVAL.constrainedEval(new StringBufferWriter(), fullSubstituted);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        render(spCenter, debugEval);
//
        IExpr generalSolved = F.eval(fullSubstituted);
        spCenter.addIconRow(TexUtils.getIcon("\\frac{\\partial{" + key + "}}{\\partial{" + diffSubject + "}} =", generalSolved));
        IExpr iEqjGeneralSolved = ASTUtils.transforms(generalSolved, Functions.REMOVE_SUM_FUNCTION, Functions.I_TO_J_FUNCTION);
        IExpr iEqjResult = F.eval(iEqjGeneralSolved);
        spCenter.addIconRow(TexUtils.getIcon("i=j, \\frac{\\partial{" + key + "}}{\\partial{" + diffSubject + "}} =", iEqjResult));
//        return F.Plus(iEqjResult, iNotJResult);
        return iEqjResult;

    }

    private void processRightPanel() {
        EvalEngine.set(EVAL_ENGINE);
        IExpr uExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfRightU.getText()));
        TeXIcon teXIcon = TexUtils.getIcon("U=", uExpr);
        spRight.addIconRow(teXIcon);
        IExpr vExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfRightV.getText()));
        teXIcon = TexUtils.getIcon("V=", vExpr);
        spRight.addIconRow(teXIcon);

        // differentiations
        teXIcon = TexUtils.getIcon(TexUtils.bDotPostProcessor, "\\dot{V}=", vExpr);
        spRight.addIconRow(teXIcon);
    }

    private void renderTeX(SolvingPanel panel, String tex) {
        panel.addIcon(TexUtils.getIcon(tex));
    }

    private void render(SolvingPanel panel, IExpr expr) {
        if (!expr.isList()) {
            TeXIcon teXIcon = TexUtils.getIcon(expr);
            panel.addIcon(teXIcon);
        } else {
            AST ast = (AST) expr;
            IExpr result = renderInternal(panel, ast, false);
            if (result != null && !ast.get(1).isList()) {
                panel.addRow();
                String bold = "\\mathbf{";
                panel.addIcon(TexUtils.renderTeX(bold + TexUtils.toTeX(ast.get(1)) + "=" + TexUtils.toTeX(result) + "}"));
            }
        }
    }

    private IExpr renderInternal(SolvingPanel panel, AST expr, boolean drawConsequence) {
        IExpr last = null;
        for (int i = 1; i < expr.size(); i++) {
            IExpr iExpr = expr.get(i);
            boolean isNotLast = i < (expr.size() - 1);
            if (iExpr.isList()) {
                if (i != 1) {
                    panel.addRow();
                }
                renderInternal(panel, (AST) iExpr, true);
                if (last != null) {
                    panel.addRow();
                    if (isNotLast) {
                        panel.addIcon(TexUtils.getIcon(last, "="));
                    } else {
                        panel.addIcon(TexUtils.getIcon(last));
                    }
                }
            } else {
                last = iExpr;
                if (isNotLast) {
                    if (!expr.get(i + 1).isList()) {
                        panel.addIcon(TexUtils.getIcon(iExpr, "="));
                    } else {
                        panel.addIcon(TexUtils.getIcon(iExpr));
                    }
                } else {
                    if (drawConsequence) {
                        panel.addIcon(TexUtils.getIcon(iExpr, "\\Rightarrow"));
                    } else {
                        panel.addIcon(TexUtils.getIcon(iExpr));
                    }
                }
            }
        }
        return last;
    }

    public static void main(String[] args) {
        setLAF();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Application app = new Application();
                frame = new JFrame("Solver by Z. Sergei");
                frame.setContentPane(app.getRootPanel());
                frame.setFocusTraversalPolicy(app.getTraversalPolicy());
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setMinimumSize(new Dimension(1000, screenSize.height - 100));
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
        qLeftImgPanel = new ImagePanel("img/thetaeq.gif");
        qRightImgPanel = new ImagePanel("img/thetaeq.gif");

        spLeft = new SolvingPanel();
        spLeft.setBackground(Color.WHITE);
        spRight = new SolvingPanel();
        spRight.setBackground(Color.WHITE);
        spCenter = new SolvingPanel();
        spCenter.setBackground(Color.WHITE);

        pLeftOutput = new JPanel(new BorderLayout());
        pLeftOutput.add(spLeft, BorderLayout.CENTER);
        pRightOutput = new JPanel(new BorderLayout());
        pRightOutput.add(spRight, BorderLayout.CENTER);
        pCenterOutput = new JPanel(new BorderLayout());
        pCenterOutput.add(spCenter, BorderLayout.CENTER);
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

    public IndexedFocusTraversalPolicy getTraversalPolicy() {
        return traversalPolicy;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(5, 1, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.add(lagrangePanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Уравнение Лагранжа 2-го рода:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 2, new Insets(3, 3, 3, 3), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JLabel label2 = new JLabel();
        label2.setText("T=");
        panel3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfT = new JTextField();
        tfT.setText("1/2*PSI*(Integrate[((D[V,t]+OM*R+OM*U)^2+(D[U,t]-OM*V)^2),{x,0,2*pi}])");
        panel3.add(tfT, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("П=");
        panel3.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfP = new JTextField();
        tfP.setText("M/2*Integrate[(D[V,Q]-D^2[U,Q^2]),{x,0,2*pi}]+nu/2*Integrate[(D[V,Q]+U)^2,{x,0,2*pi}]");
        panel3.add(tfP, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, true));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pLeft = new JPanel();
        pLeft.setLayout(new GridLayoutManager(8, 2, new Insets(3, 3, 3, 3), -1, -1));
        panel5.add(pLeft, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pLeft.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        pLeft.add(x02aPanel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfLeftU = new JTextField();
        tfLeftU.setText("A_1*Sum[Times[b_ui*Sin[i*Q]],{i,1,N}]");
        pLeft.add(tfLeftU, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfLeftV = new JTextField();
        tfLeftV.setText("A_1*Sum[Times[b_vi*Sin[i*Q]],{i,1,N}]");
        pLeft.add(tfLeftV, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfA1 = new JTextField();
        tfA1.setText("(2*alpha)/(2*Pi)");
        pLeft.add(tfA1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK10 = new JTextField();
        tfK10.setText("0");
        pLeft.add(tfK10, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("U=");
        pLeft.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("V=");
        pLeft.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pLeft.add(a1ImgPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pLeft.add(k10ImgPanel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pLeft.add(k11ImgPanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pLeft.add(panel6, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), new Dimension(-1, 200), null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder("Вывод"));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-1));
        scrollPane1.setForeground(new Color(-1));
        panel6.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pLeftOutput.setBackground(new Color(-1));
        scrollPane1.setViewportView(pLeftOutput);
        pLeft.add(qLeftImgPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfQLeft = new JTextField();
        tfQLeft.setText("k_11*x");
        pLeft.add(tfQLeft, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK11 = new JTextField();
        tfK11.setText("Pi/(2*alpha)");
        pLeft.add(tfK11, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pRight = new JPanel();
        pRight.setLayout(new GridLayoutManager(8, 2, new Insets(3, 3, 3, 3), -1, -1));
        panel5.add(pRight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pRight.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        pRight.add(x2a2pPanel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfRightU = new JTextField();
        tfRightU.setText("A_2*Sum[Times[b_ui*Sin[i*Q]],{i,1,N}]");
        pRight.add(tfRightU, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK21 = new JTextField();
        tfK21.setText("Pi/(2*Pi-2*alpha)");
        pRight.add(tfK21, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfRightV = new JTextField();
        tfRightV.setText("A_2*Sum[Times[b_vi*Sin[i*Q]],{i,1,N}]");
        pRight.add(tfRightV, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfA2 = new JTextField();
        tfA2.setText("(2*Pi-2*alpha)/(2*Pi)");
        pRight.add(tfA2, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK20 = new JTextField();
        tfK20.setText("Pi*(Pi-2*alpha)/(Pi-alpha)");
        pRight.add(tfK20, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("U=");
        pRight.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("V=");
        pRight.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pRight.add(a2ImgPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pRight.add(k20ImgPanel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pRight.add(k21ImgPanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pRight.add(panel7, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), new Dimension(-1, 200), null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder("Вывод"));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBackground(new Color(-1));
        scrollPane2.setForeground(new Color(-1));
        panel7.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pRightOutput.setBackground(new Color(-1));
        scrollPane2.setViewportView(pRightOutput);
        pRight.add(qRightImgPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfQRight = new JTextField();
        tfQRight.setText("k_20+k_21*x");
        pRight.add(tfQRight, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        bExit = new JButton();
        bExit.setText("Выход");
        panel8.add(bExit, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel8.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        bCalculate = new JButton();
        bCalculate.setText("Вывести");
        panel8.add(bCalculate, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel9, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 500), null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder("Вывод"));
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setBackground(new Color(-1));
        scrollPane3.setForeground(new Color(-1));
        panel9.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pCenterOutput.setBackground(new Color(-1));
        scrollPane3.setViewportView(pCenterOutput);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfTest = new JTextField();
        tfTest.setText("D[Sin[x]*Cos[y],x]");
        panel10.add(tfTest, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Ввод:");
        panel10.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
