package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by wilso279 on 5/27/19.
 */

public class EditTaskActivity extends AppCompatActivity {
    private final int DELETE_TASK = 2;
    private int numOfSplits = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        final TextInputLayout titleText = findViewById(R.id.newTaskNameInput);
        final TextInputLayout descriptionText = findViewById(R.id.newTaskDesciptionInput);
        final Button addSplitButton = findViewById(R.id.newTaskAddSplitButton);
        final Button resetScreenButton = findViewById(R.id.newTaskResetButton);
        final Button saveButton = findViewById(R.id.newTaskSaveButton);
        final Button deleteButton = findViewById(R.id.newTaskDeleteButton);
        final LinearLayout splitLayout = (LinearLayout)findViewById(R.id.splitLayout);

        //get data from intent
        Intent context = getIntent();
        final int splitObjectIndex = context.getIntExtra("splitObjectIndex",-1);
        if (splitObjectIndex == -1){
            System.out.println("ERROR - ID not found");
        }
        final SplitObject splitObject = TaskData.getTask(splitObjectIndex);
        Editable title = splitObject.getName();
        Editable description = splitObject.getDescription();
        Editable[] splitNames = splitObject.getSplitNamesArray();

        //add existing data
        titleText.getEditText().setText(title);
        descriptionText.getEditText().setText(description);
        for(int i = 0; i < splitNames.length; i++) {
            EditText newText = new EditText(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            newText.setLayoutParams(params);
            newText.setId(numOfSplits+1);
            newText.setText(splitNames[i]);
            splitLayout.addView(newText);
            numOfSplits++;
        }

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
                SplitObject newSplitObject = TaskData.editTask(splitObjectIndex, taskTitle,taskDescription,splitTitles);
                Intent returnIntent = getIntent();
                returnIntent.putExtra("splitObjectIndex",TaskData.getIndex(newSplitObject));
                setResult(Activity.RESULT_OK, returnIntent);
                EditTaskActivity.this.finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save data, return to previous screen
                Intent returnIntent = getIntent();
                returnIntent.putExtra("splitObjectIndex",splitObjectIndex);
                returnIntent.putExtra("splitObjectID",TaskData.getTask(splitObjectIndex).getID());
                TaskData.removeTask(splitObjectIndex);
                setResult(DELETE_TASK, returnIntent);
                EditTaskActivity.this.finish();
            }
        });

    }
}