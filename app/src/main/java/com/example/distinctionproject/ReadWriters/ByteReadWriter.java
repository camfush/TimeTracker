package com.example.distinctionproject.ReadWriters;

import android.content.res.Resources;

import com.example.distinctionproject.MainActivity;
import com.example.distinctionproject.R;
import com.example.distinctionproject.Task;
import com.example.distinctionproject.Word;

import java.io.ByteArrayOutputStream;
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
import java.util.Random;

public class ByteReadWriter extends ReadWriter {

    public ByteReadWriter(String fileName) {
        super(fileName);
    }

    @Override
    public long Read() {
        Date start = new Date();
        try {
            File file = new File(fileName);
            FileInputStream is = new FileInputStream(file);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(buffer);
            os.close();
            is.close();
            String fileContents = os.toString();
            String[] wordArray = fileContents.split("\\r?\\n");
            wordList = new ArrayList<>();
            for (String word : wordArray) {
                String[] values = word.split(",");
                if (values.length >= 2) {
                    wordList.add(new Word(values[0], Integer.parseInt(values[1])));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
            FileOutputStream fos = new FileOutputStream(file);
            //String fileContents = "";
            for (Word word : wordList) {
                String line = word.word + "," + word.weight + "\n";
                byte[] buffer = line.getBytes();
                fos.write(buffer);
            }
            //byte[] buffer = fileContents.getBytes();
            //fos.write(buffer);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date stop = new Date();
        return stop.getTime() - start.getTime();
    }
}
