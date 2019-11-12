package com.example.distinctionproject;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Task implements Parcelable, Serializable {

    public static int initial = 0;
    public static int step = 0;

    public static int STEP_AMOUNT = 36;

    private int taskId;
    private String title;
    private long total;
    private int colourInt;
    private ArrayList<Session> sessions;

    public Task(int taskId, String title, int colour) {
        this.taskId = taskId;
        this.title = title;
        this.colourInt = colour;
        sessions = new ArrayList<>();
    }

    public static void GenerateNewInitial() {
        Random rand = new Random();
        float weight = 0.6f;
        initial = Color.argb(1, rand.nextFloat() * (1 - weight) + weight, rand.nextFloat() * (1 - weight) + weight, rand.nextFloat() * (1 - weight) + weight);
        float[] hsv = new float[3];
        Color.colorToHSV(initial, hsv);
        if (hsv[1] > 0.8)
            hsv[1] = 0.8f;
        if (hsv[1] < 0.2)
            hsv[1] = 0.2f;
        if (hsv[2] < 0.75)
            hsv[2] = 0.75f;
        initial = Color.HSVToColor(hsv);
        step = -STEP_AMOUNT;
    }

    public void GenerateNewColour() {
        step += STEP_AMOUNT;
        float[] hsv = new float[3];
        Color.colorToHSV(initial, hsv);
        hsv[0] += step;
        hsv[0] %= 360;
        this.colourInt = Color.HSVToColor(hsv);
    }

    public Task(int taskId, String title) {
        this.taskId = taskId;
        Random rand = new Random();
        this.title = title;
        if (initial == 0) {
            float weight = 0.6f;
            initial = Color.argb(1, rand.nextFloat() * (1 - weight) + weight, rand.nextFloat() * (1 - weight) + weight, rand.nextFloat() * (1 - weight) + weight);
            float[] hsv = new float[3];
            Color.colorToHSV(initial, hsv);
            if (hsv[1] > 0.8)
                hsv[1] = 0.8f;
            if (hsv[1] < 0.2)
                hsv[1] = 0.2f;
            if (hsv[2] < 0.75)
                hsv[2] = 0.75f;
            initial = Color.HSVToColor(hsv);
            this.colourInt = initial;
        } else {
            step += STEP_AMOUNT;
            float[] hsv = new float[3];
            Color.colorToHSV(initial, hsv);
            hsv[0] += step;
            hsv[0] %= 360;
            this.colourInt = Color.HSVToColor(hsv);
        }
        sessions = new ArrayList<>();
    }

    public void OverrideTotal(long total) {
        this.total = total;
    }

    public float GetTotal(Date start, Date end) {
        float total = 0;
        for (Session session : sessions) {
            if (session.StartTime() > start.getTime() && session.StartTime() < end.getTime())
                total += session.TotalTime();
        }
        return total;
    }

    public float CalculateTotal() {
        long total = 0;
        Date cutoff = new Date();
        cutoff.setHours(0);
        cutoff.setMinutes(0);
        cutoff.setSeconds(0);
        switch(MainActivity.TIME_PERIOD) {
            case Daily:
                break;
            case Weekly:
                if (cutoff.getDay() != 0)
                    cutoff.setDate(1 + cutoff.getDate() - cutoff.getDay());
                else
                    cutoff.setDate(cutoff.getDate() - 6);
                //cutoff.setDate(cutoff.getDay());
                break;
            case Monthly:
                cutoff.setDate(1);
                break;
        }
        for (Session session : sessions) {
            if (session.StartTime() > cutoff.getTime())
                total += session.TotalTime();
        }
        this.total = total;
        return this.total;
    }

    public void addSession(Session newSession) {
        sessions.add(newSession);
    }

    public long getTotal() {
        return total;
    }

    public int getColour() { return colourInt; }

    public void setColour(int colour) {colourInt = colour;}

    public String getTitle() { return title; }

    public void setTitle(String title) {this.title = title;}

    public int getId() {return taskId;}

    public ArrayList<Session> getSessions() {return sessions;}

    public void setSessions(ArrayList<Session> sessions) {this.sessions = sessions;}

    public int getSessionCount() {
        return sessions.size();
    }

    protected Task(Parcel in) {
        title = in.readString();
        total = in.readLong();
        colourInt = in.readInt();
        taskId = in.readInt();
        sessions = new ArrayList<>();
        in.readParcelableList(sessions, Session.class.getClassLoader());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeLong(total);
        parcel.writeInt(colourInt);
        parcel.writeInt(taskId);
        parcel.writeParcelableList(sessions, 0);
    }
}
