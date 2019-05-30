package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;



public class newTaskActivity extends AppCompatActivity {
    private int numOfSplits = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        final TextInputLayout titleText = findViewById(R.id.newTaskNameInput);
        final TextInputLayout descriptionText = findViewById(R.id.newTaskDesciptionInput);
        final Button addSplitButton = findViewById(R.id.newTaskAddSplitButton);
        final Button resetScreenButton = findViewById(R.id.newTaskResetButton);
        final Button saveButton = findViewById(R.id.newTaskSaveButton);
        final LinearLayout splitLayout = (LinearLayout)findViewById(R.id.splitLayout);
        TaskData.init();

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
                //Credit: https://stackoverflow.com/questions/43666301/android-dynamically-add-edittext-in-linear-layout
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
                descriptionText.getEditText().setText("");
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
                Editable taskDescription = descriptionText.getEditText().getText();
                Editable[] splitTitles = new Editable[numOfSplits-1];
                for (int i = 0; i < numOfSplits-1; i++){
                    EditText currentText = (EditText)splitLayout.getChildAt(i);
                    splitTitles[i] = currentText.getText();
                }
                SplitObject newSplitObject = TaskData.addTask(taskTitle,taskDescription,splitTitles);
                FirebaseLink.dbAdd(newSplitObject, TaskData.getIndex(newSplitObject));
                Intent returnIntent = getIntent();
                returnIntent.putExtra("splitObjectIndex",TaskData.getIndex(newSplitObject));
                setResult(Activity.RESULT_OK, returnIntent);
                newTaskActivity.this.finish();
            }
        });

    }
}
