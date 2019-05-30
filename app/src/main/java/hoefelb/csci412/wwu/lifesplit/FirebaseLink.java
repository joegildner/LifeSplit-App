package hoefelb.csci412.wwu.lifesplit;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by wilso279 on 5/29/19.
 */

public class FirebaseLink {

    public static void dbAdd(SplitObject newObject) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        ref.setValue("hello world");
    }
}
