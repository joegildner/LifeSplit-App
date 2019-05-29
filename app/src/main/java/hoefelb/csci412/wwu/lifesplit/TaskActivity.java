package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.Task;


public class TaskActivity extends AppCompatActivity {

    private int numButtons = 0;
    private Button buttonList[] = new Button[12];

    final int CREATE = 0;
    final int TIMING = 1;
    final int EDIT = 2;

    //sets layout for buttons
 /*   private Button getHolder() {
        switch (numButtons) {
            case 0:
                return findViewById(R.id.holder1);
            case 1:
                return findViewById(R.id.holder2);
            case 2:
                return findViewById(R.id.holder3);
            case 3:
                return findViewById(R.id.holder4);
            case 4:
                return findViewById(R.id.holder5);
            case 5:
                return findViewById(R.id.holder6);
            case 6:
                return findViewById(R.id.holder7);
            case 7:
                return findViewById(R.id.holder8);
            case 8:
                return findViewById(R.id.holder9);
            case 9:
                return findViewById(R.id.holder10);
            case 10:
                return findViewById(R.id.holder11);
            case 11:
                return findViewById(R.id.holder12);
        }
        return findViewById(R.id.holder12);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize floating action button
        final FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setBackgroundColor(Color.TRANSPARENT);
        TaskDBHandler handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(TaskActivity.this, newTaskActivity.class);
                startActivityForResult(newTaskIntent, CREATE);
            }
        });

        final FloatingActionButton mapButton = findViewById(R.id.mapButton);
        //mapButton.setBackgroundColor(Color.TRANSPARENT);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(TaskActivity.this, newMapsTaskActivity.class);
                //newTaskIntent.putExtra("index", numButtons);
                startActivityForResult(newTaskIntent, 0);
            }
        });
    }

    //executes when floating action button returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //created task return
        if (requestCode == CREATE && resultCode == Activity.RESULT_OK) {
            //if (numButtons < 12) {

                //create new button
                final Button newButton = new Button(TaskActivity.this);
                final int index = data.getIntExtra("splitObjectIndex", -1);
                if (index != -1) {
                    System.out.println(index);
                    System.out.println(TaskData.getTask(index).getName());
              //  }
                final SplitObject newSplitObject = TaskData.getTask(index);
                newButton.setText(newSplitObject.getName());
                final LinearLayout linearLayout = findViewById(R.id.linearLayout);
                //final Button holder = getHolder();
                //ViewGroup.LayoutParams lp = holder.getLayoutParams();
                linearLayout.addView(newButton,numButtons);

                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent taskIntent = new Intent(TaskActivity.this, TimingScreen.class);
                        LinearLayout parent =  (LinearLayout)view.getParent();
                        System.out.println(parent.indexOfChild(view));
                        taskIntent.putExtra("splitObjectIndex", ((LinearLayout) view.getParent()).indexOfChild(view));
                        startActivityForResult(taskIntent, TIMING);
                    }
                });
                newButton.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        Intent taskIntent = new Intent(TaskActivity.this, EditTaskActivity.class);
                        taskIntent.putExtra("splitObjectIndex", linearLayout.indexOfChild(view));
                        startActivityForResult(taskIntent, EDIT);
                        return true;
                    }
                });
                //buttonList[numButtons] = newButton;
                numButtons++;
            }

        //timing screen return
        } else if (requestCode == TIMING && resultCode == Activity.RESULT_OK) {
            Long taskTime = data.getLongExtra("totalTimeLong", -1);
            int splitObjectIndex = data.getIntExtra("splitObjectIndex", -1);
            //store the result data from the timing screen
            //make a call to the split object for the index to recalculate the average time

        //edited task return
        } else if (requestCode == EDIT && resultCode == Activity.RESULT_OK) {
            int extras = data.getIntExtra("splitObjectIndex", -1);
            final SplitObject newSplitObject = TaskData.getTask(extras);
            Button curButton = buttonList[extras];
            curButton.setText(newSplitObject.getName());

        //deleted task return
        } else if (requestCode == EDIT && resultCode == Activity.RESULT_CANCELED) {
            int splitObjectIndex = data.getIntExtra("splitObjectIndex", -1);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.removeView(buttonList[splitObjectIndex]);
            numButtons--;
        }

    }
}

