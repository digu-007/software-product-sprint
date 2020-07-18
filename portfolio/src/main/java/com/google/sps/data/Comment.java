package com.google.sps.data;

/** An item on the comment list. */
public final class Comment {

    private final long id;
    private final String data;
    private final long timestamp;

    public Comment(long id, String data, long timestamp) {
        this.id = id;
        this.data = data;
        this.timestamp = timestamp;
    }
}
