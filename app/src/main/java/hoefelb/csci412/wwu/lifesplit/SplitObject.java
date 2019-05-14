package hoefelb.csci412.wwu.lifesplit;

import android.text.Editable;

/**
 * Created by wilso279 on 5/13/19.
 */

public class SplitObject {
    private Editable name;
    private Editable description;
    private int count;
    private float avg;
    private Editable[] splitNames;
    private float[] splitTimes;

    public SplitObject(final Editable name, final Editable description, final Editable[] splitNames) {
        this.name = name;
        this.description = description;
        this.splitNames = splitNames;
        this.splitTimes = new float[splitNames.length];
        for(int i = 0; i < splitNames.length; i++) {
            this.splitTimes[i] = 0;
        }
        this.count = 0;
        this.avg = 0;
    }

    public Editable getName() {
        return this.name;
    }

    public Editable getDescription() {
        return this.description;
    }

    public Editable getSplitName(final int index) {
        return this.splitNames[index];
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
