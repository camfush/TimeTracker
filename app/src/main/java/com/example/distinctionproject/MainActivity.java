package com.example.distinctionproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.ShapeAppearanceModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;
    private static int BORDER = 0;
    private static int MAX_VELOCITY = 10;
    // 0 = max drag, 1 = no drag
    private static float DRAG = 0.90f;

    private int nextId = -1;

    public static String fileName = "saved_tasks.txt";

    public static Period TIME_PERIOD = Period.Daily;

    private static float SIZE_MOD = 1f;
    private static int SIZE_BASE = 60;

    private static float X_START_RAW = 0;
    private static float Y_START_RAW = 0;

    public static final String TASK_MESSAGE = "com.example.distinctionproject.TASK";
    public static final String TASKS_LIST_MESSAGE = "com.example.distinctionproject.TASK_LIST";

    public static int REQUEST_CODE = 2404;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i) {
            case 0:
                TIME_PERIOD = Period.Daily;
                break;
            case 1:
                TIME_PERIOD = Period.Weekly;
                break;
            case 2:
                TIME_PERIOD = Period.Monthly;
                break;
        }
        setAllSizes();
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
        if (id == R.id.action_colour) {
            Task.GenerateNewInitial();
            for (TaskButton taskButton : tasks) {
                taskButton.task.GenerateNewColour();
            }
            SaveTasks();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ArrayList<Task> taskList = new ArrayList<>();
        for (TaskButton taskButton : tasks) {
            taskList.add(taskButton.task);
        }

        if (id == R.id.nav_home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putParcelableArrayListExtra("TASK_LIST", taskList);
            startActivity(intent);
        } else if (id == R.id.nav_data) {
            Intent intent = new Intent(this, DataActivity.class);
            intent.putParcelableArrayListExtra("TASK_LIST", taskList);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class TaskButton {

        public float xPos, yPos;
        public float xVel = 0, yVel = 0;
        public View internalLayout;
        public boolean moving = true;

        Task task;

        public TaskButton(Task task, View frameLayout, float xPos, float yPos) {
            this.task = task;
            this.internalLayout = frameLayout;
            this.xPos = xPos;
            this.yPos = yPos;

            setCurrentColour();
        }

        public void setMoving(boolean moving) {
            this.moving = moving;
        }

        public void setCurrentColour() {
            setColour(task.getColour());
        }

        public void setColour (int colour) {
            task.setColour(colour);
            FloatingActionButton btn = internalLayout.findViewById(task.getId());
            btn.setBackgroundTintList(ColorStateList.valueOf(colour));
            float[] hsv = new float[3];
            Color.colorToHSV(colour, hsv);
            hsv[2] -= 0.2f;
            int backgroundColour = Color.HSVToColor(hsv);
            FloatingActionButton background = internalLayout.findViewById(R.id.task_background);
            background.setBackgroundTintList(ColorStateList.valueOf(backgroundColour));
        }

        public void setPosition(float xPos, float yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public float getSize() {return (task.getTotal() / 60 + SIZE_BASE) * SIZE_MOD;}

        public float getXScreenPos() {
            if (xPos < 0)
                return 0;
            if (xPos > SCREEN_WIDTH)
                return 1;
            return xPos / SCREEN_WIDTH;
        }

        public float getYScreenPos() {
            if (yPos < 0)
                return 0;
            if (yPos > SCREEN_HEIGHT)
                return 1;
            return yPos / SCREEN_HEIGHT;
        }

        public FloatingActionButton getButton() {
            return internalLayout.findViewById(task.getId());
        }
    }

    public enum Period {
        Daily,
        Weekly,
        Monthly
    }

    private ArrayList<TaskButton> tasks = new ArrayList<>();

    ConstraintLayout parentLayout;

    Handler gravityHandler = new Handler();
    int delay = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        parentLayout = findViewById(R.id.constraint_layout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;

        Spinner periodSelector = findViewById(R.id.period_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.period_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSelector.setAdapter(adapter);
        periodSelector.setOnItemSelectedListener(this);

        switch(TIME_PERIOD) {
            case Daily:
                periodSelector.setSelection(0);
                break;
            case Weekly:
                periodSelector.setSelection(1);
                break;
            case Monthly:
                periodSelector.setSelection(2);
                break;
        }

        FileInputStream fis = null;
        int xCenter = SCREEN_WIDTH / 2;
        int yCenter = SCREEN_HEIGHT / 2;

        Intent inwardIntent = getIntent();
        ArrayList<Task> taskList = inwardIntent.getParcelableArrayListExtra("TASK_LIST");
        if (taskList != null && taskList.size() > 0) {
            Collections.sort(taskList, (o1, o2) -> ((Long) o2.getTotal()).compareTo(o1.getTotal()));
            for (Task task : taskList) {
                createButton((int) task.getTotal(), task.getTitle(), task.getColour(), task.getSessions(), xCenter, yCenter);
            }
        } else {
            try {
                File file = new File(getFilesDir(), fileName);
                //file.delete();
                file.createNewFile();
                fis = new FileInputStream(file);
                ObjectInputStream is = new ObjectInputStream(fis);
                ArrayList<Task> tasks = (ArrayList<Task>) is.readObject();

                if (tasks.size() == 0) {
                    createButtonTask(0, "Welcome", xCenter, yCenter);
                } else {
                    Task.initial = is.readInt();
                    Collections.sort(tasks, (o1, o2) -> ((Long) o2.getTotal()).compareTo(o1.getTotal()));
                    for (Task task : tasks) {
                        createButton((int) task.getTotal(), task.getTitle(), task.getColour(), task.getSessions(), xCenter, yCenter);
                    }
                }
                is.close();
                fis.close();
            } catch (FileNotFoundException e) {
                createButtonTask(0, "Welcome", xCenter, yCenter);
                e.printStackTrace();
            } catch (IOException e) {
                createButtonTask(0, "Welcome", xCenter, yCenter);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                createButtonTask(0, "Welcome", xCenter, yCenter);
                e.printStackTrace();
            }
        }

        setAllSizes();

        //UpdateAllSizes();

        gravityHandler.postDelayed(new Runnable() {
            public void run() {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);

                float minGap = SCREEN_HEIGHT;

                for(TaskButton task : tasks) {

                    float speedMod = 0.0015f;
                    float G = 3;

                    task.xVel *= DRAG;
                    task.yVel *= DRAG;

                    if (task.moving) {
                        for (TaskButton source : tasks) {
                            if (source != task) {
                                float distance = findDistance(task.xPos, task.yPos, source.xPos, source.yPos);
                                double force = G * (task.getSize() * source.getSize()) / (Math.pow(distance, 2));
                                float xDiff = (task.xPos - source.xPos);
                                float yDiff = (task.yPos - source.yPos);
                                float totalDiff = Math.abs(xDiff) + Math.abs(yDiff);

                                if (totalDiff != 0) {
                                    task.xVel += force * (xDiff / totalDiff);
                                    task.yVel += force * (yDiff / totalDiff);
                                } else {
                                    Random rand = new Random();
                                    task.xVel += rand.nextFloat();
                                    task.yVel += rand.nextFloat();
                                }
                            }
                        }

                        for (int i = 0; i <= 1; i++) {
                            float xDiff = ((i * SCREEN_WIDTH) - task.xPos);
                            float yDiff = ((i * SCREEN_HEIGHT) - task.yPos);
                            task.xVel += xDiff * speedMod * 1.5f;
                            task.yVel += yDiff * speedMod;
                        }
// Run this code to cause all tasks to rotate clockwise around the screen. xDiff and yDiff can be multiplied by -1 to change rotational direction
/*
                        float xDiff = SCREEN_WIDTH / 2 - task.xPos;
                        float yDiff = SCREEN_HEIGHT / 2 - task.yPos;
                        task.xVel += yDiff * speedMod * 0.5f;
                        task.yVel += xDiff * speedMod * -0.5f;
*/
                    }
                    if (Math.abs(task.xVel) > MAX_VELOCITY)
                        task.xVel = MAX_VELOCITY * Math.signum(task.xVel);
                    if (Math.abs(task.yVel) > MAX_VELOCITY)
                        task.yVel = MAX_VELOCITY * Math.signum(task.yVel);

                    task.xPos += task.xVel;
                    task.yPos += task.yVel;
                    constraintSet.setHorizontalBias(task.internalLayout.getId(), task.getXScreenPos());
                    constraintSet.setVerticalBias(task.internalLayout.getId(), task.getYScreenPos());
                }
                constraintSet.applyTo(findViewById(R.id.constraint_layout));

                gravityHandler.postDelayed(this, delay);
            }
        }, delay);

        //createButtonTask(200, "Music", xCenter, yCenter); createButtonTask(300, "Play", xCenter, yCenter); createButtonTask(175, "Exercise", xCenter, yCenter); createButtonTask(250, "Study", xCenter, yCenter); createButtonTask(30, "Other", xCenter, yCenter); createButtonTask(100, "Transport", xCenter, yCenter); createButtonTask(75, "Shopping", xCenter, yCenter);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Task.initial);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.add);
        fab.setOnClickListener(view -> {
            createButtonTask(0, "New", SCREEN_WIDTH  - 32, SCREEN_HEIGHT - 32);
            setAllSizes();
            int newColour = Task.initial;
            float[] hsv = new float[3];
            Color.colorToHSV(newColour, hsv);
            hsv[0] += Task.step + Task.STEP_AMOUNT;
            hsv[0] %= 360;
            newColour = Color.HSVToColor(hsv);
            fab.setBackgroundTintList(ColorStateList.valueOf(newColour));
        });

        int newColour = Task.initial;
        float[] hsv = new float[3];
        Color.colorToHSV(newColour, hsv);
        hsv[0] += Task.step + 30;
        hsv[0] %= 360;
        newColour = Color.HSVToColor(hsv);
        fab.setBackgroundTintList(ColorStateList.valueOf(newColour));

        FloatingActionButton delete = findViewById(R.id.delete);
        hsv[0] = 0;
        delete.setBackgroundTintList(ColorStateList.valueOf(Color.HSVToColor(hsv)));

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra(TASK_MESSAGE)) {
                Task modifiedTask = data.getParcelableExtra(TASK_MESSAGE);
                int count = 0;
                for (TaskButton taskButton : tasks) {
                    if (taskButton.task.getId() == modifiedTask.getId()) {
                        taskButton.task.setTitle(modifiedTask.getTitle());
                        taskButton.task.setSessions(modifiedTask.getSessions());
                        taskButton.task.CalculateTotal();

                        FloatingActionButton button = taskButton.getButton();
                        float size = taskButton.getSize();

                        TextView text = taskButton.internalLayout.findViewById(R.id.task_text);
                        text.setText(modifiedTask.getTitle());

                        setAllSizes();
                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {
        SaveTasks();

        super.onStop();
    }

    private void SaveTasks() {
        try {
            File file = new File(getFilesDir(), fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            ArrayList<Task> tasksToWrite = new ArrayList<>();
            for (TaskButton taskButton : tasks) {
                tasksToWrite.add(taskButton.task);
            }
            Collections.sort(tasksToWrite, (o1, o2) -> ((Long)o1.getTotal()).compareTo(o2.getTotal()));
            os.writeObject(tasksToWrite);
            os.writeInt(Task.initial);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float findDistance(float xPos1, float yPos1, float xPos2, float yPos2) {
        return (float)Math.pow(Math.pow(xPos1 - xPos2, 2) + Math.pow(yPos1 - yPos2, 2), 0.5);
    }

    private void createButton(int size, String title, int colour, ArrayList<Session> sessions, float xPos, float yPos) {
        TaskButton taskButton = createButtonTask(size, title, xPos, yPos);
        taskButton.setColour(colour);
        taskButton.task.setSessions(sessions);
    }

    private TaskButton createButtonTask(int size, String title, float xPos, float yPos) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View internalLayout = inflater.inflate(R.layout.efab_text, null);

        FloatingActionButton button = internalLayout.findViewById(R.id.floating_task);
        nextId++;
        int newId = nextId;
        button.setId(newId);
        int layoutId = newId + 500;
        internalLayout.setId(layoutId);

        TextView text = internalLayout.findViewById(R.id.task_text);
        text.setText(title);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)button.getLayoutParams();
        params.width = (int)((size + SIZE_BASE) * SIZE_MOD);
        params.height = (int)((size + SIZE_BASE) * SIZE_MOD);

        button.setLayoutParams(params);

        parentLayout.addView(internalLayout);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        constraintSet.connect(layoutId, ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT, 0);
        constraintSet.connect(layoutId, ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT, 0);
        constraintSet.connect(layoutId, ConstraintSet.TOP, parentLayout.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(layoutId, ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM, 0);

        constraintSet.setHorizontalBias(layoutId, xPos / SCREEN_WIDTH);
        constraintSet.setVerticalBias(layoutId, yPos / SCREEN_HEIGHT);

        constraintSet.applyTo(parentLayout);

        final Task task = new Task(newId, title);
        task.OverrideTotal(size);

        TaskButton taskButton = new TaskButton(task, internalLayout, xPos, yPos);

        tasks.add(taskButton);

        final Context context = this;

        button.setOnTouchListener((view, motionEvent)->{
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                taskButton.moving = false;
                X_START_RAW = motionEvent.getRawX();
                Y_START_RAW = motionEvent.getRawY();
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                float newX = motionEvent.getRawX();
                float newY = motionEvent.getRawY();
                taskButton.setPosition(newX, newY);
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                taskButton.moving = true;
                if (Math.abs(motionEvent.getRawX() - X_START_RAW) < 10 && Math.abs(motionEvent.getRawY() - Y_START_RAW) < 10) {
                    Intent intent = new Intent(context, SessionActivity.class);
                    intent.putExtra(TASK_MESSAGE, (Parcelable)task);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                if (Math.abs(motionEvent.getRawX() - 90) < 90 && Math.abs(motionEvent.getRawY() - (SCREEN_HEIGHT - 90)) < 90) {
                    taskButton.internalLayout.setVisibility(View.GONE);
                    tasks.remove(taskButton);
                    setAllSizes();
                }
                return true;
            }
            return super.onTouchEvent(motionEvent);
        });

        return taskButton;
    }

    public void setAllSizes() {
        float totalA = 0;
        for (TaskButton task : tasks) {
            float area = (float)(Math.PI * Math.pow(task.task.CalculateTotal() / 60 + SIZE_BASE, 2));
            totalA += area;
        }

        SIZE_MOD = (float)Math.pow(1500000 / totalA, 0.5f);// * (1 + (sigFunction((tasks.size() - 7) / 2) * 2));

        for (TaskButton task : tasks) {
            FloatingActionButton taskButton = task.getButton();
            FloatingActionButton backgroundButton = task.internalLayout.findViewById(R.id.task_background);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)taskButton.getLayoutParams();
            params.width = (int)task.getSize();
            params.height = (int)task.getSize();

            taskButton.setLayoutParams(params);


            if (task.task.getSessionCount() > 0
                    && task.task.getSessions().get(task.task.getSessionCount() - 1).StopTime() == 0) {
                ConstraintLayout.LayoutParams bparams = (ConstraintLayout.LayoutParams) backgroundButton.getLayoutParams();
                bparams.width = (int) task.getSize() + 20;
                bparams.height = (int) task.getSize() + 20;

                backgroundButton.setLayoutParams(bparams);
                backgroundButton.setVisibility(View.VISIBLE);
            } else {
                backgroundButton.setVisibility(View.INVISIBLE);
            }

        }
    }
}
