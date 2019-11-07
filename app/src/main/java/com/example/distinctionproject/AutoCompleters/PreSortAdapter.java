package com.example.distinctionproject.AutoCompleters;

import android.content.Context;
import android.widget.Filter;

import com.example.distinctionproject.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PreSortAdapter extends WordAdapter {

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
                int count = 0;
                for (Word word : wordList) {
                    if (word.word.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(word);
                        count++;
                        if (count >= 5)
                            break;
                    }
                }

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

    public PreSortAdapter(Context context, int resource, ArrayList<Word> wordList) {
        super(context, resource, wordList);
        Collections.sort(this.wordList, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                if (o1.weight > o2.weight) {
                    return -1;
                } else if (o1.weight == o2.weight) {
                    if (o1.word.length() < o2.word.length()) {
                        return -1;
                    } else if (o1.word.length() == o2.word.length()) {
                        return (o1.word).compareTo(o2.word);
                    }
                }
                return 1;
            }
        });
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
