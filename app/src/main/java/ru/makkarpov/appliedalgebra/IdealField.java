package ru.makkarpov.appliedalgebra;

import android.widget.EditText;

/**
 * Created by makkarpov on 15.12.16.
 */

public class IdealField extends FiniteField {
    private Polynomial ideal;

    public IdealField(int order, Polynomial ideal) {
        super(order);
        this.ideal = super.wrap(ideal);
    }

    public Polynomial getIdeal() {
        return ideal;
    }

    @Override
    public Polynomial wrap(Polynomial p) {
        return divide(super.wrap(p), ideal)[1];
    }

    public Polynomial inverse(Polynomial input) {
        Polynomial r_2 = ideal, r_1 = input;
        Polynomial y_2 = Polynomial.ZERO, y_1 = Polynomial.ONE;

        int i = 0;
        while (true) {
            Polynomial[] rem = divide(r_2, r_1); // { q_0, r_0 }
            Polynomial y_0 = subtract(y_2, multiply(y_1, rem[0]));

            if (rem[1].highestPower() == 0) {
                int k = inverse(rem[1].coefficient(0));
                return multiply(y_0, k);
            }

            if (i > 200) {
                throw new IllegalStateException("Iteration count exceeded");
            }

            r_2 = r_1; r_1 = rem[1];
            y_2 = y_1; y_1 = y_0;
            i++;
        }
    }

    public static IdealField parse(EditText order, EditText ideal) {
        Polynomial i = new Polynomial(ideal.getText().toString());
        int o = Integer.parseInt(order.getText().toString().trim());

        IdealField r = new IdealField(o, i);
        ideal.setText(r.getIdeal().toString());

        return r;
    }
}
