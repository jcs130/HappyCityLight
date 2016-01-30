var apiToken = "ArashiArashiFordream";
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

var keyword, data_filter_lang_array = new Array(),
    data_filter_source_array = new Array(),
    data_filter_lang, data_filter_source,
    flag_lang, flag_source;

var realTime;
var allInfoboxs = new Array();
var allMarkers = new Array();
var infoboxNow = null;
var listenPlaceList = new Array();
var cityBoundaryArray = new Array();
var cityBoundaryPaths = new Array();

var infowindow, cityMarker, cityBoundary;


var checkMsgById = function (msg1, msg2) {
    return msg1.num_id === msg2.num_id;
}



function getListenPlaceList() {
    if (user_id != null && logintoken != null) {
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "collector/getlistenplacelist",
            data: {
                userID: user_id,
                token: logintoken
            },
            dataType: "json",
            success: function (data, textStatus) {
                if (data.code == 200) {
                    console.log("success in getListenPlaceList(): " + JSON.stringify(data));

                    $.each(data.obj, function () {
                        //alert(this);
                        //listenPlaceList.push(JSON.stringify(this));
                        listenPlaceList.push(this.toString());
                        console.log(this.toString());
                    });
                    //alert(listenPlaceList.toString());
                    map.setZoom(9);

                } else {
                    if (data.code == 400) {
                        window.location.href = "login.html?message=Please Login Again.";
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("error in getListenPlaceList(): " + JSON.stringify(data));
            }
        });
    } else {
        alert("Auth error");
    }

}


function getPlaceEdge(place) {
    if (user_id != null && logintoken != null) {
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "collector/getplaceinfo",
            data: {
                userID: user_id,
                token: logintoken,
                place_name: place,
            },
            dataType: "json",
            success: function (data, textStatus) {
                if (data.code == 200) {
                    console.log("success in getPlaceEdge(): " + JSON.stringify(data));
                    map.setCenter(new google.maps.LatLng(data.obj.areas[0].north, data.obj.areas[0].east));
                    //close current elements
                    if (cityBoundaryArray != null) {
                        $.each(cityBoundaryArray, function () {
                            this.setMap(null);
                        });
                        cityBoundaryArray = [];
                    }
                    if (cityBoundary != null) {
                        infowindow.close();
                        cityMarker.setVisible(false);
                        cityBoundary.setMap(null);
                    }

                    cityBoundaryPaths = [];

                    $.each(data.obj.areas, function () {
                        /*var cityBoundaryListened = new google.maps.Rectangle({
                            strokeWeight: 0,
                            fillColor: '#FF0000',
                            fillOpacity: 0.2,
                            bounds: {
                                north: this.north,
                                south: this.south,
                                east: this.east,
                                west: this.west
                            }
                        });*/
                        var cityPaths = [new google.maps.LatLng(this.north, this.east), new google.maps.LatLng(this.south, this.east), new google.maps.LatLng(this.south, this.west), new google.maps.LatLng(this.north, this.west)];
                        //cityBoundaryArray.push(cityBoundaryListened);
                        cityBoundaryPaths.push(cityPaths);
                    });

                    cityBoundaryArray.push(mergeRegions(cityBoundaryPaths));

                } else {
                    alert(JSON.stringify(data));
                    window.location.href = "login.html?message=Please Login Again.";
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("error in getPlaceEdge(): " + JSON.stringify(data));
            }
        });
    } else {
        alert("Auth error");
    }
}



