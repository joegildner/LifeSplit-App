package hoefelb.csci412.wwu.lifesplit;

//Some example code from Android Studio 3.0's
//example app database

/**
 * Created by hoefelb on 5/22/19.
 */
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.ContentResolver;
import android.text.Editable;
import android.text.SpannableStringBuilder;


public class TaskDBHandler extends SQLiteOpenHelper {
    private ContentResolver myCR;

    //Make sure to increment/decrement this based on number of tasks
    private int numOfTasks = 4;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "taskDB.db";
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_SPLITS = "splits";

    //TABLE_TASKS columns
    private static final String COLUMN_TASK_ID = "task_id";
    private static final String COLUMN_TASK_NAME = "task_name";
    private static final String COLUMN_TASK_DESCRIPTION = "task_description";
    private static final String COLUMN_TASK_NUMBER_SPLITS = "task_number_splits";
    private static final String COLUMN_TASK_AVERAGE_TIME = "task_average_time";
    private static final String COLUMN_TASK_TIMES_RUN = "task_times_run";
    private static final String COLUMN_PRESET_TASK_NUM = "task_preset_num";

    //TABLE_SPLITS columns (minus TASK_ID)
    private static final String COLUMN_SPLIT_ORDER_NUMBER = "split_order_number";
    private static final String COLUMN_SPLIT_NAME = "split_name";
    private static final String COLUMN_SPLIT_AVERAGE_TIME = "split_average_time";
    private SQLiteDatabase db = getWritableDatabase();
    private int largestTaskID;

    TaskDBHandler(Context context, String name,
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
                " INTEGER," + COLUMN_PRESET_TASK_NUM + " INTEGER" + ")";
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

    void addTask(SplitObject split,SQLiteDatabase db){
    //First, add the task
        String taskSQLQuery = "Select max(task_id) from tasks";
        Cursor d = db.rawQuery(taskSQLQuery,null);
        if(d.moveToFirst())
            largestTaskID = d.getInt(0) + 1;
        this.largestTaskID = largestTaskID;

        ContentValues taskValues = new ContentValues();
        //REMEMBER TO CONSIDER DELETED TASKS FOR IDs
        taskValues.put(COLUMN_TASK_ID,largestTaskID);
        taskValues.put(COLUMN_TASK_NAME,split.getName().toString());
        taskValues.put(COLUMN_TASK_DESCRIPTION,split.getDescription().toString());
        taskValues.put(COLUMN_TASK_NUMBER_SPLITS,split.getNumSplits());
        taskValues.put(COLUMN_TASK_AVERAGE_TIME,0);
        taskValues.put(COLUMN_TASK_TIMES_RUN,0);
        taskValues.put(COLUMN_PRESET_TASK_NUM,split.getPresetNum());
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
        this.largestTaskID++;
    }

    void removeTask(SQLiteDatabase db, int i){
        //first, check if it is a preset task
        String taskSQLQuery = "Delete from " + TABLE_TASKS + " where " + COLUMN_TASK_ID + " = " + i +
        " and " +COLUMN_PRESET_TASK_NUM + " = -1";
        String where = COLUMN_TASK_ID + " = " + i +" and "+COLUMN_PRESET_TASK_NUM + " = -1";
        int rowsDeleted = db.delete(TABLE_TASKS,where,null);
        if(rowsDeleted == 1){
        taskSQLQuery = "Delete from " + TABLE_SPLITS + " where " + COLUMN_TASK_ID + " = " + i +
                " and " +COLUMN_PRESET_TASK_NUM + " = -1";
        db.execSQL(taskSQLQuery);
        }
    }

    void populateTaskData(){
        String taskSQLQuery = "Select * from " + TABLE_TASKS;
        Cursor c = db.rawQuery(taskSQLQuery,null);
        if(c.moveToFirst())
            do {
                createSplitObjectFromSQLite(c, db);
            }
            while(c.moveToNext());
            //there are tasks in sqlite. add them one at a time.

        //get max id for future inserts
        taskSQLQuery = "Select max(task_id) from tasks";
        Cursor d = db.rawQuery(taskSQLQuery,null);
        if(d.moveToFirst())
            largestTaskID = d.getInt(0) + 1;
        this.largestTaskID = largestTaskID;
    }

    private void createSplitObjectFromSQLite (Cursor c, SQLiteDatabase db){
        int task_id = c.getInt(0);
        Editable task_name = new SpannableStringBuilder(c.getString(1));
        Editable task_description = new SpannableStringBuilder(c.getString(2));
        int task_number_splits = c.getInt(3);
        Long task_average_time = c.getLong(4);
        int task_times_run = c.getInt(5);
        int task_preset_num = c.getInt(6);

        //get the split information from splits
        Editable[] task_descriptions = getTaskDescriptionsFromSQLite(task_id,task_number_splits, db);
        SplitObject  newSplitObject = TaskData.addTaskExisting(task_name,task_description,task_descriptions,task_average_time,task_times_run,task_preset_num);
        newSplitObject.setID(task_id);
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
                returnArrayPos++;
        }
        while(d.moveToNext());
        return returnArray;
    }

    private void addPresetTasksToDB(SQLiteDatabase db){
        String title = "Morning Routine";
        String description = "Typical morning routine for a user";
        String morningSplits[] = new String[3];
        morningSplits[0] = "Shower";
        morningSplits[1] = "Eat breakfast";
        morningSplits[2] = "Morning commute";
        SplitObject preset = presetTask(title, description, morningSplits,0);
        addTask(preset,db);

        title = "Groceries";
        description = "Typical steps for buying groceries";
        String grocerySplits[] = new String[5];
        grocerySplits[0] = "Write list";
        grocerySplits[1] = "Drive to store";
        grocerySplits[2] = "Collect groceries";
        grocerySplits[3] = "Checkout";
        grocerySplits[4] = "Drive home";
        preset = presetTask(title, description, grocerySplits,1);
        addTask(preset,db);

        title = "Evening Routine";
        description = "Typical evening routine for a user";
        String eveningSplits[] = new String[3];
        eveningSplits[0] = "Shower";
        eveningSplits[1] = "Brush teeth";
        eveningSplits[2] = "Sleep";
        preset = presetTask(title, description, eveningSplits,2);
        addTask(preset,db);

        title = "Cook";
        description = "Typical steps needed to cook a meal";
        String cookSplits[] = new String[4];
        cookSplits[0] = "Prep ingredients";
        cookSplits[1] = "Cook ingredients";
        cookSplits[2] = "Plate food";
        cookSplits[3] = "Serve food";
        preset = presetTask(title, description, cookSplits,3);
        addTask(preset,db);

        title = "Do Laundry";
        description = "Common steps for doing laundry";
        String thingSplits[] = new String[3];
        thingSplits[0] = "Wash";
        thingSplits[1] = "Dry";
        thingSplits[2] = "Fold";
        preset = presetTask(title, description, thingSplits,4);
        addTask(preset,db);
        TaskData.removeAllTasks();
    }


    private SplitObject presetTask(String title, String description, String splitStrings[],int presetID) {
        Editable.Factory factory = Editable.Factory.getInstance();
        Editable taskTitle = factory.newEditable(title);
        Editable taskDescription = factory.newEditable(description);
        int numSplits = splitStrings.length;
        Editable splitTitles[] = new Editable[numSplits];
        for(int i = 0; i < numSplits; i++) {
            splitTitles[i] = factory.newEditable(splitStrings[i]);
        }
        return TaskData.addTask(taskTitle, taskDescription, splitTitles,presetID);
    }
}
