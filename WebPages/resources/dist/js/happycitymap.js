var map;
var proj;
var TILE_SIZE = 256;
var l_earth = 40075000;
var r_earth = l_earth / (2 * Math.PI);
var R = 10000;
var minZoomLevel = 7;
//var hexSize = radius * TILE_SIZE / l_earth;


var msgDataQueue = new buckets.Queue();
var msgDataQueueSize = 500;
var hexArray = new Array();


var placeCenter = null;
var city = null;
var N, E, W, S;
var allMarkers = [],
    allInfowindows = [],
    allPolygons = [];


var checkMsgById = function (msg1, msg2) {
    return msg1.obj[0].num_id === msg2.obj[0].num_id;
}


function getSpecificData() {
    hexArray = [];

    var hashtags = $("input#topics").val(),
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

    //alert(range + "\n" + data_filter_lang + "\n" + data_filter_source + "\n" + hashtags);

    var realtimeInfowindow = new google.maps.InfoWindow({
        maxWidth: 200
    });

    var realTime = setInterval(function () {
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
                hashtags: hashtags
            },
            dataType: "json",
            success: function (data, textStatus) {
                if (data.code == 200) {
                    //alert(data.obj[0].text);

                    if (!msgDataQueue.contains(data, checkMsgById)) {
                        msgDataQueue.add(data);
                        if (msgDataQueue.size == msgDataQueueSize) {
                            msgDataQueue.clear();
                        }
                        var newCenterLatlng = new google.maps.LatLng(data.obj[0].query_location_latitude, data.obj[0].query_location_langtitude);

                        map.panTo(newCenterLatlng);
                        realtimeInfowindow.close();

                        realtimeInfowindow.setOptions({
                            content: data.obj[0].text
                        });




                        switch (data.obj[0].emotion_text) {
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
                        updateHex(data);

                    }


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
    }, 3000);



    $("#stopRealtime").click(function () {
        $("#sendRequest").prop('disabled', false);
        clearInterval(realTime);
        realtimeInfowindow.close();
    });


}


//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}


$(function () {

    //初始化map所在div大小
    var h = $(window).height(),
        offsetTop = $(".main-header").height() + 51; // Calculate the top offset
    //alert(h + "," + offsetTop);
    $('#map-div').css('height', (h - offsetTop));
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


    range = $("#rangeSlider").data('slider').getValue();
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



        var rectangle = new google.maps.Rectangle({
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


        var circle = new google.maps.Circle({
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
            if (map.getZoom() < minZoomLevel) {
                // get analysis data if zoom level < 9
                //map.setZoom(minZoomLevel);
                //drawHexFromHex(this, map.getZoom());
            } else {
                //alert("Zoom change!");
                // get original data if zoom level >=9
                $.each(hexArray, function () {
                    this.hexPolygon.setMap(null);
                });
                hexArray = [];
                msgDataQueue.forEach(function (msgData) {
                    updateHex(msgData);
                });

            }
        }

    });

    $("#sendRequest").click(function () {
        $.each(hexArray, function () {
            this.hexPolygon.setMap(null);
        });
        hexArray = [];
        msgDataQueue.clear();
        $("#sendRequest").prop('disabled', true);
        getSpecificData();

    });


    $("#advanceFilter").click(function () {
        $("#advance").slideToggle("slow");
    });




    //窗口大小改变时重新设置map所在div大小
    $(window).resize(function () {
        setTimeout(function () {
            var h = $(window).height(),
                offsetTop = $(".main-header").height() + 51; // Calculate the top offset
            //alert(h + "," + $(".main-header").height() + "," + $("footer").height());
            $('#map-div').css('height', (h - offsetTop));
            $(".slider").width(function () {
                return $("#pac-input").width() / 2.2;
            });
        }, 30);






    });



});