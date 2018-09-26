package z.houbin.kq.util;

public class L {
    public static void d(String tag, String text) {
        System.out.println(tag + " : " + text);
    }

    public static void e(String tag, Exception e) {
        System.out.println(tag + " : " + e.getMessage());
    }

    public static void e(String tag, String text) {
        System.out.println(tag + " : " + text);
    }

    public static void e(Exception e) {
        System.out.println(e.getMessage());
    }
}
