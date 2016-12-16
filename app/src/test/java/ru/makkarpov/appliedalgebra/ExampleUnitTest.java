package ru.makkarpov.appliedalgebra;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void polynomialsParse() throws Exception {
        assertEquals(new Polynomial("x").toString(), "x");
        assertEquals(new Polynomial("1").toString(), "1");
        assertEquals(new Polynomial("-x").toString(), "-x");
        assertEquals(new Polynomial("-1").toString(), "-1");

        assertEquals(new Polynomial("x+1").toString(), "x + 1");
        assertEquals(new Polynomial("1+x").toString(), "x + 1");
        assertEquals(new Polynomial("x^2").toString(), "x^2");
        assertEquals(new Polynomial("-x^2").toString(), "-x^2");

        assertEquals(new Polynomial("x^2-x").toString(), "x^2 - x");
        assertEquals(new Polynomial("x-x^2").toString(), "-x^2 + x");

        assertEquals(new Polynomial("").toString(), "0");
    }

    @Test
    public void finiteField() throws Exception {
        FiniteField ff = new FiniteField(5);

        assertEquals(ff.add(2, 2), 4);
        assertEquals(ff.add(3, 3), 1);

        assertEquals(ff.subtract(2, 4), 3);
        assertEquals(ff.subtract(0, 4), 1);

        assertEquals(ff.multiply(3, 2), 1);
        assertEquals(ff.multiply(3, 3), 4);

        assertEquals(ff.divide(4, 2), 2);
        assertEquals(ff.divide(3, 2), 4);
    }

    @Test
    public void polynomialsInField() throws Exception {
        FiniteField ff = new FiniteField(5);

        assertEquals(
            ff.add(new Polynomial("x"), new Polynomial("1")),
            new Polynomial("x + 1")
        );

        assertEquals(
            ff.add(new Polynomial("3x^2 + x"), new Polynomial("2x^2 + 1")),
            new Polynomial("x + 1")
        );

        assertEquals(
            ff.subtract(new Polynomial("x^5 + x^3"), new Polynomial("x^5 + x^4")),
            new Polynomial("4x^4 + x^3")
        );

        assertEquals(
            ff.multiply(new Polynomial("2x^2 + x"), new Polynomial("3x + 1")),
            new Polynomial("x^3 + x")
        );

        assertArrayEquals(
            ff.divide(new Polynomial("2x^5 + x^4 + 4x + 3"), new Polynomial("3x^2 + 1")),
            new Polynomial[] {
                new Polynomial("4x^3 + 2x^2 + 2x + 1"),
                new Polynomial("2x + 2")
            }
        );
    }

    @Test
    public void irreductiblePolynomials() throws Exception {
        List<Polynomial> irreductibles = new FiniteField(2).listIrreductibles(8);

        assertEquals(irreductibles.size(), 71);

        assertTrue(irreductibles.contains(new Polynomial("x^5+x^2+1")));
        assertFalse(irreductibles.contains(new Polynomial("x^6+x^2+1")));
    }
}