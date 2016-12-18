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

public class ElementOrder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_order);

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Throwable e) {
                    Toast.makeText(ElementOrder.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculate() {
        IdealField ff = IdealField.parse((EditText) findViewById(R.id.fieldOrder),
                (EditText) findViewById(R.id.fieldIdeal));
        Polynomial input = Polynomial.parse((EditText) findViewById(R.id.input1), ff);
        Polynomial buffer = input;

        StringBuilder log = new StringBuilder();

        log.append("f(x) = ").append(input).append("\n");

        for (int i = 2; i <= 200; i++) {
            buffer = ff.wrap(ff.multiply(buffer, input));
            log.append("f(x)^").append(i).append(" = ").append(buffer).append("\n");

            if (buffer.equals(Polynomial.ONE))
                break;
        }

        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
