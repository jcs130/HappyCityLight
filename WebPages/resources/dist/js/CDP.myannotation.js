        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Huiwen Hong -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------

        //get annotation history and display
        function getAnnotationHistory() {
            if (user_id != null && logintoken != null) {
                $
                    .ajax({
                        type: "GET",
                        crossDomain: true,
                        url: serverURL + "annotation/getallrecord",
                        data: {
                            userID: user_id,
                            token: logintoken
                        },
                        dataType: "json",
                        success: function (data, textStatus) {
                            var num = 1;
                            $
                                .each(
                                    data.obj,
                                    function () {
                                        $("#annotation-history")
                                            .append(
                                                "<tr><td>" + num + "</td><td>" + this.mark_at + "</td><td>" + this.text + "</td><td><span class='label emotion'>" + this.emotion_text + "</span></td><td><img src='" + this.media_urls[0] + "' class='annotation-img' alt='Product Image'></td><td><span class='label emotion'>" + this.emotion_medias[0] + "</span></td></tr>");
                                        num = num + 1;
                                    });

                            $.each($(".emotion"), function () {
                                switch ($(this).html()) {
                                case "positive":
                                    $(this).addClass("label-success");
                                    break;
                                case "neutral":
                                    $(this).addClass("label-warning");
                                    break;
                                case "negative":
                                    $(this).addClass("label-danger");
                                    break;
                                default:
                                    break;
                                }
                            });

                            $("#example1").DataTable({
                                "paging": true,
                                "searching": true,
                                "ordering": true,
                                "info": true,
                                "autoWidth": true
                            });

                        }
                    });
            } else {
                alert("Auth error");
            }
        }

        $(function () {
            getAnnotationHistory();
        });