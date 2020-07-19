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

// Fetch the user status and initiates the required front end.
function getUserStatus() {
    fetch('/user-status').then(response => response.json()).then((userStatus) => {
        if (userStatus.userLoggedIn) {
            const urlToLogoutElement = document.getElementById('logout');
            urlToLogoutElement.href = userStatus.urlToRedirect;

            const hideLeaveYourCommentElement = document.getElementById('box4');
            hideLeaveYourCommentElement.hidden = false;

            const hideLogoutElement = document.getElementById('logout-form');
            hideLogoutElement.hidden = false;
        } else {
            const urlToLoginElement = document.getElementById('login');
            urlToLoginElement.href = userStatus.urlToRedirect;

            const hideLoginElement = document.getElementById('login-form');
            hideLoginElement.hidden = false;
        }
    });

    getComments();
}

// Fetch the comment list and adds equivalent HTML code to the homepage.
function getComments() {
    fetch('/data').then(response => response.json()).then((comments) => {
        const commentListElement = document.getElementById('comment-list');
       
        commentListElement.innerHTML = '';
        for (var i = 0; i < comments.length; ++i) {
            commentListElement.appendChild(createListElement(comments[i]));
            commentListElement.appendChild(createBreakElement());
        }
    });
}

// Create <li> item for comments
function createListElement(comment) {
    const liElement = document.createElement('li');
    liElement.innerText = comment.userEmail + ': ' + comment.data;
    return liElement;
}

// Creates <br> item after a comment
function createBreakElement() {
    const brElement = document.createElement('br');
    return brElement;
}
