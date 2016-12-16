package ru.makkarpov.appliedalgebra.actions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.makkarpov.appliedalgebra.FiniteField;
import ru.makkarpov.appliedalgebra.IdealField;
import ru.makkarpov.appliedalgebra.Polynomial;
import ru.makkarpov.appliedalgebra.R;

public class PolynomialInverse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polynomial_inverse);

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Exception e) {
                    Toast.makeText(PolynomialInverse.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculate() {
        IdealField ff = IdealField.parse((EditText) findViewById(R.id.fieldOrder),
                (EditText) findViewById(R.id.fieldIdeal));
        Polynomial input = Polynomial.parse((EditText) findViewById(R.id.input1), ff);

        if (input.highestPower() == 0) {
            throw new IllegalArgumentException("deg(input) = 0, no inverse");
        }

        StringBuilder log = new StringBuilder();

        Polynomial r_2 = ff.getIdeal(), r_1 = input;
        Polynomial y_2 = Polynomial.ZERO, y_1 = Polynomial.ONE;

        log.append("r(-2) = ").append(r_2).append("\n");
        log.append("r(-1) = ").append(r_1).append("\n");
        log.append("y(-2) = 0; y(-1) = 1\n\n");

        int i = 0;
        while (true) {
            Polynomial[] rem = ff.divide(r_2, r_1); // { q_0, r_0 }
            Polynomial y_0 = ff.subtract(y_2, ff.multiply(y_1, rem[0]));

            log.append("r(").append(i - 2).append(") = r(").append(i - 1).append(")q(").append(i)
               .append(") + r(").append(i).append(")\n");

            log.append("q(").append(i).append(") = ").append(rem[0]).append("; r(").append(i)
               .append(") = ").append(rem[1]).append("\n");

            log.append("y(").append(i).append(") = y(").append(i - 2).append(") - y(").append(i - 1)
               .append(")q(").append(i).append(") = ").append(y_0).append("\n\n");

            if (rem[1].highestPower() == 0) {
                int k = ff.inverse(rem[1].coefficient(0));
                Polynomial result = ff.multiply(y_0, k);

                log.append("deg(r(").append(i).append(")) = 0, завершение. Домножаем на ")
                   .append(k).append("\n");
                log.append("Результат: ").append(result).append("\n");

                break;
            }

            if (i > 20) {
                log.append("Сделано больше 20 итераций. Принудительное завершение без результата\n");
                break;
            }

            r_2 = r_1; r_1 = rem[1];
            y_2 = y_1; y_1 = y_0;
            i++;
        }

        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
