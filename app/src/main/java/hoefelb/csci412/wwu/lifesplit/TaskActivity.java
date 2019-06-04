package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


//main menu task for app
public class TaskActivity extends AppCompatActivity {

    private int numButtons = 0;

    final int CREATE = 0;
    final int TIMING = 1;
    final int EDIT = 2;
    final int CREATE_MAP = 3;
    final boolean DELETE_DATABASE = false;
    final int DELETE_TASK = 2;

    private TaskDBHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize floating action button
        final FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setBackgroundColor(Color.TRANSPARENT);
        if(DELETE_DATABASE) {
            getApplicationContext().deleteDatabase("taskDB.db");
        }
        this.handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
        this.handler.populateTaskData();
        //Add all the buttons
        int taskDataSize = TaskData.getIndexSize();

        System.out.println("THERE ARE THIS MANY TASKS "+taskDataSize);


        for (int i = 0; i < taskDataSize; i++) {

            SplitObject thisObject = TaskData.getTask(i);

            if (thisObject.getDescription().toString().equals(getResources().getString(R.string.map_activity_hash))) {
                generateMapButton(thisObject);
            } else {
                generateButton(thisObject);
            }
        }

        handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(TaskActivity.this, newTaskActivity.class);
                startActivityForResult(newTaskIntent, CREATE);
            }
        });

        final FloatingActionButton mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(TaskActivity.this, newMapsTaskActivity.class);
                startActivityForResult(newTaskIntent, CREATE_MAP);
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
            handler.addTask(newSplitObject, handler.getWritableDatabase());
            generateButton(newSplitObject);

            //timing screen return
        } else if (requestCode == CREATE_MAP && resultCode == Activity.RESULT_OK) {

            //create new button
            final int index = data.getIntExtra("splitObjectIndex", -1);
            if (index != -1) {
                System.out.println(index);
                System.out.println(TaskData.getTask(index).getName());
            }
            final SplitObject newSplitObject = TaskData.getTask(index);
            newSplitObject.setIsMapTask(true);
            handler.addTask(newSplitObject, handler.getWritableDatabase());
            generateMapButton(newSplitObject);


            //timing screen return
        } else if (requestCode == TIMING && resultCode == Activity.RESULT_OK) {
            int splitObjectID = data.getIntExtra("splitObjectID",-1);
            Long averageTaskTime = data.getLongExtra("totalTimeLong", -1);
            int totalTimesRun = data.getIntExtra("totalTimesRun", -1);
            if(splitObjectID != -1)
                handler.updadateAverageTime(handler.getWritableDatabase(),splitObjectID,averageTaskTime,totalTimesRun);


            //edited task return
        } else if (requestCode == EDIT && resultCode == Activity.RESULT_OK) {
            int extras = data.getIntExtra("splitObjectIndex", -1);
            final SplitObject newSplitObject = TaskData.getTask(extras);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            Button curButton = (Button) linearLayout.getChildAt(extras);
            curButton.setText(newSplitObject.getName());

            //deleted task return
        } else if (requestCode == EDIT && resultCode == DELETE_TASK) {
            int splitObjectIndex = data.getIntExtra("splitObjectIndex", -1);
            int splitObjectID = data.getIntExtra("splitObjectID", -1);
            handler.removeTask(handler.getWritableDatabase(), splitObjectID);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.removeView(linearLayout.getChildAt(splitObjectIndex));
            numButtons--;
        }
    }

    //generates a button in the view for the provided SplitObject
    void generateButton(final SplitObject newObject) {
        final Button newButton = new Button(TaskActivity.this);
        newButton.setText(newObject.getName());
        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.addView(newButton, numButtons);


        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseLink.dbPullAll();
                Intent taskIntent = new Intent(TaskActivity.this, TimingScreen.class);
                LinearLayout parent = (LinearLayout) view.getParent();
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

    void generateMapButton(final SplitObject newObject) {
        final Button newButton = new Button(TaskActivity.this);
        newButton.setText(newObject.getName());
        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.addView(newButton, numButtons);


        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent taskIntent = new Intent(TaskActivity.this, mapsTimingScreen.class);
                LinearLayout parent = (LinearLayout) view.getParent();
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

