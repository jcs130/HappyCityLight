var minZoomLevel = 9;
//defination of a hex
function Hex(q, r, s, positive, neutral, negative, msg, hexPolygon, infobox) {
    this.q = q;
    this.r = r;
    this.s = s;
    this.positive = positive;
    this.neutral = neutral;
    this.negative = negative;
    this.msg = msg;
    this.hexPolygon = hexPolygon;
    this.infobox = infobox;
}


function Cube(x, y, z) {
    this.x = x;
    this.y = y;
    this.z = z;
}


function hexIsEqual(hexA, hexB) {
    return (hexA.q == hexB.q) && (hexA.r == hexB.r) && (hexA.s == hexB.s);
}

function hexDistance(hexA, hexB) {
    return Math.round((Math.abs(hexA.q - hexB.q) + Math.abs(hexA.r - hexB.r) + Math.abs(hexA.s - hexB.s)) / 2);
}

function cubeDistance(hexA, hexB) {
    return Math.max(Math.abs(hexA.q - hexB.q), Math.abs(hexA.r - hexB.r), Math.abs(hexA.s - hexB.s));
}


function cubeRound(h) {
    var rx = Math.round(h.x);
    var ry = Math.round(h.y);
    var rz = Math.round(h.z);

    var x_diff = Math.abs(rx - h.x);
    var y_diff = Math.abs(ry - h.y);
    var z_diff = Math.abs(rz - h.z);

    if ((x_diff > y_diff) && (x_diff > z_diff)) {
        rx = -ry - rz;
    } else if (y_diff > z_diff) {
        ry = -rx - rz;
    } else {
        rz = -rx - ry;
    }

    var hex = new Hex(rx, ry, rz, 0, 0, 0, new Array(), null, null);
    return hex;
}


function cubeLerp(hexA, hexB, t) {
    return new Cube((hexA.q + (hexB.q - hexA.q) * t), (hexA.r + (hexB.r - hexA.r) * t), (hexA.s + (hexB.s - hexA.s) * t));
}


function hexLinedraw(hexA, hexB) {
    var N = cubeDistance(hexA, hexB);
    var resultHexs = new Array();
    for (var i = 0; i < N; i++) {
        resultHexs.push(cubeRound(cubeLerp(hexA, hexB, 1.0 / N * i)));
    }
    return resultHexs;
}

function getRadius() {
    if (map.getZoom() < minZoomLevel) {
        return R;
    } else {
        return R * Math.pow(2, 9) / Math.pow(2, map.getZoom());
    }
}



//world coordinate to hex coordinate
function getHexFromWorld(worldCoordinate) {
    var hexSize = getRadius() * TILE_SIZE / l_earth;
    var x = worldCoordinate.x;
    var y = worldCoordinate.y;
    var q = (x * Math.sqrt(3) / 3 - y / 3) / hexSize;
    var r = y * 2 / 3 / hexSize;
    var cx = q;
    var cz = r;
    var cy = -cx - cz;

    var rx = Math.round(cx);
    var ry = Math.round(cy);
    var rz = Math.round(cz);

    var x_diff = Math.abs(rx - cx);
    var y_diff = Math.abs(ry - cy);
    var z_diff = Math.abs(rz - cz);

    if ((x_diff > y_diff) && (x_diff > z_diff)) {
        rx = -ry - rz;
    } else if (y_diff > z_diff) {
        ry = -rx - rz;
    } else {
        rz = -rx - ry;
    }

    var hex = new Hex(rx, ry, rz, 0, 0, 0, new Array(), null, null);
    return hex;
}


//world coordinate to hex coordinate
function getHexFromLatlng(latlong) {
    var proj = map.getProjection();
    var point_xy = proj.fromLatLngToPoint(latlong);
    return getHexFromWorld(point_xy);
}


//get hex center's world coordinate from hex coordinate
function getCenter(hex) {
    var hexSize = getRadius() * TILE_SIZE / l_earth;
    var x = hexSize * Math.sqrt(3) * (hex.q + hex.s / 2);
    var y = hexSize * 3 / 2 * hex.s;
    return new google.maps.Point(x, y);
}


//update a hex with a msg data recieved from server
function updateHex(msgData) {

    var num_id = msgData.num_id;
    var latlong = new google.maps.LatLng(msgData.query_location_latitude, msgData.query_location_langtitude);

    var hex = getHexFromLatlng(latlong);
    //var center_xy = getCenter(hex);

    var flag = 0;

    $.each(hexArray, function () {
        if ((this.q == hex.q) && (this.r == hex.r) && (this.s == hex.s)) {
            flag = 1;
            this.msg.push(msgData);
            //alert("1:" + this.msg);
            switch (msgData.emotion_text) {
            case "positive":
                this.positive++;
                break;
            case "neutral":
                this.neutral++;
                break;
            case "negative":
                this.negative++;
                break;
            default:
                break;
            }
            drawHexFromHex(this);
        }

    });

    if (flag == 0) {
        hex.msg.push(msgData);
        //alert("0:" + hex.msg);
        switch (msgData.emotion_text) {
        case "positive":
            hex.positive++;
            break;
        case "neutral":
            hex.neutral++;
            break;
        case "negative":
            hex.negative++;
            break;
        default:
            break;
        }
        drawHexFromHex(hex);
        hexArray.push(hex);
    }

}


