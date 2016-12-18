package ru.makkarpov.appliedalgebra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ru.makkarpov.appliedalgebra.actions.*;

public class MainActivity extends AppCompatActivity {
    public static class SubAction {
        public final int nameRes;
        public final Class<? extends Activity> target;

        public SubAction(int nameRes, Class<? extends Activity> target) {
            this.nameRes = nameRes;
            this.target = target;
        }
    }

    public static final SubAction[] subActions = {
        new SubAction(R.string.act_simple_operations, SimpleOperations.class),
        new SubAction(R.string.act_matrix_inverse, MatrixInverse.class),
        new SubAction(R.string.act_decompose_irreductible, DecomposeIrreductible.class),
        new SubAction(R.string.act_find_roots, FindRoots.class),
        new SubAction(R.string.act_element_order, ElementOrder.class)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView) findViewById(R.id.functionList);
        lv.setAdapter(new ArrayAdapter<SubAction>(this, 0, subActions) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                final View view;

                if (convertView == null) {
                    view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                } else {
                    view = convertView;
                }

                ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).nameRes);

                return view;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, subActions[position].target));
            }
        });
    }
}
