package hoefelb.csci412.wwu.lifesplit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class newTaskActivity extends AppCompatActivity {

    private Fragment addNewTaskFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        addNewTaskFragment = new AddNewTask();
        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction =  fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentConstraintLayout,addNewTaskFragment);
        transaction.commit();
        Bundle arguments = new Bundle();

    }
}
