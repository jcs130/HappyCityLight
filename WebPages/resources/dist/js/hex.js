//defination of a hex
function Hex(q, r, s, positive, neutral, negative, msg, hexPolygon) {
    this.q = q;
    this.r = r;
    this.s = s;
    this.positive = positive;
    this.neutral = neutral;
    this.negative = negative;
    this.msg = msg;
    this.hexPolygon = hexPolygon;
}

//update a hex with a msg data recieved from server
function updateHex(msgData) {

    var num_id = msgData.obj[0].num_id;
    var latlong = new google.maps.LatLng(msgData.obj[0].query_location_latitude, msgData.obj[0].query_location_langtitude);
    var proj = map.getProjection();
    var point_xy = proj.fromLatLngToPoint(latlong);
    var radius = R * Math.pow(2, 9) / Math.pow(2, map.getZoom());
    var hexSize = radius * TILE_SIZE / l_earth;
    var hex = getHex(hexSize, point_xy);
    //var center_xy = getCenter(hexSize, hex);

    var flag = 0;

    $.each(hexArray, function () {
        if ((this.q == hex.q) && (this.r == hex.r) && (this.s == hex.s)) {
            flag = 1;
            this.msg.push(num_id);
            //alert("1:" + this.msg);
            switch (msgData.obj[0].emotion_text) {
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
            drawHexFromHex(this, map.getZoom());
        }

    });

    if (flag == 0) {
        hex.msg.push(num_id);
        //alert("0:" + hex.msg);
        switch (msgData.obj[0].emotion_text) {
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
        drawHexFromHex(hex, map.getZoom());
        hexArray.push(hex);
    }

}



//draw all hex in the regions array
//(array contains msgs from servers)
function drawAllHex(msgDatas) {

    $.each(msgDatas, function () {
        updateHex(this);
    });

}




//world coordinate to hex coordinate
function getHex(hexSize, worldCoordinate) {
    var x = worldCoordinate.x;
    var y = worldCoordinate.y;
    q = (x * Math.sqrt(3) / 3 - y / 3) / hexSize;
    r = y * 2 / 3 / hexSize;
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

    var hex = new Hex(rx, ry, rz, 0, 0, 0, new Array(), null);
    return hex;
}



//get hex center's world coordinate from hex coordinate
function getCenter(hexSize, hex) {
    var x = hexSize * Math.sqrt(3) * (hex.q + hex.s / 2);
    var y = hexSize * 3 / 2 * hex.s;
    return new google.maps.Point(x, y);

}



function drawHexFromHex(hex, zoom) {
    var hexPolygon = hex.hexPolygon;
    if (hex.hexPolygon != null) {
        hex.hexPolygon.setMap(null);
    }
    var proj = map.getProjection();
    var radius = R * Math.pow(2, 9) / Math.pow(2, zoom);
    var hexSize = radius * TILE_SIZE / l_earth;
    var center = getCenter(hexSize, hex);

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
}




function getColorFromHex(hex) {
    var red = d3.rgb(238, 44, 44); //浅蓝
    var green = d3.rgb(162, 205, 90); //深蓝
    var yellow = d3.rgb(255, 215, 0); //深蓝
    if (hex.positive > hex.negative) {
        var color = d3.interpolate(yellow, green); //颜色插值函数
        var linear = d3.scale.linear().domain([0, 1]).range([0, 1]);
    } else {
        var color = d3.interpolate(red, yellow); //颜色插值函数
        var linear = d3.scale.linear().domain([-1, 0]).range([0, 1]);
    }

    var value = (hex.positive - hex.negative) / (hex.positive + hex.neutral + hex.negative);
    return color(linear(value));
}