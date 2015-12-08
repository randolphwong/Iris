package com.example.randolph.sqlDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by junhaochiew on 6/11/2015.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="tags.db";
    public static final String TABLE_TAG ="tagtable";

    public static final String COLUMN_ID="_id";
    public static final String COLUMN_TAGID ="tagID";
    public static final String COLUMN_ENABLED ="enabled";
    public static final String COLUMN_STOLEN ="stolen";
    public static final String COLUMN_TIMESAPPEARED ="timesappeared";
    public static final String COLUMN_LASTKNOWNLOCATION ="lastknownlocation";
    public static final String COLUMN_TAGNAME ="TAGname";
    public static final String COLUMN_TAGITEM ="remark";



    @Override
    public void onCreate(SQLiteDatabase db) {
        String query= "CREATE TABLE " + TABLE_TAG + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COLUMN_TAGID + " TEXT, " +
                COLUMN_ENABLED + " INTEGER, " + COLUMN_STOLEN + " INTEGER, " +
                COLUMN_TIMESAPPEARED + " INTEGER, " +
                COLUMN_LASTKNOWNLOCATION + " TEXT, "+ COLUMN_TAGNAME + " TEXT, " +
                COLUMN_TAGITEM + " TEXT " + ");";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        onCreate(db);
    }

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public void addTag(DBTags tags){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_TAGID, tags.get_tagID());
        cv.put(COLUMN_ENABLED, tags.get_enabled());
        cv.put(COLUMN_STOLEN, tags.get_stolen());
        cv.put(COLUMN_TIMESAPPEARED, tags.get_timesappeared());
        cv.put(COLUMN_LASTKNOWNLOCATION, tags.get_lastknownlocation());
        cv.put(COLUMN_TAGNAME, tags.get_tagname());
        cv.put(COLUMN_TAGITEM, tags.get_tagItem());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TAG,null,cv);
        db.close();
    }

    public void deleteTag(long id){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_TAG, COLUMN_ID + "=" + id, null);
    }
    public void eraseDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '" + TABLE_TAG + "' WHERE 1";

        Cursor c= db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_TAGID))!=null){
                deleteTag(Long.parseLong(c.getString(c.getColumnIndex(COLUMN_ID))));
            }
            c.moveToNext();
        }
        db.close();
    }

    public String databaseToString(){
        String dbString="";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '" + TABLE_TAG + "' WHERE 1";

        Cursor c= db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_TAGID))!=null){
                dbString += c.getString(c.getColumnIndex(COLUMN_TAGID)) + " ";
                dbString += c.getLong(c.getColumnIndex(COLUMN_TIMESAPPEARED)) + " ";
                dbString += c.getLong(c.getColumnIndex(COLUMN_LASTKNOWNLOCATION)) + " ";
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }
    public List<String> getTagList(){
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '" + TABLE_TAG + "' WHERE 1";

        Cursor c= db.rawQuery(query, null);

        c.moveToFirst();
        String tagInfo;
        while (!c.isAfterLast()){
            tagInfo = "";
            if(c.getString(c.getColumnIndex(COLUMN_TAGID))!=null){
                tagInfo += c.getString(c.getColumnIndex(COLUMN_TAGNAME)).replace(" ", "") + "          Status: ";

                tagInfo += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ENABLED))) == 1 ? "Enabled    " : "Disabled    ";

                tagInfo += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_STOLEN))) == 1 ? "Stolen!!!" : "";
                names.add(tagInfo);
            }
            c.moveToNext();
        }
        db.close();
        return names;
    }


    public boolean checkExistence(String tagName){
        boolean exist = false;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '" + TABLE_TAG + "' WHERE 1";
        Cursor c= db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_TAGNAME)).equals(tagName)) exist = true;
            c.moveToNext();
        }

        db.close();
        return exist;
    }

    // returns detail of the route in String[] format, 0-rowid, 1-Tag Name, 2-Enabled, 3-Stolen, 4-timesappeared, 5-lastknownlocation, 6- owner, 7-remark
    public String[] getTagDetails(String tagName){
        String[] columns = new String[]{COLUMN_ID, COLUMN_TAGID, COLUMN_ENABLED, COLUMN_STOLEN, COLUMN_TIMESAPPEARED, COLUMN_LASTKNOWNLOCATION, COLUMN_TAGNAME,
                COLUMN_TAGITEM};
        SQLiteDatabase db = getWritableDatabase();
        long i=1;
        Cursor c = db.query(TABLE_TAG, columns, COLUMN_TAGNAME + " =?", new String[]{tagName}, null, null, null);
        String[] s = new String[]{null,null,null,null,null,null,null,null};
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            s[0] = c.getString(0);
            s[1] = c.getString(1);
            s[2]=c.getString(2);
            s[3]=c.getString(3);
            s[4]=c.getString(4);
            s[5]=c.getString(5);
            s[6]=c.getString(6);
            s[7]=c.getString(7);

        }
        return s;
    }
    public String[] getTimesAppeared(String tagid){
        String[] columns = new String[]{COLUMN_ID, COLUMN_TAGID, COLUMN_TIMESAPPEARED, COLUMN_LASTKNOWNLOCATION};
        SQLiteDatabase db = getWritableDatabase();
        long i=1;
        Cursor c = db.query(TABLE_TAG, columns, COLUMN_TAGID +" =?", new String[]{tagid},null,null,null);
        String[] s = new String[]{null,"location do not exist in database",null,null};
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            s[0] = c.getString(2);
            s[1] = c.getString(3);
        }
        return s;
    }

    public Double getBusRouteCost(String location1, String location2){
        String[] columns = new String[]{COLUMN_ID, COLUMN_TAGID};
        SQLiteDatabase db = getWritableDatabase();
        String route = location1 + " to " + location2;
        Cursor c = db.query(TABLE_TAG, columns, COLUMN_TAGID +" =?", new String[]{route},null,null,null);
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            return c.getDouble(2);
        }
        return null;
    }
    public Double getTaxiRouteCost(String location1, String location2){
        String[] columns = new String[]{COLUMN_ID, COLUMN_TAGID};
        SQLiteDatabase db = getWritableDatabase();
        String route = location1 + " to " + location2;
        Cursor c = db.query(TABLE_TAG, columns, COLUMN_TAGID +" =?", new String[]{route},null,null,null);
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            return c.getDouble(2);
        }
        return null;
    }

    public void updateData(long id, String name, String name2, long coordx, long coordy, String walktime,
                           String bustime){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_TAGID, name + " to " + name2);
        cv.put(COLUMN_ENABLED, name);
        cv.put(COLUMN_STOLEN, name2);
        cv.put(COLUMN_TIMESAPPEARED, coordx);
        cv.put(COLUMN_LASTKNOWNLOCATION,coordy);
        cv.put(COLUMN_TAGNAME, walktime);
        cv.put(COLUMN_TAGITEM, bustime);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TAG,cv,COLUMN_ID + "=" + id, null);

    }

    public void reportLost(long id){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STOLEN,1);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TAG,cv,COLUMN_ID + "=" + id,null);
    }

    public void reportFound(long id){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STOLEN,0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TAG,cv,COLUMN_ID + "=" + id,null);
    }

    public void enableTag(long id){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ENABLED,1);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TAG,cv,COLUMN_ID + "=" + id,null);
    }

    public void disableTag(long id){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ENABLED,0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TAG,cv,COLUMN_ID + "=" + id,null);
    }



    public  String[] searchDB(String location1, String location2){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_TAG +" WHERE " + COLUMN_TAGID + " = '" + location1+" to "+location2 + "'", null);

        String[] dbString = new String[3];

        if(c.getCount()!=0){
            int i=0;
            c.moveToFirst();
            do {
                dbString[i] += c.getString(c.getColumnIndex(COLUMN_TAGID)) + " ";
                dbString[i] += c.getLong(c.getColumnIndex(COLUMN_TIMESAPPEARED)) + " ";
                dbString[i] += c.getLong(c.getColumnIndex(COLUMN_LASTKNOWNLOCATION)) + " ";
                i++;
            } while (c.moveToNext() && i<3);

            c.close();
            return dbString;
        }
        else {
            return null;
        }
    }
}




















