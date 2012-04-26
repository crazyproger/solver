import javax.swing.*;

public class EditorTest {

    public static void main(String[] args) {
        EquationEditor equationEditor = new EquationEditor("A=", "Sin[x]", new EquationEditor.ResultListener() {
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
}
