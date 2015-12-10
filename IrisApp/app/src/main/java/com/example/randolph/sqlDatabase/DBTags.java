package com.example.randolph.sqlDatabase;

/**
 * Created by junhaochiew on 7/11/2015.
 */
public class DBTags {

    private int _id;
    private String _tagID;
    private Integer _enabled;
    private Integer _stolen;
    private Integer _timesappeared;
    private String _lastknownlocation;
    private String _tagname;
    private String _tagItem;

    public DBTags(){

    }
    public DBTags(String _tagID, Integer _enabled, Integer _stolen, Integer _timesappeared, String _lastknownlocation,
                  String _tagname, String _tagItem) {
        this._tagID = _tagID;
        this._enabled = _enabled;
        this._stolen = _stolen;
        this._timesappeared = _timesappeared;
        this._lastknownlocation = _lastknownlocation;
        this._tagname = _tagname;
        this._tagItem = _tagItem;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_tagID() {
        return _tagID;
    }

    public void set_tagID(String _tagID) {
        this._tagID = _tagID;
    }

    public void set_enabled(Integer _enabled) {
        this._enabled = _enabled;
    }

    public void set_stolen(Integer _stolen) {
        this._stolen = _stolen;
    }

    public void set_timesappeared(Integer _timesappeared) {
        this._timesappeared = _timesappeared;
    }

    public void set_lastknownlocation(String _lastknownlocation) {
        this._lastknownlocation = _lastknownlocation;
    }

    public void set_tagname(String _tagname) {
        this._tagname = _tagname;
    }

    public void set_tagItem(String _tagItem) {
        this._tagItem = _tagItem;
    }

    public int get_id() {
        return _id;
    }

    public Integer get_enabled() {
        return _enabled;
    }

    public Integer get_stolen() {
        return _stolen;
    }

    public Integer get_timesappeared() {
        return _timesappeared;
    }

    public String get_lastknownlocation() {
        return _lastknownlocation;
    }

    public String get_tagname() {
        return _tagname;
    }

    public String get_tagItem() {
        return _tagItem;
    }
}
