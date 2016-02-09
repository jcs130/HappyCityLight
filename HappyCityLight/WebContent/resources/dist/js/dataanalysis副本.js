var map;
var TILE_SIZE = 256;
var l_earth = 40075000;
var r_earth = l_earth / (2 * Math.PI);
var R = 10000;
var hexArray = new Array();
var hexPolyArray = new Array();
var selectedShape;
var minZoomLevel = 7;
var maxValue = 0;
var minValue = 0;
//var hexSize = radius * TILE_SIZE / l_earth;
var placeCenter = null;
var regions = null;
var regionsN = [
    {
        "id": 001,
        "lat": 45.42929873257377,
        "long": -75.38818359375,
        "message": "人は誰もが挑戦者 迷い戸惑う闘(たたか)って",
        "create_date": "2015-10-01",
        "emotion": "positive",
        "value": 16
                }, {
        "id": 002,
        "lat": 45.39844997630408,
        "long": -74.86083984375,
        "message": "二つで一つが一つの虚しさ あるはずを失った暮らしが",
        "create_date": "2015-10-01",
        "emotion": "negative",
        "value": 90
                }, {
        "id": 003,
        "lat": 45.440862671781765,
        "long": -75.2508544921875,
        "message": "燃える夕焼け ふわり初雪",
        "create_date": "2015-10-01",
        "emotion": "neutral",
        "value": 85
                }, {
        "id": 004,
        "lat": 45.54483149242463,
        "long": -75.0531005859375,
        "message": "孤独や涙を 痛みや悲しみを",
        "create_date": "2015-10-01",
        "emotion": "neutral",
        "value": 47
                }, {
        "id": 005,
        "lat": 45.25555527789205,
        "long": -75.772705078125,
        "message": "他の空と繋ぐ “Hey！Hello！”",
        "create_date": "2015-10-01",
        "emotion": "positive",
        "value": 72
                }, {
        "id": 006,
        "lat": 45.30773430004869,
        "long": -75.6298828125,
        "message": "優しさを誇りに 気高くしなやかに",
        "create_date": "2015-10-01",
        "emotion": "positive",
        "value": 66
                }, {
        "id": 007,
        "lat": 45.37144349133922,
        "long": -75.552978515625,
        "message": "春待ち桜 月夜の花火",
        "create_date": "2015-10-01",
        "emotion": "neutral",
        "value": 23
                }, {
        "id": 008,
        "lat": 45.30000710263142,
        "long": -75.970458984375,
        "message": "君のいない部屋は　空き箱みたいに軽く",
        "create_date": "2015-10-01",
        "emotion": "negative",
        "value": 43
                }, {
        "id": 009,
        "lat": 45.37530235052552,
        "long": -75.61065673828125,
        "message": "どんなときも　離れてても 心に同じ空がある",
        "create_date": "2015-10-01",
        "emotion": "positive",
        "value": 82
                }
            ];


