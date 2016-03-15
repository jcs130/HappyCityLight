//-------------
//- HEX CLASS -
//-------------

//defination of a hex
function Hex(q, r, s) {
    this.q = q;
    this.r = r;
    this.s = s;
};

Hex.hexIsEqual = function (hexA, hexB) {
    return (hexA.q == hexB.q) && (hexA.r == hexB.r) && (hexA.s == hexB.s);
};

Hex.hexDistance = function (hexA, hexB) {
    return Math.max(Math.abs(hexA.q - hexB.q), Math.abs(hexA.r - hexB.r), Math.abs(hexA.s - hexB.s));
};

Hex.hexRound = function (h) {
    var rx = Math.round(h.q);
    var ry = Math.round(h.r);
    var rz = Math.round(h.s);

    var x_diff = Math.abs(rx - h.q);
    var y_diff = Math.abs(ry - h.r);
    var z_diff = Math.abs(rz - h.s);

    if ((x_diff > y_diff) && (x_diff > z_diff)) {
        rx = -ry - rz;
    } else if (y_diff > z_diff) {
        ry = -rx - rz;
    } else {
        rz = -rx - ry;
    }

    return new Hex(rx, ry, rz);
};

Hex.hexLerp = function (hexA, hexB, t) {
    return new Hex((hexA.q + (hexB.q - hexA.q) * t), (hexA.r + (hexB.r - hexA.r) * t), (hexA.s + (hexB.s - hexA.s) * t));
};

Hex.hexLinedraw = function (hexA, hexB) {
    var N = Hex.hexDistance(hexA, hexB);
    var resultHexs = new Array();
    for (var i = 0; i < N; i++) {
        resultHexs.push(Hex.hexRound(Hex.hexLerp(hexA, hexB, 1.0 / N * i)));
    }
    return resultHexs;
};
//-------------
//- END OF HEX CLASS -
//-------------



//-------------
//- HEXONMAP CLASS -
//-------------
function HexOnMap(map) {
    this.map = map;
    this.TILE_SIZE = 256;
    this.l_earth = 40075000;
    this.r_earth = this.l_earth / (2 * Math.PI);
    this.defaultZoomR = 10000;
    this.defaultZoomLevel = 9;
    this.minZoomLevel = 9;
    console.log("Create HexOnMap Object.");
};


