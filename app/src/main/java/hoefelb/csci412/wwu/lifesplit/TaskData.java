package hoefelb.csci412.wwu.lifesplit;

import android.text.Editable;

import java.util.ArrayList;

/**
 * Created by wilso279 on 5/13/19.
 */

class TaskData {

    private static ArrayList<SplitObject> taskData;

    static void init(){
        taskData = new ArrayList<>();
    }

    static void addTask(Editable taskTitle, Editable taskDescription, Editable[] splitTitles) {
        SplitObject newSplitObject = new SplitObject(taskTitle, taskDescription, splitTitles);

        taskData.add(newSplitObject);
    }

    static int getIndex(SplitObject task) {
        return taskData.indexOf(task);
    }

    static SplitObject getTask(int index) {
        return taskData.get(index);
    }

    static void removeTask(int index) {
        taskData.remove(index);
    }
}
