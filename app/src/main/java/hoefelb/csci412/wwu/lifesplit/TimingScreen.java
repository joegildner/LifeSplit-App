package hoefelb.csci412.wwu.lifesplit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TimingScreen extends AppCompatActivity {

    public String[] names = {"Cook food", "Eat", "Put Away Dishes"};
    public ArrayList<String> splitNames = new ArrayList<String>(Arrays.asList(names));

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

        RecyclerView splitItems = (RecyclerView) findViewById(R.id.split_views);
        SplitAdapter sAdapter = new SplitAdapter(splitNames);
        splitItems.setAdapter(sAdapter);
        splitItems.setLayoutManager(new LinearLayoutManager(this));

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

}
