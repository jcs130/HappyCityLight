        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Zhongli Li -
        //- Concept and supervision Abdulmotaleb El Saddik -

        //-------------

        $(function () {
            $('input').iCheck({
                checkboxClass: 'icheckbox_square-blue',
                radioClass: 'iradio_square-blue',
                increaseArea: '20%' // optional
            });
        });


        var homepage = "" + default_homepage;

        function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //format a regular expression obj that includes parameter name
            var r = window.location.search.substr(1).match(reg); //match the regular expression
            if (r != null) return unescape(r[2]);
            return null;
        }
        //check if login, if already login, redirect to index page
        //funtion to check user info
        function checkLogin() {
            var token = $.cookie("token");
            var user_id = $.cookie("user_id");
            if (token == null || user_id == null) {
                token = localStorage.getItem("token");
                user_id = localStorage.getItem("user_id");
                if (token == null || user_id == null) {
                    //if cookie is banned
                    token = getUrlParam("token");
                    user_id = getUrlParam("user_id");
                    homepage = homepage + "?user_id=" + user_id + "&token=" + token;
                }
            }
            if (token != null && user_id != null) {
                //if token correct, redirect to homepage
                $.ajax({
                    type: "POST",
                    crossDomain: true,
                    url: serverURL + "user/tokencheck",
                    data: {
                        "userID": user_id,
                        "token": token
                    },
                    dataType: "json",
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            //token正确,直接前往homepage
                            window.location.href = homepage;
                        } else {
                            //清除cookie
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
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        //清除cookie
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
            
            var message = getUrlParam("message");
            if (message != null) {
                //                    alert(message);
                $("#infobar").html("<div class='alert alert-info alert-dismissible'><button type='button'class='close' data-dismiss='alert'aria-hidden='true'>×</button><div class='text-center'<h4><i class='icon fa fa-info'></i> " + message + "</h4></div></div>");
            }
        }
        $(document).ready(function () {
            checkLogin();
            $("form").submit(function (e) {
                e.preventDefault();
                /* alert("Submit prevented"); */
            });
            $('#loginBtn').click(function () {
                var email = $("#email").val();
                var password = $("#password").val();
                var dataString = 'email=' + email + '&password=' + password;
                //                alert(dataString);
                if ($.trim(email).length > 0 && $.trim(password).length > 0) {
                    $.ajax({
                        type: "POST",
                        crossDomain: true,
                        url: serverURL + "user/login",
                        data: {
                            "email": email,
                            "password": password
                        },
                        cache: false,
                        beforeSend: function () {
                            $("#loginBtn").val('Connecting...');
                        },
                        success: function (data) {
                            //alert(JSON.stringify(data));
                            if (data.code == 200) {
                                //如果启用了cookie则存储在cookie中
                                //向浏览器cookie中保存登录信息
                                $.cookie("user_id", data.obj.user_id, {
                                    expires: 30
                                });
                                $.cookie("token", data.message, {
                                    expires: 30
                                });
                                //同时向本地存储存(Chrome禁用了本地Cookie)
                                localStorage.setItem("user_id", data.obj.user_id);
                                localStorage.setItem("token", data.message);
                                $.cookie("email", data.obj.email, {
                                    expires: 30
                                });
                                $.cookie("firstname", data.obj.firstname, {
                                    expires: 30
                                });
                                $.cookie("lastname", data.obj.lastname, {
                                    expires: 30
                                });
                                $.cookie("user_picture", data.obj.user_picture, {
                                    expires: 30
                                });
                                $.cookie("user_roles", data.obj.user_roles, {
                                    expires: 30
                                });
                                if ($.cookie("user_id") == null || $.cookie("token") == null) {
                                    //如果cookie被禁用，则使用HTML5的本地存储
                                    localStorage.setItem("user_id", data.obj.user_id);
                                    localStorage.setItem("token", data.message);
                                    //既不能用cookie也不能用本地存储
                                    if (localStorage.user_id == null || localStorage.token == null) {
                                        homepage = homepage + "?user_id=" + data.obj.user_id + "&token=" + data.message;
                                    }
                                }
                                localStorage.setItem("email", data.obj.email);
                                localStorage.setItem("firstname", data.obj.firstname);
                                localStorage.setItem("lastname", data.obj.lastname);
                                localStorage.setItem("user_picture", data.obj.user_picture);
                                localStorage.setItem("user_roles", data.obj.user_roles);
                                window.location.href = homepage;
                            } else {
                                $("#loginBtn").val('Sign In');
                                alert(data.message);
                            }
                        },
                        error: function (data) {
                            $("#loginBtn").val('Sign In');
                            alert(JSON.stringify(data));

                        }
                    });
                } else {
                    alert("empty");
                }
                return false;
            });

            $('#loginAsGuest').click(function () {
                $.ajax({
                    type: "POST",
                    crossDomain: true,
                    url: serverURL + "user/login",
                    data: {
                        "email": "test@test.com",
                        "password": "test"
                    },
                    cache: false,
                    beforeSend: function () {
                        $("#loginBtn").val('Connecting...');
                    },
                    success: function (data) {
                        //alert(JSON.stringify(data));
                        if (data.code == 200) {
                            //如果启用了cookie则存储在cookie中
                            //向浏览器cookie中保存登录信息
                            $.cookie("user_id", data.obj.user_id, {
                                expires: 30
                            });
                            $.cookie("token", data.message, {
                                expires: 30
                            });
                            //同时向本地存储存(Chrome禁用了本地Cookie)
                            localStorage.setItem("user_id", data.obj.user_id);
                            localStorage.setItem("token", data.message);
                            $.cookie("email", data.obj.email, {
                                expires: 30
                            });
                            $.cookie("firstname", data.obj.firstname, {
                                expires: 30
                            });
                            $.cookie("lastname", data.obj.lastname, {
                                expires: 30
                            });
                            $.cookie("user_picture", data.obj.user_picture, {
                                expires: 30
                            });
                            $.cookie("user_roles", data.obj.user_roles, {
                                expires: 30
                            });
                            if ($.cookie("user_id") == null || $.cookie("token") == null) {
                                //如果cookie被禁用，则使用HTML5的本地存储
                                localStorage.setItem("user_id", data.obj.user_id);
                                localStorage.setItem("token", data.message);
                                //既不能用cookie也不能用本地存储
                                if (localStorage.user_id == null || localStorage.token == null) {
                                    homepage = homepage + "?user_id=" + data.obj.user_id + "&token=" + data.message;
                                }
                            }
                            localStorage.setItem("email", data.obj.email);
                            localStorage.setItem("firstname", data.obj.firstname);
                            localStorage.setItem("lastname", data.obj.lastname);
                            localStorage.setItem("user_picture", data.obj.user_picture);
                            localStorage.setItem("user_roles", data.obj.user_roles);
                            window.location.href = homepage;
                        } else {
                            $("#loginBtn").val('Sign In');
                            alert(data.message);
                        }
                    },
                    error: function (data) {
                        $("#loginBtn").val('Sign In');
                        alert(JSON.stringify(data));
                    }
                });
            });

        });