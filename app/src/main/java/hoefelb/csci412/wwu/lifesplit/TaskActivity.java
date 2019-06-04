package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
    SQLiteDatabase db;

    final int CREATE = 0;
    final int TIMING = 1;
    final int EDIT = 2;

    private TaskDBHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize floating action button
        final FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setBackgroundColor(Color.TRANSPARENT);
        //getApplicationContext().deleteDatabase("taskDB.db");
        this.handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
        this.handler.populateTaskData();
        //Add all the buttons
        int taskDataSize = TaskData.getIndexSize();
        for(int i = 0; i < taskDataSize; i++){
            generateButton(TaskData.getTask(i));
        }

        handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
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

        FirebaseLink.dbPullAll();
    }

    //executes when floating action button returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //created task return
        if (requestCode == CREATE && resultCode == Activity.RESULT_OK) {

                //create new button
                final int index = data.getIntExtra("splitObjectIndex", -1);
                if (index != -1) {
                    System.out.println(index);
                    System.out.println(TaskData.getTask(index).getName());
                }
                final SplitObject newSplitObject = TaskData.getTask(index);
                handler.addTask(newSplitObject,handler.getWritableDatabase());
                generateButton(newSplitObject);


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
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            Button curButton = (Button)linearLayout.getChildAt(extras);
            curButton.setText(newSplitObject.getName());

        //deleted task return
        } else if (requestCode == EDIT && resultCode == Activity.RESULT_CANCELED) {
            int splitObjectIndex = data.getIntExtra("splitObjectIndex", -1);
            int splitObjectID = data.getIntExtra("splitObjectID",-1);
            handler.removeTask(handler.getWritableDatabase(),splitObjectID);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.removeView(linearLayout.getChildAt(splitObjectIndex));
            numButtons--;
        }

    }

    //generates a preset task based on the provided name and description
    SplitObject presetTask(final String title, final String description, final String splitStrings[], final int taskNum) {
        Editable.Factory factory = Editable.Factory.getInstance();
        Editable taskTitle = factory.newEditable(title);
        Editable taskDescription = factory.newEditable(description);
        int numSplits = splitStrings.length;
        Editable[] splitTitles = new Editable[numSplits];
        for(int i = 0; i < numSplits; i++) {
            splitTitles[i] = factory.newEditable(splitStrings[i]);
        }
        return TaskData.addTask(taskTitle, taskDescription, splitTitles, taskNum);
    }

    //generates a button in the view for the provided SplitObject
    void generateButton(final SplitObject newObject) {
        final Button newButton = new Button(TaskActivity.this);
        newButton.setText(newObject.getName());
        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.addView(newButton,numButtons);


        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseLink.dbPullAll();
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
                taskIntent.putExtra("splitObjectIndex", ((LinearLayout) view.getParent()).indexOfChild(view));
                startActivityForResult(taskIntent, EDIT);
                return true;
            }
        });
        numButtons++;
    }

}

