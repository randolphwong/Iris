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
    private String tagID;
    private String tagColumnID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);
        TextView tagName = (TextView)findViewById(R.id.tagname);
        tagName.setText("Editing Tag: " + MainActivity.tagName);
        tagDB = new MyDBHandler(this,null,null,1);

        BTApp = (BlueToothApp) getApplicationContext();

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
        tagID = tagDetails[1];
        tagColumnID = tagDetails[0];
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

    public void delete(View view){
        if (!BTApp.isConnected()) {
            Toast.makeText(getApplicationContext(),"Phone not connected to reader.",Toast.LENGTH_LONG).show();
            return;
        }
        BTApp.deleteTag();
        BTApp.write(tagID);
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Deleted",Toast.LENGTH_SHORT).show();
        tagDB.deleteTag(Long.parseLong(tagColumnID));
        MainActivity.databaseUpdated = true;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void reportLost(){
        if (!BTApp.isConnected()) {
            Toast.makeText(getApplicationContext(),"Phone not connected to reader.",Toast.LENGTH_LONG).show();
            return;
        }
        BTApp.loseTag();
        BTApp.write(tagID);
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Reported",Toast.LENGTH_SHORT).show();
        tagDB.reportLost(Long.parseLong(tagColumnID));
        MainActivity.databaseUpdated = true;
    }

    public void found(){
        if (!BTApp.isConnected()) {
            Toast.makeText(getApplicationContext(),"Phone not connected to reader.",Toast.LENGTH_LONG).show();
            return;
        }
        BTApp.foundTag();
        BTApp.write(tagID);
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Found",Toast.LENGTH_SHORT).show();
        tagDB.found(Long.parseLong(tagColumnID));
        MainActivity.databaseUpdated = true;
    }

    public void enableTag(){
        if (!BTApp.isConnected()) {
            Toast.makeText(getApplicationContext(),"Phone not connected to reader.",Toast.LENGTH_LONG).show();
            return;
        }
        BTApp.enableTag();
        BTApp.write(tagID);
        Toast.makeText(getApplicationContext(),MainActivity.tagName + "Enabled",Toast.LENGTH_SHORT).show();
        tagDB.enableTag(Long.parseLong(tagColumnID));
        MainActivity.databaseUpdated = true;
    }

    public void disableTag(){
        if (!BTApp.isConnected()) {
            Toast.makeText(getApplicationContext(),"Phone not connected to reader.",Toast.LENGTH_LONG).show();
            return;
        }
        BTApp.disableTag();
        BTApp.write(tagID);
        Toast.makeText(getApplicationContext(),MainActivity.tagName + "Disabled",Toast.LENGTH_SHORT).show();
        tagDB.disableTag(Long.parseLong(tagColumnID));
        MainActivity.databaseUpdated = true;
    }


}
