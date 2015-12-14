package com.example.randolph.irisapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randolph.bluetooth.BlueToothApp;

public class ThresholdActivity extends AppCompatActivity {

    public BlueToothApp BTApp;
    private ReadFromBtTask1 mTask1;
    private TextView thresholdValue;
    private EditText newThreshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BTApp = (BlueToothApp) getApplicationContext();

        thresholdValue = (TextView)findViewById(R.id.thresholdValue);
        getThreshold();


    }

    @Override
    public void onPause() {  // activity is not in the foreground but still alive
        super.onPause();
        destroyTask();
    }

    private void startTask() {
        if (mTask1 == null) {
            mTask1 = new ReadFromBtTask1();
            mTask1.execute();
        }
    }

    private void destroyTask() {
        try {
            mTask1.pause();
            mTask1.cancel(true);
            mTask1 = null;

        }
        catch (Exception ex) {
            Log.e("ThresholdActivity", Log.getStackTraceString(ex));
        }
    }

    public void getThreshold(){

        BTApp.getThreshold();
        startTask();
    }

    public void setNewThreshold(View view){

        BTApp.setThreshold();

        newThreshold = (EditText)findViewById(R.id.newThreshold);
        BTApp.write(newThreshold.getText().toString());

        getThreshold();

        Toast.makeText(this,"Threshold updated!",Toast.LENGTH_LONG).show();



    }

    private void updateThreshold(String threshold) {
        thresholdValue.setText(threshold);
        destroyTask();
    }

    private class ReadFromBtTask1 extends AsyncTask<Void, String, Void> {

        private boolean running = true;

        public void pause() {
            running = false;
        }

        protected Void doInBackground(Void... params) {
            Log.e("AsynTask","Running");
            while (running && !isCancelled()) {
                String idString = BTApp.read();

                if (idString != null) {
                    publishProgress(idString);
                    break;
                }
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String thresholdValueString = progress[0];
            Log.e("checkThreshold",thresholdValueString);

            updateThreshold(thresholdValueString);
        }
    }
}






