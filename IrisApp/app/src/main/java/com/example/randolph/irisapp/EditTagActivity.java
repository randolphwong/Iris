package com.example.randolph.irisapp;

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

public class EditTagActivity extends AppCompatActivity {

    MyDBHandler tagDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);
        TextView tagName = (TextView)findViewById(R.id.tagname);
        tagName.setText("Editing Tag: " + MainActivity.tagName);
        final ToggleButton toggle = (ToggleButton)findViewById(R.id.enable);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            /**
             * TODO: Implement onCheckedChanged method. When the toggle button change status, change the status enabled/disabled of this tag
             *
             */
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) toggle.setText("Enabled");
                else toggle.setText("Disabled");
            }
        });
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
     * TODO: Implement delete function. When the delete method is called, delete that tag from database
     *
     */

    public void delete(View view){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Deleted",Toast.LENGTH_SHORT).show();
        String[] tagDetails = tagDB.getTagDetails(MainActivity.tagName);
        tagDB.deleteTag(Long.parseLong(tagDetails[0]));
    }

    /**
     * TODO: Implement report lost function. When the reportLost method is called, mark the corresponding tag in database as lost
     * @param view
     */
    public void reportLost(View view){
        Toast.makeText(getApplicationContext(),MainActivity.tagName + " Reported",Toast.LENGTH_SHORT).show();
        String[] tagDetails = tagDB.getTagDetails(MainActivity.tagName);

    }


}
