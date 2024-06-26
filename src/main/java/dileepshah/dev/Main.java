package dileepshah.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int[] a = {13, 5604, 31, 2, 13, 4560, 546, 654, 456};
        System.out.println(solution(a));  // Output should be 5
    }

    public static long solution(int[] a) {
        Map<String, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < a.length; i++) {
            String canonicalForm = getCanonicalForm(a[i]);
            map.computeIfAbsent(canonicalForm, k -> new ArrayList<>()).add(i);
        }

        long count = 0;

        for (List<Integer> indices : map.values()) {
            int size = indices.size();
            if (size > 1) {
                count += (long) size * (size - 1) / 2;
            }
        }

        return count;
    }

    private static String getCanonicalForm(int number) {
        String numStr = Integer.toString(number);
        String doubleNumStr = numStr + numStr;
        String smallest = numStr;

        for (int i = 0; i < numStr.length(); i++) {
            String candidate = doubleNumStr.substring(i, i + numStr.length());
            if (candidate.compareTo(smallest) < 0) {
                smallest = candidate;
            }
        }

        return smallest;
    }
}