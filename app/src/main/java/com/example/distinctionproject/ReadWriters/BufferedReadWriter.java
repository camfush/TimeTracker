package com.example.distinctionproject.ReadWriters;

import com.example.distinctionproject.Word;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class BufferedReadWriter extends ReadWriter {

    public BufferedReadWriter(String fileName) {
        super(fileName);
    }

    @Override
    public long Read() {
        Date start = new Date();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            wordList = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    wordList.add(new Word(values[0], Integer.parseInt(values[1])));
                }
            }

            bufferedReader.close();
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
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Word word : wordList) {
                String line = word.word + "," + word.weight + "\n";
                bufferedWriter.write(line);
            }
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date stop = new Date();
        return stop.getTime() - start.getTime();
    }
}
