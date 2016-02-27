var apiToken = "ArashiArashiFordream";
//constraints used for computation
var TILE_SIZE = 256;
//var TILE_SIZE = 128;
var l_earth = 40075000;
var r_earth = l_earth / (2 * Math.PI);
var R = 10000;
//variables for setting googlemap
var map;
var proj;
var minZoomLevel = 9;
//msg queue for saving msgs (repetation filtered) recieved from backend
var msgDataQueue = new buckets.Queue();
var msgDataQueueSize = 500;
//array for saving ids only of msgs recieved from backend, helps compute more quickly
var nonRepeatIds = new Array();
//variable for saving city name
var city = null;
var N, E, W, S;
//variables for getting data filter options
var keyword, data_filter_lang_array = new Array(),
    data_filter_source_array = new Array(),
    data_filter_lang, data_filter_source,
    flag_lang, flag_source;

var location_areas;
var realTime;
var listenPlaceList = new Array();

var hexArray = new Array();
var hexBoundaryArray = new Array();
var regionBoundary = null;
var allInfoboxs = new Array();
var allMarkers = new Array();
var infoboxNow = null;
var markerNow = null;
var boundaryRecArray = new Array();




var infowindow, cityMarker;

//Set the condition to decide if two messages are the same (used for buckets.js)
var checkMsgById = function (msg1, msg2) {
    return msg1.num_id === msg2.num_id;
}


//
function drawRegionBoundary(areas) {
    var cityBoundaryPaths = new Array();
    if (regionBoundary != null) {
        regionBoundary.setMap(null);
    }

    $.each(areas, function () {
        var cityPaths = [new google.maps.LatLng(this.north, this.east), new google.maps.LatLng(this.south, this.east), new google.maps.LatLng(this.south, this.west), new google.maps.LatLng(this.north, this.west)];
        cityBoundaryPaths.push(cityPaths);
    });

    regionBoundary = mergeRegions(cityBoundaryPaths);
    regionBoundary.setMap(map);

}


