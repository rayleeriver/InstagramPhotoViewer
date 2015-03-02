package com.swpbiz.instagramphotoviewer.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class InstagramPhoto implements Parcelable {
    public String mediaId;
    public String username;
    public String profilePictureUrl;
    public String caption;
    public String imageUrl;
    public String likesCount;
    public String timeSpan;
    public int allCommentsCount;
    public List<Comment> comments = new ArrayList<Comment>();

    public static List<InstagramPhoto> fromJsonArray(JSONArray photosJSON) {
        List<InstagramPhoto> photos = new ArrayList<InstagramPhoto>();

        try {
            for (int i = 0; i < photosJSON.length(); i++) {
                JSONObject photoJSON = photosJSON.getJSONObject(i);
                InstagramPhoto photo = new InstagramPhoto();

                photo.mediaId = photoJSON.getString("id");

                JSONObject user = photoJSON.getJSONObject("user");
                if (user != null) {
                    photo.username = user.getString("username");
                    photo.profilePictureUrl = user.getString("profile_picture");
                }

                String createdTime = photoJSON.getString("created_time");
                if (createdTime != null) {
                    photo.timeSpan = DateUtils.getRelativeTimeSpanString(
                            Long.valueOf(createdTime + "000").longValue(),
                            (new Date()).getTime(),
                            0,
                            DateUtils.FORMAT_ABBREV_ALL
                    ).toString();
                }

                JSONObject caption = photoJSON.optJSONObject("caption");
                if (caption != null) photo.caption = caption.getString("text");


                JSONObject images = photoJSON.getJSONObject("images");
                if (images != null)
                    photo.imageUrl = images.getJSONObject("standard_resolution").getString("url");


                JSONObject likes = photoJSON.getJSONObject("likes");
                if (likes != null)
                    photo.likesCount = String.valueOf(new DecimalFormat("#,###,###").format(likes.getInt("count"))) + " likes";

                processCommentsJSON(photoJSON, photo);

                photos.add(photo);
            }
        } catch (JSONException e) {
            Log.e("ERROR", e.getMessage(), e);
        }
        return photos;
    }

    private static void processCommentsJSON(JSONObject photoJSON, InstagramPhoto photo) {
        JSONObject commentsJSON = photoJSON.optJSONObject("comments");
        if (commentsJSON != null) {
            photo.allCommentsCount = commentsJSON.optInt("count");

            JSONArray dataJSON = commentsJSON.optJSONArray("data");
            if (dataJSON != null) {
                for (int j = 0; j < dataJSON.length(); j++) {
                    JSONObject commentJSON = dataJSON.optJSONObject(j);
                    if (commentJSON != null) {
                        Comment comment = new Comment();
                        comment.timestamp = Long.valueOf(commentJSON.optString("created_time"));
                        comment.text = commentJSON.optString("text");

                        JSONObject fromJSON = commentJSON.optJSONObject("from");
                        if (fromJSON != null) {
                            comment.username = fromJSON.optString("username");
                        } else {
                            comment.username = "sortmous";
                        }

                        photo.comments.add(comment);
                    }
                }
            }
        }

        Collections.sort(photo.comments, new CommentComparator());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(caption);
        dest.writeTypedList(comments);
    }
}
