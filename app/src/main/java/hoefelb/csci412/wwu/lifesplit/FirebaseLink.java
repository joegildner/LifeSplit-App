package hoefelb.csci412.wwu.lifesplit;


import android.text.Editable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by wilso279 on 5/29/19.
 */

public class FirebaseLink {

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

    public static void dbGet() {

    }

}
