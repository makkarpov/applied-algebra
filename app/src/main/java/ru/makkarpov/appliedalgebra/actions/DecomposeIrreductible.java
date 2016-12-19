package ru.makkarpov.appliedalgebra.actions;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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
        final FiniteField ff = FiniteField.parse((EditText) findViewById(R.id.fieldOrder));
        final Polynomial input = Polynomial.parse((EditText) findViewById(R.id.input1), ff);

        final ProgressDialog dialog =
                ProgressDialog.show(this, "Вычисление...", "Генерация неприводимых многочленов...", true);

        AsyncTask<Void, Object, String> task = new AsyncTask<Void, Object, String>() {
            @Override
            protected String doInBackground(Void... params) {
                StringBuilder log = new StringBuilder();

                List<Polynomial> irreductibles = ff.listIrreductibles(input.highestPower());
                log.append("Неприводимых многочленов: ").append(irreductibles.size()).append("\n");
                publishProgress("Разложение...");

                HashMap<Polynomial, Integer> decompositions = new HashMap<>();
                Polynomial buf = input;

                log.append("f(x) = ").append(buf).append("\n");

                while (true) {
                    boolean irreductible = true;

                    for (Polynomial p: irreductibles) {
                        Polynomial[] div = ff.divide(buf, p);

                        if (div[1].equals(Polynomial.ZERO)) {
                            log.append("f(x) / (").append(p).append(") = ").append(div[0]).append("\n\n");
                            log.append("f(x) = ").append(div[0]).append("\n");

                            Integer i = decompositions.get(p);

                            if (i == null)
                                i = 0;

                            decompositions.put(p, i + 1);
                            buf = div[0];
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

                if (buf.highestPower() > 0) {
                    log.append("(").append(buf).append(")");
                } else {
                    log.append(buf);
                }

                log.append("\n");

                return log.toString();
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                for (Object v: values) {
                    if (v instanceof String) {
                        dialog.setMessage((String) v);
                    }
                }
            }

            @Override
            protected void onPostExecute(String s) {
                dialog.hide();
                ((TextView) findViewById(R.id.result)).setText(s);
            }
        };

        task.execute();
    }
}
