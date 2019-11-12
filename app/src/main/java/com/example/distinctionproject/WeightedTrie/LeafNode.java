package com.example.distinctionproject.WeightedTrie;

public class LeafNode extends Node {

    private int weight;

    public LeafNode(int weight) {
        this.weight = weight;
    }

    @Override
    int getWeight() {
        return weight;
    }

    @Override
    String getType() {
        return "Leaf";
    }
}
