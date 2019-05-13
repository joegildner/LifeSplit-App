package hoefelb.csci412.wwu.lifesplit;

/**
 * Created by wilso279 on 5/13/19.
 */

public class SplitObject {
    private String name;
    private String description;
    private int count;
    private float avg;
    private String[] splitNames;
    private float[] splitTimes;

    public SplitObject(final String name, final String description, final String[] splitNames) {
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

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSplitName(final int index) {
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
        for(int i = 0; i < splitTimes.length; i++) {
            total += splitTimes[i];
        }
        this.avg = total/this.count;
        return this.avg;
    }
}
