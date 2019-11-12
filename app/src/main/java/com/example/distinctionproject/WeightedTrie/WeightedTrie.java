package com.example.distinctionproject.WeightedTrie;

import com.example.distinctionproject.Word;

import java.util.ArrayList;

public class WeightedTrie {

    BranchNode root;

    public WeightedTrie(ArrayList<Word> wordList) {
        root = new BranchNode(" ".charAt(0));
        for (Word word : wordList) {
            addWord(word.word.toLowerCase(), word.weight);
        }
        sortList();
    }

    public void addWord(String word, int weight) {
        BranchNode current = root;
        for (char c : word.toCharArray()) {
            current = (BranchNode)current.getAddChild(c, weight);
        }
        current.addChild(new LeafNode(weight), weight);
    }

    public void sortList() {
        root.orderChildren();
    }

    public ArrayList<Word> getSuggestions(String header, int suggestionCount) {
        BranchNode current = root;
        ArrayList<Word> results = new ArrayList<>();
        for (char c : header.toCharArray()) {
            System.out.println("CHARACTER: " + c);
            current = (BranchNode)current.getChild(c);
            if (current == null)
                return results;
        }

        System.out.println("NEXT STAGE!");

        ArrayList<NodeWrapper> toBeParsed = new ArrayList<>();
        toBeParsed.add(new NodeWrapper(current, header));
        while (toBeParsed.size() > 0) {
            NodeWrapper currentParse = toBeParsed.remove(0);
            Node nextNode = currentParse.getNext();
            if (nextNode.getType() == "Leaf") {
                results.add(new Word(currentParse.getHeader().substring(0, 1).toUpperCase() + currentParse.getHeader().substring(1), currentParse.getMax()));
                if (results.size() >= suggestionCount)
                    return results;
            } else {
                int index = 0;
                for (NodeWrapper parsable : toBeParsed) {
                    if (nextNode.getWeight() >= parsable.getMax())
                        break;
                    else
                        index++;
                }
                NodeWrapper newParsable = new NodeWrapper((BranchNode)nextNode, currentParse.getHeader() + ((BranchNode)nextNode).getValue());
                toBeParsed.add(index, newParsable);
            }

            if (currentParse.getMax() > 0) {
                int index = 0;
                for (NodeWrapper parsable : toBeParsed) {
                    if (currentParse.getMax() >= parsable.getMax())
                        break;
                    else
                        index++;
                }
                toBeParsed.add(index, currentParse);
            }
        }
        return results;
    }
}
