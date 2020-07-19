package com.google.sps.data;

/** An item on the comment list. */
public final class Comment {

    private final long id;
    private final String data;
    private final String userEmail;
    private final long timestamp;

    public Comment(long id, String data, String userEmail, long timestamp) {
        this.id = id;
        this.data = data;
        this.userEmail = userEmail;
        this.timestamp = timestamp;
    }
}
