    //-------------
    //- ELEMENT SIZE -
    //-------------

    //size initialization of some elements 
    if ($(window).width() < 768) {
        $("#header-logo").hide();
        $("#titleForPhone").show();
        $('#titleForPhone').css('margin-left', $(window).width() / 2 - 82);
    } else {
        $("#header-logo").show();
        $("#titleForPhone").hide();
    }
    $('.content-wrapper').css('height', $(window).height() - 101);

    //resize & show/hide elements when window size changes
    $(window).resize(function () {
        setTimeout(function () {
            if ($(window).width() < 768) {
                $("#header-logo").hide();
                $("#titleForPhone").show();
                $('#titleForPhone').css('margin-left', $(window).width() / 2 - 82);
            } else {
                $("#header-logo").show();
                $("#titleForPhone").hide();
            }
            $('.content-wrapper').css('height', $(window).height() - 101);
        }, 30);
    });

    //-------------
    //- END OF ELEMENT SIZE -
    //-------------