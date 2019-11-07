package com.example.distinctionproject;

import java.io.Serializable;
import java.util.ArrayList;

public class Word implements Serializable {
    public String word;
    public int weight;

    public Word(String word, int weight) {
        this.word = word;
        this.weight = weight;
    }

    public static String[] FormatWordList(ArrayList<Word> wordList) {
        String[] result = new String[wordList.size()];
        for (int i = 0; i < wordList.size(); i++) {
            result[i] = wordList.get(i).word;
        }
        return result;
    }
}
