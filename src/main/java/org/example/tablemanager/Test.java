package org.example.tablemanager;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        String[][] mat = new String[5][3];
        populate(mat, "lorem ipsum dolor sit amet consectetur adipiscing elit quisque et arcu iaculis facilisis orci et".split("\\s+"));
        show(mat);
        Arrays.sort(mat[0]);
        mat = sort(mat, 0);
        System.out.println();
        show(Objects.requireNonNull(mat));
    }

    private static void populate(String[][] m, String[] population) {
        for (int i = 0; i < m.length; i++) {
            System.arraycopy(population, i * m[0].length, m[i], 0, m[0].length);
        }
    }

    private static void show(String[][] a) {
        for (String[] strings : a) {
            for (String s : strings) {
                System.out.printf("%s ", s);
            }
            System.out.println();
        }
    }

    private static String[][] sort(String[][] m, int field) {
        if (field >= m.length) {
            return null;
        }
        List<String[]> l = new ArrayList<>();
        Collections.addAll(l, m);
        l.sort(Comparator.comparing(a -> a[field]));
        return l.toArray(new String[0][0]);
    }
}
