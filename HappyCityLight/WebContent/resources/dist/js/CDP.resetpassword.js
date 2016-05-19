        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Huiwen Hong -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------


        var token = null;

        function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]);
            return null;
        }

        //submit reset info to the server
        function reset(event) {
            event.preventDefault();
            $(".alert").html("").hide();
            $(".error-list").html("");
            if (token == null) {
                $("#globalError")
                    .show()
                    .html("Token Error!");
                return;
            }
            if ($("#password").val() != $("#matchPassword").val()) {
                $("#globalError")
                    .show()
                    .html("Password not match.");
                return;
            }
            var password = $("#password").val();
            $.ajax({
                type: "POST",
                dataType: "JSON",
                crossDomain: true,
                url: serverURL + "user/resetPassword",
                data: {
                    "token": token,
                    "newpassword": password
                },
                beforeSend: function () {
                    //                    alert("click on submit!");
                    $("title").html("Uploading Registration Info ...");
                    $("#ResetButton").attr("disabled", "true");
                    $("#ResetButton").html("");
                    $("#ResetButton")
                        .append(
                            "<i class='fa fa-refresh fa-spin text-muted'></i>");
                },
                success: function (data) {
                    //                        alert(JSON.stringify(data));
                    if (data.code == 200) {
                        //                            alert("success!");
                        window.location.href = "login.html?message=Reset password success.";
                    } else {
                        $("#ResetButton").html("Reset");
                        $("#ResetButton").attr("disabled", "false");
                        alert(data.message);
                        window.location.href = "forgetPassword.html";
                    }
                },
                error: function (data) {
                    $("#ResetButton").html("Reset");
                    $("#ResetButton").attr("disabled", "false");
                    alert(JSON.stringify(data));
                }
            });
        }


        $(function () {
            token = getUrlParam("token");
            //alert(token);
            $('input').iCheck({
                checkboxClass: 'icheckbox_square-blue',
                radioClass: 'iradio_square-blue',
                increaseArea: '20%' // optional
            });

            $('form').submit(function (event) {
                reset(event);
            });
            $(":password").keyup(function () {
                if ($("#password").val() != $("#matchPassword").val()) {
                    $("#globalError").show().html("Password not match");
                } else {
                    $("#globalError").html("").hide();
                }
            });

            options = {
                common: {
                    minChar: 8,
                    usernameField: "firstName"
                },
                ui: {
                    showVerdictsInsideProgressBar: true,
                    showErrors: true,
                    errorMessages: {
                        wordLength: "Password too short.",
                        wordNotEmail: "The email format is wrong.",
                        wordLowercase: "Need at least one lowercase word",
                        wordOneNumber: "Need at least one number",
                        wordLetterNumberCombo: "Need both letter and number."
                    }
                },
                rules: {
                    scores: {
                        wordNotEmail: -100,
                        wordLength: -50,
                        wordSimilarToUsername: -50,
                        wordSequences: 0,
                        wordTwoCharacterClasses: 2,
                        wordRepetitions: -6,
                        wordLowercase: 5,
                        wordUppercase: 3,
                        wordOneNumber: 3,
                        wordThreeNumbers: 5,
                        wordOneSpecialChar: 3,
                        wordTwoSpecialChar: 5,
                        wordUpperLowerCombo: 2,
                        wordLetterNumberCombo: 3,
                        wordLetterNumberCharCombo: 2
                    },
                    activated: {
                        wordNotEmail: true,
                        wordLength: true,
                        wordSimilarToUsername: true,
                        wordSequences: false,
                        wordTwoCharacterClasses: false,
                        wordRepetitions: false,
                        wordLowercase: true,
                        wordUppercase: false,
                        wordOneNumber: true,
                        wordThreeNumbers: false,
                        wordOneSpecialChar: false,
                        wordTwoSpecialChar: false,
                        wordUpperLowerCombo: false,
                        wordLetterNumberCombo: true,
                        wordLetterNumberCharCombo: false
                    }
                }
            };
            $('#password').pwstrength(options);
        });