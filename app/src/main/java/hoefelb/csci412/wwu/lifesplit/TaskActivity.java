package hoefelb.csci412.wwu.lifesplit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button task1Button = findViewById(R.id.task1);
        task1Button.setBackgroundColor(Color.TRANSPARENT);
        task1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TaskActivity.this, TimingScreen.class));
                //findViewById(R.id.text_view).setVisibility(View.INVISIBLE);
            }
        });

        final Button task2Button = findViewById(R.id.task2);
        task2Button.setBackgroundColor(Color.TRANSPARENT);
        task2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TaskActivity.this, TimingScreen.class));
                //findViewById(R.id.text_view).setVisibility(View.INVISIBLE);
            }
        });

        final Button task3Button = findViewById(R.id.task3);
        task3Button.setBackgroundColor(Color.TRANSPARENT);
        task3Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TaskActivity.this, newTaskActivity.class));
                //findViewById(R.id.text_view).setVisibility(View.INVISIBLE);
            }
        });
    }
}
