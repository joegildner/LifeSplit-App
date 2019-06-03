package hoefelb.csci412.wwu.lifesplit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

//Timer inspired by https://www.c-sharpcorner.com/article/creating-stop-watch-android-application-tutorial/

public class TimingScreen extends AppCompatActivity {

    public String[] names = {"Cook food", "Eat", "Put Away Dishes"};
    public ArrayList<Editable> splitNames;

    private RecyclerView splitItems;
    private int splitObjectIndex;

    private boolean isPaused = true;
    private boolean isStarted = false;
    private boolean isCompleted = false;
    private int currentSplitIndex = 0;

    public TextView timer;
    public TextView description;
    public Button pauseButton;
    public Button splitButton;

    private long totalTime=0;
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
        splitObjectIndex = context.getIntExtra("splitObjectIndex",-1);
        if (splitObjectIndex == -1){
            System.out.println("ERROR - ID not found");
        }
        final SplitObject  splitObject = TaskData.getTask(splitObjectIndex);
        Editable title = splitObject.getName();
        splitNames = new ArrayList<Editable>(Arrays.asList(splitObject.getSplitNamesArray()));
        //splitNames.addAll(Arrays.asList(splitObject.getSplitNamesArray()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.taskName);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        splitItems = (RecyclerView) findViewById(R.id.split_views);
        final SplitAdapter sAdapter = new SplitAdapter(splitNames);
        splitItems.setAdapter(sAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        splitItems.setLayoutManager(linearLayoutManager);

        timer = (TextView)findViewById(R.id.timerText);
        pauseButton = (Button) findViewById(R.id.pause_button);
        splitButton = (Button) findViewById(R.id.split_button);
        description = (TextView) findViewById(R.id.taskDescription);

        TextView localAvg = findViewById(R.id.localAvg);
        TextView globalAvg = findViewById(R.id.globalAvg);
        localAvg.setText("Average: " + toTimeFormat((long)splitObject.getAvg()));
        int preset = splitObject.getPresetNum();
        if(preset != -1) {
            globalAvg.setText("Global Average: " + Float.toString(FirebaseLink.getGlobalAvg(preset)));
        } else {
            globalAvg.setText("");
        }

        handler = new Handler();
        timer.setText("--:--:--");
        description.setText(splitObject.getDescription());

        splitButton.setOnClickListener(splitButtonListener);

        pauseButton.setOnClickListener(pauseButtonListener);

    }

    private View.OnClickListener splitButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            if(isCompleted){
                //Save all the split data, get ready for handing back to parent activity
                //Perhaps keep the bottom timing view as total time, then pull value from that?
                Intent returnIntent = getIntent();
                returnIntent.putExtra("splitObjectIndex",splitObjectIndex);
                returnIntent.putExtra("totalTimeLong",totalTime);
                setResult(Activity.RESULT_OK, returnIntent);
                TimingScreen.this.finish();
            }
            else if(isPaused&&!isStarted) {
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
                    isCompleted = true;
                    handler.removeCallbacks(runnable);
                    System.out.println(totalTime);
                    timer.setText(toTimeFormat(totalTime));
                    splitButton.setBackgroundColor(Color.CYAN);
                    splitButton.setText("Save");
                    pauseButton.setText("Reset");
                    isPaused=true;
                    final SplitObject  splitObject = TaskData.getTask(splitObjectIndex);
                    splitObject.runSplit();
                    splitObject.calcAvg(totalTime);

                }
                //CHECK FOR LAST SPLIT

            }
        }
    };

    private View.OnClickListener pauseButtonListener = new View.OnClickListener() {
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
                //Reset all the timers and buttons
                splitButton.setText("Start");
                splitButton.setBackgroundColor(Color.parseColor("#45c15c"));
                pauseButton.setText("Pause");
                isPaused= true;
                isStarted = false;
                isCompleted = false;
                timeBuff = 0;
                totalTime = 0;
                for(int i = 0; i < currentSplitIndex; i++){
                    LinearLayout currentSplit = (LinearLayout) splitItems.getChildAt(i);
                    TextView splitTextView = (TextView) currentSplit.getChildAt(1);
                    splitTextView.setText("--:--:--");
                }
                timer.setText("--:--:--");
                currentSplitIndex = 0;
            }
        }
    };


    //to get current split's time, formatted as --:--:--
    String toTimeFormat(){
        milSecs = SystemClock.uptimeMillis() - startTime;
        //System.out.println(SystemClock.uptimeMillis());
        //System.out.println(milSecs);
        totalTime+= milSecs;

        upTime = timeBuff + milSecs;
        hours = TimeUnit.MILLISECONDS.toHours(upTime);
        mins = TimeUnit.MILLISECONDS.toMinutes(upTime)-hours*60;
        secs = TimeUnit.MILLISECONDS.toSeconds(upTime)-3600*hours-60*mins;

        String timerText = String.format("%02d:%02d:%02d", hours, mins,secs);

        return timerText;
    }


    //Overloaded method for when miliseconds is known, to format as --:--:--
    String toTimeFormat(long time){
        upTime = timeBuff + milSecs;
        hours = TimeUnit.MILLISECONDS.toHours(time);
        mins = TimeUnit.MILLISECONDS.toMinutes(time)-hours*60;
        secs = TimeUnit.MILLISECONDS.toSeconds(time)-3600*hours-60*mins;

        String timerText = String.format("%02d:%02d:%02d", hours, mins,secs);

        return timerText;
    }

    public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.ViewHolder> {


        class ViewHolder extends RecyclerView.ViewHolder{

            TextView splitNameView;
            TextView splitTiming;

            ViewHolder(View itemView){
                super(itemView);

                splitNameView = (TextView) itemView.findViewById(R.id.split_name);
                splitTiming = (TextView) itemView.findViewById(R.id.split_timing);


            }

        }

        private List<Editable> mSplitNames;

        SplitAdapter(List<Editable> splitNames){
            mSplitNames = splitNames;
        }

        public SplitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int vType){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View splitView = inflater.inflate(R.layout.split_item, parent, false);

            return new ViewHolder(splitView);
        }

        public void onBindViewHolder(SplitAdapter.ViewHolder vHolder, int position){
            Editable thisSplit = mSplitNames.get(position);
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