function getHexInsideRec(rec) {
    var NW = new google.maps.LatLng(rec.north, rec.west),
        NE = new google.maps.LatLng(rec.north, rec.east),
        SE = new google.maps.LatLng(rec.south, rec.east),
        SW = new google.maps.LatLng(rec.south, rec.west);
    var hex_NW = getHexFromLatlng(NW),
        hex_NE = getHexFromLatlng(NE),
        hex_SE = getHexFromLatlng(SE),
        hex_SW = getHexFromLatlng(SW);
    var resultHexArray = new Array();
    /*    resultHexArray.push(hex_NW);
        resultHexArray.push(hex_NE);
        resultHexArray.push(hex_SE);
        resultHexArray.push(hex_SW);*/
    $.each(hexLinedraw(hex_NW, hex_NE), function () {
        resultHexArray.push(this);
    });
    $.each(hexLinedraw(hex_NE, hex_SE), function () {
        resultHexArray.push(this);
    });
    $.each(hexLinedraw(hex_SE, hex_SW), function () {
        resultHexArray.push(this);
    });
    $.each(hexLinedraw(hex_SW, hex_NW), function () {
        resultHexArray.push(this);
    });

    var resultHexArraySortByS = resultHexArray.sort(function (hexA, hexB) {
        if (hexA.s == hexB.s) {
            return hexA.q - hexB.q;
        } else {
            return hexA.s - hexB.s;
        }
    });

    var s = resultHexArraySortByS[0].s;
    var temp = new Array();

    $.each(resultHexArraySortByS, function () {
        //console.log(this.q + "," + this.r + "," + this.s);
        if (this.s == s) {
            temp.push(this);
        } else if (this.s == (s + 1)) {
            //            if (temp.length == 2) {
            //                if ((temp[1].q - temp[0].q) > 1) {
            //                    //console.log("(" + temp[0].q + "," + temp[0].r + "," + temp[0].s + ") <-> (" + temp[1].q + "," + temp[1].r + "," + temp[1].s + ")");
            //                    for (var q = temp[0].q + 1; q < temp[1].q; q++) {
            //                        //                        console.log("Adding: " + q + "," + (-q - temp[0].s) + "," + temp[0].s);
            //                        resultHexArray.push(new Hex(q, (-q - temp[0].s), temp[0].s, 0, 0, 0, new Array(), null, null));
            //                    }
            //                }
            //            }
            var len = temp.length;
            for (var i = 0; i < len - 1; i++) {
                if ((temp[i + 1].q - temp[i].q) > 1) {
                    //console.log("(" + temp[0].q + "," + temp[0].r + "," + temp[0].s + ") <-> (" + temp[1].q + "," + temp[1].r + "," + temp[1].s + ")");
                    for (var q = temp[i].q + 1; q < temp[i + 1].q; q++) {
                        //                        console.log("Adding: " + q + "," + (-q - temp[0].s) + "," + temp[0].s);
                        resultHexArray.push(new Hex(q, (-q - temp[i].s), temp[i].s, 0, 0, 0, new Array(), null, null));
                    }
                }
            }
            temp = [];
            temp.push(this);
            s += 1;
        }

    });

    /* console.log("NW: " + hex_NW.q + "," + hex_NW.r + "," + hex_NW.s);
     console.log("NE: " + hex_NE.q + "," + hex_NE.r + "," + hex_NE.s);
     console.log("SE: " + hex_SE.q + "," + hex_SE.r + "," + hex_SE.s);
     console.log("SW: " + hex_SW.q + "," + hex_SW.r + "," + hex_SW.s);*/
    /*    $.each(resultHexArray, function () {
            drawHexFromHex(this);
            console.log(this.q + "," + this.r + "," + this.s);
            hexArray.push(this);
        });*/


    return resultHexArray;

}



