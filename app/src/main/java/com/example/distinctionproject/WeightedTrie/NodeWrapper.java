package com.example.distinctionproject.WeightedTrie;

public class NodeWrapper {

    private BranchNode node;
    private String header;
    private int position;
    private int max;

    NodeWrapper(BranchNode node, String header) {
        this.node = node;
        this.header = header;
        position = 0;
        max = node.getWeight();
    }

    public Node getNext() {
        Node result = node.getNext(position);
        position += 1;
        max = node.getNewMax(position);
        return result;
    }

    public int getMax() {
        return max;
    }

    public String getHeader() {
        return header;
    }
}
