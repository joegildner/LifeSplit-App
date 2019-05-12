package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class newTaskActivity extends AppCompatActivity {
    private int numOfSplits = 1;
   // private Fragment addNewTaskFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        final TextInputLayout titleText = findViewById(R.id.newTaskNameInput);
        final TextInputLayout desctiptionText = findViewById(R.id.newTaskDesciptionInput);
        final Button addSplitButton = findViewById(R.id.newTaskAddSplitButton);
        final Button resetScreenButton = findViewById(R.id.newTaskResetButton);
        final Button saveButton = findViewById(R.id.newTaskSaveButton);
        final LinearLayout splitLayout = (LinearLayout)findViewById(R.id.splitLayout);

        EditText newText = new EditText(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        newText.setLayoutParams(params);
        newText.setId(numOfSplits);
        newText.setHint("Split " + numOfSplits);
        splitLayout.addView(newText);
        numOfSplits++;

        addSplitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newText = new EditText(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                newText.setLayoutParams(params);
                newText.setId(numOfSplits+1);
                newText.setHint("Split " + numOfSplits);
                splitLayout.addView(newText);
                numOfSplits++;
            }
        });

        resetScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleText.getEditText().setText("");
                desctiptionText.getEditText().setText("");
                splitLayout.removeAllViews();
                numOfSplits = 1;
                EditText newText = new EditText(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                newText.setLayoutParams(params);
                newText.setId(numOfSplits);
                newText.setHint("Split " + numOfSplits);
                splitLayout.addView(newText);
                numOfSplits++;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save data, return to previous screen
                Editable taskTitle = titleText.getEditText().getText();
                Editable taskDescription = desctiptionText.getEditText().getText();
                Editable[] splitTitles = new Editable[numOfSplits];
                for (int i = 0; i < numOfSplits-1; i++){
                    EditText currentText = (EditText)splitLayout.getChildAt(i);
                    splitTitles[i] = currentText.getText();
                }
                //Save this data to whatever data structure is created
                newTaskActivity.this.finish();
            }
        });
        //addNewTaskFragment = new AddNewTask();
       // final FragmentManager fragmentManager = getFragmentManager();
       // FragmentTransaction transaction =  fragmentManager.beginTransaction();
       // transaction.replace(R.id.fragmentConstraintLayout,addNewTaskFragment);
       // transaction.commit();
      //  Bundle arguments = new Bundle();

    }
}
