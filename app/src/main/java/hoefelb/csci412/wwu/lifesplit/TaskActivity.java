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
        getApplicationContext().deleteDatabase("taskDB.db");
        //System.exit(0);
        this.handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
        this.handler.populateTaskData();
        //Add all the buttons
        int taskDataSize = TaskData.getIndexSize();
        for(int i = 0; i < taskDataSize; i++){
            generateButton(TaskData.getTask(i));
        }

        handler = new TaskDBHandler(getApplicationContext(), null, null, 1);
//        Editable[] e = new Editable[1];
//        e[0] = Editable.Factory.getInstance().newEditable("Split 1");
        //handler.addTask(new SplitObject(Editable.Factory.getInstance().newEditable("Testtask"),Editable.Factory.getInstance().newEditable("TestTaskDesc"),e));
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

        generatePresetTasks();
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

    //generates the preset tasks
    void generatePresetTasks() {
        //Done in TaskDBHandler.java


//        String title = "Morning Routine";
//        String description = "Typical morning routine for a user";
//        String morningSplits[] = new String[3];
//        morningSplits[0] = "Shower";
//        morningSplits[1] = "Eat breakfast";
//        morningSplits[2] = "Morning commute";
//        SplitObject preset = presetTask(title, description, morningSplits);
//        generateButton(preset);
//
//        title = "Groceries";
//        description = "Typical steps for buying groceries";
//        String grocerySplits[] = new String[5];
//        grocerySplits[0] = "Write list";
//        grocerySplits[1] = "Drive to store";
//        grocerySplits[2] = "Collect groceries";
//        grocerySplits[3] = "Checkout";
//        grocerySplits[4] = "Drive home";
//        preset = presetTask(title, description, grocerySplits);
//        generateButton(preset);
//
//        title = "Evening Routine";
//        description = "Typical evening routine for a user";
//        String eveningSplits[] = new String[3];
//        eveningSplits[0] = "Shower";
//        eveningSplits[1] = "Brush teeth";
//        eveningSplits[2] = "Sleep";
//        preset = presetTask(title, description, eveningSplits);
//        generateButton(preset);
//
//        title = "Cook";
//        description = "Typical steps needed to cook a meal";
//        String cookSplits[] = new String[4];
//        cookSplits[0] = "Prep ingredients";
//        cookSplits[1] = "Cook ingredients";
//        cookSplits[2] = "Plate food";
//        cookSplits[3] = "Serve food";
//        preset = presetTask(title, description, cookSplits);
//        generateButton(preset);
//
//        title = "Idk man";
//        description = "Someone come up with another of these";
//        String thingSplits[] = new String[2];
//        thingSplits[0] = "thing1";
//        thingSplits[1] = "thing2";
//        preset = presetTask(title, description, thingSplits);
//        generateButton(preset);
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
    void generateButton(SplitObject newObject) {
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

