package hoefelb.csci412.wwu.lifesplit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//Timer inspired by https://www.c-sharpcorner.com/article/creating-stop-watch-android-application-tutorial/

public class TimingScreen extends AppCompatActivity {

    public String[] names = {"Cook food", "Eat", "Put Away Dishes"};
    public ArrayList<String> splitNames = new ArrayList<String>(Arrays.asList(names));

    public TextView timer;
    public Button pauseButton;
    public Button splitButton;
    long milSecs;
    long startTime;
    long timeBuff;
    long upTime = 0L;
    Handler handler;
    long hours;
    long secs;
    long mins;

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

        final RecyclerView splitItems = (RecyclerView) findViewById(R.id.split_views);
        SplitAdapter sAdapter = new SplitAdapter(splitNames);
        splitItems.setAdapter(sAdapter);
        splitItems.setLayoutManager(new LinearLayoutManager(this));

        timer = (TextView)findViewById(R.id.timerText);
        pauseButton = (Button) findViewById(R.id.pause_button);
        splitButton = (Button) findViewById(R.id.split_button);
        handler = new Handler();

        splitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timeBuff += milSecs;
                handler.removeCallbacks(runnable);
            }
        });

    }

    public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.ViewHolder> {


        public class ViewHolder extends RecyclerView.ViewHolder{

            public TextView splitNameView;
            public TextView splitTiming;

            public ViewHolder(View itemView){
                super(itemView);

                splitNameView = (TextView) itemView.findViewById(R.id.split_name);
                splitTiming = (TextView) itemView.findViewById(R.id.split_timing);


            }

        }

        private List<String> mSplitNames;

        public SplitAdapter(List<String> splitNames){
            mSplitNames = splitNames;
        }

        public SplitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int vType){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View splitView = inflater.inflate(R.layout.split_item, parent, false);

            return new ViewHolder(splitView);
        }

        public void onBindViewHolder(SplitAdapter.ViewHolder vHolder, int position){
            String thisSplit = mSplitNames.get(position);
            TextView nameText = vHolder.splitNameView;

            nameText.setText(thisSplit);
        }

        public int getItemCount(){
            return mSplitNames.size();
        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milSecs = SystemClock.uptimeMillis() - startTime;
            upTime = timeBuff + milSecs;

            hours = TimeUnit.MILLISECONDS.toHours(upTime);
            mins = TimeUnit.MILLISECONDS.toMinutes(upTime)-hours*60;
            secs = TimeUnit.MILLISECONDS.toSeconds(upTime)-3600*hours-60*mins;

            String timerText = String.format("%02d:%02d:%02d", hours, mins,secs);

            timer.setText(timerText);

            handler.postDelayed(this, 0);
        }
    };

}