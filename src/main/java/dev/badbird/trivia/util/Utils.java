package dev.badbird.trivia.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isSimilar(String a, String b) { // we have fuzzy search at home
        return a.equalsIgnoreCase(b) || a.startsWith(b) || b.startsWith(a) || a.contains(b) || b.contains(a);
    }

    public static Comparator<Sortable> sortableComparator() {
        return Comparator.comparing(Sortable::getSortableValue);
    }

    private static final Pattern HTML_ESCAPE_PATTERN = Pattern.compile("&#[0-9]*;");

    public static String unescapeHtml(String in) { // turns &#039; to '
        Matcher matcher = HTML_ESCAPE_PATTERN.matcher(in);
        while (matcher.find()) {
            String code = matcher.group(0);
            int i = Integer.parseInt(code.substring(2, code.length() - 1));
            in = in.replace(code, Character.toString(i));
        }
        return in;
    }

    public static void main(String[] args) {
        System.out.println(unescapeHtml("What word represents the letter &#039;T&#039; in the NATO phonetic alphabet?"));
    }
}
