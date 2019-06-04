package hoefelb.csci412.wwu.lifesplit;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TitleScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);

        final Button titleButton = findViewById(R.id.title_button);
        titleButton.setBackgroundColor(Color.TRANSPARENT);
        titleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(TitleScreen.this, TaskActivity.class));
            }
        });
    }

}
