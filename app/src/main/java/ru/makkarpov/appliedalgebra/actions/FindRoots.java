package ru.makkarpov.appliedalgebra.actions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.makkarpov.appliedalgebra.FiniteField;
import ru.makkarpov.appliedalgebra.Polynomial;
import ru.makkarpov.appliedalgebra.R;

public class FindRoots extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_roots);

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Throwable e) {
                    Toast.makeText(FindRoots.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculate() {
        FiniteField ff = FiniteField.parse((EditText) findViewById(R.id.fieldOrder));
        Polynomial input = Polynomial.parse((EditText) findViewById(R.id.input1), ff);

        StringBuilder log = new StringBuilder();

        for (int i = 0; i < ff.getOrder(); i++) {
            log.append("f(").append(i).append(") = ").append(ff.evaluate(input, i)).append("\n");
        }

        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
