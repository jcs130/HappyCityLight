        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Huiwen Hong -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------

        //function when click on submit button
        function resetPass() {
            var email = $("#email").val();
            $
                .ajax({
                    type: "POST",
                    crossDomain: true,
                    url: serverURL + "user/sendresetpwdmail",
                    data: {
                        "email": email
                    },
                    beforeSend: function () {
                        $("title").html("LOADING ...");
                        $(".input-group-btn").addClass("overlay");
                        $("#arrow").removeClass("fa-arrow-right");
                        $("#arrow").addClass("fa-refresh fa-spin");
                        $("#arrow").attr("disabled", "true");
                    },
                    success: function (data) {
                        if (data.code == 200) {
                            window.location.href = "login.html?message=" + data.message;
                        } else {
                            $(".input-group-btn").removeClass("overlay");
                            $("#arrow").addClass("fa-arrow-right");
                            $("#arrow").removeClass("fa-refresh fa-spin");
                            $("#arrow").attr("disabled", "false");
                        }
                    },
                    error: function (data) {
                        $(".input-group-btn").removeClass("overlay");
                        $("#arrow").addClass("fa-arrow-right");
                        $("#arrow").removeClass("fa-refresh fa-spin");
                        $("#arrow").attr("disabled", "false");
                        alert(data.responseJSON.error);
                        if (data.responseJSON.error.indexOf("MailError") > -1) {
                            $("#mailerror").css("display", "block");
                        } else {
                            window.location.href = "login.html";
                        }
                    }

                });
        }