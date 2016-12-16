package ru.makkarpov.appliedalgebra;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by makkarpov on 15.12.16.
 */

public class Polynomial {
    public static class Builder {
        private int[] coefficients;

        public Builder() {
            coefficients = new int[0];
        }

        public int coefficient(int n) {
            return n < coefficients.length ? coefficients[n] : 0;
        }

        public int highestPower() {
            return Math.max(coefficients.length - 1, 0);
        }

        public int add(int power, int k) {
            if (power >= coefficients.length) {
                coefficients = Arrays.copyOf(coefficients, power + 1);
            }

            return (coefficients[power] += k);
        }

        public void set(int power, int k) {
            if (k == coefficient(power)) {
                return;
            }

            if (power >= coefficients.length) {
                coefficients = Arrays.copyOf(coefficients, power + 1);
            }

            coefficients[power] = k;
        }

        public Polynomial build() {
            int trailingZeros = 0;

            for (int i = coefficients.length - 1; i >= 0; i--) {
                if (coefficients[i] == 0) {
                    trailingZeros++;
                } else {
                    break;
                }
            }

            if (trailingZeros == 0) {
                return new Polynomial(true, coefficients);
            } else {
                return new Polynomial(coefficients);
            }
        }
    }

    public static final Polynomial ZERO = new Polynomial("0");
    public static final Polynomial ONE = new Polynomial("1");

    private int[] coefficients;

    private Polynomial(boolean notUsed, int[] k) {
        coefficients = k;
    }

    public Polynomial(int[] k) {
        int trailingZeros = 0;

        for (int i = k.length - 1; i >= 0; i--) {
            if (k[i] == 0) {
                trailingZeros++;
            } else {
                break;
            }
        }

        coefficients = new int[k.length - trailingZeros];
        System.arraycopy(k, 0, coefficients, 0, coefficients.length);
    }

    public Polynomial(Polynomial x) {
        this(x.coefficients);
    }

    public Polynomial(String s) throws IllegalArgumentException {
        coefficients = new int[0];

        s = s.replaceAll("[ \t\n]+", "");

        try {
            int idx = 0;
            while (idx < s.length()) {
                int end = idx + 1;

                while (end < s.length()) {
                    char c = s.charAt(end);

                    if (c == '+' || c == '-') {
                        break;
                    }

                    end++;
                }

                String part = s.substring(idx, end);

                int k, p;

                if (part.contains("x")) {
                    if (part.contains("x^")) {
                        String[] parts = part.split("x\\^", 2);

                        k = parseInt(parts[0]);
                        p = Integer.parseInt(parts[1]);
                    } else {
                        if (!part.endsWith("x")) {
                            throw new IllegalArgumentException("Malformed part: '" + part + "': '" + s + "'");
                        }

                        part = part.substring(0, part.length() - 1);

                        p = 1;
                        k = parseInt(part);
                    }
                } else {
                    p = 0;
                    k = Integer.parseInt(part);
                }

                if (p >= coefficients.length) {
                    coefficients = Arrays.copyOf(coefficients, p + 1);
                }

                coefficients[p] += k;

                idx = end;
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }

    public int coefficient(int i) {
        return (i < coefficients.length) ? coefficients[i] : 0;
    }

    public int highestPower() {
        return Math.max(coefficients.length - 1, 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = coefficients.length - 1; i >= 0; i--) {
            if (coefficients[i] == 0) {
                continue;
            }

            int k = coefficients[i];

            if (sb.length() != 0) {
                sb.append(k >= 0 ? " + " : " - ");
            } else if (k < 1) {
                sb.append("-");
            }

            if (Math.abs(k) != 1 || i == 0) {
                sb.append(Math.abs(k));
            }

            if (i != 0) {
                sb.append("x");

                if (i != 1) {
                    sb.append("^").append(i);
                }
            }
        }

        if (sb.length() == 0) {
            sb.append("0");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polynomial that = (Polynomial) o;

        for (int i = 0; i <= Math.max(highestPower(), that.highestPower()) + 1; i++) {
            if (coefficient(i) != that.coefficient(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        int mul = 31;
        for (int i = 0; i <= highestPower(); i++) {
            hash += mul * coefficient(i);
            mul *= 31;
        }

        return hash;
    }

    public static Polynomial parse(EditText text) {
        Polynomial p = new Polynomial(text.getText().toString());
        text.setText(p.toString());
        return p;
    }

    public static Polynomial parse(EditText text, FiniteField ff) {
        Polynomial p = new Polynomial(text.getText().toString());
        p = ff.wrap(p);
        text.setText(p.toString());
        return p;
    }

    public static Polynomial singleTerm(int power, int k) {
        int[] coefficients = new int[power + 1];
        coefficients[power] = k;
        return new Polynomial(true, coefficients);
    }

    private static int parseInt(String s) {
        if (s.isEmpty() || s.equals("+")) {
            return 1;
        }

        if (s.equals("-")) {
            return -1;
        }

        return Integer.parseInt(s, 10);
    }
}
