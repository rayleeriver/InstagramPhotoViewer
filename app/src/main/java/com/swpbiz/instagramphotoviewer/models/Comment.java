package com.swpbiz.instagramphotoviewer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable{
    public long timestamp;
    public String username;
    public String text;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeString(username);
        dest.writeString(text);
    }
}
