package ru.makkarpov.appliedalgebra;

/**
 * Created by makkarpov on 15.12.16.
 */

public class Algorithms {
    /**
     * @param a First number
     * @param b Second number
     * @return {q1, q2, gcd}
     */
    public static int[] extendedEuclidean(int a, int b) {
        if (a < b) {
            int t = a;
            a = b;
            b = t;
        }

        int[] l1 = {a, 1, 0};
        int[] l2 = {b, 0, 1};
        int[] l3 = new int[3];

        while (l1[0]-l2[0]*(l1[0]/l2[0]) > 0) {
            System.arraycopy(l2, 0, l3, 0, 3);

            int q = l1[0]/l2[0];

            for (int i = 0; i < 3; i++) {
                l2[i] = (l1[i]-l2[i]*q);
            }

            System.arraycopy(l3, 0, l1, 0, 3);
        }

        return l3;
    }
}
