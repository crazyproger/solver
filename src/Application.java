import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.util.WriterOutputStream;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXEnvironment;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class Application {

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

    public static final Function<IExpr, IExpr> SUM_PREDICATE = new Function<IExpr, IExpr>() {
        @Override
        public IExpr apply(@Nullable IExpr iExpr) {
            if (F.Sum.isSame(iExpr.head())) {
                return iExpr.getAt(1);
            }
            return iExpr;
        }
    };

    public static BiMap<String, String> replacements = HashBiMap.create();
    public static BiMap<String, String> texReplacements = HashBiMap.create();


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

        texReplacements.put("ddt(\\wzz\\w+)", "\\\\dot{$1}");

        // todo for test
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
        String testExpr = tfTest.getText();
        final IExpr expr = EVAL.constrainedEval(buf0, testExpr, true);
        render(spCenter, expr);

        System.out.println(buf0.toString());
        System.out.println(pout.toString());
    }

    private void processLeftPanel() {
        EvalEngine.set(EVAL_ENGINE);
        IExpr uExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfLeftU.getText()));
        TeXIcon teXIcon = TexUtils.getIcon("U=", uExpr);
        spLeft.addIconRow(teXIcon);
        IExpr vExpr = EVAL_ENGINE.parse(TexUtils.preprocessInput(tfLeftV.getText()));
        teXIcon = TexUtils.getIcon("V=", vExpr);
        spLeft.addIconRow(teXIcon);

        // differentiations
        teXIcon = TexUtils.getIcon(TexUtils.bDotPostProcessor, "\\dot{V}=", vExpr);
        spLeft.addIconRow(teXIcon);
        teXIcon = TexUtils.getIcon(TexUtils.bDotPostProcessor, "\\dot{U}=", uExpr);
        spLeft.addIconRow(teXIcon);

        // partial differentiations
//        IExpr vDExpr = transform(uExpr, SUM_PREDICATE);
//        vDExpr = transform(vDExpr, SUM_PREDICATE);
//        teXIcon = getIcon("\\dot{V}/=", vDExpr);
//        spLeft.addIconRow(teXIcon);

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

    private IExpr transform(IExpr srcExpr, Function<IExpr, IExpr> function) {
        IExpr processed = function.apply(srcExpr);
        if (processed.isAST()) {
            AST result = new AST();
            result.set(0, processed.getAt(0));
            for (IExpr expr : (AST) processed) {
                IExpr filtered = transform(expr, function);
                if (filtered != null) {
                    result.add(filtered);
                }
            }
            return result;
        } else {
            return processed;
        }
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
                panel.addRow();
                if (last != null) {
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
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.add(lagrangePanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Уравнение Лагранжа 2-го рода:");
        panel2.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(3, 3, 3, 3), -1, -1));
        panel1.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JLabel label2 = new JLabel();
        label2.setText("T=");
        panel3.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfT = new JTextField();
        tfT.setText("1/2Y(Integrate[((D[V,t]+OM*R+OM*U)^2+(D[U,t]-OM*V)^2),{x,0,2*pi}])");
        panel3.add(tfT, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("П=");
        panel3.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfP = new JTextField();
        tfP.setText("M/2*Integrate[(D[V,Q]-D^2[U,Q^2]),{x,0,2*pi}]+vi/2*Integrate[(D[V,Q]+U)^2,{x,0,2*pi}]");
        panel3.add(tfP, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, true));
        panel4.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pLeft = new JPanel();
        pLeft.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 2, new Insets(3, 3, 3, 3), -1, -1));
        panel5.add(pLeft, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pLeft.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        pLeft.add(x02aPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfLeftU = new JTextField();
        tfLeftU.setText("A_1*Sum[Times[b_ui*Sin[i*Q]],{i,1,N}]");
        pLeft.add(tfLeftU, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfLeftV = new JTextField();
        tfLeftV.setText("A_1*Sum[Times[b_vi*Sin[i*Q]],{i,1,N}]");
        pLeft.add(tfLeftV, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfA1 = new JTextField();
        tfA1.setText("(2*alpha)/(2*Pi)");
        pLeft.add(tfA1, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK10 = new JTextField();
        tfK10.setText("0");
        pLeft.add(tfK10, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("U=");
        pLeft.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("V=");
        pLeft.add(label5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pLeft.add(a1ImgPanel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pLeft.add(k10ImgPanel, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pLeft.add(k11ImgPanel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pLeft.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), new Dimension(-1, 200), null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder("Вывод"));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-1));
        scrollPane1.setForeground(new Color(-1));
        panel6.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pLeftOutput.setBackground(new Color(-1));
        scrollPane1.setViewportView(pLeftOutput);
        pLeft.add(qLeftImgPanel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfQLeft = new JTextField();
        tfQLeft.setText("k_11*x");
        pLeft.add(tfQLeft, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK11 = new JTextField();
        tfK11.setText("Pi/(2*alpha)");
        pLeft.add(tfK11, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pRight = new JPanel();
        pRight.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 2, new Insets(3, 3, 3, 3), -1, -1));
        panel5.add(pRight, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pRight.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        pRight.add(x2a2pPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfRightU = new JTextField();
        tfRightU.setText("A_2*Sum[Times[b_ui*Sin[i*Q]],{i,1,N}]");
        pRight.add(tfRightU, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK21 = new JTextField();
        tfK21.setText("Pi/(2*Pi-2*alpha)");
        pRight.add(tfK21, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfRightV = new JTextField();
        tfRightV.setText("A_2*Sum[Times[b_vi*Sin[i*Q]],{i,1,N}]");
        pRight.add(tfRightV, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfA2 = new JTextField();
        tfA2.setText("(2*Pi-2*alpha)/(2*Pi)");
        pRight.add(tfA2, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tfK20 = new JTextField();
        tfK20.setText("Pi*(Pi-2*alpha)/(Pi-alpha)");
        pRight.add(tfK20, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("U=");
        pRight.add(label6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("V=");
        pRight.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pRight.add(a2ImgPanel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pRight.add(k20ImgPanel, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pRight.add(k21ImgPanel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pRight.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), new Dimension(-1, 200), null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder("Вывод"));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBackground(new Color(-1));
        scrollPane2.setForeground(new Color(-1));
        panel7.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pRightOutput.setBackground(new Color(-1));
        scrollPane2.setViewportView(pRightOutput);
        pRight.add(qRightImgPanel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfQRight = new JTextField();
        tfQRight.setText("k_20+k_21*x");
        pRight.add(tfQRight, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel8, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        bExit = new JButton();
        bExit.setText("Выход");
        panel8.add(bExit, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel8.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        bCalculate = new JButton();
        bCalculate.setText("Вывести");
        panel8.add(bCalculate, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel9, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 500), null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder("Вывод"));
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setBackground(new Color(-1));
        scrollPane3.setForeground(new Color(-1));
        panel9.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pCenterOutput.setBackground(new Color(-1));
        scrollPane3.setViewportView(pCenterOutput);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel10, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tfTest = new JTextField();
        tfTest.setText("D[Sin[x]*Cos[y],x]");
        panel10.add(tfTest, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Ввод:");
        panel10.add(label8, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
