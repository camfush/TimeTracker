package com.example.distinctionproject.AutoCompleters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.distinctionproject.R;
import com.example.distinctionproject.Word;

import java.util.ArrayList;
import java.util.Date;

public class WordAdapter extends ArrayAdapter<Word> {

    protected ArrayList<Word> wordList;
    protected LayoutInflater layoutInflater;

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
                Word currentHold;
                for (Word word : wordList) {
                    if (word.word.toLowerCase().startsWith(constraint.toString().toLowerCase())) {

                        currentHold = word;
                        for (int i = 0; i < 5; i++) {
                            if (i >= suggestions.size()) {
                                suggestions.add(i, currentHold);
                                break;
                            }
                            Word comparison = suggestions.get(i);
                            if (currentHold.weight > comparison.weight || (currentHold.weight == comparison.weight && currentHold.word.length() < comparison.word.length()) || (currentHold.weight == comparison.weight && currentHold.word.length() == comparison.word.length() && currentHold.word.compareTo(comparison.word) < 0)) {
                                Word temp = suggestions.remove(i);
                                suggestions.add(i, currentHold);
                                currentHold = temp;
                            }
                        }
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

    public WordAdapter(Context context, int resource, ArrayList<Word> wordList) {
        super(context, resource, wordList);
        this.wordList = new ArrayList<>();
        this.wordList.addAll(wordList);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.autocorrect_option, null);
        }

        Word word = getItem(position);

        TextView name = view.findViewById(R.id.autocorrect_option);
        name.setText(word.word);

        return view;
    }

}
