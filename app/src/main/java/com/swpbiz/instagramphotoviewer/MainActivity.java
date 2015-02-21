package com.swpbiz.instagramphotoviewer;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static final String CLIENT_ID = "9e72661ad71e41c0802074bff1f5cd18";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);

        photos = new ArrayList<InstagramPhoto>();
        aPhotos = new InstagramPhotosAdapter(this, photos);

        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);
        // send out API request to popular photos
        fetchPopularPhotos();

    }

    private void fetchPopularPhotos() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new MyJsonHttpResponseHandler());
    }

    private class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            JSONArray photosJSON = null;
            try {
                aPhotos.clear();

                photosJSON = response.getJSONArray("data");
                for (int i = 0; i < photosJSON.length(); i++) {
                    JSONObject photoJSON = photosJSON.getJSONObject(i);
                    InstagramPhoto photo = new InstagramPhoto();


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
                    if (likes != null) photo.likesCount = String.valueOf(new DecimalFormat("#,###,###").format(likes.getInt("count"))) + " likes";

                    photos.add(photo);
                }
            } catch (JSONException e) {
                Log.e("ERROR", e.getMessage(), e);
            }

            aPhotos.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }
    }
}
