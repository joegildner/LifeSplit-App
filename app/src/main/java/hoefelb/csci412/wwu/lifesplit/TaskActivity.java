package hoefelb.csci412.wwu.lifesplit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TaskActivity extends AppCompatActivity {

    private int numButtons = 3;

    private Button getHolder() {
        switch(numButtons) {
            //case 2: return findViewById(R.id.holder3);
            case 3: return findViewById(R.id.holder4);
            case 4: return findViewById(R.id.holder5);
            case 5: return findViewById(R.id.holder6);
            case 6: return findViewById(R.id.holder7);
            case 7: return findViewById(R.id.holder8);
            case 8: return findViewById(R.id.holder9);
            case 9: return findViewById(R.id.holder10);
            case 10: return findViewById(R.id.holder11);
            case 11: return findViewById(R.id.holder12);
        }
        return findViewById(R.id.holder12);
    }

    private String getName() {
        switch(numButtons) {
            //case 2: return findViewById(R.id.holder3);
            case 3: return "Task 4";
            case 4: return "Task 5";
            case 5: return "Task 6";
            case 6: return "Task 7";
            case 7: return "Task 8";
            case 8: return "Task 9";
            case 9: return "Task 10";
            case 10: return "Task 11";
            case 11: return "Task 12";
        }
        return "what have you done";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button task1Button = findViewById(R.id.task1);
        task1Button.setBackgroundColor(Color.TRANSPARENT);
        task1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TaskActivity.this, TimingScreen.class));
            }
        });

        final Button task2Button = findViewById(R.id.task2);
        task2Button.setBackgroundColor(Color.TRANSPARENT);
        task2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TaskActivity.this, TimingScreen.class));
            }
        });

        final Button task3Button = findViewById(R.id.task3);
        task3Button.setBackgroundColor(Color.TRANSPARENT);
        task3Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newButtonClick(v);
            }
        });

        final FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setBackgroundColor(Color.TRANSPARENT);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(TaskActivity.this, newTaskActivity.class);
                SplitObject newSplitObject = new SplitObject();
                newTaskIntent.putExtra("data", newSplitObject);
                startActivity(newTaskIntent);
            }
        });
    }

    public void newButtonClick(View v){
        if(numButtons < 12) {
            Button newButton = new Button(TaskActivity.this);
            newButton.setText(getName());
            ConstraintLayout cl = findViewById(R.id.cl);
            final Button holder = getHolder();
            ViewGroup.LayoutParams lp = holder.getLayoutParams();
            cl.addView(newButton, lp);
            numButtons++;
            newButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent taskIntent = new Intent(TaskActivity.this, TimingScreen.class);
                    SplitObject newSplitObject = new SplitObject();
                    taskIntent.putExtra("data", newSplitObject);
                    startActivity(taskIntent);
                }
            });
        }
    }
}
