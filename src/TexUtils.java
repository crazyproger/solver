import com.google.common.base.Function;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TexUtils {
    public static final Function<String, String> bDotPostProcessor = new Function<String, String>() {
        @Override
        public String apply(@Nullable String s) {
            return s.replaceAll("(bzz\\w+)", "\\\\dot{$1}");
        }
    };
    private static boolean collect;

    public static void startCollect(){
        collect = true;
    }

    public static void stopCollect(){
        collect = false;
    }

    public static String preprocessInput(String text) {
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile("(\\w+)_([\\d\\w]+)");
        Matcher matcher = pattern.matcher(text);
        int previous = 0;
        int start;
        int end;
        while (matcher.find()) {
            String group = matcher.group();
            start = matcher.start();
            end = matcher.end();
            result.append(text.substring(previous, start));
            previous = end;
            String replacement;
            if (Application.inputReplacements.containsKey(group)) {
                replacement = Application.inputReplacements.get(group);
            } else {
                replacement = matcher.group(1) + "zz" + Utils.generateString(3);
                Application.inputReplacements.put(group, replacement);
                Application.texReplacements.put(replacement, matcher.group(1) + "_{" + matcher.group(2) + "}");
            }
            result.append(replacement);
        }
        result.append(text.substring(previous, text.length()));
        return replaceByMap(result.toString(), Application.inputReplacements);
    }

    public static TeXIcon renderTeX(String tex) {
        String result = replaceByMap(tex, Application.texReplacements);
        if (collect) {
            Application.textBuffer.add(result);
        }
        try {
            TeXFormula formula = new TeXFormula(result);
            return formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, Application.FONT_SIZE_TEX, TeXConstants.UNIT_PIXEL, 80,
                    TeXConstants.ALIGN_LEFT);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String replaceByMap(String tex, Map<String, String> replacements) {
        String result = tex;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String toTeX(Object... mathML) {
        final StringBufferWriter buffer = new StringBufferWriter();
        final TeXUtilities texUtil = new TeXUtilities(Application.EVAL_ENGINE);
        if (mathML != null) {
            for (Object o : mathML) {
                texUtil.toTeX(o, buffer);
            }
        }
        return buffer.toString();
    }

    public static TeXIcon getIcon(Object... expr) {
        String tex = toTeX(expr);
        return renderTeX(tex);
    }

    public static TeXIcon getIcon(Function<String, String> postProcessor, Object... expr) {
        String tex = toTeX(expr);
        if (postProcessor != null) {
            return renderTeX(postProcessor.apply(tex));
        }
        return renderTeX(tex);
    }
}
