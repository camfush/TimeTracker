package com.example.distinctionproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WeightedTrie {

    static int minMax = 0;

    class TrieNode {
        char value;
        int valueWeight;
        int maxChildWeight;
        boolean checked;
        ArrayList<TrieNode> children;

        public TrieNode(char value, int valueWeight) {
            this.value = value;
            this.valueWeight = valueWeight;
            maxChildWeight = 0;
            children = new ArrayList<>();
            checked = false;
        }

        public TrieNode getChild(char value) {
            for (TrieNode child : children) {
                if (child.value == value)
                    return child;
            }
            return null;
        }

        public TrieNode addChild(TrieNode child) {
            children.add(child);
            return child;
        }

        public String printTrie() {
            String result = "[" + value + "," + valueWeight;
            for (TrieNode child : children) {
                result += child.printTrie();
            }
            result += "]";
            return result;
        }

        public void OrderTrie() {
            Collections.sort(children, (o1, o2) -> {
                if (Math.max(o1.maxChildWeight, o1.valueWeight) > Math.max(o2.maxChildWeight, o2.valueWeight)) {
                    return -1;
                } else if (Math.max(o1.maxChildWeight, o1.valueWeight) == Math.max(o2.maxChildWeight, o2.valueWeight)) {
                    return Character.compare(o1.value, o2.value);
                }
                return 1;
            });
            for (TrieNode child : children) {
                child.OrderTrie();
            }
        }

        public int MaxWeight() {
            return Math.max(maxChildWeight, valueWeight);
        }
    }

    TrieNode root;

    public WeightedTrie(ArrayList<Word> wordList) {
        root = new TrieNode(Character.MIN_VALUE, 0);
        for (Word word : wordList) {
            addWord(word.word, word.weight);
        }
        root.OrderTrie();
    }

    private void addWord(String word, int weight) {
        word = word.toLowerCase();
        TrieNode current = root;
        int previousMaxChildWeight = 0;
        for (char c : word.toCharArray()) {
            TrieNode child = current.getChild(c);
            if (child == null) {
                current = current.addChild(new TrieNode(c, 0));
            } else {
                current = child;
            }
            previousMaxChildWeight = current.maxChildWeight;
            if (current.maxChildWeight < weight) {
                current.maxChildWeight = weight;
            }
        }
        current.valueWeight = weight;
        current.maxChildWeight = previousMaxChildWeight;
    }

    class Parsable {
        TrieNode start;
        int traversed;
        String header;
        int maxWeight;

        public Parsable(TrieNode start, String header) {
            this.start = start;
            traversed = 0;
            this.header = header;
            maxWeight = start.MaxWeight();
        }

        public void Parse() {
            traversed++;
            int max = 0;
            int startTraversal = traversed;
            if (traversed > 0 && start.children.size() > traversed + 1 && start.valueWeight >= start.children.get(traversed - 1).MaxWeight())
                startTraversal--;
            else
                max = start.valueWeight;
            for (int i = startTraversal; i < start.children.size(); i ++) {
                TrieNode child = start.children.get(i);
                int childMax = child.MaxWeight();
                if (childMax > max) {
                    max = childMax;
                }
            }
            maxWeight = max;
        }

        public int nextPos() {
            int result = traversed;
            if (result > 0 && start.children.size() > traversed + 1 && start.valueWeight >= start.children.get(traversed - 1).MaxWeight()) {
                result--;
            }
            return result;
        }
    }

    public ArrayList<Word> getSuggestions(String header, int resultCount) {
        ArrayList<Word> result = new ArrayList<>();
        TrieNode current = root;
        for (char c : header.toCharArray()) {
            current = current.getChild(c);
            if (current == null) {
                return result;
            }
        }

        ArrayList<Parsable> toBeParsed = new ArrayList<>();
        toBeParsed.add(new Parsable(current, header));

        while (toBeParsed.size() > 0) {
            Parsable currentParse = toBeParsed.remove(0);
            if (currentParse.start.valueWeight >= currentParse.maxWeight) {
                result.add(new Word(currentParse.header.substring(0, 1).toUpperCase() + currentParse.header.substring(1), currentParse.start.valueWeight));
                currentParse.Parse();
                if (result.size() == resultCount)
                    return result;
            }
            if (toBeParsed.size() == 0) {
                int pos = currentParse.nextPos();
                if (pos < currentParse.start.children.size()) {
                    TrieNode newStart = currentParse.start.children.get(pos);
                    toBeParsed.add(new Parsable(newStart, currentParse.header + newStart.value));
                    currentParse.Parse();
                    if (currentParse.maxWeight > 0)
                        toBeParsed.add(currentParse);
                }
            } else {
                int pos = currentParse.nextPos();
                if (pos < currentParse.start.children.size()) {
                    TrieNode newStart = currentParse.start.children.get(pos);
                    int placePos = 0;
                    Parsable newParsable = new Parsable(newStart, currentParse.header + newStart.value);
                    for (Parsable existingParsable : toBeParsed) {
                        if (newParsable.maxWeight > existingParsable.maxWeight)
                            break;
                        else
                            placePos++;
                    }
                    toBeParsed.add(placePos, newParsable);
                    currentParse.Parse();
                    int i;
                    for (i = placePos; i < toBeParsed.size(); i++) {
                        if (currentParse.maxWeight > toBeParsed.get(i).maxWeight)
                            break;
                    }
                    toBeParsed.add(i, currentParse);
                }
            }
        }
        return result;
    }

    public ArrayList<Word> getSuggestions(String header) {
        ArrayList<Word> result = new ArrayList<>();
        TrieNode current = root;
        for (char c : header.toCharArray()) {
            current = current.getChild(c);
            if (current == null) {
                return result;
            }
        }

        while (true) {
            if (current.valueWeight >= current.maxChildWeight) {
                result.add(new Word(header.substring(0, 1).toUpperCase() + header.substring(1), current.valueWeight));
                return result;
            }
            for (TrieNode child : current.children) {
                if (child.valueWeight >= current.maxChildWeight || child.maxChildWeight >= current.maxChildWeight) {
                    current = child;
                    header += current.value;
                    break;
                }
            }
        }
    }

    public String printTrie() {
        return root.printTrie();
    }

    public String printTrie(String header) {
        String result = "";
        TrieNode current = root;
        for (char c : header.toCharArray()) {
            current = root.getChild(c);
            if (current == null)
                return result + " NULL FOUND";
        }
        result += current.printTrie();
        return result;
    }
/*
    public ArrayList<String> getSuggestions(String header) {
        ArrayList<String> result = new ArrayList<>();
        TrieNode current = root;
        for (char c : header.toCharArray()) {
            current = current.getChild(c);
            if (current == null) {
                return result;
            }
        }

        //ArrayList<TrieNode> toBeParsed = new ArrayList<>();
        HashMap<TrieNode, String> toBeParsed = new HashMap<>();
        toBeParsed.put(current, header);
        for (int i = 0; i < 5; i ++) {
            while(current != null && toBeParsed.size() > 0) {
                for (TrieNode child : current.children) {
                    int min = child.valueWeight;
                    for (int j = 0; j < 5 - i; j++) {
                        TrieNode toBeRemoved = child;
                        if (toBeParsed.size() < j)
                        for (TrieNode node : toBeParsed.keySet()) {
                            if (node.valueWeight < min) {
                                min = node.valueWeight;
                                toBeRemoved = node;
                                toBeParsed.put(child, header + current.value);
                            }
                        }
                    }
                }

            }
        }
        return result;
    }
    */
}
