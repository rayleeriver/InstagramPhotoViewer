package com.swpbiz.instagramphotoviewer.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.swpbiz.instagramphotoviewer.R;
import com.swpbiz.instagramphotoviewer.models.Comment;

import java.util.List;

public class CommentsArrayAdapter extends ArrayAdapter<Comment> {

    // TODO:  use the Holder patter

    public CommentsArrayAdapter(Context context, int resource, List<Comment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment, parent, false);
        }
        TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);

        Comment comment = getItem(position);
        if (comment!= null) {
            if (tvComment != null) tvComment.setText(Html.fromHtml("<font color='005566'>" + comment.username + "</font> " + comment.text));
        }

        return convertView;
    }
}
