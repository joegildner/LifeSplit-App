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



public class TaskDBHandler extends SQLiteOpenHelper {
    private ContentResolver myCR;

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

    public TaskDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        myCR = context.getContentResolver();
        SQLiteDatabase db = getWritableDatabase();

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
                "TEXT," + COLUMN_SPLIT_AVERAGE_TIME + "LONG" + ")";
        db.execSQL(CREATE_SPLITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //not implemented for this project
    }

    public void populateTaskData(){


    }
}
