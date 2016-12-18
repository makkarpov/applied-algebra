package ru.makkarpov.appliedalgebra.actions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.makkarpov.appliedalgebra.FiniteField;
import ru.makkarpov.appliedalgebra.IdealField;
import ru.makkarpov.appliedalgebra.Polynomial;
import ru.makkarpov.appliedalgebra.R;

public class SimpleOperations extends AppCompatActivity {
    static abstract class Operation {
        public abstract String getSymbol();
        public abstract void calculate(FiniteField ff, Polynomial input1, Polynomial input2,
                                       StringBuilder log);

        public boolean hasSecondInput() {
            return true;
        }

        public boolean hasIdealSupport() {
            return true;
        }

        public String getName() {
            return getSymbol();
        }
    }

    public static final Operation[] KNOWN_OPERATIONS = {
        new Operation() {
            @Override
            public String getSymbol() {
                return "+";
            }

            @Override
            public void calculate(FiniteField ff, Polynomial input1, Polynomial input2, StringBuilder log) {
                log.append(ff.wrap(ff.add(input1, input2)));
            }
        },
        new Operation() {
            @Override
            public String getSymbol() {
                return "-";
            }

            @Override
            public void calculate(FiniteField ff, Polynomial input1, Polynomial input2, StringBuilder log) {
                log.append(ff.wrap(ff.subtract(input1, input2)));
            }
        },
        new Operation() {
            @Override
            public String getSymbol() {
                return "*";
            }

            @Override
            public void calculate(FiniteField ff, Polynomial input1, Polynomial input2, StringBuilder log) {
                log.append(ff.wrap(ff.multiply(input1, input2)));
            }
        },
        new Operation() {
            @Override
            public String getSymbol() {
                return "/";
            }

            @Override
            public void calculate(FiniteField ff, Polynomial input1, Polynomial input2, StringBuilder log) {
                if (ff instanceof IdealField) {
                    log.append("Идеалы пока не поддерживаются");
                } else {
                    Polynomial result = Polynomial.ZERO;
                    Polynomial cur = input1;

                    while (cur.highestPower() >= input2.highestPower()) {
                        Polynomial term = Polynomial.singleTerm(
                                cur.highestPower() - input2.highestPower(),
                                ff.divide(cur.coefficient(cur.highestPower()), input2.coefficient(input2.highestPower()))
                        );

                        Polynomial mul = ff.multiply(input2, term);

                        log.append("Вычитаем ").append(term).append(" * делитель:\n");
                        log.append("(").append(cur).append(") - (").append(mul).append(") = ");

                        result = ff.add(result, term);
                        cur = ff.subtract(cur, mul);

                        log.append(cur).append("\n\n");
                    }

                    log.append("-------------------\n");
                    log.append("Частное: ").append(result).append("\n");
                    log.append("Остаток: ").append(cur).append("\n");
                }
            }
        },
        new Operation() {
            @Override
            public String getSymbol() {
                return "1/x";
            }

            @Override
            public boolean hasSecondInput() {
                return false;
            }

            @Override
            public void calculate(FiniteField ff, Polynomial input1, Polynomial input2, StringBuilder log) {
                if (!(ff instanceof IdealField)) {
                    log.append("Идеал не задан");
                    return;
                }

                Polynomial r_2 = ((IdealField) ff).getIdeal(), r_1 = input1;
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
            }
        },
        new Operation() {
            @Override
            public String getSymbol() {
                return "|x=";
            }

            @Override
            public void calculate(FiniteField ff, Polynomial input1, Polynomial input2, StringBuilder log) {
                log.append(ff.wrap(ff.evaluate(input1, input2)));
            }
        }
    };

    private int currentOperation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_operations);

        final Button opBtn = (Button) findViewById(R.id.opButton);
        final EditText input2 = (EditText) findViewById(R.id.input2);
        final EditText fieldIdeal = (EditText) findViewById(R.id.fieldIdeal);

        opBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOperation = (currentOperation + 1) % KNOWN_OPERATIONS.length;
                Operation op = KNOWN_OPERATIONS[currentOperation];

                opBtn.setText(op.getSymbol());
                input2.setVisibility(op.hasSecondInput() ? View.VISIBLE : View.GONE);
                fieldIdeal.setEnabled(op.hasIdealSupport());
            }
        });

        opBtn.setText(KNOWN_OPERATIONS[currentOperation].getSymbol());

        findViewById(R.id.doButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calculate();
                } catch (Exception e) {
                    Toast.makeText(SimpleOperations.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void calculate() {
        Operation op = KNOWN_OPERATIONS[currentOperation];

        FiniteField ff;

        if (op.hasIdealSupport()) {
            IdealField f = IdealField.parse((EditText) findViewById(R.id.fieldOrder),
                                            (EditText) findViewById(R.id.fieldIdeal));

            if (f.getIdeal().equals(Polynomial.ZERO)) {
                ff = new FiniteField(f.getOrder());
            } else {
                ff = f;
            }
        } else {
            ff = FiniteField.parse((EditText) findViewById(R.id.fieldOrder));
        }

        Polynomial input1 = Polynomial.parse((EditText) findViewById(R.id.input1), ff);
        Polynomial input2 = null;

        if (op.hasSecondInput()) {
            input2 = Polynomial.parse((EditText) findViewById(R.id.input2), ff);
        }

        StringBuilder log = new StringBuilder();

        try {
            op.calculate(ff, input1, input2, log);
        } catch (Exception e) {
            log.append(e.toString());
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.result)).setText(log.toString());
    }
}
