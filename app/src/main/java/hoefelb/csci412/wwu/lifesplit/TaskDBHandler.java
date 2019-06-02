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
    private final SQLiteDatabase db;
    private int largestTaskID;

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

    public void addTask(SplitObject split){
    //First, add the task

        ContentValues taskValues = new ContentValues();
        //REMEMBER TO CONSIDER DELETED TASKS FOR IDs
        taskValues.put(COLUMN_TASK_ID,numOfTasks);
        taskValues.put(COLUMN_TASK_NAME,split.getName().toString());
        taskValues.put(COLUMN_TASK_DESCRIPTION,split.getDescription().toString());
        taskValues.put(COLUMN_TASK_NUMBER_SPLITS,split.getNumSplits());
        taskValues.put(COLUMN_TASK_AVERAGE_TIME,0);
        taskValues.put(COLUMN_TASK_TIMES_RUN,0);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TASKS,null,taskValues);

    //Next, add all the splits.
        for(int i = 0; i < split.getNumSplits(); i++){
            ContentValues splitValues = new ContentValues();
            taskValues.put(COLUMN_SPLIT_ORDER_NUMBER,i);
            //REMEMBER TO CONSIDER DELETED TASKS FOR IDs
            splitValues.put(COLUMN_TASK_ID,numOfTasks);
            splitValues.put(COLUMN_SPLIT_NAME,split.getSplitName(i).toString());
            splitValues.put(COLUMN_SPLIT_AVERAGE_TIME,0.0);

            db.insert(TABLE_SPLITS,null,splitValues);


        }

        numOfTasks++;


    }

    public void populateTaskData(SQLiteDatabase db){
        String taskSQLQuery = "Select * from " + TABLE_TASKS;
        Cursor c = db.rawQuery(taskSQLQuery,null);
        if(c.moveToFirst()){
            do
                createSplitObjectFromSQLite(c,db);
            while(c.moveToNext());
            //there are tasks in sqlite. add them one at a time.
        }




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
        TaskData.addTask(task_name,task_description,task_descriptions,task_average_time,task_times_run, -1);

    }

    private Editable[] getTaskDescriptionsFromSQLite(int id,int numSplits, SQLiteDatabase db){
        Editable[] returnArray = new Editable[numSplits];
        int returnArrayPos = 0;
        String taskSQLQuery = "Select * from " + TABLE_SPLITS +
                                " Where task_id = \"" + id+"\"";
        Cursor d = db.rawQuery(taskSQLQuery,null);
        do{
            if(d.moveToFirst()){
                returnArray[returnArrayPos] = new SpannableStringBuilder(d.getString(2));
            }
        }
        while(d.moveToNext());
        return returnArray;
    }

    private void addPresetTasksToDB(SQLiteDatabase db){

    }

}
