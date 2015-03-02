package com.swpbiz.instagramphotoviewer.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.swpbiz.instagramphotoviewer.R;
import com.swpbiz.instagramphotoviewer.activities.MainActivity;
import com.swpbiz.instagramphotoviewer.models.Comment;
import com.swpbiz.instagramphotoviewer.models.CommentComparator;
import com.swpbiz.instagramphotoviewer.models.InstagramPhoto;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> implements AdapterView.OnItemClickListener {

    // TODO:  still need more work to handle ViewHolderItem properly.
    ViewHolderItem holder;

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);

            holder = new ViewHolderItem();

            holder.ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePhoto);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            holder.tvTimespan = (TextView) convertView.findViewById(R.id.tvTimespan);
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            holder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            holder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            holder.tvViewAllComments = (TextView) convertView.findViewById(R.id.tvViewAllComments);
            holder.tvLatestComment = (TextView) convertView.findViewById(R.id.tvLastestComment);
            holder.tvSecondLatestComment = (TextView) convertView.findViewById(R.id.tvSecondLastestComment);
            holder.position = position;

        } else {
            holder = (ViewHolderItem) convertView.getTag();
        }

        InstagramPhoto photo = getItem(position);

        if (photo != null) {

            holder.mediaId = photo.mediaId;

            holder.ivProfilePicture.setImageResource(0);
            Transformation transformation = new RoundedTransformationBuilder()
                    .oval(true)
                    .build();
            Picasso.with(getContext()).load(photo.profilePictureUrl).placeholder(R.drawable.abc_ab_share_pack_holo_light).transform(transformation).into(holder.ivProfilePicture);

            holder.tvUsername.setText(photo.username);

            holder.tvTimespan.setText(photo.timeSpan);

            holder.ivPhoto.setImageResource(0);
            Picasso.with(getContext()).load(photo.imageUrl).fit().centerCrop().placeholder(R.drawable.abc_ab_share_pack_holo_light).into(holder.ivPhoto);

            holder.tvLikes.setText(photo.likesCount);

            holder.tvCaption.setText(photo.caption);

            if (photo.comments.size() > 2) {
                holder.tvViewAllComments.setVisibility(View.VISIBLE);
                holder.tvViewAllComments.setText("view all " + photo.allCommentsCount + " comments...");
            }

            if (photo.comments.size() >= 2) {
                holder.tvSecondLatestComment.setVisibility(View.VISIBLE);
                Comment comment = photo.comments.get(1);
                holder.tvSecondLatestComment.setText(Html.fromHtml("<font color='005566'>" + comment.username + "</font> " + comment.text));
            }

            if (photo.comments.size() >= 1) {
                holder.tvLatestComment.setVisibility(View.VISIBLE);
                Comment comment = photo.comments.get(0);
                holder.tvLatestComment.setText(Html.fromHtml("<font color='005566'>" + comment.username + "</font> " + comment.text));
            }

        }

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fetchAllCommentsForMediaId(getItem(position));
    }

    static class ViewHolderItem {
        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvTimespan;
        ImageView ivPhoto;
        TextView tvLikes;
        TextView tvCaption;
        TextView tvViewAllComments;
        TextView tvLatestComment;
        TextView tvSecondLatestComment;
        String mediaId;
        int position;
    }

    private void fetchAllCommentsForMediaId(InstagramPhoto instagramPhoto) {
        String url = "https://api.instagram.com/v1/media/" + instagramPhoto.mediaId + "/comments?client_id=" + MainActivity.CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new MediaCommentsJsonHttpResponseHandler(instagramPhoto));
    }

    private class MediaCommentsJsonHttpResponseHandler extends JsonHttpResponseHandler {
        InstagramPhoto instagramPhoto;

        public MediaCommentsJsonHttpResponseHandler(InstagramPhoto instagramPhoto) {
            this.instagramPhoto = instagramPhoto;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            List<Comment> comments = new ArrayList<Comment>();
            JSONArray commentsJSON = response.optJSONArray("data");
            if (commentsJSON != null) {
                for (int i = 0; i < commentsJSON.length(); i++) {
                    JSONObject commentJSON = commentsJSON.optJSONObject(i);
                    if (commentJSON != null) {
                        Comment comment = new Comment();
                        comment.timestamp = Long.valueOf(commentJSON.optString("created_time"));
                        comment.text = commentJSON.optString("text");

                        JSONObject fromJSON = commentJSON.optJSONObject("from");
                        if (fromJSON != null) {
                            comment.username = fromJSON.optString("username");
                        } else {
                            comment.username = "anonymous";
                        }
                        comments.add(comment);
                    }
                }
            }

            Collections.sort(comments, new CommentComparator());

            Bundle args = new Bundle();
            args.putParcelable("instagramPhoto", instagramPhoto);
            args.putParcelableArrayList("comments", (ArrayList<Comment>) comments);
            CommentsDialogFragment dialogFragment = new CommentsDialogFragment();
            dialogFragment.setArguments(args);

            FragmentManager fragmentManager = ((Activity) getContext()).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            CommentsDialogFragment previousDialogFragment = (CommentsDialogFragment) fragmentManager.findFragmentByTag("dialog");
            if (previousDialogFragment != null) {
                fragmentTransaction.remove(previousDialogFragment);
            }

            dialogFragment.show(fragmentTransaction, "dialog");

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }
    }

    public static class CommentsDialogFragment extends DialogFragment {

        public CommentsDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();
            ArrayList<Comment> comments = args.getParcelableArrayList("comments");
            InstagramPhoto instagramPhoto = args.getParcelable("instagramPhoto");
            return new AlertDialog.Builder(getActivity())
                    .setTitle("All Comments for "+ instagramPhoto.username)
                    .setAdapter(new CommentsArrayAdapter(getActivity(), 0, comments), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }
}