function getLatestData() {
    console.log("getLatestData();");
    $.ajax({
        type: "GET",
        crossDomain: true,
        url: serverURL + "messageonmap/getlatest",
        data: {
            token: apiToken,
            location_lat_min: S,
            location_lan_min: W,
            location_lat_max: N,
            location_lan_max: E,
            city: city,
            lang: data_filter_lang,
            message_from: data_filter_source,
            keyword: keyword,
            limit: 10,
            skip_num_ids: skip_num_ids.toString()
        },
        dataType: "json",
        success: function (data, textStatus) {
            if (data.code == 200) {
                console.log("success in getLatestData();");
                console.log(JSON.stringify(data));

                var i = 0;
                if (infoboxNow != null) {
                    infoboxNow.setVisible(false);
                }

                var msgData = data.obj[i++];
                if (!msgDataQueue.contains(msgData, checkMsgById) && ((msgData.emotion_text == 'positive') || (msgData.emotion_text == 'negative') || (msgData.emotion_text == 'neutral'))) {
                    //manage msgDataQueue that stores raw datas recieved from server
                    msgDataQueue.add(msgData);
                    skip_num_ids.push(msgData.num_id);
                    console.log("skip_num_ids adding: " + msgData.num_id);
                    if (msgDataQueue.size == msgDataQueueSize) {
                        msgDataQueue.clear();
                        skip_num_ids = [];
                    }
                    //set map center / marker / infobox
                    var newCenterLatlng = new google.maps.LatLng(msgData.query_location_latitude, msgData.query_location_langtitude);
                    var createAt = new Date(msgData.creat_at * 1000);
                    var placeName;
                    if (msgData.place_fullname != null) {
                        placeName = msgData.place_fullname;
                    } else if (msgData.place_name != null) {
                        placeName = msgData.place_name;
                    } else {
                        placeName = "";
                    }

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

                    var content = "<div id='infobox' class='box box-widget'><div class='box-header with-border'><div class='user-block'><img class='img-circle' src='resources/img/userDefault.png' alt='User Image'><span class='username'><a href='#'>" + msgData.user_name + "</a></span><span class='description'>" + placeName + " - " + createAt.toDateString() + "</span></div><div class='box-tools'><button type='button' class='btn btn-box-tool' data-toggle='tooltip' title='Mark as read'><i class='fa fa-circle-o'></i></button><button type='button' class='btn btn-box-tool' data-widget='collapse'><i class='fa fa-minus'></i></button><button type='button' class='btn btn-box-tool btn-close' data-widget='remove'><i class='fa fa-times'></i></button></div></div><div class='box-body'>";
                    if ((msgData.media_type[0] == 'photo') && (msgData.media_urls[0] != null)) {
                        content += "<img class='img-responsive' src='" + msgData.media_urls[0] + "' alt='Photo' style='width:120px;'>"
                    }

                    content += "<p>" + msgData.text + "</p></div></div>";


                    var realtimeInfowindow = new InfoBox({
                        content: content,
                        disableAutoPan: false,
                        maxWidth: 120,
                        pixelOffset: new google.maps.Size(-140, 0),
                        zIndex: null
                    });


                    map.panTo(newCenterLatlng);
                    realtimeInfowindow.open(map, centerMarker);
                    updateHex(msgData);
                    infoboxNow = realtimeInfowindow;
                    allInfoboxs.push(realtimeInfowindow);
                    allMarkers.push(centerMarker);

                    centerMarker.addListener('click', function () {
                        if (realtimeInfowindow.getVisible()) {
                            realtimeInfowindow.setVisible(false);
                        } else {
                            realtimeInfowindow.setVisible(true);
                        }
                    });

                }
                if (i >= data.obj.length) {
                    clearInterval(displayData);
                };

            } else {
                if (data.code == 400) {
                    window.location.href = "login.html?message=Please Login Again.";
                }
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("error in getLatestData(): " + JSON.stringify(data));
        }
    });
}




function getSpecificData() {
    console.log("getSpecificData();");
    //get data filter options
    keyword = $("input#topics").val();
    flag_lang = 1;
    flag_source = 1;
    $("#language option").each(function () {
        if (this.selected) {
            data_filter_lang_array.push(this.val());
            console.log(this.val());
        } else {
            flag_lang = 0;
        }
    });
    $("#dataSource option").each(function () {
        if (this.selected) {
            data_filter_source_array.push(this.val());
            console.log(this.val());
        } else {
            flag_source = 0;
        }
    });
    data_filter_lang = data_filter_lang_array.toString();
    data_filter_source = data_filter_source_array.toString();
    if (flag_lang == 1) {
        data_filter_lang = null;
    }
    if (flag_source == 1) {
        data_filter_source = null;
    }

    console.log("Search Options: " + city + " | " + keyword + " | " + data_filter_lang + " | " + data_filter_source);

    realTime = setInterval(function () {
        getLatestData();
    }, 3000);


}





//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}


$(function () {

    //initialization of some elements 
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

    //init rangeSlider
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
    //calculate slider size
    $("#rangeSlider").width(function () {
        return $("#pac-input").width() / 2.2;
    });
    //select all language initially
    $('.selectpicker #language').selectpicker('selectAll');
    //init tagsinput
    $('#topics').tagsinput({
        maxTags: 3,
        maxChars: 8
    });

    getListenPlaceList();

    //init map
    map = new google.maps.Map(document.getElementById('map'), {
        center: new google.maps.LatLng(45.42929873257377, -75.38818359375),
        zoom: 9,
        mapTypeControl: false
    });
    proj = map.getProjection();

    //init autocomplete
    var input = (document.getElementById('pac-input'));
    var autocomplete = new google.maps.places.Autocomplete(input);
    autocomplete.bindTo('bounds', map);
    infowindow = new google.maps.InfoWindow();
    cityMarker = new google.maps.Marker({
        map: map,
        anchorPoint: new google.maps.Point(0, -29),
        draggable: true
    });
    cityBoundary = new google.maps.Rectangle();


    range = $("#rangeSlider").data('slider').getValue();
    autocomplete.addListener('place_changed', function () {
        //close current elements
        if (cityBoundaryArray != null) {
            $.each(cityBoundaryArray, function () {
                this.setMap(null);
            });
            cityBoundaryArray = [];
        }
        if (cityBoundary != null) {
            infowindow.close();
            cityMarker.setVisible(false);
            cityBoundary.setMap(null);
        }
        // get new place info
        var place = autocomplete.getPlace();
        alert(place.name);
        if ($.inArray(place.name.toLowerCase(), listenPlaceList) > -1) {
            //向后台请求自定义的区域边界信息并显示
            getPlaceEdge(place.name);
            return;
        } else {
            if (!place.geometry) {
                window.alert("Autocomplete's returned place contains no geometry");
                return;
            }
            // If the place has a geometry, present it on a map.
            if (place.geometry.viewport) {
                map.fitBounds(place.geometry.viewport);
            } else {
                map.setCenter(place.geometry.location);
                map.setZoom(9);
            }
            //set cityMarker
            cityMarker.setIcon( /** @type {google.maps.Icon} */ ({
                url: place.icon,
                size: new google.maps.Size(71, 71),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(17, 34),
                scaledSize: new google.maps.Size(35, 35)
            }));
            cityMarker.setPosition(place.geometry.location);
            cityMarker.setVisible(true);
            //show place name through infowindow
            var address = '';
            if (place.address_components) {
                address = [(place.address_components[0] && place.address_components[0].short_name || ''),
                    (place.address_components[1] && place.address_components[1].short_name || ''),
                    (place.address_components[2] && place.address_components[2].short_name || '')].join(' ');
            }
            infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + address);
            infowindow.open(map, cityMarker);
            placeCenter = place.geometry.location;
            city = place.name;
            //show city boundary in form of rectangle
            N = place.geometry.location.lat() + range * 180 / (Math.PI * r_earth);
            S = place.geometry.location.lat() - range * 180 / (Math.PI * r_earth);
            E = place.geometry.location.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * place.geometry.location.lat() / 180));
            W = place.geometry.location.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * place.geometry.location.lat() / 180));
            cityBoundary.setOptions({
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

        }

    });


    //When finish dragging the cityCarker, update city boundary and delete autocomplete-cityName
    cityMarker.addListener('dragend', function () {
        var markerPlace = cityMarker.getPosition();
        N = markerPlace.lat() + range * 180 / (Math.PI * r_earth);
        S = markerPlace.lat() - range * 180 / (Math.PI * r_earth);
        E = markerPlace.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));
        W = markerPlace.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));

        cityBoundary.setOptions({
            bounds: {
                north: N,
                south: S,
                east: E,
                west: W
            }
        });
        cityBoundary.setMap(map);
        infowindow.close();
        city = null;
    });


    //When dragging the rangeSlider, update city boundary
    $("#rangeSlider").on('slide', function (e) {
        range = $("#rangeSlider").data('slider').getValue();
        var markerPlace = cityMarker.getPosition();
        N = markerPlace.lat() + range * 180 / (Math.PI * r_earth),
            S = markerPlace.lat() - range * 180 / (Math.PI * r_earth),
            E = markerPlace.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180)),
            W = markerPlace.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));

        cityBoundary.setOptions({
            bounds: {
                north: N,
                south: S,
                east: E,
                west: W
            }
        });
        cityBoundary.setMap(map);
    });




    //redraw hex grid when map zoom changes
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



    //operations when click on GO! button
    $("#sendRequest").click(function () {
        $.each(hexArray, function () {
            this.hexPolygon.setMap(null);
        });
        hexArray = [];
        cityMarker.setMap(null);
        cityBoundary.setMap(null);
        infowindow.close();
        msgDataQueue.clear();
        msgDataQueue = new buckets.Queue();
        $("#sendRequest").prop('disabled', true);
        console.log("Real-time Visualization Start.");
        getSpecificData();
    });




    $("#stopRealtime").click(function () {
        console.log("Real-time Visualization Stop.");
        clearInterval(realTime);
        $("#sendRequest").prop('disabled', false);
        if (infoboxNow != null) {
            infoboxNow.setVisible(false);
        }
    });


    $(".btn-close").click(function () {

    });

    //slide to show/hide the advance search options
    $("#advanceFilter").click(function () {
        $("#advance").slideToggle("slow");
    });


    //resize & show/hide elements when window size changes
    $(window).resize(function () {
        setTimeout(function () {
            //footer & logo styles, map size
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
            //slider size
            $(".slider").width(function () {
                return $("#pac-input").width() / 2.2;
            });
        }, 30);


    });



});