HexOnMap.prototype.getRadius = function () {
    if (this.map.getZoom() < this.minZoomLevel) {
        return this.defaultZoomR;
    } else {
        return this.defaultZoomR * Math.pow(2, this.defaultZoomLevel) / Math.pow(2, this.map.getZoom());
    }
};
HexOnMap.prototype.getHexSize = function () {
    return this.getRadius() * this.TILE_SIZE / this.l_earth;
};
HexOnMap.prototype.getHexCenterWorldCoordinate = function (hex) {
    var x = this.getHexSize() * Math.sqrt(3) * (hex.q + hex.s / 2);
    var y = this.getHexSize() * 3 / 2 * hex.s;
    return new google.maps.Point(x, y);
};
HexOnMap.prototype.getHexFromWorld = function (worldCoordinate) {
    var cx = (worldCoordinate.x * Math.sqrt(3) / 3 - worldCoordinate.y / 3) / this.getHexSize();
    var cz = worldCoordinate.y * 2 / 3 / this.getHexSize();
    var cy = -cx - cz;
    return Hex.hexRound(new Hex(cx, cy, cz));
};
HexOnMap.prototype.getHexFromLatlng = function (latlong) {
    var proj = this.map.getProjection();
    return this.getHexFromWorld(proj.fromLatLngToPoint(latlong));
};
HexOnMap.prototype.getHexPolygonPathFromHex = function (hex) {
    //    console.log("HexOnMap.getHexPolygonPathFromHex: hex= " + hex.q + "," + hex.r + "," + hex.s);
    var proj = this.map.getProjection();
    var hexSize = this.getHexSize();
    var center = this.getHexCenterWorldCoordinate(hex);
    //    console.log("HexOnMap.getHexPolygonPathFromHex: center= " + center);
    var p1, p2, p3, p4, p5, p6;
    p1 = new google.maps.Point((center.x - Math.sqrt(3) * hexSize / 2), (center.y + 0.5 * hexSize));
    p2 = new google.maps.Point(center.x, (center.y + hexSize));
    p3 = new google.maps.Point((center.x + Math.sqrt(3) * hexSize / 2), (center.y + 0.5 * hexSize));
    p4 = new google.maps.Point((center.x + Math.sqrt(3) * hexSize / 2), (center.y - 0.5 * hexSize));
    p5 = new google.maps.Point(center.x, (center.y - hexSize));
    p6 = new google.maps.Point((center.x - Math.sqrt(3) * hexSize / 2), (center.y - 0.5 * hexSize));
    //    console.log("HexOnMap.getHexPolygonPathFromHex: path= " + [proj.fromPointToLatLng(p1), proj.fromPointToLatLng(p2), proj.fromPointToLatLng(p3), proj.fromPointToLatLng(p4), proj.fromPointToLatLng(p5), proj.fromPointToLatLng(p6), proj.fromPointToLatLng(p1)].toString());
    return [proj.fromPointToLatLng(p1), proj.fromPointToLatLng(p2), proj.fromPointToLatLng(p3), proj.fromPointToLatLng(p4), proj.fromPointToLatLng(p5), proj.fromPointToLatLng(p6), proj.fromPointToLatLng(p1)];
};
//-------------
//- ENd OF HEXONMAP CLASS -
//-------------



//-------------
//- HEXBOUNDARY CLASS -
//-------------
function HexBoundary(hom, rec) {
    if (hom instanceof HexOnMap) {
        this.hom = hom;
        this.rec = rec;
        this.defaultColor = "#dbdbdb";
        this.defaultOpacity = 0.7;
        this.currentColor = null;
        this.currentOpacity = null;
        this.fadeOutColor = "#b2b1ad";
        this.hexBoundaryPolygon = new Array();
        this.colorAnimationInterval = null;
        console.log("Create HexBoundary Object.");
    } else {
        console.error("Error:Fail in HexBoundary Initialisation.");
    }
};

HexBoundary.negativeColor = d3.rgb(0, 92, 175); //color for negative
HexBoundary.positiveColor = d3.rgb(225, 107, 140); //color for positive

HexBoundary.prototype.getHexInsideRec = function () {
    //    console.log("HexBoundary.getHexInsideRec.");
    var _this = this;
    var calHexArrayS = new Array();
    $.each(_this.rec, function () {
        var NW = new google.maps.LatLng(this.north, this.west),
            NE = new google.maps.LatLng(this.north, this.east),
            SE = new google.maps.LatLng(this.south, this.east),
            SW = new google.maps.LatLng(this.south, this.west);
        var hex_NW = _this.hom.getHexFromLatlng(NW),
            hex_NE = _this.hom.getHexFromLatlng(NE),
            hex_SE = _this.hom.getHexFromLatlng(SE),
            hex_SW = _this.hom.getHexFromLatlng(SW);
        var calHexArray = new Array();
        $.each(Hex.hexLinedraw(hex_NW, hex_NE), function () {
            calHexArray.push(this);
        });
        $.each(Hex.hexLinedraw(hex_NE, hex_SE), function () {
            calHexArray.push(this);
        });
        $.each(Hex.hexLinedraw(hex_SE, hex_SW), function () {
            calHexArray.push(this);
        });
        $.each(Hex.hexLinedraw(hex_SW, hex_NW), function () {
            calHexArray.push(this);
        });
        var calHexArraySortByS = calHexArray.sort(function (hexA, hexB) {
            if (hexA.s == hexB.s) {
                return hexA.q - hexB.q;
            } else {
                return hexA.s - hexB.s;
            }
        });
        var s = calHexArraySortByS[0].s;
        var temp = new Array();
        $.each(calHexArraySortByS, function () {
            if (this.s == s) {
                temp.push(this);
            } else if (this.s == (s + 1)) {
                var len = temp.length;
                for (var i = 0; i < len - 1; i++) {
                    if ((temp[i + 1].q - temp[i].q) > 1) {
                        for (var q = temp[i].q + 1; q < temp[i + 1].q; q++) {
                            calHexArray.push(new Hex(q, (-q - temp[i].s), temp[i].s));
                        }
                    }
                }
                temp = [];
                temp.push(this);
                s += 1;
            }
        });
        $.each(calHexArray, function () {
            calHexArrayS.push(this);
        });
    });
    var resultHexArray = new Array();
    var resultInfoArray = new Array();
    $.each(calHexArrayS, function () {
        if ($.inArray("" + this.q + this.r + this.s, resultInfoArray) <= -1) {
            resultInfoArray.push("" + this.q + this.r + this.s);
            resultHexArray.push(this);
        }
    });
    return resultHexArray;
};

