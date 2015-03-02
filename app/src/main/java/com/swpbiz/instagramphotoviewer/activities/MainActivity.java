package com.swpbiz.instagramphotoviewer.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.swpbiz.instagramphotoviewer.models.InstagramPhoto;
import com.swpbiz.instagramphotoviewer.adapters.InstagramPhotosAdapter;
import com.swpbiz.instagramphotoviewer.R;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "9e72661ad71e41c0802074bff1f5cd18";
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
        lvPhotos.setOnItemClickListener(aPhotos);

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
            try {
                aPhotos.clear();
                aPhotos.addAll(InstagramPhoto.fromJsonArray(response.getJSONArray("data")));
            } catch (JSONException e) {
                e.printStackTrace();
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
