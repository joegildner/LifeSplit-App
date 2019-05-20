package hoefelb.csci412.wwu.lifesplit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
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
    private boolean isPaused = true;
    private boolean isStarted = false;
    private boolean isCompleted = false;
    private int currentSplitIndex = 0;
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
        final SplitAdapter sAdapter = new SplitAdapter(splitNames);
        splitItems.setAdapter(sAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        splitItems.setLayoutManager(linearLayoutManager);

        timer = (TextView)findViewById(R.id.timerText);
        pauseButton = (Button) findViewById(R.id.pause_button);
        splitButton = (Button) findViewById(R.id.split_button);

        handler = new Handler();
        timer.setText("--:--:--");

        splitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isCompleted)
                    return;
                if(isPaused&&!isStarted){
                    //Start the stopwatch for the first time
                    isPaused = false;
                    isStarted = true;
                    splitButton.setText("Split");
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                }
                else if(isPaused){
                    //Resume from last point.
                    isPaused = false;
                    splitButton.setText("Split");
                    pauseButton.setText("Pause");
                    startTime = SystemClock.uptimeMillis()-timeBuff;
                    timeBuff=0;
                    handler.postDelayed(runnable, 0);
                }
                else{
                    //Take a split
                    //reset timeBuff so pausing previous split will not affect future split
                    LinearLayout current =  (LinearLayout)splitItems.getChildAt(currentSplitIndex);
                    TextView splitTextView = (TextView)current.getChildAt(1);
                    splitTextView.setText(toTimeFormat());
                    handler.postDelayed(runnable, 0);
                    startTime = SystemClock.uptimeMillis();
                    timeBuff=0;
                    currentSplitIndex++;

                    if (currentSplitIndex == splitItems.getChildCount()){
                        //Save all the split data, get ready for handing back to parent activity
                        //Perhaps keep the bottom timing view as total time, then pull value from that?
                    }
                    //CHECK FOR LAST SPLIT

                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isStarted&&!isPaused){
                    //Pause the timer
                    splitButton.setText("Resume");
                    pauseButton.setText("Reset");
                    timeBuff += milSecs;
                    handler.removeCallbacks(runnable);
                    isPaused = true;
                }
                else if(isPaused){
                    //Reset all the timers.
                    splitButton.setText("Start");
                    pauseButton.setText("Pause");
                    isPaused= true;
                    isStarted = false;
                    timeBuff = 0;
                    for(int i = 0; i < currentSplitIndex; i++){
                        LinearLayout currentSplit = (LinearLayout) splitItems.getChildAt(i);
                        TextView splitTextView = (TextView) currentSplit.getChildAt(1);
                        splitTextView.setText("--:--:--");
                    }
                    timer.setText("--:--:--");
                    currentSplitIndex = 0;
                }
            }
        });

    }

    String toTimeFormat(){
        milSecs = SystemClock.uptimeMillis() - startTime;
        System.out.println(SystemClock.uptimeMillis());
        System.out.println(milSecs);
        upTime = timeBuff + milSecs;

        hours = TimeUnit.MILLISECONDS.toHours(upTime);
        mins = TimeUnit.MILLISECONDS.toMinutes(upTime)-hours*60;
        secs = TimeUnit.MILLISECONDS.toSeconds(upTime)-3600*hours-60*mins;

        String timerText = String.format("%02d:%02d:%02d", hours, mins,secs);

        return timerText;
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