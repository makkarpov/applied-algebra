package ru.makkarpov.appliedalgebra.actions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.makkarpov.appliedalgebra.FiniteField;
import ru.makkarpov.appliedalgebra.Polynomial;
import ru.makkarpov.appliedalgebra.R;

public class DecomposeIrreductible extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decompose_irreductible);

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Throwable e) {
                    Toast.makeText(DecomposeIrreductible.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculate() {
        FiniteField ff = FiniteField.parse((EditText) findViewById(R.id.fieldOrder));

        HashMap<Polynomial, Integer> decompositions = new HashMap<>();
        Polynomial input = Polynomial.parse((EditText) findViewById(R.id.input1), ff);
        List<Polynomial> irreductibles = ff.listIrreductibles(input.highestPower());

        StringBuilder log = new StringBuilder();

        log.append("f(x) = ").append(input).append("\n");

        while (true) {
            boolean irreductible = true;

            for (Polynomial p: irreductibles) {
                Polynomial[] div = ff.divide(input, p);

                if (div[1].equals(Polynomial.ZERO)) {
                    log.append("f(x) / (").append(p).append(") = ").append(div[0]).append("\n\n");
                    log.append("f(x) = ").append(div[0]).append("\n");

                    Integer i = decompositions.get(p);

                    if (i == null)
                        i = 0;

                    decompositions.put(p, i + 1);
                    input = div[0];
                    irreductible = false;
                    break;
                }
            }

            if (irreductible) {
                break;
            }
        }

        log.append("Многочлен неприводим. Итоговое разложение:\n");

        for (Map.Entry<Polynomial, Integer> e: decompositions.entrySet()) {
            if (e.getKey().highestPower() > 0) {
                log.append("(").append(e.getKey()).append(")");
            } else {
                log.append(e.getKey());
            }

            if (e.getValue() > 1) {
                log.append("^").append(e.getValue());
            }
        }

        if (input.highestPower() > 0) {
            log.append("(").append(input).append(")");
        } else {
            log.append(input);
        }

        log.append("\n");

        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
