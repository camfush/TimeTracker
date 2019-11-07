package com.example.distinctionproject.AutoCompleters;

import android.content.Context;
import android.widget.Filter;

import com.example.distinctionproject.WeightedTrie;
import com.example.distinctionproject.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TrieAdapter extends WordAdapter {

    protected Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String result = ((Word)resultValue).word;
            return result;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Date start = new Date();
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<Word> suggestions = new ArrayList<Word>();
                //suggestions = trie.getSuggestions(constraint.toString().toLowerCase());
                suggestions = trie.getSuggestions(constraint.toString().toLowerCase(), 5);
                results.values = suggestions;
                results.count = suggestions.size();
            }
            Date stop = new Date();
            System.out.println("TIME CHECK: FILTER TIME: " + (stop.getTime() - start.getTime()));

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<Word>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    private WeightedTrie trie;

    public TrieAdapter(Context context, int resource, ArrayList<Word> wordList) {
        super(context, resource, wordList);
        trie = new WeightedTrie(wordList);
    }

    public String printTrie() {
        return trie.printTrie();
    }

    public String getChildren(String header) {
        return trie.printTrie(header);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
