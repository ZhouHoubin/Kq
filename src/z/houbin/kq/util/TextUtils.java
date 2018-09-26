package z.houbin.kq.util;

public class TextUtils {

    public static boolean isEmpty(String text) {
        if (text == null || "".equalsIgnoreCase(text)) {
            return true;
        } else {
            return false;
        }
    }
}
