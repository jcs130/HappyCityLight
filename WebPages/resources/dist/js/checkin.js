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
    if (navigator.cookieEnabled) {
        logintoken = $.cookie("token");
        user_email = $.cookie("email");
        user_firstname = $.cookie("firstname");
        user_lastname = $.cookie("lastname");
        user_picture = $.cookie("user_picture");
        user_id = $.cookie("user_id");
        //如果参数获取不全则退回登陆界面
        if (logintoken == null || user_id == null) {
            //alert("Please Login First.");
            window.location.href = "login.html?message=Please Login First.";
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
                    checkin_afterChencin();
                } else {
                    alert(JSON.stringify(data));
                    if (data.code == 400) {
                        window.location.href = "login.html?message=Please Login Again.";
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // set [submit] button able if the submit is not
                // successful
            }
        });
    } else {
        //如果Cookie被禁用则通过URL获取tooken和userID然后向服务器请求其他参数
        logintoken = getUrlParam("token");
        user_id = getUrlParam("user_id");
        //如果参数获取不全则退回登陆界面
        if (logintoken == null || user_id == null) {
            //            alert("Please Login First.");
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
                    window.location.href = "login.html?message=Logout success.";
                } else {
                    alert(JSON.stringify(data));
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