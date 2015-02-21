package com.swpbiz.instagramphotoviewer;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class InstagramPhoto {
    public String mediaId;
    public String username;
    public String profilePictureUrl;
    public String caption;
    public String imageUrl;
    public String likesCount;
    public String timeSpan;
    public int allCommentsCount;
    public List<Comment> comments = new ArrayList<Comment>();

}
