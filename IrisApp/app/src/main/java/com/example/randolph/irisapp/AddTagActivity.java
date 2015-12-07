package com.example.randolph.irisapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.randolph.sqlDatabase.DBTags;
import com.example.randolph.sqlDatabase.MyDBHandler;

public class AddTagActivity extends AppCompatActivity {
    MyDBHandler tagDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        tagDB = new MyDBHandler(this,null,null,1);
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

    /**
     * TODO: Test add tag function. When the reportLost method is called, mark the corresponding tag in database as lost
     * @param view
     */

    public void add(View view){
        EditText name = (EditText)findViewById(R.id.tagname);
        EditText id = (EditText)findViewById(R.id.tagid);
        DBTags newTag = new DBTags(id.getText().toString(),1,0,0,"",name.getText().toString(),"");
        if(tagDB == null) Log.e("TAG", "Null tagDB");
        tagDB.addTag(newTag);
        Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
    }
}
