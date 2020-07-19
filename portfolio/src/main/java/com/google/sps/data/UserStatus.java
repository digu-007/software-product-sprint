package com.google.sps.data;

/** Authentication info about a user. */
public final class UserStatus {

    private final boolean userLoggedIn;
    private final String urlToRedirect;

    public UserStatus(boolean userLoggedIn, String urlToRedirect) {
        this.userLoggedIn = userLoggedIn;
        this.urlToRedirect = urlToRedirect;
    }
}
