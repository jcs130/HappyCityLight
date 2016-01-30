var map;
var proj;
var TILE_SIZE = 256;
var l_earth = 40075000;
var r_earth = l_earth / (2 * Math.PI);
var R = 10000;
var minZoomLevel = 9;
//var hexSize = radius * TILE_SIZE / l_earth;


var msgDataQueue = new buckets.Queue();
var msgDataQueueSize = 500;
var hexArray = new Array();
var skip_num_ids = new Array();

var placeCenter = null;
var city = null;
var N, E, W, S;
var hashtags, data_filter_lang_array = new Array(),
    data_filter_source_array = new Array(),
    flag_lang, flag_source, data_filter_lang, data_filter_source;
var allMarkers = [],
    allInfowindows = [],
    allPolygons = [];
var j = 0;
var realTime;
var displayData;

var checkMsgById = function (msg1, msg2) {
    return msg1.num_id === msg2.num_id;
}


function getDataFromServer(realtimeInfowindow) {
    $.ajax({
        type: "GET",
        crossDomain: true,
        url: serverURL + "messageonmap/getlatest",
        data: {
            token: "ArashiArashiFordream",
            location_lat_min: S,
            location_lan_min: W,
            location_lat_max: N,
            location_lan_max: E,
            city: city,
            lang: data_filter_lang,
            message_from: data_filter_source,
            hashtags: hashtags,
            limit: 10,
            skip_num_ids: skip_num_ids.toString()
        },
        dataType: "json",
        success: function (data, textStatus) {
            if (data.code == 200) {
                var i = 0;
                displayData = setInterval(function () {
                    var msgData = data.obj[i++];

                    if (!msgDataQueue.contains(msgData, checkMsgById) && ((msgData.emotion_text == 'positive') || (msgData.emotion_text == 'negative') || (msgData.emotion_text == 'neutral'))) {

                        //console.log(JSON.stringify(data));
                        //alert("1");
                        msgDataQueue.add(msgData);
                        skip_num_ids.push(msgData.num_id);

                        if (msgDataQueue.size == msgDataQueueSize) {
                            msgDataQueue.clear();
                            skip_num_ids = [];
                        }
                        var newCenterLatlng = new google.maps.LatLng(msgData.query_location_latitude, msgData.query_location_langtitude);

                        map.panTo(newCenterLatlng);
                        realtimeInfowindow.close();

                        /*realtimeInfowindow.setOptions({
                            content: msgData.text
                        });*/


                        //realtimeInfowindow.setContent("<div id='infobox'>" + msgData.text + "</div>");
                        //placeName

                        var content = "<div id='infobox' class='box box-widget'><div class='box-header with-border'><div class='user-block'><img class='img-circle' src='resources/img/userDefault.png' alt='User Image'><span class='username'><a href='#'>" + msgData.user_name + "</a></span><span class='description'>Shared publicly - " + msgData.creat_at + "</span></div><div class='box-tools'><button type='button' class='btn btn-box-tool' data-toggle='tooltip' title='Mark as read'><i class='fa fa-circle-o'></i></button><button type='button' class='btn btn-box-tool' data-widget='collapse'><i class='fa fa-minus'></i></button><button type='button' class='btn btn-box-tool' data-widget='remove'><i class='fa fa-times'></i></button></div></div><div class='box-body'>";
                        if ((msgData.media_type[0] == 'photo') && (msgData.media_urls[0] != null)) {
                            content += "<img class='img-responsive pad' src='" + msgData.media_urls[0] + "' alt='Photo'>"
                        }

                        content += "<p>" + msgData.text + "</p></div></div>";

                        realtimeInfowindow.setContent(content);

                        switch (msgData.emotion_text) {
                        case "positive":
                            var centerMarker = new google.maps.Marker({
                                map: map,
                                animation: google.maps.Animation.DROP,
                                position: newCenterLatlng,
                                icon: "resources/img/positive.png"
                            });
                            break;
                        case "neutral":
                            var centerMarker = new google.maps.Marker({
                                map: map,
                                animation: google.maps.Animation.DROP,
                                position: newCenterLatlng,
                                icon: "resources/img/neutral.png"
                            });
                            break;
                        case "negative":
                            var centerMarker = new google.maps.Marker({
                                map: map,
                                animation: google.maps.Animation.DROP,
                                position: newCenterLatlng,
                                icon: "resources/img/negative.png"
                            });
                            break;
                        default:
                            break;
                        }

                        allMarkers.push(centerMarker);
                        realtimeInfowindow.open(map, centerMarker);
                        //alert(JSON.stringify(data));
                        updateHex(msgData);
                        console.log(j++);

                    }

                    if (i >= data.obj.length) {
                        clearInterval(displayData);
                    };

                }, 3000);


            } else {
                alert(JSON.stringify(data));
                if (data.code == 400) {
                    window.location.href = "login.html?message=Please Login Again.";
                    realtimeInfowindow.close();
                }
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // successful
            alert("error!");
            realtimeInfowindow.close();
        }
    });
}

function getSpecificData() {
    hexArray = [];

    hashtags = $("input#topics").val(),
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

    data_filter_lang = data_filter_lang_array.toString(),
        data_filter_source = data_filter_source_array.toString();

    if (flag_lang == 1) {
        data_filter_lang = null;
    }
    if (flag_source == 1) {
        data_filter_source = null;
    }

    //alert(range + "\n" + data_filter_lang + "\n" + data_filter_source + "\n" + hashtags);

    /*var realtimeInfowindow = new google.maps.InfoWindow({
        maxWidth: 200
    });*/

    /*    var realtimeInfowindow = new InfoBox({
            disableAutoPan: false,
            maxWidth: 150,
            pixelOffset: new google.maps.Size(-140, 0),
            zIndex: null,
            boxStyle: {
                background: "#FFF",
                opacity: 0.95,
                width: "280px"
            },
            closeBoxURL: "http://www.google.com/intl/en_us/mapfiles/close.gif"
        });*/

    var realtimeInfowindow = new InfoBox({
        disableAutoPan: false,
        maxWidth: 150,
        pixelOffset: new google.maps.Size(-140, 0),
        zIndex: null
    });

    getDataFromServer(realtimeInfowindow);

    realTime = setInterval(getDataFromServer(realtimeInfowindow), 30000);

    $("#stopRealtime").click(function () {
        $("#sendRequest").prop('disabled', false);
        clearInterval(displayData);
        clearInterval(realTime);
        realtimeInfowindow.close();
        j = 0;
    });

}


//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}


$(function () {

    //初始化map所在div大小
    if ($(window).width() < 768) {
        $("footer").hide();
        $("#header-logo").hide();
        $("#titleForPhone").show();
        $('#titleForPhone').css('margin-left', $(window).width() / 2 - 82);
        $('#map-div').css('height', ($(window).height() - $(".main-header").height()));
    } else {
        $("footer").show();
        $("#header-logo").show();
        $("#titleForPhone").hide();
        $('#map-div').css('height', ($(window).height() - $(".main-header").height() - 51));
    }

    //初始化rangeSlider
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
    //计算rangeSlider大小，防止溢出div之外
    $("#rangeSlider").width(function () {
        return $("#pac-input").width() / 2.2;
    });
    //初始状态下selectpicker中的语言下拉菜单处在全选状态下
    $('.selectpicker #language').selectpicker('selectAll');
    //初始化tagsinput
    $('#topics').tagsinput({
        maxTags: 3,
        maxChars: 8
    });



    //初始化map，设置map属性
    map = new google.maps.Map(document.getElementById('map'), {
        center: new google.maps.LatLng(45.42929873257377, -75.38818359375),
        zoom: 9,
        mapTypeControl: false
    });
    proj = map.getProjection();

    //初始化自动补全功能
    var input = /** @type {!HTMLInputElement} */ (
        document.getElementById('pac-input'));
    var autocomplete = new google.maps.places.Autocomplete(input);
    autocomplete.bindTo('bounds', map);
    var infowindow = new google.maps.InfoWindow();
    var marker = new google.maps.Marker({
        map: map,
        anchorPoint: new google.maps.Point(0, -29),
        draggable: true
    });
    var rectangle = new google.maps.Rectangle();
    var circle = new google.maps.Circle();


    range = $("#rangeSlider").data('slider').getValue();
    autocomplete.addListener('place_changed', function () {
        infowindow.close();
        marker.setVisible(false);
        $.each(allPolygons, function () {
            this.setMap(null);
        });
        allPolygons = [];
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
            map.setZoom(9); // Why 9? Because it looks good.
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

        N = place.geometry.location.lat() + range * 180 / (Math.PI * r_earth);
        S = place.geometry.location.lat() - range * 180 / (Math.PI * r_earth);
        E = place.geometry.location.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * place.geometry.location.lat() / 180));
        W = place.geometry.location.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * place.geometry.location.lat() / 180));

        rectangle.setOptions({
            strokeWeight: 0,
            fillColor: '#FF0000',
            fillOpacity: 0.2,
            map: map,
            bounds: {
                north: N,
                south: S,
                east: E,
                west: W
            }
        });


        circle.setOptions({
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 0.5,
            fillOpacity: 0,
            map: map,
            center: place.geometry.location,
            radius: range
        });


        allPolygons.push(rectangle);
        allPolygons.push(circle);
        allMarkers.push(marker);
        allInfowindows.push(infowindow);

        marker.addListener('dragend', function () {

            var markerPlace = marker.getPosition();
            //alert(markerPlace);
            N = markerPlace.lat() + range * 180 / (Math.PI * r_earth);
            S = markerPlace.lat() - range * 180 / (Math.PI * r_earth);
            E = markerPlace.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));
            W = markerPlace.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));

            rectangle.setOptions({
                bounds: {
                    north: N,
                    south: S,
                    east: E,
                    west: W
                }
            });
            circle.setCenter(markerPlace);
            rectangle.setMap(map);
            circle.setMap(map);
            infowindow.close();
            city = null;

        });



        $("#rangeSlider").on('slide', function (e) {
            //alert("!!!");
            range = $("#rangeSlider").data('slider').getValue();
            var markerPlace = marker.getPosition();
            N = markerPlace.lat() + range * 180 / (Math.PI * r_earth),
                S = markerPlace.lat() - range * 180 / (Math.PI * r_earth),
                E = markerPlace.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180)),
                W = markerPlace.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));

            rectangle.setOptions({
                bounds: {
                    north: N,
                    south: S,
                    east: E,
                    west: W
                }
            });
            circle.setRadius(range);
            rectangle.setMap(map);
            circle.setMap(map);
        });


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
        city = place.name;

    });


    //当地图缩放级别变化时重绘hex grid
    google.maps.event.addListener(map, 'zoom_changed', function () {
        if (!msgDataQueue.isEmpty()) {
            $.each(hexArray, function () {
                this.hexPolygon.setMap(null);
            });
            hexArray = [];
            msgDataQueue.forEach(function (msgData) {
                updateHex(msgData);
            });

        }

    });

    $("#sendRequest").click(function () {
        $.each(hexArray, function () {
            this.hexPolygon.setMap(null);
        });
        $.each(allInfowindows, function () {
            this.setMap(null);
        });
        $.each(allMarkers, function () {
            this.setMap(null);
        });
        $.each(allPolygons, function () {
            this.setMap(null);
        });
        hexArray = [];
        allInfowindows = [];
        allMarkers = [];
        allPolygons = [];
        msgDataQueue.clear();
        msgDataQueue = new buckets.Queue();
        $("#sendRequest").prop('disabled', true);
        getSpecificData();

    });


    $("#advanceFilter").click(function () {
        $("#advance").slideToggle("slow");
    });




    //窗口大小改变时重新设置map所在div大小
    $(window).resize(function () {
        setTimeout(function () {
            //map所在div大小
            if ($(window).width() < 768) {
                $("footer").hide();
                $("#header-logo").hide();
                $("#titleForPhone").show();
                $('#titleForPhone').css('margin-left', $(window).width() / 2 - 82);
                $('#map-div').css('height', ($(window).height() - $(".main-header").height()));
            } else {
                $("footer").show();
                $("#header-logo").show();
                $("#titleForPhone").hide();
                $('#map-div').css('height', ($(window).height() - $(".main-header").height() - 51));
            }

            $(".slider").width(function () {
                return $("#pac-input").width() / 2.2;
            });
        }, 30);


    });



});