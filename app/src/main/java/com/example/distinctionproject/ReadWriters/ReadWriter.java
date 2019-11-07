package com.example.distinctionproject.ReadWriters;

import android.content.res.Resources;

import com.example.distinctionproject.Word;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public abstract class ReadWriter {

    protected String fileName;
    ArrayList<Word> wordList;

    public ReadWriter(String fileName) {
        this.fileName = fileName;
        wordList = new ArrayList<>();
    }

    public void ReadRaw(int resourceId, Resources resources) {
        try {
            InputStream is = resources.openRawResource(resourceId);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(buffer);
            os.close();
            is.close();
            String fileContents = os.toString();
            String[] wordArray = fileContents.split("\\r?\\n");
            Random random = new Random();
            for (String word : wordArray) {
                int value = random.nextInt(50);
                value = 50 - word.length();
                wordList.add(new Word(word.substring(0, 1).toUpperCase() + word.substring(1), value));
            }
            File file = new File(fileName);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract long Read();
    public abstract long Write();

    public void setWordList(ArrayList<Word> wordList) {
        this.wordList = wordList;
    }

    public ArrayList<Word> getWordList() {
        return wordList;
    }
}
