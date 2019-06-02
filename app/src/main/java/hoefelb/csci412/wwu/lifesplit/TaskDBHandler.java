package hoefelb.csci412.wwu.lifesplit;

//Some example code from Android Studio 3.0's
//example app database

/**
 * Created by hoefelb on 5/22/19.
 */
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.ContentResolver;
import android.text.Editable;
import android.text.SpannableStringBuilder;

import com.google.android.gms.tasks.Task;


public class TaskDBHandler extends SQLiteOpenHelper {
    private ContentResolver myCR;

    //Make sure to increment/decrement this based on number of tasks
    private int numOfTasks = 4;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "taskDB.db";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_SPLITS = "splits";

    //TABLE_TASKS columns
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_TASK_DESCRIPTION = "task_description";
    public static final String COLUMN_TASK_NUMBER_SPLITS = "task_number_splits";
    public static final String COLUMN_TASK_AVERAGE_TIME = "task_average_time";
    public static final String COLUMN_TASK_TIMES_RUN = "task_times_run";

    //TABLE_SPLITS columns (minus TASK_ID)
    public static final String COLUMN_SPLIT_ORDER_NUMBER = "split_order_number";
    public static final String COLUMN_SPLIT_NAME = "split_name";
    public static final String COLUMN_SPLIT_AVERAGE_TIME = "split_average_time";
    private SQLiteDatabase db = getWritableDatabase();
    private int largestTaskID = 0;

    public TaskDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        myCR = context.getContentResolver();
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE "+ TABLE_TASKS +
                "("+COLUMN_TASK_ID+" INTEGER PRIMARY KEY," + COLUMN_TASK_NAME+
                " TEXT," + COLUMN_TASK_DESCRIPTION + " TEXT," + COLUMN_TASK_NUMBER_SPLITS +
                " INTEGER," + COLUMN_TASK_AVERAGE_TIME +" LONG," + COLUMN_TASK_TIMES_RUN +
                " INTEGER" + ")";
        db.execSQL(CREATE_TASKS_TABLE);

        String CREATE_SPLITS_TABLE = "CREATE TABLE "+ TABLE_SPLITS +
                "("+COLUMN_SPLIT_ORDER_NUMBER+" INTEGER," + COLUMN_TASK_ID +" INTEGER,"+ COLUMN_SPLIT_NAME+
                " TEXT," + COLUMN_SPLIT_AVERAGE_TIME + " LONG" + ")";
        db.execSQL(CREATE_SPLITS_TABLE);

        //Add the preset tasks
        addPresetTasksToDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //not implemented for this project
    }

    public void addTask(SplitObject split,SQLiteDatabase db){
    //First, add the task

        ContentValues taskValues = new ContentValues();
        //REMEMBER TO CONSIDER DELETED TASKS FOR IDs
        taskValues.put(COLUMN_TASK_ID,largestTaskID);
        taskValues.put(COLUMN_TASK_NAME,split.getName().toString());
        taskValues.put(COLUMN_TASK_DESCRIPTION,split.getDescription().toString());
        taskValues.put(COLUMN_TASK_NUMBER_SPLITS,split.getNumSplits());
        taskValues.put(COLUMN_TASK_AVERAGE_TIME,0);
        taskValues.put(COLUMN_TASK_TIMES_RUN,0);
        db.insert(TABLE_TASKS,null,taskValues);

    //Next, add all the splits.
        for(int i = 0; i < split.getNumSplits(); i++){
            ContentValues splitValues = new ContentValues();
            taskValues.put(COLUMN_SPLIT_ORDER_NUMBER,i);
            //REMEMBER TO CONSIDER DELETED TASKS FOR IDs
            splitValues.put(COLUMN_TASK_ID,largestTaskID);
            splitValues.put(COLUMN_SPLIT_NAME,split.getSplitName(i).toString());
            splitValues.put(COLUMN_SPLIT_AVERAGE_TIME,0.0);

            db.insert(TABLE_SPLITS,null,splitValues);


        }

        largestTaskID++;


    }

    public void populateTaskData(){
        String taskSQLQuery = "Select * from " + TABLE_TASKS;
        Cursor c = db.rawQuery(taskSQLQuery,null);
        if(c.moveToFirst())
            do {
                createSplitObjectFromSQLite(c, db);
            }
            while(c.moveToNext());
            //there are tasks in sqlite. add them one at a time.

        //get max id for future inserts
        taskSQLQuery = "Select max(" + COLUMN_TASK_ID + ") from " + TABLE_TASKS;
        c = db.rawQuery(taskSQLQuery,null);
        if(c.moveToFirst())
            largestTaskID = c.getInt(0) + 1;
    }

    private void createSplitObjectFromSQLite (Cursor c, SQLiteDatabase db){
        int task_id = c.getInt(0);
        Editable task_name = new SpannableStringBuilder(c.getString(1));
        Editable task_description = new SpannableStringBuilder(c.getString(2));
        int task_number_splits = c.getInt(3);
        Long task_average_time = c.getLong(4);
        int task_times_run = c.getInt(5);

        //get the split information from splits
        Editable[] task_descriptions = getTaskDescriptionsFromSQLite(task_id,task_number_splits, db);
        TaskData.addTaskExisting(task_name,task_description,task_descriptions,task_average_time,task_times_run);

    }

    private Editable[] getTaskDescriptionsFromSQLite(int id,int numSplits, SQLiteDatabase db){
        Editable[] returnArray = new Editable[numSplits];
        int returnArrayPos = 0;
        String taskSQLQuery = "Select * from " + TABLE_SPLITS +
                                " Where task_id = \"" + id+"\"";
        Cursor d = db.rawQuery(taskSQLQuery,null);
        if(d.moveToFirst())
        do{
                returnArray[returnArrayPos] = new SpannableStringBuilder(d.getString(2));
        }
        while(d.moveToNext());
        return returnArray;
    }

    private void addPresetTasksToDB(SQLiteDatabase db){
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
        Editable.Factory factory = Editable.Factory.getInstance();

    }


    SplitObject presetTask(String title, String description, String splitStrings[]) {
        Editable.Factory factory = Editable.Factory.getInstance();
        Editable taskTitle = factory.newEditable(title);
        Editable taskDescription = factory.newEditable(description);
        int numSplits = splitStrings.length;
        Editable[] splitTitles = new Editable[numSplits];
        for(int i = 0; i < numSplits; i++) {
            splitTitles[i] = factory.newEditable(splitStrings[i]);
        }
        return TaskData.addTask(taskTitle, taskDescription, splitTitles);
    }
}
