        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Huiwen Hong -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------


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
        //$('.content-wrapper').css('height', $(window).height() - 101);

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
                //$('.content-wrapper').css('height', $(window).height() - 101);
            }, 30);
        });

        //-------------
        //- END OF ELEMENT SIZE -
        //-------------

        //-------------
        //- FUNCTIONS -
        //-------------
        var weekdayArray = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        var monthArray = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

        //a function to parse date obj to a certain format 
        //e.g. 2016-01-01
        function getFormattedDate(date) {
            var year = date.getFullYear();
            var month = (1 + date.getMonth()).toString();
            month = month.length > 1 ? month : '0' + month;
            var day = date.getDate().toString();
            day = day.length > 1 ? day : '0' + day;
            return year + '-' + month + '-' + day;
        }

        //a function to parse date obj to another certain format 
        //e.g. May 6, 2016
        function getDisplayFormattedDate(date) {
            var year = date.getFullYear();
            var month = date.getMonth();
            var date = date.getDate();
            //    var day = date.getDay();
            return monthArray[month] + ' ' + date + ', ' + year;
        }

        //-------------
        //- END OF FUNCTIONS -
        //-------------