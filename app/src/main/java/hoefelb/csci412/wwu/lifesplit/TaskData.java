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

    static SplitObject addTask(final Editable taskTitle, final Editable taskDescription, final Editable[] splitTitles) {
        SplitObject newSplitObject = new SplitObject(taskTitle, taskDescription, splitTitles);

        taskData.add(newSplitObject);
        return newSplitObject;
    }

    static SplitObject editTask(final int index, final Editable taskTitle, final Editable taskDescription, final Editable[] splitTitles) {

        SplitObject current = getTask(index);
        current.editTask(taskTitle, taskDescription, splitTitles);
        return current;
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
