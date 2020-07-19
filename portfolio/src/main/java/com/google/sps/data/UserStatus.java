package com.google.sps.data;

import lombok.AllArgsConstructor;

/** Authentication info about a user. */
@AllArgsConstructor
public final class UserStatus {

    private final boolean userLoggedIn;
    private final String urlToRedirect;
}
