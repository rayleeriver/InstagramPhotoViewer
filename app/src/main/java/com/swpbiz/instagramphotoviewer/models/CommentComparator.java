package com.swpbiz.instagramphotoviewer.models;

public class CommentComparator implements java.util.Comparator<Comment> {

    @Override
    public int compare(Comment lhs, Comment rhs) {
        if (lhs.timestamp < rhs.timestamp) {
            return 1;
        } else {
            return -1;
        }
    }
}
