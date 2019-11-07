package com.example.distinctionproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.distinctionproject.AutoCompleters.PreSortAdapter;
import com.example.distinctionproject.AutoCompleters.TrieAdapter;
import com.example.distinctionproject.AutoCompleters.WordAdapter;
import com.example.distinctionproject.ReadWriters.BufferedReadWriter;
import com.example.distinctionproject.ReadWriters.ByteReadWriter;
import com.example.distinctionproject.ReadWriters.ObjectReadWriter;
import com.example.distinctionproject.ReadWriters.ReadWriter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class SessionActivity extends AppCompatActivity {
    private Task task;
    private boolean running = false;
    private int totalTime = 0;

    Handler timeHandler = new Handler();
    int delay = 1000;

    private Session currentSession;
    private TextView timeDisplay;

    private String[] database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        ReadWriter readWriter = new ByteReadWriter(getFilesDir() + "dictionary.txt");
        System.out.println("TIME CHECK: READ TIME: " + readWriter.Read());
        //readWriter.ReadRaw(R.raw.english_dictionary_short, getResources());
        database = Word.FormatWordList(readWriter.getWordList());
        System.out.println("TIME CHECK: WRITE TIME: " + readWriter.Write());

        Intent intent = getIntent();
        task = intent.getParcelableExtra(MainActivity.TASK_MESSAGE);

        AutoCompleteTextView title = findViewById(R.id.task_title);
        title.setText(task.getTitle());

        View root = title.getRootView();
        //root.setBackgroundColor(task.getColour());

        Toolbar toolbar = findViewById(R.id.task_toolbar);
        float[] hsv = new float[3];
        Color.colorToHSV(task.getColour(), hsv);
        hsv[2] -= 0.4f;
        toolbar.setBackgroundColor(Color.HSVToColor(hsv));
        toolbar.setBackgroundColor(task.getColour());

        final FloatingActionButton playButton = findViewById(R.id.record_button);

        int Av = (task.getColour() >> 24) & 0xff; // or color >>> 24
        int Rv = (task.getColour() >> 16) & 0xff;
        int Gv = (task.getColour() >>  8) & 0xff;
        int Bv = (task.getColour()      ) & 0xff;

        Av -= 125;

        int background = (Color.argb(Av, Rv, Gv, Bv));

        playButton.setBackgroundTintList(ColorStateList.valueOf(background));
        timeDisplay = findViewById(R.id.total_time_display);

        if (task.getSessionCount() > 0) {
            currentSession = task.getSessions().get(task.getSessionCount() - 1);

            if (currentSession.StopTime() == 0) {
                running = true;
                playButton.setImageResource(R.drawable.ic_pause_circle_filled_black_96dp);
            } else {
                running = false;
            }

            totalTime = (int)task.CalculateTotal();
            DisplayText();
        }


        timeHandler.postDelayed(new Runnable() {
            public void run() {
                if (running) {
                    totalTime += 1f;
                    DisplayText();
                }
                timeHandler.postDelayed(this, delay);
            }
        }, delay);

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (running) {
                    //currentSession = task.getSessions().get(task.getSessionCount());
                    currentSession.endSession();
                    playButton.setImageResource(R.drawable.ic_play_circle_outline_black_96dp);
                    running = false;
                } else {
                    currentSession = new Session();
                    task.addSession(currentSession);
                    playButton.setImageResource(R.drawable.ic_pause_circle_filled_black_96dp);
                    running = true;
                }
            }
        });

        Date start = new Date();
        ArrayAdapter<Word> adapter = new TrieAdapter(this, android.R.layout.simple_dropdown_item_1line, readWriter.getWordList());
        Date stop = new Date();
        System.out.println("TIME CHECK: LOAD TIME: " + (stop.getTime() - start.getTime()));

        System.out.println(((TrieAdapter)adapter).printTrie());

        title.setAdapter(adapter);
        title.setDropDownWidth(400);
        title.setThreshold(1);
    }

    private void DisplayText() {
        int seconds = totalTime % 60;
        int minutes = (int)(totalTime / 60) % 60;
        int hours = (int)(totalTime / 3600);
        String display = "";
        if (hours > 0)
            display += hours + " hours, ";
        if (minutes > 0 || hours > 0)
            display += minutes + " minutes, ";
        display += seconds + " seconds";
        timeDisplay.setText(display);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        EditText editText = findViewById(R.id.task_title);
        String newTitle = editText.getText().toString();
        task.setTitle(newTitle);

        Intent intent = new Intent();
        intent.putExtra(MainActivity.TASK_MESSAGE, (Parcelable)task);
        setResult(RESULT_OK, intent);
        finish();
    }
}
