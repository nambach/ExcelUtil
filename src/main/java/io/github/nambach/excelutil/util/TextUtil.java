package io.github.nambach.excelutil.util;

public class TextUtil {

    private TextUtil() {
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
    }

    // https://stackoverflow.com/a/2560017
    public static String splitCamelCase(String s) {
        String result = s.replaceAll(
                String.format("%s|%s|%s",
                              "(?<=[A-Z])(?=[A-Z][a-z])",
                              "(?<=[^A-Z])(?=[A-Z])",
                              "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
        return capitalize(result);
    }

    public static String getNotNull(String... vals) {
        if (vals == null || vals.length == 0) {
            return null;
        }
        for (String val : vals) {
            if (val != null) {
                return val;
            }
        }
        return vals[vals.length - 1];
    }

}
