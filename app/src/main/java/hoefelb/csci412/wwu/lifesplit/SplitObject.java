package hoefelb.csci412.wwu.lifesplit;

import android.text.Editable;

/**
 * Created by wilso279 on 5/13/19.
 * Object to store task data
 */

public class SplitObject {
    private Editable name;
    private Editable description;
    private int count;
    private float avg;
    private Editable[] splitNames;
    private float[] splitTimes;
    private int presetNum;

    public SplitObject(final Editable name, final Editable description, final Editable[] splitNames, final int presetNum) {
        this.name = name;
        this.description = description;
        this.splitNames = splitNames;
        this.splitTimes = new float[splitNames.length];
        for(int i = 0; i < splitNames.length; i++) {
            this.splitTimes[i] = 0;
        }
        this.count = 0;
        this.avg = 0;
        this.presetNum = presetNum;
    }

    public SplitObject(final Editable taskTitle, final Editable taskDescription,
                       final Editable[] splitTitles, final Long averageTime, final int timesRun, final int presetNum){
        this.name = name;
        this.description = description;
        this.splitNames = splitNames;
        this.splitTimes = new float[splitNames.length];
        for(int i = 0; i < splitNames.length; i++) {
            this.splitTimes[i] = 0;
        }
        this.count = timesRun;
        this.avg = averageTime;
        this.presetNum = presetNum;

    }

    public void editTask(final Editable name, final Editable description, final Editable[] splitNames) {
        this.name = name;
        this.description = description;
        this.splitNames = splitNames;
    }

    public Editable getName() {
        return this.name;
    }

    public Editable getDescription() {
        return this.description;
    }

    public Editable[] getSplitNamesArray(){return this.splitNames;}

    public float[] getSplitTimesArray(){return this.splitTimes;}

    public Editable getSplitName(final int index) {
        return this.splitNames[index];
    }

    public int getCount() {
        return this.count;
    }

    public int getNumSplits(){return this.splitNames.length;}

    public float getAvg() {
        return this.avg;
    }

    public int getPresetNum() {
        return this.presetNum;
    }

    public float getSplitTime(final int index) {
        return this.splitTimes[index];
    }

    public void runSplit() {
        this.count++;
    }

    public float calcAvg() {
        float total = 0;
        for (float splitTime : splitTimes) {
            total += splitTime;
        }
        this.avg = total/this.count;
        return this.avg;
    }
}