function setLocationAreas(N, E, S, W) {
    var area = {};
    area["north"] = N;
    area["south"] = S;
    area["west"] = W;
    area["east"] = E;
    area["locID"] = 0;
    var jsonArea = [];
    jsonArea.push(area);
    location_areas = JSON.stringify(jsonArea);
    console.log("Set location_areas: " + location_areas);
    return jsonArea;
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
                        listenPlaceList.push(this.toString());
                        //                        console.log(this.toString());
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
                    infowindow.close();
                    map.panTo(new google.maps.LatLng(data.obj.areas[0].north, data.obj.areas[0].east));
                    //getHexInsideRec(data.obj.areas[0]);

                    var hexInfoArray = new Array();
                    location_areas = JSON.stringify(data.obj.areas);
                    console.log("Set location_areas: " + location_areas);


                    $.each(data.obj.areas, function () {
                        boundaryRecArray.push(this);
                        $.each(getHexInsideRec(this), function () {
                            if ($.inArray("" + this.q + this.r + this.s, hexInfoArray) <= -1) {
                                hexInfoArray.push("" + this.q + this.r + this.s);
                                drawHexFromHex(this);
                                hexBoundaryArray.push(this);
                                //                                console.log("Adding Hex: " + this.q + "," + this.r + "," + this.s);
                            } else {
                                console.log("Hex already exists.");
                            }
                        });
                    });


                    if ((data.obj.box_points != null) && (data.obj.box_points != [])) {
                        console.log("box_points exists.");

                        if (regionBoundary != null) {
                            regionBoundary.setMap(null);
                        }

                        regionBoundary = new google.maps.Polyline({
                            path: $.parseJSON(data.obj.box_points),
                            map: map,
                            strokeColor: "#9F353A",
                            strokeOpacity: 0.6,
                            strokeWeight: 1
                        });


                    } else {

                        drawRegionBoundary(data.obj.areas);

                        //alert(resultRegion.getPath().getArray().toString());

                        $.ajax({
                            type: "PUT",
                            crossDomain: true,
                            url: serverURL + "collector/updateareabox",
                            data: {
                                userID: user_id,
                                token: logintoken,
                                place_name: place,
                                box_points: JSON.stringify(regionBoundary.getPath().getArray())
                            },
                            dataType: "json",
                            success: function (data, textStatus) {
                                //alert(JSON.stringify(data));
                                console.log("Update Area Box successfully......" + data.obj.box_points);

                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                console.log("error: " + JSON.stringify(data));
                            }
                        });


                    }


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
        url: serverURL + "message/getlatest",
        data: {
            token: apiToken,
            location_areas: location_areas,
            city: city,
            lang: data_filter_lang,
            message_from: data_filter_source,
            keyword: keyword,
            skip_num_ids: nonRepeatIds.toString()
        },
        dataType: "json",
        success: function (data, textStatus) {
            console.log(JSON.stringify(data));
            if (data.code == 200) {
                console.log("success in getLatestData();");
                if ((data.obj != null) && (data.obj.length != 0)) {

                    if (infoboxNow != null) {
                        infoboxNow.setVisible(false);
                    }
                    if (markerNow != null) {
                        markerNow.setVisible(false);
                        console.log("remove a marker.")
                    }

                    var msgData = data.obj[0];
                    if (!msgDataQueue.contains(msgData, checkMsgById) && ((msgData.emotion_text == 'positive') || (msgData.emotion_text == 'negative') || (msgData.emotion_text == 'neutral'))) {
                        //manage msgDataQueue that stores raw datas recieved from server
                        msgDataQueue.add(msgData);
                        nonRepeatIds.push(msgData.num_id);
                        console.log("nonRepeatIds adding: " + msgData.num_id);
                        if (msgDataQueue.size == msgDataQueueSize) {
                            msgDataQueue.clear();
                            nonRepeatIds = [];
                            if (hexArray != null) {
                                $.each(hexArray, function () {
                                    this.hexPolygon.setMap(null);
                                });
                                hexArray = [];
                            }
                        }
                        //set map center / marker / infobox
                        var newCenterLatlng = new google.maps.LatLng(msgData.query_location_latitude, msgData.query_location_langtitude);
                        var createAt = new Date(msgData.creat_at);
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

                        var content = "<div id='infobox' class='box box-widget'><div class='box-header with-border'><div class='user-block'><img class='img-circle'";

                        if (msgData.profile_image_url != null) {
                            content += "src='" + msgData.profile_image_url + "'";

                        } else {
                            content += "src='resources/img/userDefault.png'";
                        }


                        content += "alt='User Image'><span class='username'><a href='#'>" + msgData.user_name + "</a></span><span class='description'>" + placeName + " - " + createAt.getFullYear() + "/" + createAt.getMonth() + "/" + createAt.getDate() + "</span></div><div class='box-tools'><button type='button' class='btn btn-box-tool' data-toggle='tooltip' title='Mark as read'><i class='fa fa-circle-o'></i></button><button type='button' class='btn btn-box-tool' data-widget='collapse'><i class='fa fa-minus'></i></button><button type='button' class='btn btn-box-tool btn-close' data-widget='remove'><i class='fa fa-times'></i></button></div></div><div class='box-body'>";
                        if ((msgData.media_type != null) && (msgData.media_type != []) && (msgData.media_type[0] == 'photo') && (msgData.media_urls[0] != null)) {
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
                        markerNow = centerMarker;
                        allInfoboxs.push(realtimeInfowindow);
                        allMarkers.push(centerMarker);


                    }

                } else {
                    console.log("No data in this getLatestData()......");
                }

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
            data_filter_lang_array.push(this.value);
            //            console.log(this.val());
        } else {
            flag_lang = 0;
        }
    });
    $("#dataSource option").each(function () {
        if (this.selected) {
            data_filter_source_array.push(this.value);
            //            console.log(this.val());
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

    console.log("Search Options: " + city + " | " + range + " | " + keyword + " | " + data_filter_lang + " | " + data_filter_source);

    realTime = setInterval(function () {
        getLatestData();
    }, 3000);


}





//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}


function init_map_after_load() {
    //...........................
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

    $("#rangeSlider").slider("disable");




    range = $("#rangeSlider").data('slider').getValue();
    autocomplete.addListener('place_changed', function () {

        $("#rangeSlider").slider("disable");
        if (hexBoundaryArray != null) {
            $.each(hexBoundaryArray, function () {
                this.hexPolygon.setMap(null);
            });
            hexBoundaryArray = [];
        }
        boundaryRecArray = [];

        // get new place info
        var place = autocomplete.getPlace();
        //alert(place.name);
        if ($.inArray(place.name.toLowerCase(), listenPlaceList) > -1) {
            //向后台请求自定义的区域边界信息并显示
            city = place.name;
            getPlaceEdge(place.name);
            return;
        } else {
            if (!place.geometry) {
                window.alert("Autocomplete's returned place contains no geometry");
                return;
            }
            $("#rangeSlider").slider("enable");
            // If the place has a geometry, present it on a map.
            //            if (place.geometry.viewport) {
            //                map.fitBounds(place.geometry.viewport);
            //            } else {
            //                map.setCenter(place.geometry.location);
            //                map.setZoom(9);
            //            }
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

            city = place.name;
            //show city boundary in form of rectangle
            N = place.geometry.location.lat() + range * 180 / (Math.PI * r_earth);
            S = place.geometry.location.lat() - range * 180 / (Math.PI * r_earth);
            E = place.geometry.location.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * place.geometry.location.lat() / 180));
            W = place.geometry.location.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * place.geometry.location.lat() / 180));


            drawRegionBoundary(setLocationAreas(N, E, S, W));

        }

    });


    //When finish dragging the cityCarker, update city boundary and delete autocomplete-cityName
    cityMarker.addListener('dragend', function () {
        var markerPlace = cityMarker.getPosition();
        N = markerPlace.lat() + range * 180 / (Math.PI * r_earth);
        S = markerPlace.lat() - range * 180 / (Math.PI * r_earth);
        E = markerPlace.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));
        W = markerPlace.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));

        drawRegionBoundary(setLocationAreas(N, E, S, W));

        infowindow.close();
        city = null;
    });


    //When dragging the rangeSlider, update city boundary
    $("#rangeSlider").on('slide', function (e) {
        //        range = $("#rangeSlider").data('slider').getValue();
        range = e.value;
        var markerPlace = cityMarker.getPosition();
        N = markerPlace.lat() + range * 180 / (Math.PI * r_earth);
        S = markerPlace.lat() - range * 180 / (Math.PI * r_earth);
        E = markerPlace.lng() + range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));
        W = markerPlace.lng() - range * 180 / (Math.PI * r_earth * Math.cos(Math.PI * markerPlace.lat() / 180));


        drawRegionBoundary(setLocationAreas(N, E, S, W));

    });



    //redraw hex grid when map zoom changes
    google.maps.event.addListener(map, 'zoom_changed', function () {
        console.log("Googlemap zoom changed.");

        if (allInfoboxs != null) {
            $.each(allInfoboxs, function () {
                this.setVisible(false);
            });
            allInfoboxs = [];
        }

        if (hexArray != null) {
            $.each(hexArray, function () {
                this.hexPolygon.setMap(null);
            });
            hexArray = [];
        }
        if (!msgDataQueue.isEmpty()) {
            msgDataQueue.forEach(function (msgData) {
                updateHex(msgData);
            });
        }

        if (hexBoundaryArray != null) {
            $.each(hexBoundaryArray, function () {
                this.hexPolygon.setMap(null);
            });
            hexBoundaryArray = [];
            console.log("hexBoundaryArrayChange: " + hexBoundaryArray.toString());

            var hexInfoArray = new Array();
            $.each(boundaryRecArray, function () {
                $.each(getHexInsideRec(this), function () {
                    if ($.inArray("" + this.q + this.r + this.s, hexInfoArray) <= -1) {
                        hexInfoArray.push("" + this.q + this.r + this.s);
                        drawHexFromHex(this);
                        hexBoundaryArray.push(this);
                        //console.log("Adding Hex: " + this.q + "," + this.r + "," + this.s);
                    } else {
                        //console.log("Hex already exists.");
                    }
                });
            });

        }
    });



    //operations when click on GO! button
    $("#sendRequest").click(function () {
        if (hexBoundaryArray != null) {
            $.each(hexBoundaryArray, function () {
                this.hexPolygon.setMap(null);
            });
            hexBoundaryArray = [];
        }
        boundaryRecArray = [];
        cityMarker.setMap(null);
        infowindow.close();
        $("#sendRequest").prop('disabled', true);
        console.log("Real-time Visualization Start.");
        getSpecificData();
    });



    //operations when click on STOP! button
    $("#stopRealtime").click(function () {
        console.log("Real-time Visualization Stop.");
        clearInterval(realTime);
        $("#sendRequest").prop('disabled', false);
        if (infoboxNow != null) {
            infoboxNow.setVisible(false);
        }
        if (markerNow != null) {
            markerNow.setVisible(false);
        }
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

}



$(function () {

    //加载地图的js
    $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4&language=en&libraries=geometry,places")
        .done(function () {
            console.log("Map Loaded.");
            init_map_after_load();
        }).fail(function () {
            console.log("Change to the Chinese Server");
            $.getScript("http://ditu.google.cn/maps/api/js?sensor=false&language=en&libraries=geometry,places&key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4").done(function () {
                console.log("Map Loaded.");
                init_map_after_load();
            });
        }).always(function () {});


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


    $("#rangeSlider").slider({
        min: 1000,
        max: 20000,
        step: 1000,
        value: 10000,
        handle: 'square',
        tooltip: 'show',
        formatter: function (value) {
            return 'Current range: ' + value + ' meters';
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
        maxChars: 10
    });





});