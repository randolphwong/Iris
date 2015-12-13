package com.example.randolph.irisapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.randolph.bluetooth.BlueToothApp;
import com.example.randolph.sqlDatabase.DBTags;
import com.example.randolph.sqlDatabase.MyDBHandler;

public class AddTagActivity extends AppCompatActivity {

    public BlueToothApp BTApp;
    private EditText tagName;
    private EditText tagID;
    private ReadFromBtTask mTask;
    MyDBHandler tagDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        tagDB = new MyDBHandler(this,null,null,1);

        tagName = (EditText)findViewById(R.id.tagname);
        tagID = (EditText)findViewById(R.id.tagid);

        BTApp = (BlueToothApp) getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {  // activity is not in the foreground but still alive
        super.onPause();
        try {
            mTask.pause();
            mTask = null;
        }
        catch (Exception ex) {
            Log.e("AddTagActivity", Log.getStackTraceString(ex));
        }
    }

    public void add(View view){
        if (!BTApp.isConnected()) {
            Toast.makeText(getApplicationContext(),"Phone not connected to reader.",Toast.LENGTH_LONG).show();
            return;
        }
        BTApp.addTag(); // tell arduino to set reader to add mode

        Toast.makeText(getApplicationContext(),"Place the tag in front of the reader.",Toast.LENGTH_LONG).show();
        tagID.setText("Waiting for reader...");

        mTask = new ReadFromBtTask();
        mTask.execute();
    }

    private class ReadFromBtTask extends AsyncTask<Void, String, Void> {

        private boolean running = true;

        public void pause() {
            running = false;
        }

        protected Void doInBackground(Void... params) {
            while (running) {
                String idString = BTApp.read();
                if (idString != null) {
                    publishProgress(idString);
                }
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String tagIDString = progress[0];
            String tagNameString = tagName.getText().toString();
            tagID.setText(tagIDString);

            if (tagDB.checkExistence(tagNameString)) {
                Toast.makeText(getApplicationContext(),"Duplicate Tags!",Toast.LENGTH_SHORT).show();
            }
            else {
                DBTags newTag = new DBTags(tagIDString,1,0,0,"",tagNameString.replace(" ",""),"");
                tagDB.addTag(newTag);
                Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
                MainActivity.databaseUpdated = true;
            }
        }
    }
}
