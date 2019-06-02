package hoefelb.csci412.wwu.lifesplit;


import android.text.Editable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by wilso279 on 5/29/19.
 */

public class FirebaseLink {
    static float[] taskAvg = new float[5];
    static int[] taskCount = new int[5];

    //add complete object to firebase
    public static void dbAdd(SplitObject newObject, int index) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String objname = "task" + (index);
        DatabaseReference ref = db.getReference(objname + "/name");
        ref.setValue( newObject.getName().toString());
        ref = db.getReference(objname + "/description");
        ref.setValue( newObject.getDescription().toString());
        ref = db.getReference(objname + "/count");
        ref.setValue( newObject.getCount());
        ref = db.getReference(objname + "/avg");
        ref.setValue( newObject.getAvg());

        float[] splitTimes = newObject.getSplitTimesArray();
        for(int i = 0; i < splitTimes.length; i++) {
            ref = db.getReference(objname + "/splitTimes/splitTime" + i);
            ref.setValue(splitTimes[i]);
        }

        Editable[] splitNames = newObject.getSplitNamesArray();
        for(int i = 0; i < splitNames.length; i++) {
            ref = db.getReference(objname + "/splitNames/splitName" + i);
            ref.setValue(splitNames[i].toString());
        }
    }

    public static void dbUpdate(final int index, final int time) {
        int count = taskCount[index] + 1;
        float newAvg = ((taskAvg[index] * (count-1)) + time) / count;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String objname = "task" + (index);
        DatabaseReference ref = db.getReference(objname + "/avg");
        ref.setValue(Float.toString(newAvg));
        ref = db.getReference(objname + "/count");
        ref.setValue(Integer.toString(count));
        taskAvg[index] = newAvg;
        taskCount[index] = count;
    }

    public static void populate() {
        for(int i = 0; i < 4; i++) {
            taskAvg[i] = 0;
            taskCount[i] = 0;
        }
    }



    public static void dbPull(final int index) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(("task"+ index + "/avg"));
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                taskAvg[index] = Integer.valueOf(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        ref = db.getReference(("task"+ index + "/count"));
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                taskCount[index] = Integer.valueOf(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public static float getGlobalAvg(int index) {
        return taskAvg[index];
    }
}
