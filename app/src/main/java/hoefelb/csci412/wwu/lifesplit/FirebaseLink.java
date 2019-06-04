package hoefelb.csci412.wwu.lifesplit;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//handles all firebase connections and related comments
class FirebaseLink {
    private static float[] taskAvg = new float[5];
    private static int[] taskCount = new int[5];

    //updates db at the end of a split
    static void dbUpdate(final int index, final float time) {
        if(index == -1) {
            return;
        }
        int count = taskCount[index] + 1;
        float newAvg = ((taskAvg[index] * (count-1)) + time) / count;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String objname = "task" + (index);
        DatabaseReference ref = db.getReference(objname + "/avg");
        ref.setValue(newAvg);
        ref = db.getReference(objname + "/count");
        ref.setValue(count);
        taskAvg[index] = newAvg;
        taskCount[index] = count;
    }

    //pulls data for all tasks
    static void dbPullAll() {
        for(int i = 0; i < 5; i++) {
            dbPull(i);
        }
    }

    //initiates a pull from db for a specific task
    private static void dbPull(final int index) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(("task"+ index + "/avg"));
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Float value = dataSnapshot.getValue(Float.class);
                taskAvg[index] = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        ref = db.getReference(("task"+ index + "/count"));
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(Integer.class);
                taskCount[index] = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    //gets locally stored global avg value. Pull to update value
    static float getGlobalAvg(final int index) {
        return taskAvg[index];
    }
}
