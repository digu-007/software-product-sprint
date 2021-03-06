// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles data of comment section. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final String dataProperty = "data";
    private static final String userEmailProperty = "userEmail";
    private static final String timestampProperty = "timestamp";
    private static final String commentQuery = "Comment";
    private static final String commentParameter = "comment";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query(commentQuery).addSort(timestampProperty, SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        ArrayList<Comment> comments = new ArrayList<>();
        comments = addComments(results);
        
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();

        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }
        
        String data = request.getParameter(commentParameter);
        String userEmail = userService.getCurrentUser().getEmail();

        if (checkCharacterOtherThanWhitespace(data)) {
            long timestamp = System.currentTimeMillis();

            Entity commentEntity = new Entity(commentQuery);
            commentEntity.setProperty(dataProperty, data);
            commentEntity.setProperty(userEmailProperty, userEmail);
            commentEntity.setProperty(timestampProperty, timestamp);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(commentEntity);
        }

        response.sendRedirect("/index.html");
    }

    /** Convert entities into Comment and populates the ArrayList. */
    private ArrayList<Comment> addComments(PreparedQuery results) {        
        ArrayList<Comment> comments = new ArrayList<>();

        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String data = (String) entity.getProperty(dataProperty);
            String userEmail = (String) entity.getProperty(userEmailProperty);
            long timestamp = (long) entity.getProperty(timestampProperty);

            Comment comment = new Comment(id, data, userEmail, timestamp);
            comments.add(comment);
        }

        return comments;
    }

    /** Checks if comment is valid or not. */
    private boolean checkCharacterOtherThanWhitespace(String data) {
       if (data == null) {
           return false;
       }

       return !data.trim().isEmpty();
    }
}
