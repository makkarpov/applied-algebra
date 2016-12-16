package ru.makkarpov.appliedalgebra.actions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.makkarpov.appliedalgebra.FiniteField;
import ru.makkarpov.appliedalgebra.Polynomial;
import ru.makkarpov.appliedalgebra.R;

public class PolynomialDivide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polynomial_divide);

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Exception e) {
                    Toast.makeText(PolynomialDivide.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculate() {
        FiniteField ff = FiniteField.parse((EditText) findViewById(R.id.fieldOrder));
        Polynomial inp1 = Polynomial.parse((EditText) findViewById(R.id.input1), ff);
        Polynomial inp2 = Polynomial.parse((EditText) findViewById(R.id.input2), ff);

        StringBuilder log = new StringBuilder();

        Polynomial result = Polynomial.ZERO;
        Polynomial cur = inp1;

        while (cur.highestPower() >= inp2.highestPower()) {
            Polynomial term = Polynomial.singleTerm(
                cur.highestPower() - inp2.highestPower(),
                ff.divide(cur.coefficient(cur.highestPower()), inp2.coefficient(inp2.highestPower()))
            );

            Polynomial mul = ff.multiply(inp2, term);

            log.append("Вычитаем ").append(term).append(" * делитель:\n");
            log.append("(").append(cur).append(") - (").append(mul).append(") = ");

            result = ff.add(result, term);
            cur = ff.subtract(cur, mul);

            log.append(cur).append("\n\n");
        }

        log.append("-------------------\n");
        log.append("Частное: ").append(result).append("\n");
        log.append("Остаток: ").append(cur).append("\n");

        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
