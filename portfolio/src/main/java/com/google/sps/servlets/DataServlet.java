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
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles data of comment section. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String data = (String) entity.getProperty("data");
            long timestamp = (long) entity.getProperty("timestamp");

            Comment comment = new Comment(id, data, timestamp);
            comments.add(comment);
        }
        
        Gson gson = new Gson();

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = request.getParameter("comment");

        if (checkValid(data)) {
            long timestamp = System.currentTimeMillis();

            Entity commentEntity = new Entity("Comment");
            commentEntity.setProperty("data", data);
            commentEntity.setProperty("timestamp", timestamp);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(commentEntity);
        }

        response.sendRedirect("/index.html");
    }

    /** Checks if comment is valid or not. */
    private boolean checkValid(String data) {
        boolean valid = true;

        if (data.length() > 0) {
            boolean text_exist = false;
            for (int i = 0; i < data.length(); ++i) {
                char cur = data.charAt(i);
                if (cur != ' ' && cur != '\n' && cur != '\t' && cur != '\r') {
                    text_exist = true;
                    break;
                }
            }

            if (!text_exist) {
                valid = false;
            }
        } else {
            valid = false;
        }

        return valid;
    }
}
