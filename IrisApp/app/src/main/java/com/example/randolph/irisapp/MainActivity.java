package com.example.randolph.irisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.randolph.sqlDatabase.MyDBHandler;

public class MainActivity extends AppCompatActivity {

    MyDBHandler tagDB;
    private ListView tagList;
    private ArrayAdapter arrayAdapter;
    public static String tagName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        final TempDatabase database = new TempDatabase();
        String[] data = database.getData();
        tagDB = new MyDBHandler(this,null,null,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
     * TODO: Test the initialize method
     *
     */
    public void initialize(){

        final String[] data = tagDB.getNames().toArray(new String[tagDB.getNames().size()]);
        tagList = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,data);
        tagList.setAdapter(arrayAdapter);

        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagName = data[position];
                Intent intent = new Intent(MainActivity.this,EditTagActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addTag(View view){
        Intent intent = new Intent(MainActivity.this,AddTagActivity.class);
        startActivity(intent);
    }
}
