package com.google.sps.data;

import lombok.AllArgsConstructor;

/** An item on the comment list. */
@AllArgsConstructor
public final class Comment {

    private final long id;
    private final String data;
    private final String userEmail;
    private final long timestamp;
}
