package com.example.distinctionproject;

import android.os.Parcel;
import android.os.Parcelable;

import android.text.format.Time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Session implements Parcelable, Serializable {

    private long startTime;
    private long stopTime = 0;

    public long TotalTime() {
        if (stopTime > startTime)
            return (stopTime - startTime) / 1000;
        else {
            Date date = new Date();
            return (date.getTime() - startTime) / 1000;
        }
    }

    public long StartTime() {
        return startTime;
    }

    public long StopTime() {return stopTime;}

    public Session() {
        Date date = new Date();
        startTime = date.getTime();
    }

    public void endSession() {
        Date date = new Date();
        stopTime = date.getTime();
    }

    protected Session(Parcel in) {
        startTime = in.readLong();
        stopTime = in.readLong();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(startTime);
        parcel.writeLong(stopTime);
    }
}