HexBoundary.prototype.createHexBoundary = function () {
    //    console.log("HexBoundary.createHexBoundary.");
    var _this = this;
    var hexArray = this.getHexInsideRec();
    $.each(hexArray, function () {
        //        console.log(this.q + "," + this.r + "," + this.s);
        var paths = _this.hom.getHexPolygonPathFromHex(this);
        var hexPolygon = new google.maps.Polygon({
            map: _this.hom.map,
            paths: paths,
            strokeWeight: 0,
            fillColor: _this.defaultColor,
            fillOpacity: _this.defaultOpacity
        });
        _this.hexBoundaryPolygon.push(hexPolygon);
    });
    _this.currentColor = _this.defaultColor;
    _this.currentOpacity = _this.defaultOpacity;
};
HexBoundary.prototype.hideHexBoundary = function () {
    //    console.log("HexBoundary.hideHexBoundary.");
    var _this = this;
};
HexBoundary.prototype.showHexBoundary = function () {
    //    console.log("HexBoundary.showHexBoundary.");
    var _this = this;
};
HexBoundary.prototype.deleteHexBoundary = function () {
    //    console.log("HexBoundary.deleteHexBoundary.");
    var _this = this;
    if ((_this.hexBoundaryPolygon != []) && (_this.hexBoundaryPolygon.length != 0)) {
        $.each(_this.hexBoundaryPolygon, function () {
            this.setMap(null);
        });
        _this.hexBoundaryPolygon = [];
    } else {
        console.error("ERROR in HexBoundary.deleteHexBoundary: hexBoundaryPolygon is empty. / length of hexBoundaryPolygon is zero.");
    }
};
HexBoundary.prototype.updateHexBoundary = function () {
    //    console.log("HexBoundary.updateHexBoundary.");
    var color = this.currentColor;
    var opacity = this.currentOpacity;
    this.deleteHexBoundary();
    this.createHexBoundary();
    this.changeHexBoundaryColor(color);
    this.changeHexBoundaryOpacity(opacity);
};
HexBoundary.prototype.changeHexBoundaryColor = function (color) {
    //    console.log("HexBoundary.changeHexBoundaryColor: " + color);
    var _this = this;
    if ((_this.hexBoundaryPolygon != []) && (_this.hexBoundaryPolygon.length != 0)) {
        $.each(_this.hexBoundaryPolygon, function () {
            this.setOptions({
                fillColor: color,
            });
        });
        _this.currentColor = color;
    } else {
        console.error("ERROR in HexBoundary.changeHexBoundaryColor: hexBoundaryPolygon is empty. / length of hexBoundaryPolygon is zero.");
    }
};
HexBoundary.prototype.changeHexBoundaryColorFromValue = function (value) {
    //    console.log("HexBoundary.changeHexBoundaryColorFromValue.");
    this.changeHexBoundaryColor(this.getColorFromValue(value));
};
HexBoundary.prototype.changeHexBoundaryOpacity = function (opacity) {
    //    console.log("HexBoundary.changeHexBoundaryOpacity.");
    var _this = this;
    if ((_this.hexBoundaryPolygon != []) && (_this.hexBoundaryPolygon.length != 0)) {
        $.each(_this.hexBoundaryPolygon, function () {
            this.setOptions({
                fillOpacity: opacity,
            });
        });
        _this.currentOpacity = opacity;
    } else {
        console.error("ERROR in HexBoundary.changeHexBoundaryOpacity: hexBoundaryPolygon is empty. / length of hexBoundaryPolygon is zero.");
    }
};
HexBoundary.prototype.getColorFromValue = function (value) {
    //    console.log("HexBoundary.getColorFromValue.");
    //    var red = d3.rgb(238, 44, 44); //color for negative
    //    var green = d3.rgb(162, 205, 90); //color for positive
    //    var yellow = d3.rgb(255, 215, 0); //color for neutral
    //    if (value >= 0) {
    //        var color = d3.interpolate(yellow, green); //颜色插值函数
    //        var linear = d3.scale.linear().domain([0, 5]).range([0, 1]);
    //        return color(linear(value));
    //    } else {
    //        var color = d3.interpolate(red, yellow); //颜色插值函数
    //        var linear = d3.scale.linear().domain([-5, 0]).range([0, 1]);
    //        return color(linear(value));
    //    }
    //    console.log("HexBoundary.getColorFromValue: " + value);
    var min = 0.4,
        max = 0.6;
    if (value > max) {
        value = max;
    } else if (value < min) {
        value = min;
    }
    var negaColor = HexBoundary.negativeColor; //color for negative
    var posiColor = HexBoundary.positiveColor; //color for positive
    var color = d3.interpolate(negaColor, posiColor); //颜色插值函数
    var linear = d3.scale.linear().domain([min, max]).range([0, 1]);
    //    console.log(color(linear(0.493))); #0059f2
    //    console.log(color(linear(0.573))); #00a5fc
    return color(linear(value));
};
HexBoundary.prototype.playColorAnimation = function (graghDataArray, startIndex, intervalTime) {
    var _this = this;
    if (graghDataArray.length != 0) {
        _this.stopColorAnimation();
        console.log("HexBoundary.playColorAnimation.");
        var index = startIndex;
        var step = 20;
        var i = 0;
        var value = graghDataArray[index]["pulse_value"];
        var startColor = _this.currentColor;
        var endColor = _this.getColorFromValue(value);
        var interpolator = d3.interpolate(startColor, endColor);
        var linear = d3.scale.linear().domain([0, step]).range([0, 1]);
        _this.colorAnimationInterval = setInterval(function () {
            _this.changeHexBoundaryColor(interpolator(linear(i)));
            i++;
            if (i > step) {
                if ((index + 1) == graghDataArray.length) {
                    console.log("HexBoundary.playColorAnimation:FADEOUT.");
                    startColor = _this.currentColor;
                    endColor = _this.fadeOutColor;
                    interpolator = d3.interpolate(startColor, endColor);
                    i = 0;
                    index = 0;
                } else {
                    index = (index + 1) % (graghDataArray.length);
                    value = graghDataArray[index]["pulse_value"];
                    startColor = _this.currentColor;
                    endColor = _this.getColorFromValue(value);
                    interpolator = d3.interpolate(startColor, endColor);
                    i = 0;
                }
            }
        }, intervalTime / step);
    }
};
HexBoundary.prototype.stopColorAnimation = function () {
    var _this = this;
    if (_this.colorAnimationInterval != null) {
        console.log("HexBoundary.stopColorAnimation.");
        clearInterval(_this.colorAnimationInterval);
        _this.colorAnimationInterval = null;
    }
};
//-------------
//- HEXBOUNDARY CLASS -
//-------------