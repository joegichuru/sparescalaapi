//handles form authentication
//send data using ajax to server
//save token if success
(function() {
    'use strict';

    var x = document.forms["login-form"]["email"].value;
    if (x == "") {
        alert("Name must be filled out");
        return false;
    }

})();