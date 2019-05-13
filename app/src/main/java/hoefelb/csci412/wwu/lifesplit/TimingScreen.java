package hoefelb.csci412.wwu.lifesplit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;



public class TimingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent context = getIntent();
        String title = context.getStringExtra("title");
        System.out.println(title);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.taskName);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

    }

}
