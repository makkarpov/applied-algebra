package ru.makkarpov.appliedalgebra;

import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by makkarpov on 15.12.16.
 */

public class FiniteField {
    private int order;

    public FiniteField(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    // Number operations:

    public int wrap(int n) {
        n = n % order;

        if (n < 0)
            n += order;

        return n;
    }

    public int add(int a, int b) {
        return (a + b) % order;
    }

    public int subtract(int a, int b) {
        int r = (a - b) % order;

        if (r < 0)
            r += order;

        return r;
    }

    public int multiply(int a, int b) {
        return (a * b) % order;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("/ by zero");
        }

        for (int i = 0; i < order; i++) {
            if (multiply(b, i) == a) {
                return i;
            }
        }

        throw new RuntimeException("Should not reach here");
    }

    public int inverse(int n) {
        for (int i = 0; i < order; i++) {
            if (multiply(n, i) == 1) {
                return i;
            }
        }

        throw new RuntimeException("No inverse!");
    }

    // Polynomial operations:

    public Polynomial wrap(Polynomial p) {
        boolean allOk = true;

        for (int i = 0; i <= p.highestPower(); i++) {
            if (p.coefficient(i) >= order || p.coefficient(i) < 0) {
                allOk = false;
                break;
            }
        }

        if (allOk) {
            return p;
        }

        Polynomial.Builder bld = new Polynomial.Builder();

        for (int i = 0; i <= p.highestPower(); i++) {
            bld.set(i, wrap(p.coefficient(i)));
        }

        return bld.build();
    }

    public Polynomial add(Polynomial a, Polynomial b, int n) {
        Polynomial.Builder bld = new Polynomial.Builder();

        n = wrap(n);

        for (int i = 0; i <= Math.max(a.highestPower(), b.highestPower()); i++) {
            bld.set(i, add(a.coefficient(i), multiply(b.coefficient(i), n)));
        }

        return bld.build();
    }

    public Polynomial add(Polynomial a, Polynomial b) {
        return add(a, b, 1);
    }

    public Polynomial subtract(Polynomial a, Polynomial b) {
        return add(a, b, -1);
    }

    public Polynomial multiply(Polynomial a, int n) {
        Polynomial.Builder bld = new Polynomial.Builder();

        for (int i = 0; i <= a.highestPower(); i++) {
            bld.set(i, multiply(a.coefficient(i), n));
        }

        return bld.build();
    }

    public Polynomial multiply(Polynomial a, Polynomial b) {
        Polynomial.Builder bld = new Polynomial.Builder();

        for (int i = 0; i <= a.highestPower(); i++) {
            for (int j = 0; j <= b.highestPower(); j++) {
                bld.set(i + j, add(multiply(a.coefficient(i), b.coefficient(j)), bld.coefficient(i + j)));
            }
        }

        return bld.build();
    }

    /**
     * @param a Dividend
     * @param b Divisor
     * @return { quotient, remainder }
     */
    public Polynomial[] divide(Polynomial a, Polynomial b) {
        Polynomial result = Polynomial.ZERO;
        Polynomial cur = a;

        int i = 0;
        while (cur.highestPower() >= b.highestPower()) {
            Polynomial term = Polynomial.singleTerm(
                    cur.highestPower() - b.highestPower(),
                    divide(cur.coefficient(cur.highestPower()), b.coefficient(b.highestPower()))
            );

            if (i > 200) {
                throw new IllegalStateException("Iteration count exceeded");
            }

            result = add(result, term);
            cur = subtract(cur, multiply(b, term));
            i++;
        }

        return new Polynomial[] { result, cur };
    }

    public int evaluate(Polynomial a, int x) {
        int r = 0;

        int px = 1;
        for (int i = 0; i <= a.highestPower(); i++) {
            r = add(r, multiply(px, a.coefficient(i)));
            px = multiply(px, x);
        }

        return r;
    }

    public List<Polynomial> listIrreductibles(int highestPower) {
        ArrayList<Polynomial> ret = new ArrayList<>();

        int[] coefficients = new int[highestPower + 1];
        outer: while (true) {
            Polynomial p = new Polynomial(coefficients);

            if (p.highestPower() > 0) {
                if (p.highestPower() == 1) {
                    ret.add(p);
                } else {
                    boolean reductible = false;

                    for (Polynomial d: ret) {
                        Polynomial[] div = divide(p, d);

                        if (div[1].equals(Polynomial.ZERO)) {
                            reductible = true;
                            break;
                        }
                    }

                    if (!reductible) {
                        ret.add(p);
                    }
                }
            }

            int i = 0;
            while (true) {
                coefficients[i]++;

                if (coefficients[i] >= getOrder()) {
                    coefficients[i] = 0;
                    i++;

                    if (i >= coefficients.length) {
                        break outer;
                    }
                } else {
                    break;
                }
            }
        }

        return ret;
    }

    public static FiniteField parse(EditText text) {
        return new FiniteField(Integer.parseInt(text.getText().toString().trim()));
    }
}
