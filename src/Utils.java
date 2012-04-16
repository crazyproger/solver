import java.util.Random;

public class Utils {
    private static String characters ="abcdefghinmABCDEFGHINM";
    private static Random rnd = new Random(System.currentTimeMillis());
    public static String generateString(int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
    }

}
