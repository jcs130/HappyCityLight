var logintoken = null;
var user_firstname = "FIRSTNAME";
var user_lastname = "LASTNAME";
var user_picture = "";
var user_id = null;
var user_email = "";

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) return unescape(r[2]);
    return null; //返回参数值
}

//检查用户信息的函数
function checkLogin() {
    logintoken = $.cookie("token");
    user_id = $.cookie("user_id");
    user_email = localStorage.getItem("email");
    user_firstname = localStorage.getItem("firstname");
    user_lastname = localStorage.getItem("lastname");
    user_picture = localStorage.getItem("user_picture");
    //    alert(user_picture);
    //更改页面上显示文字
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
        alert(user_picture);
        $(".user_picture").attr("src", user_picture);
    }
    $(".useremail").html(user_email);
    if (logintoken == null || user_id == null) {
        //通过localStorage获取登录信息
        logintoken = localStorage.getItem("token");
        user_id = localStorage.getItem("user_id");
        //如果还是没有,则检查URL参数
        if (logintoken == null || user_id == null) {
            //如果Cookie被禁用则通过URL获取tooken和userID然后向服务器请求其他参数
            logintoken = getUrlParam("token");
            user_id = getUrlParam("user_id");
            //alert("Cookie 被禁用,  token: " + logintoken + " <> user_id: " + user_id);
            //如果参数获取不全则退回登陆界面
            if (logintoken == null || user_id == null) {
                //alert("参数不全");
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
                window.location.href = "login.html?message=Please Login First.";
            }
            //连接服务器获取其他信息
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
                        //在inside-link后面添加参数
                        $("a.inside-link").each(function () {
                            var _href = $(this).attr("href");
                            $(this).attr("href", _href + "?user_id=" + user_id + "&token=" + logintoken);
                            //alert($(this).attr("href"));
                        });
                        //更改页面上显示文字
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
                            alert(user_picture);
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
                    // set [submit] button able if the submit is not
                    // successful
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
    }
    //如果token过期则返回登陆界面
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
                //token正确
                //更改页面上显示文字
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
    //检查登陆
    checkLogin();
    //登出按钮的方法
    $("#btn-signout").click(function () {
        //重置Token清空Cookie并且返回登陆界面
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
                    localStorage.clear();
                    window.location.href = "login.html?message=Logout success.";
                } else {
                    alert(JSON.stringify(data));
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