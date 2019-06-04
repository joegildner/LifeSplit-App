package hoefelb.csci412.wwu.lifesplit;

import android.text.Editable;

import java.util.ArrayList;

//class stores all splitObjects
class TaskData {

    private static ArrayList<SplitObject> taskData = new ArrayList<>();

    static void init(){
    }

    static SplitObject addTask(final Editable taskTitle, final Editable taskDescription, final Editable[] splitTitles, final int taskNum) {
        SplitObject newSplitObject = new SplitObject(taskTitle, taskDescription, splitTitles, taskNum);

        taskData.add(newSplitObject);
        return newSplitObject;
    }

    static SplitObject addTaskExisting(final Editable taskTitle, final Editable taskDescription, final Editable[] splitTitles,Long averageTime,int timesRun, int presetNum){
        SplitObject newSplitObject = new SplitObject(taskTitle, taskDescription, splitTitles,presetNum);
        for(int i = 0; i < splitTitles.length; i++) {
            newSplitObject.setSplitTime(i,0);
        }
        newSplitObject.setAverageTime(averageTime.floatValue());
        newSplitObject.setTimesRun(timesRun);
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

    static  int getIndexSize(){
        return taskData.size();
    }

    static SplitObject getTask(int index) {
        return taskData.get(index);
    }

    static void removeTask(int index) {
        taskData.remove(index);
    }

    static void removeAllTasks(){
        taskData = new ArrayList<>();
    }
}
