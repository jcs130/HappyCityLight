//the id of current displayed msg, saved for POSTing annotation data to server
var msg_id;
// the local counter showing total number of annotated messages during the
// current annotation
var counter;
// function to get total number of annotations for [Recently Annotated Messages]
// box
function getrecordcount() {
    if (user_id != null && logintoken != null) {
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "annotation/getrecordcount",
            data: {
                userID: user_id,
                token: logintoken
            },
            dataType: "json",
            success: function (data, textStatus) {
                if (data.code == 200) {
                    $(".recent-count").html("View All " + data.obj + " Annotation History");
                } else {
                    alert(JSON.stringify(data));
                    if (data.code == 400) {
                        window.location.href = "login.html?message=Please Login Again.";
                    }
                }
            }
        });
    } else {
        alert("Auth error");
    }
}

// function to get recent annotations records for [Recently Annotated Messages]
// box
function getrecentannotation() {
    if (user_id != null && logintoken != null) {
        $
            .ajax({
                type: "GET",
                crossDomain: true,
                url: serverURL + "annotation/getrecentannotation",
                data: {
                    userID: user_id,
                    token: logintoken
                },
                dataType: "json",
                success: function (data, textStatus) {
                    if (data.code == 200) {
                        $("#recent-annotation").empty();
                        $
                            .each(
                                data.obj,
                                function () {

                                    $("#recent-annotation")
                                        .append(
                                            "<li class='item'><div class='product-img'><img src='" + this.media_urls[0] + "' alt='Product Image'></div><div class='product-info'><a href='javascript::;' class='product-title'>" + this.mark_at + "<span class='label pull-right label-recent'>" + this.emotion_text + "</span></a><span class='product-description'>" + this.text + "</span></div></li>");

                                });
                        $.each($(".label-recent"), function () {
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
                    } else {
                        alert(JSON.stringify(data));
                        if (data.code == 400) {
                            window.location.href = "login.html?message=Please Login Again.";
                        }
                    }
                }

            });
    } else {
        alert("Auth error");
    }
}

// //function to get a new msg to be annotated
function getnewmessage() {

    // get annotation language list
    var annotation_lang_array = new Array();
    $("#annotation-lang option").each(function () {
        if (this.selected) {
            annotation_lang_array.push(this.value);
        }
    });
    var annotation_lang = annotation_lang_array.toString();

    // get annotation part option
    annotation_part = $('input[type="radio"][name="annotation-part"]:checked')
        .val();
    if (user_id != null && logintoken != null) {
        // get a new msg that need to be annotated
        $
            .ajax({
                type: "GET",
                crossDomain: true,
                url: serverURL + "annotation/getnewmessage",
                data: {
                    userID: user_id,
                    token: logintoken,
                    user_email: "huiwen.hong@gmial.com",
                    languages: annotation_lang,
                    annotate_part: annotation_part
                },
                dataType: "json",
                success: function (data, textStatus) {
                    if (data.code == 200) {
                        msg_id = data.obj.msg_id;
                        // clear html element
                        $("#annotation").empty();
                        // append new msg to html element
                        $("#annotation").append(
                            "<div id='owl-demo' class='owl-carousel'></div>");
                        $("#owl-demo")
                            .append(
                                "<div><center><p id='msg-text'>" + data.obj.text + "</p></center><center><div class='btn-group' id='text-btn-group' data-toggle='buttons'><label class='btn btn-app btn-emo btn-positive' ><input type='radio' name='options' id='positive'><i class='fa fa-smile-o'></i>Positive</label><label class='btn btn-app btn-emo btn-neutral'><input type='radio' name='options' id='neutral'><i class='fa fa-meh-o'></i>Neutral</label><label class='btn btn-app btn-emo btn-negative'><input type='radio' name='options' id='negative'><i class='fa fa-frown-o'></i>Negative</label></div></center></div>");
                        $
                            .each(
                                data.obj.medias,
                                function () {
                                    $("#owl-demo")
                                        .append(
                                            "<div><img class='col-md-12' src='" + this.media_url + "' /><center><div class='btn-group img-btn-group'  data-toggle='buttons'><label class='btn btn-app btn-emo btn-positive'><input type='radio' name='options' id='positive'><i class='fa fa-smile-o'></i>Positive</label><label class='btn btn-app btn-emo btn-neutral'><input type='radio' name='options' id='neutral'><i class='fa fa-meh-o'></i>Neutral</label><label class='btn btn-app btn-emo btn-negative'><input type='radio' name='options' id='negative'><i class='fa fa-frown-o'></i>Negative</label></div></center></div>");

                                });

                        // activate owl-carousel
                        $("#owl-demo").owlCarousel({
                            navigation: true,
                            paginationSpeed: 1000,
                            goToFirstSpeed: 2000,
                            singleItem: true,
                            autoHeight: true,
                            transitionStyle: "fade",
                            navigationText: ["prev item", "next item"]
                        });

                        // click event to validate if all msg item are annotated
                        $("#owl-demo").find("label").mouseup(
                            function () {
                                setTimeout(function () {

                                    var select_num = $("#owl-demo").find(
                                        "label.active").length;
                                    var all_num = $("#owl-demo").find(
                                        ".btn-group").length;
                                    if (all_num == select_num) {
                                        // alert("all selected!");
                                        $("#btn-next").prop('disabled', false);
                                    }

                                }, 30);

                            });

                        $("#btn-skip").prop('disabled', false);
                    } else {
                        alert(JSON.stringify(data));
                        if (data.code == 400) {
                            window.location.href = "login.html?message=Please Login Again.";
                        }
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $("#btn-skip").prop('disabled', false);
                }
            });
    } else {
        alert("Auth error");
    }
}
//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {
    // get data for [Recently Annotated Messages] box
    getrecordcount();
    getrecentannotation();
}
$(function () {
    // Initialize Select2 plugin
    $(".select2").select2();
    // Initialize iCheck plugin
    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
        checkboxClass: 'icheckbox_flat-green',
        radioClass: 'iradio_flat-green'
    });
    // reload the page when annotation modal is closed (to update [Recently
    // Annotated Messages] box)
    $("#btn-closemaodal").click(function () {
        location.reload();
    });
    // clear html elements and counter when annotation modal is closed
    // successfully
    $('#annotation-modal').on('hide.bs.modal', function () {
        $("#annotation").empty();
        counter = 0;
    });

    /*
     * $('#iannotation-modal').on('show.bs.modal', function() { })
     */

    // Operations when click on [start-annotation] button
    $("#start-annotation").click(function () {
        counter = 0;
        $("#this-counter").html(counter);
        // set [submit] button disabled until the new message be fully annotated
        $("#btn-next").prop('disabled', true);
        $("#btn-skip").prop('disabled', true);
        getnewmessage();

    });

    // Operations when click on [skip] button
    $("#btn-skip").click(function () {
        // set [submit] button disabled until the new message be fully annotated
        $("#btn-next").prop('disabled', true);
        $("#btn-skip").prop('disabled', true);
        getnewmessage();
    });

    // Operations when click on [submit] button
    $("#btn-next").click(
        function () {

            // set [submit] button disabled until the new message be fully
            // annotated
            $("#btn-next").prop('disabled', true);

            // get annotation data (emotions)
            var text_emotion = $("#text-btn-group").find(
                "label.active input").attr("id");

            var media_emotion_array = new Array();
            $.each($(".img-btn-group").find("label.active input"),
                function () {
                    media_emotion_array.push($(this).attr("id"));
                });
            var media_emotions = media_emotion_array.toString();

            // send annotation data (emotions) to server
            $.ajax({
                type: "POST",
                crossDomain: true,
                url: serverURL + "annotation/sendannotatedmessage",
                data: {
                    userID: user_id,
                    token: logintoken,
                    user_email: "temp",
                    msg_id: msg_id,
                    text_emotion: text_emotion,
                    media_emotions: media_emotions
                },
                dataType: "json",
                success: function (data, textStatus) {
                    if (data.code == 200) {

                        // update counter
                        counter = counter + 1;
                        $("#this-counter").html(counter);
                        // get a new message that need to be annotated
                        getnewmessage();
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
                    $("#btn-next").prop('disabled', false);
                }
            });

        });

});