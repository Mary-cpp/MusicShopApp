package com.example.myapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;

public class FragmentPost extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        Post post = getArguments().getParcelable("post");

        TextView header = v.findViewById(R.id.post_page_header);
        header.setText(post.getTitle());
        TextView text = v.findViewById(R.id.post_page_text);
        text.setText(post.getText());
        TextView author = v.findViewById(R.id.post_page_author);
        author.setText(post.getAuthor());
        TextView date = v.findViewById(R.id.post_page_date);
        // Форматируем дату чтобы передать в кач-ве String аргумента
        SimpleDateFormat myFormat = new SimpleDateFormat("EEE, d MMM yyyy '\nв' K:mm a");
        date.setText(myFormat.format(post.getDate()));
        return v;
    }
}
