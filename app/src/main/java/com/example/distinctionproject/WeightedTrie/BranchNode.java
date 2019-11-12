package com.example.distinctionproject.WeightedTrie;

import java.util.ArrayList;
import java.util.Collections;

public class BranchNode extends Node {

    private char value;
    private ArrayList<Node> children;
    private int maxChildWeight;

    public BranchNode(char value) {
        this.value = value;
        children = new ArrayList<>();
        maxChildWeight = 0;
    }

    @Override
    int getWeight() {
        return maxChildWeight;
    }

    @Override
    String getType() {
        return "Branch";
    }

    public char getValue() {
        return value;
    }

    public void addChild(Node child, int weight) {
        children.add(child);
        if (weight > maxChildWeight)
            maxChildWeight = weight;
    }

    public Node getChild(char value) {
        for (Node child : children) {
            if (child.getType() == "Branch") {
                if (((BranchNode)child).getValue() == value) {
                    return child;
                }
            }
        }
        return null;
    }

    public Node getAddChild(char value, int weight) {
        Node result = getChild(value);
        if (weight > maxChildWeight)
            maxChildWeight = weight;
        if (result == null) {
            result = new BranchNode(value);
            addChild(result, weight);
        }
        return result;
    }

    public void orderChildren() {
        Collections.sort(children, (o1, o2) -> {
            if (o1.getWeight() > o2.getWeight()) {
                return -1;
            } else if (o1.getWeight() == o2.getWeight()) {
                return 0;
            }
            return 1;
        });
        for (Node child : children) {
            if (child.getType() == "Branch") {
                ((BranchNode)child).orderChildren();
            }
        }
    }

    public Node getNext(int position) {
        Node result = children.get(position);
        return result;
    }

    public int getNewMax(int position) {
        if (children.size() > position) {
            return children.get(position).getWeight();
        } else {
            return 0;
        }
    }
}
