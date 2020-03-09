package hu.flexisys.kbr.util;

/**
 * Created by peter on 28/09/14.
 */
public class KbrCompare {

    public static int compareNumericStrings(String left, String right) {
        for (int i = 0; i < left.length() && i < right.length(); i++) {
            Integer leftInt = Integer.valueOf(String.valueOf(left.charAt(i)));
            Integer rightInt = Integer.valueOf(String.valueOf(right.charAt(i)));
            int value = leftInt.compareTo(rightInt);
            if (value != 0) {
                return value;
            }
        }
        return Integer.valueOf(left.length()).compareTo(right.length());
    }
}
