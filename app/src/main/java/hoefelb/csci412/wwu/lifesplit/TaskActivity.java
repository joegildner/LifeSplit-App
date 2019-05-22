package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TaskActivity extends AppCompatActivity {

    private int numButtons = 0;

    private Button getHolder() {
        switch(numButtons) {
            case 0: return findViewById(R.id.holder1);
            case 1: return findViewById(R.id.holder2);
            case 2: return findViewById(R.id.holder3);
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
            case 0: return "Task 1";
            case 1: return "Task 2";
            case 2: return "Task 3";
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

        final FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setBackgroundColor(Color.TRANSPARENT);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(TaskActivity.this, newTaskActivity.class);
                //newTaskIntent.putExtra("index", numButtons);
                startActivityForResult(newTaskIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==0 && resultCode == Activity.RESULT_OK){
            if(numButtons < 12) {
                Button newButton = new Button(TaskActivity.this);
                //newButton.setText(TaskData.getTask(numButtons).getName());
                int extras = data.getIntExtra("splitObjectIndex", -1);
                if (extras != -1) {
                    System.out.println(extras);
                    System.out.println(TaskData.getTask(extras).getName());
                }
                final SplitObject newSplitObject = TaskData.getTask(extras);
                newButton.setText(newSplitObject.getName());
                ConstraintLayout cl = findViewById(R.id.cl);
                final Button holder = getHolder();
                ViewGroup.LayoutParams lp = holder.getLayoutParams();
                cl.addView(newButton, lp);
                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent taskIntent = new Intent(TaskActivity.this, TimingScreen.class);
                        taskIntent.putExtra("splitObjectIndex", TaskData.getIndex(newSplitObject));
                        startActivityForResult(taskIntent,1);
                    }
                });
                numButtons++;
            }
            else if(requestCode == 1 && resultCode == Activity.RESULT_OK){
                Long taskTime = data.getLongExtra("totalTimeLong",-1);
                int splitObjectIndex = data.getIntExtra("splitObjectIndex",-1);
                System.out.println(taskTime);
                System.out.println(splitObjectIndex);
                //store the result data from the timing screen
                //make a call to the split object for the index to recalculate the average time

            }

        }

        }
    }

