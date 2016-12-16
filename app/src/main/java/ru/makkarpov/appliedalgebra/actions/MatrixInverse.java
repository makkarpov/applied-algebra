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

public class MatrixInverse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_inverse);

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Exception e) {
                    Toast.makeText(MatrixInverse.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.inputPanel).setVisibility(View.VISIBLE);
                findViewById(R.id.resultPanel).setVisibility(View.GONE);
            }
        });
    }

    private Polynomial[][] readMatrix(FiniteField ff, String s) throws IllegalArgumentException {
        String[] rows = s.split("[\r\n]+");
        Polynomial[][] ret = new Polynomial[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            String[] cols = rows[i].split(";");

            if (i != 0 && cols.length != ret[i - 1].length) {
                throw new IllegalArgumentException("Rows have different number of columns");
            }

            ret[i] = new Polynomial[cols.length];

            for (int j = 0; j < cols.length; j++) {
                ret[i][j] = ff.wrap(new Polynomial(cols[j]));
            }
        }

        return ret;
    }

    private void calculate() {
        IdealField ff = IdealField.parse((EditText) findViewById(R.id.fieldOrder),
                (EditText) findViewById(R.id.fieldIdeal));

        Polynomial[][] matrix = readMatrix(ff, ((EditText) findViewById(R.id.input1)).getText().toString());

        if (matrix.length == 0) {
            return;
        }

        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("Non-square matrix");
        }

        if (matrix.length != 2) {
            throw new IllegalArgumentException("Only 2x2 matrices are supported");
        }

        Polynomial determinant = ff.wrap(ff.subtract(ff.multiply(matrix[0][0], matrix[1][1]),
                ff.multiply(matrix[0][1], matrix[1][0])));

        StringBuilder log = new StringBuilder();

        log.append("det M = ").append(determinant).append("\n");

        if (determinant.highestPower() == 0) {
            log.append("deg(det M) = 0, вырожденная матрица.\n");
        } else {
            Polynomial invDet = ff.wrap(ff.inverse(determinant));

            log.append("1/(det M) = ").append(invDet).append("\n\n");
            log.append("Обратная матрица:\n");

            Polynomial[][] invMatrix = {
                    { matrix[1][1], ff.multiply(matrix[0][1], -1) },
                    { ff.multiply(matrix[1][0], -1), matrix[0][0] }
            };

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    invMatrix[i][j] = ff.wrap(ff.multiply(invDet, invMatrix[i][j]));

                    log.append(invMatrix[i][j]).append("; ");
                }

                log.append("\n");
            }
        }

        findViewById(R.id.inputPanel).setVisibility(View.GONE);
        findViewById(R.id.resultPanel).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
