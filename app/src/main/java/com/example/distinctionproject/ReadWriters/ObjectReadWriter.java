package com.example.distinctionproject.ReadWriters;

import android.content.res.Resources;

import com.example.distinctionproject.MainActivity;
import com.example.distinctionproject.R;
import com.example.distinctionproject.Task;
import com.example.distinctionproject.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ObjectReadWriter extends ReadWriter {

    public ObjectReadWriter(String fileName) {
        super(fileName);
    }

    @Override
    public long Read() {
        Date start = new Date();
        try {
            File file = new File(fileName);
            FileInputStream is = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(is);
            wordList = (ArrayList<Word>) ois.readObject();
            ois.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Date stop = new Date();
        return stop.getTime() - start.getTime();
    }

    @Override
    public long Write() {
        Date start = new Date();
        try {
            File file = new File(fileName);
            FileOutputStream os = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(wordList);
            oos.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date stop = new Date();
        return stop.getTime()  - start.getTime();
    }
}