function getColorFromHex(hex) {
    if ((hex.positive == 0) && (hex.neutral == 0) && (hex.negative == 0)) {
        return d3.rgb(208, 90, 110);
        //        return d3.rgb(222, 222, 222);
    }

    var red = d3.rgb(238, 44, 44); //
    var green = d3.rgb(162, 205, 90); //
    var yellow = d3.rgb(255, 215, 0); //
    if (hex.positive > hex.negative) {
        var color = d3.interpolate(yellow, green); //颜色插值函数
        var linear = d3.scale.linear().domain([0, 1]).range([0, 1]);
        var value = (hex.positive - hex.negative) / (hex.positive + hex.neutral + hex.negative);
        return color(linear(value));
    } else {
        var color = d3.interpolate(red, yellow); //颜色插值函数
        var linear = d3.scale.linear().domain([-1, 0]).range([0, 1]);
        var value = (hex.positive - hex.negative) / (hex.positive + hex.neutral + hex.negative);
        return color(linear(value));
    }

}



function drawHexFromHex(hex) {
    var hexPolygon = hex.hexPolygon;
    if (hex.hexPolygon != null) {
        hex.hexPolygon.setMap(null);
    }

    var proj = map.getProjection();
    var hexSize = getRadius() * TILE_SIZE / l_earth;
    var center = getCenter(hex);

    var p1, p2, p3, p4, p5, p6;
    p1 = new google.maps.Point((center.x - Math.sqrt(3) * hexSize / 2), (center.y + 0.5 * hexSize));
    p2 = new google.maps.Point(center.x, (center.y + hexSize));
    p3 = new google.maps.Point((center.x + Math.sqrt(3) * hexSize / 2), (center.y + 0.5 * hexSize));
    p4 = new google.maps.Point((center.x + Math.sqrt(3) * hexSize / 2), (center.y - 0.5 * hexSize));
    p5 = new google.maps.Point(center.x, (center.y - hexSize));
    p6 = new google.maps.Point((center.x - Math.sqrt(3) * hexSize / 2), (center.y - 0.5 * hexSize));
    //alert(p1 + " " + p2 + " " + p3 + " " + p4 + " " + p5);
    var newHexPolygon = new google.maps.Polygon({
        paths: [proj.fromPointToLatLng(p1), proj.fromPointToLatLng(p2), proj.fromPointToLatLng(p3), proj.fromPointToLatLng(p4), proj.fromPointToLatLng(p5), proj.fromPointToLatLng(p6), proj.fromPointToLatLng(p1)],
        strokeWeight: 0,
        fillColor: getColorFromHex(hex),
        fillOpacity: 0.5
    });
    //alert(newHexPolygon.getPath().getAt(0).lat());
    hex.hexPolygon = newHexPolygon;
    hex.hexPolygon.setMap(map);
    google.maps.event.addListener(hex.hexPolygon, 'click', function (event) {
        if (!((hex.positive == 0) && (hex.negative == 0) && (hex.neutral == 0))) {
            if (hex.infobox != null) {
                if (hex.infobox.getVisible()) {
                    hex.infobox.setVisible(false);
                } else {
                    hex.infobox.setVisible(true);
                }
            } else {
                //            alert(proj.fromPointToLatLng(getCenter(hex)));
                var content = "<div id='infobox' class='box box-widget infobox'><div class='box-header with-border'><div class='user-block'><span class='username'><a href='#'>Region</a></span><span class='description'>Public Region</span></div><div class='box-tools'><button type='button' class='btn btn-box-tool closeInfobox' data-widget='remove'><i class='fa fa-times'></i></button></div></div><div class='box-footer box-comments'>";
                if (hex.msg != null) {
                    console.log("Display msg in the hex.");
                    $.each(hex.msg, function () {
                        content += "<div class='box-comment'><img class='img-circle img-sm' src='";
                        if (this.profile_image_url != null) {
                            content += this.profile_image_url;
                        } else {
                            content += "resources/img/userDefault.png";
                        }
                        var date = new Date(this.creat_at);
                        content += "' alt='User Image'><div class='comment-text'><span class='username'>" + this.user_name + "<span class='text-muted pull-right'>" + date.toDateString() + "</span>" + this.text + "</span></div></div>";
                    });

                }
                content += "</div></div>";

                var hexInfobox = new InfoBox({
                    content: content,
                    maxWidth: 60,
                    pixelOffset: new google.maps.Size(-70, 0),
                    disableAutoPan: false,
                    zIndex: null
                });
                var Marker = new google.maps.Marker({
                    map: map,
                    position: proj.fromPointToLatLng(getCenter(hex)),
                });

                hexInfobox.open(map, Marker);
                Marker.setMap(null);
                hex.infobox = hexInfobox;
                allInfoboxs.push(hex.infobox);

                $(".box-tools").click(function () {
                    hex.infobox.setVisible(false);
                });


            }
        }
    });
}



//draw all hex in the regions array
//(array contains msgs from servers)
function drawAllHex(msgDatas) {
    $.each(msgDatas, function () {
        updateHex(this);
    });
}