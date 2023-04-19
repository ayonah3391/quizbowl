package com.example.quizbowl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LifecycleData {
    int onCreate = 0;
    int onDestroy = 0;
    String duration;

    public String toString() {
        return duration + "\n"+
                "onCreate \t"+onCreate+"\n" +
                "onDestroy \t"+onDestroy+"\n";
    }

    String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this, LifecycleData.class);
    }

    static LifecycleData parseJSON(String fromSharedPreferences){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(fromSharedPreferences, LifecycleData.class);
    }

    void updateEvent(String eventName){
        switch(eventName) {
            case "onCreate":
                onCreate++;
                break;
            case "onDestroy":
                onDestroy++;
                break;
            default:break;
        }
    }

}