function getSpecificData() {

    var start = $('#sandbox-container .input-daterange').datepicker().data('datepicker').pickers[0].getDate(),
        end = $('#sandbox-container .input-daterange').datepicker().data('datepicker').pickers[1].getDate(),
        range = $("#rangeSlider").data('slider').getValue(),
        hashtags = $("input#topics").val(),
        data_filter_lang_array = new Array(),
        data_filter_source_array = new Array(),
        flag_lang = 1,
        flag_source = 1;

    $("#language option").each(function () {
        if (this.selected) {
            data_filter_lang_array.push(this.value);
        } else {
            flag_lang = 0;
        }
    });
    $("#dataSource option").each(function () {
        if (this.selected) {
            data_filter_source_array.push(this.value);
        } else {
            flag_source = 0;
        }
    });

    var data_filter_lang = data_filter_lang_array.toString(),
        data_filter_source = data_filter_source_array.toString();

    if (flag_lang == 1) {
        data_filter_lang = null;
    }
    if (flag_source == 1) {
        data_filter_source = null;
    }

    alert(start + "\n" + end + "\n" + range + "\n" + data_filter_lang + "\n" + data_filter_source + "\n" + hashtags);

    $.ajax({
        type: "GET",
        crossDomain: true,
        url: serverURL + "dataanalysis/get",
        data: {
            token:"Arashi!Arashi!For dream!",
            location_lat_min:null,
            location_lan_min:null,
            location_lat_max:null,
            location_lan_max:null,
            city:null,
            timestamp_start: start,
            timestamp_end: end,
            lang: data_filter_lang,
            message_from: data_filter_source,
            hashtags: hashtags
        },
        dataType: "json",
        success: function (data, textStatus) {
            if (data.code == 200) {
                alert("success!");
                $("#sendRequest").prop('disabled', true);
                return regionsN;
            } else {
                alert(JSON.stringify(data));
                if (data.code == 400) {
                    window.location.href = "login.html?message=Please Login Again.";
                }
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // successful
            $("#sendRequest").prop('disabled', false);
        }
    });


    return regionsN;

}



//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}


$(function () {

    //初始化map所在div大小
    var h = $(".wrapper").height(),
        offsetTop = $(".main-header").height(); // Calculate the top offset

    $('#map-div').css('height', (h - offsetTop));

    var rangeSlider = $('#rangeSlider').slider({
        min: 1000,
        max: 20000,
        step: 1000,
        value: 10000,
        handle: 'square',
        formatter: function (value) {
            return 'Current range: ' + value;
        }
    });



    $('.selectpicker #language').selectpicker('selectAll');

    $('#sandbox-container .input-daterange').datepicker({
        startDate: "01/01/2010",
        endDate: "today",
        todayBtn: true,
        clearBtn: true,
        todayHighlight: true,
        datesDisabled: ['01/06/2015', '01/21/2015']
    });

    $('#sandbox-container .input-daterange').datepicker().data('datepicker').pickers[0].setDate('today');

    $('#topics').tagsinput({
        maxTags: 3,
        maxChars: 8
    });

    $("#rangeSlider").width(function () {
        return $("#pac-input").width() / 2.2;
    });



    //设置map初始属性
    var ottawa = new google.maps.LatLng(45.42929873257377, -75.38818359375);

    map = new google.maps.Map(document.getElementById('map'), {
        center: ottawa,
        zoom: 9,
        mapTypeControl: false
    });


    var input = /** @type {!HTMLInputElement} */ (
        document.getElementById('pac-input'));


    var autocomplete = new google.maps.places.Autocomplete(input);
    autocomplete.bindTo('bounds', map);
    var infowindow = new google.maps.InfoWindow();
    var marker = new google.maps.Marker({
        map: map,
        anchorPoint: new google.maps.Point(0, -29)
    });



    autocomplete.addListener('place_changed', function () {
        infowindow.close();
        marker.setVisible(false);
        var place = autocomplete.getPlace();
        if (!place.geometry) {
            window.alert("Autocomplete's returned place contains no geometry");
            return;
        }
        // If the place has a geometry, then present it on a map.
        if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
        } else {
            map.setCenter(place.geometry.location);
            map.setZoom(17); // Why 17? Because it looks good.
        }
        marker.setIcon( /** @type {google.maps.Icon} */ ({
            url: place.icon,
            size: new google.maps.Size(71, 71),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(17, 34),
            scaledSize: new google.maps.Size(35, 35)
        }));
        marker.setPosition(place.geometry.location);
        marker.setVisible(true);

        var address = '';
        if (place.address_components) {
            address = [
                (place.address_components[0] && place.address_components[0].short_name || ''),
                (place.address_components[1] && place.address_components[1].short_name || ''),
                (place.address_components[2] && place.address_components[2].short_name || '')
      ].join(' ');
        }

        infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + address);
        infowindow.open(map, marker);
        placeCenter = place.geometry.location;
    });




    //restrict zoom level to be at city level
    google.maps.event.addListener(map, 'zoom_changed', function () {
            if (regions != null) {
                if (map.getZoom() < minZoomLevel) {
                    // get analysis data if zoom level < 9
                    map.setZoom(minZoomLevel);
                } else {
                    // get original data if zoom level >=9
                    hexArray = [];
                    drawAllHex(map.getZoom(), regions);
                }
            }

        }

    );


    $("#sendRequest").click(function () {

        if (placeCenter == null) {
            //alert("Please select a place!");
            return;
        } else {
            regions = getSpecificData();

            $.each(hexPolyArray, function () {
                this.setMap(null);
            });

            hexPolyArray = [];
            hexArray = [];
            drawAllHex(map.getZoom(), regions);

        }



    });



    //窗口大小改变时重新设置map所在div大小
    $(window).resize(function () {
        var h = $(".wrapper").height(),
            offsetTop = $(".main-header").height(); // Calculate the top offset

        $('#map-div').css('height', (h - offsetTop));

        $(".slider").width(function () {
            return $("#pac-input").width() / 2.2;
        });


    }).resize();


    $("#advanceFilter").click(function () {
        $("div #advance").slideToggle("slow");
    });


});