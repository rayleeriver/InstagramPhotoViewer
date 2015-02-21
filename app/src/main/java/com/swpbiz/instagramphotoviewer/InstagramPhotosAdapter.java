package com.swpbiz.instagramphotoviewer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.List;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        ImageView ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePhoto);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView tvTimespan = (TextView) convertView.findViewById(R.id.tvTimespan);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);

        tvUsername.setText(photo.username);
        tvTimespan.setText(photo.timeSpan);
        tvCaption.setText(photo.caption);
        tvLikes.setText(photo.likesCount);

        // download image with Picasso
        ivProfilePicture.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .oval(true)
                .build();
        Picasso.with(getContext()).load(photo.profilePictureUrl).placeholder(R.drawable.abc_ab_share_pack_holo_light).transform(transformation).into(ivProfilePicture);

        ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl).fit().centerCrop().placeholder(R.drawable.abc_ab_share_pack_holo_light).into(ivPhoto);

        return convertView;
    }
}
