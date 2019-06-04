package hoefelb.csci412.wwu.lifesplit;

import android.text.Editable;

/**
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
    private int ID;

    SplitObject(final Editable name, final Editable description, final Editable[] splitNames,final int presetNum) {
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
        this.ID = -1;
    }

    void editTask(final Editable name, final Editable description, final Editable[] splitNames) {
        this.name = name;
        this.description = description;
        this.splitNames = splitNames;
    }

    public Editable getName() {
        return this.name;
    }

    Editable getDescription() {
        return this.description;
    }

    Editable[] getSplitNamesArray(){return this.splitNames;}

    Editable getSplitName(final int index) {
        return this.splitNames[index];
    }

    int getNumSplits(){return this.splitNames.length;}

    float getAvg() {
        return this.avg;
    }

    int getPresetNum() {
        return this.presetNum;
    }

    void setAverageTime(final Float avg){
        this.avg = avg;
    }

    void setTimesRun(final int count){
        this.count = count;
    }

    void setSplitTime(final int index, long time){
        this.splitTimes[index] = time;
    }

    int getID(){return this.ID;}

    void setID(final int ID){this.ID = ID;}

    void runSplit() {
        this.count++;
    }

    void calcAvg(float newTime) {
        float newAvg = (this.avg*(this.count -1)) + newTime;
        this.avg = newAvg/this.count;
    }
}
