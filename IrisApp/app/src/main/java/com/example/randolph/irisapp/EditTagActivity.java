package com.example.randolph.irisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.randolph.sqlDatabase.MyDBHandler;
import com.example.randolph.bluetooth.BlueToothApp;

public class EditTagActivity extends AppCompatActivity {

    public BlueToothApp BTApp;
    MyDBHandler tagDB;
    String[] tagDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);
        TextView tagName = (TextView)findViewById(R.id.tagname);
        tagName.setText("Editing Tag: " + MainActivity.tagName);
        tagDB = new MyDBHandler(this,null,null,1);

        BlueToothApp BTApp = (BlueToothApp) getApplicationContext();

        final ToggleButton toggle = (ToggleButton)findViewById(R.id.enable);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toggle.setText("Enabled");
                    enableTag();
                }else{
                    toggle.setText("Disabled");
                    disableTag();
                }
            }
        });

        final ToggleButton lost = (ToggleButton)findViewById(R.id.lost);
        lost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lost.setText("Lost!");
                    reportLost();
                }else{
                    lost.setText("Safe");
                    found();
                }
            }
        });
        tagDetails = tagDB.getTagDetails(MainActivity.tagName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_tag, menu);
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

    /**
     * TODO: Send message to Arduino, synchronize the databases
     *
     */

    public void delete(View view){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Deleted",Toast.LENGTH_SHORT).show();
        tagDB.deleteTag(Long.parseLong(tagDetails[0]));
        MainActivity.databaseUpdated = true;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    /**
     * TODO: Send message to Arduino, synchronize the databases
     *
     */
    public void reportLost(){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Reported",Toast.LENGTH_SHORT).show();
        tagDB.reportLost(Long.parseLong(tagDetails[0]));
        MainActivity.databaseUpdated = true;
    }

    public void found(){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Found",Toast.LENGTH_SHORT).show();
        tagDB.found(Long.parseLong(tagDetails[0]));
        MainActivity.databaseUpdated = true;
    }

    /**
     * TODO: Send message to Arduino, synchronize the databases
     *
     */
    public void enableTag(){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + "Enabled",Toast.LENGTH_SHORT).show();
        tagDB.enableTag(Long.parseLong(tagDetails[0]));
        MainActivity.databaseUpdated = true;
    }

    public void disableTag(){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + "Disabled",Toast.LENGTH_SHORT).show();
        tagDB.disableTag(Long.parseLong(tagDetails[0]));
        MainActivity.databaseUpdated = true;
    }


}
