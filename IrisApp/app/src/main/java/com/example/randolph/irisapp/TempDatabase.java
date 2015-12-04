package com.example.randolph.irisapp;

/**
 * Created by liusu on 4/12/15.
 */
public class TempDatabase {
    private String[] data = {"Test Tag 1","Test Tag 2","Test Tag 3","Test Tag 4","Test Tag 5","Test Tag 6","Test Tag 7","Test Tag 8","Test Tag 9","Test Tag 10","Test Tag 11","Test Tag 12","Test Tag 13","Test Tag 14","Test Tag 15"};
    private boolean[] enabled = {true,false,false,true,true,true,false,false,true,true,true,false,false,true,true};
    private boolean[] stolen = {true,false,false,false,false,true,false,false,false,false,true,false,false,false,false};
    public String[] getData(){
        return data;
    }

    public boolean[] getEnabled(){
        return enabled;
    }

    public boolean[] getStolen(){
        return stolen;
    }

    public String getNameByIndex(int index){
        return data[index];
    }
}
