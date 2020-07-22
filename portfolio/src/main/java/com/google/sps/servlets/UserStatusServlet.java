package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.UserStatus;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that checks if the user is logged in or not and gets the redirect url. */
@WebServlet("/user-status")
public class UserStatusServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();

        boolean userLoggedIn = userService.isUserLoggedIn();
        String urlToRedirect;

        if (userLoggedIn) {
            urlToRedirect = userService.createLogoutURL("/");
        } else {
            urlToRedirect = userService.createLoginURL("/");
        }

        UserStatus userStatus = new UserStatus(userLoggedIn, urlToRedirect);

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(userStatus));
    }
}
