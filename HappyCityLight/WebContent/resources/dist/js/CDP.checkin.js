        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Zhongli Li -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------

        var logintoken = null;
        var user_firstname = "FIRSTNAME";
        var user_lastname = "LASTNAME";
        var user_picture = "";
        var user_id = null;
        var user_email = "";

        function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //format a regular expression obj that includes parameter name
            var r = window.location.search.substr(1).match(reg); //match the regular expression
            if (r != null) return unescape(r[2]);
            return null;
        }

        //funtion to check user info
        function checkLogin() {
            logintoken = $.cookie("token");
            user_id = $.cookie("user_id");
            user_email = localStorage.getItem("email");
            user_firstname = localStorage.getItem("firstname");
            user_lastname = localStorage.getItem("lastname");
            user_picture = localStorage.getItem("user_picture");

            //change the text diaplayed on the webpage
            if (user_firstname != "null") {
                $(".userfirstname").html(user_firstname);
            } else {
                $(".userfirstname").html("");
            }
            if (user_lastname != "null") {
                $(".userlastname").html(user_lastname);
            } else {
                $(".userlastname").html("");
            }
            if ((user_picture != null) && (user_picture != "null") && (user_picture != undefined)) {
                $(".user_picture").attr("src", user_picture);
            }
            $(".useremail").html(user_email);

            if (logintoken == null || user_id == null) {
                //get login info through localStorage
                logintoken = localStorage.getItem("token");
                user_id = localStorage.getItem("user_id");

                //check URL parameters
                if (logintoken == null || user_id == null) {
                    //if Cookie is banned, get logintoken and user ID vie URL and request other parameter through server
                    logintoken = getUrlParam("token");
                    user_id = getUrlParam("user_id");
                    //alert("Cookie is banned,  token: " + logintoken + " <> user_id: " + user_id);
                    //if parameter is not imcomplete, redirect to login page
                    if (logintoken == null || user_id == null) {
                        //alert("parameter imcomplete!");
                        //remove Cookie
                        $.removeCookie('token', {
                            path: '/'
                        });
                        $.removeCookie('email', {
                            path: '/'
                        });
                        $.removeCookie('firstname', {
                            path: '/'
                        });
                        $.removeCookie('lastname', {
                            path: '/'
                        });
                        $.removeCookie('user_picture', {
                            path: '/'
                        });
                        $.removeCookie('user_id', {
                            path: '/'
                        });
                        window.location.href = "login.html?message=Please Login First.";
                    }

                    //get user detail info 
                    $.ajax({
                        type: "GET",
                        crossDomain: true,
                        url: serverURL + "user/getuserdetail",
                        data: {
                            userID: user_id,
                            token: logintoken
                        },
                        dataType: "json",
                        success: function (data, textStatus) {
                            if (data.code == 200) {
                                user_email = data.obj.email;
                                user_picture = data.obj.user_picture;
                                user_firstname = data.obj.firstname;
                                user_lastname = data.obj.lastname;
                                //add parameter in inside-link tag
                                $("a.inside-link").each(function () {
                                    var _href = $(this).attr("href");
                                    $(this).attr("href", _href + "?user_id=" + user_id + "&token=" + logintoken);
                                    //alert($(this).attr("href"));
                                });
                                //change the text diaplayed on the webpage
                                if (user_firstname != "null") {
                                    $(".userfirstname").html(user_firstname);
                                } else {
                                    $(".userfirstname").html("");
                                }
                                if (user_lastname != "null") {
                                    $(".userlastname").html(user_lastname);
                                } else {
                                    $(".userlastname").html("");
                                }
                                if ((user_picture != null) && (user_picture != "null") && (user_picture != undefined)) {
                                    $(".user_picture").attr("src", user_picture);
                                }
                                $(".useremail").html(user_email);
                                checkin_afterChencin();
                            } else {
                                alert(JSON.stringify(data));
                                window.location.href = "login.html?message=Please Login Again."
                            }
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            alert(JSON.stringify(jqXHR));
                            // set [submit] button able if the submit is not successful
                            //remove Cookie
                            $.removeCookie('token', {
                                path: '/'
                            });
                            $.removeCookie('email', {
                                path: '/'
                            });
                            $.removeCookie('firstname', {
                                path: '/'
                            });
                            $.removeCookie('lastname', {
                                path: '/'
                            });
                            $.removeCookie('user_picture', {
                                path: '/'
                            });
                            $.removeCookie('user_id', {
                                path: '/'
                            });
                        }
                    });
                }
            }

            //if logintoken expires, redirect to login page 
            $.ajax({
                type: "POST",
                crossDomain: true,
                url: serverURL + "user/tokencheck",
                data: {
                    userID: user_id,
                    token: logintoken
                },
                dataType: "json",
                success: function (data, textStatus) {
                    if (data.code == 200) {
                        //lgointoken is correct
                        //change the text diaplayed on the webpage
                        if (user_firstname != "null") {
                            $(".userfirstname").html(user_firstname);
                        } else {
                            $(".userfirstname").html("");
                        }
                        if (user_lastname != "null") {
                            $(".userlastname").html(user_lastname);
                        } else {
                            $(".userlastname").html("");
                        }
                        $(".useremail").html(user_email);
                        checkin_afterChencin();
                    } else {
                        alert(JSON.stringify(data));

                        window.location.href = "login.html?message=Please Login Again.";

                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // set [submit] button able if the submit is not
                    // successful
                    window.location.href = "login.html?message=Please Login Again."
                }
            });
        }

        $(function () {
            //check user info
            checkLogin();
            //function when user lick [sign out] button
            $("#btn-signout").click(function () {
                //reset Token, remove Cookie, and redirect yo login page
                $.ajax({
                    type: "POST",
                    crossDomain: true,
                    url: serverURL + "user/logout",
                    data: {
                        userID: user_id,
                        token: logintoken
                    },
                    dataType: "json",
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            //remove Cookie
                            $.removeCookie('token', {
                                path: '/'
                            });
                            $.removeCookie('email', {
                                path: '/'
                            });
                            $.removeCookie('firstname', {
                                path: '/'
                            });
                            $.removeCookie('lastname', {
                                path: '/'
                            });
                            $.removeCookie('user_picture', {
                                path: '/'
                            });
                            $.removeCookie('user_id', {
                                path: '/'
                            });
                            localStorage.clear();
                            window.location.href = "login.html?message=Logout success.";
                        } else {
                            alert(JSON.stringify(data));
                            //remove Cookie
                            $.removeCookie('token', {
                                path: '/'
                            });
                            $.removeCookie('email', {
                                path: '/'
                            });
                            $.removeCookie('firstname', {
                                path: '/'
                            });
                            $.removeCookie('lastname', {
                                path: '/'
                            });
                            $.removeCookie('user_picture', {
                                path: '/'
                            });
                            $.removeCookie('user_id', {
                                path: '/'
                            });
                            localStorage.clear();
                            window.location.href = "login.html?message=Logout success.";
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $.removeCookie('token', {
                            path: '/'
                        });
                        $.removeCookie('email', {
                            path: '/'
                        });
                        $.removeCookie('firstname', {
                            path: '/'
                        });
                        $.removeCookie('lastname', {
                            path: '/'
                        });
                        $.removeCookie('user_picture', {
                            path: '/'
                        });
                        $.removeCookie('user_id', {
                            path: '/'
                        });
                        window.location.href = "login.html?message=Please Login Again..";
                    }
                });
            });
        });