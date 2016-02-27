var apiToken = "ArashiArashiFordream";
var map = null;
var TILE_SIZE = 256;
var l_earth = 40075000;
var r_earth = l_earth / (2 * Math.PI);
var R = 10000;
var minZoomLevel = 9;
var numPerTable = 10;
var topicNum = 10;
var multiThreshold = 6;
var hexBoundaryArray = new Array();
var boundaryRecArray = new Array();
var regionBoundary = null;
var getOneStatisticRecordData = null;
var getregionranksData = new Array();
var colorBox = ["#da8b8b", "#8bbdda", "#c6da8b", "#F7C242", "#8B81C3", "#8bdac6", "#B4A582", "#E3916E"];


var citypulseExample = {
    "code": 200,
    "type": "OK",
    "message": "Get Record Success.",
    "obj": {
        "record_id": 1334,
        "date_timestamp_ms": 1453420723350,
        "local_date": "2016-01-21",
        "regInfo": {
            "regID": 7,
            "regName": "ottawa",
            "country": "Canada",
            "streamState": 0,
            "center_lat": 45.330275043785676,
            "center_lan": -75.68262099304684,
            "areas": [{
                "north": 45.4330899762748,
                "south": 45.1397566429415,
                "west": -75.7642068262205,
                "east": -75.3472814245606,
                "locID": 14
            }, {
                "north": 45.4021412729911,
                "south": 45.0354746063245,
                "west": -76.0727988431375,
                "east": -75.5522621920187,
                "locID": 15
            }, {
                "north": 45.4804785791851,
                "south": 45.2238119125184,
                "west": -76.249205916566,
                "east": -75.8839727943714,
                "locID": 16
            }, {
                "north": 45.4669690171921,
                "south": 45.2836356838588,
                "west": -75.6944586488989,
                "east": -75.433471038601,
                "locID": 17
            }, {
                "north": 45.5103630399519,
                "south": 45.3270297066186,
                "west": -75.5448701777805,
                "east": -75.2836820683133,
                "locID": 18
            }],
            "box_points": "[{\"lat\":45.4804785791851,\"lng\":-75.8839727943714},{\"lat\":45.4021412729911,\"lng\":-75.8839727943714},{\"lat\":45.4021412729911,\"lng\":-75.76420682622052},{\"lat\":45.4330899762748,\"lng\":-75.76420682622052},{\"lat\":45.4330899762748,\"lng\":-75.69445864889889},{\"lat\":45.4669690171921,\"lng\":-75.69445864889889},{\"lat\":45.4669690171921,\"lng\":-75.54487017778047},{\"lat\":45.5103630399519,\"lng\":-75.54487017778047},{\"lat\":45.5103630399519,\"lng\":-75.28368206831328},{\"lat\":45.3270297066186,\"lng\":-75.28368206831328},{\"lat\":45.3270297066186,\"lng\":-75.3472814245606},{\"lat\":45.1397566429415,\"lng\":-75.3472814245606},{\"lat\":45.1397566429415,\"lng\":-75.5522621920187},{\"lat\":45.0354746063245,\"lng\":-75.5522621920187},{\"lat\":45.0354746063245,\"lng\":-76.07279884313749},{\"lat\":45.2238119125184,\"lng\":-76.07279884313749},{\"lat\":45.2238119125184,\"lng\":-76.24920591656598},{\"lat\":45.4804785791851,\"lng\":-76.24920591656598},{\"lat\":45.4804785791851,\"lng\":-75.8839727943714}]",
            "time_zone": -5
        },
        "pulse": {
            "timestamp": 0,
            "positive_num": 0,
            "negative_num": 0,
            "neutral_num": 2,
            "unknown_num": 2,
            "sum_num": 4,
            "pulse_value": 1.5
        },
        "rank": 11,
        "hot_topics": [{
            "text": "meetingplanner",
            "images": ["http://ww2.sinaimg.cn/bmiddle/7a24169dgw1ezooq01c6qj20fp0qe0vj.jpg", "http://ww4.sinaimg.cn/bmiddle/6a5ff038gw1ezb70hntm9j20qo0zkn4q.jpg", "http://ww2.sinaimg.cn/bmiddle/7a24169dgw1ezoopz2xodj20fz0mf779.jpg", "http://ww1.sinaimg.cn/bmiddle/7a24169dgw1ezooq133x8j20ez0mp0ur.jpg"],
            "pulse": {
                "timestamp": 0,
                "positive_num": 2,
                "negative_num": 3,
                "neutral_num": 4,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "with",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "funfunfun",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "charity",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "party",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "montrealeventplanner",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "montrealeventplanner",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "montrealeventplanner",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "montrealeventplanner",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }, {
            "text": "gala",
            "images": [],
            "pulse": {
                "timestamp": 0,
                "positive_num": 0,
                "negative_num": 0,
                "neutral_num": 0,
                "unknown_num": 2,
                "sum_num": 2,
                "pulse_value": 1.0
            },
            "count": 2
        }],
        "message_from": "all",
        "language": "all"
    }
};


//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}


function getFormattedDate(date) {
    var year = date.getFullYear();
    var month = (1 + date.getMonth()).toString();
    month = month.length > 1 ? month : '0' + month;
    var day = date.getDate().toString();
    day = day.length > 1 ? day : '0' + day;
    return year + '-' + month + '-' + day;
}



function IsValidImageUrl(url) {
    $("<img>", {
        src: url,
        error: function () {
            return false;
        },
        load: function () {
            return true;
        }
    });
}



function dataAnalysis(place_id) {
    //alert(place_id);
    var startDate = $('.input-daterange').datepicker().data('datepicker').pickers[0].getDate();
    var endDate = $('.input-daterange').datepicker().data('datepicker').pickers[1].getDate();
    console.log(startDate + " ~ " + endDate);
    //    console.log(startDate.getTime() + " ~ " + endDate.getTime());

    $.ajax({
        type: "GET",
        crossDomain: true,
        url: serverURL + "message/gethistoryinfo",
        data: {
            userID: user_id,
            token: logintoken,
            place_ids: place_id,
            date_start: getFormattedDate(startDate),
            date_end: getFormattedDate(endDate)
        },
        dataType: "json",
        beforeSend: function () {
            console.log("message/gethistoryinfo......");
            $("#CityEmotionChange-col").html("<div class='overlay' style='height:200px;'><i class='fa fa-refresh fa-spin'></i></div>");
        },
        success: function (data, textStatus) {
            //console.log(JSON.stringify(data));
            if (data.code == 200) {
                console.log("Success in message/gethistoryinfo: " + JSON.stringify(data));
                $("#CityEmotionChange-col .overlay").remove();
                //                var length = endDate.getTime() - startDate.getTime();
                //                var dataArray = new Array(length / dateOffset + 1);
                //                var dateOffset = 24 * 60 * 60 * 1000;
                var length = data.obj.length;
                var dataArray = new Array(length);
                var i = 0;
                $.each(data.obj[place_id], function () {
                    dataArray[i] = new Array(6);
                    dataArray[i]["date"] = this.date;
                    dataArray[i]["positive_num"] = this.pulse.positive_num;
                    dataArray[i]["neutral_num"] = this.pulse.neutral_num;
                    dataArray[i]["negative_num"] = this.pulse.negative_num;
                    dataArray[i]["pulse_value"] = this.pulse.pulse_value.toFixed(4);
                    dataArray[i]["score"] = this.pulse.score;
                    i++;
                });


                var chart = AmCharts.makeChart("CityInfoChart", {
                    "type": "serial",
                    "theme": "light",
                    "dataDateFormat": "YYYY-MM-DD",
                    "precision": -1,
                    "valueAxes": [{
                            "id": "v1",
                            "title": "Message Number",
                            "position": "left",
                            "autoGridCount": false,
                            "stackType": "regular",
                  }, {
                            "id": "v2",
                            "title": "Score",
                            "gridAlpha": 0,
                            "position": "right",
                            "autoGridCount": false
                                  }
                  ],
                    "graphs": [
                        {
                            "id": "g1",
                            "valueAxis": "v1",
                            "lineColor": "#ED784A",
                            "fillColors": "#ED784A",
                            "fillAlphas": 1,
                            "type": "column",
                            "title": "Negative",
                            "valueField": "negative_num",
                            "clustered": false,
                            "columnWidth": 0.3,
                            "legendValueText": "[[value]]",
                            "balloonText": "[[title]]<br/><b style='font-size: 130%'>[[value]]</b>",
                            animationPlayed: true
                  },
                        {
                            "id": "g2",
                            "valueAxis": "v1",
                            "lineColor": "#FAD689",
                            "fillColors": "#FAD689",
                            "fillAlphas": 1,
                            "type": "column",
                            "title": "Neutral",
                            "valueField": "neutral_num",
                            "clustered": false,
                            "columnWidth": 0.3,
                            "legendValueText": "[[value]]",
                            "balloonText": "[[title]]<br/><b style='font-size: 130%'>[[value]]</b>",
                            animationPlayed: true
                  }, {
                            "id": "g3",
                            "valueAxis": "v1",
                            "lineColor": "#91AD70",
                            "fillColors": "#91AD70",
                            "fillAlphas": 1,
                            "type": "column",
                            "title": "Positive",
                            "valueField": "positive_num",
                            "clustered": false,
                            "columnWidth": 0.3,
                            "legendValueText": "[[value]]",
                            "balloonText": "[[title]]<br/><b style='font-size: 130%'>[[value]]</b>",
                            animationPlayed: true
                  },
                        {
                            "id": "g5",
                            "valueAxis": "v2",
                            "bullet": "round",
                            "bulletBorderAlpha": 1,
                            "bulletColor": "#FFFFFF",
                            "bulletSize": 5,
                            "hideBulletsCount": 50,
                            "lineThickness": 2,
                            "lineColor": "#81C7D4",
                            "type": "smoothedLine",
                            "title": "Score",
                            "useLineColorForBulletBorder": true,
                            "valueField": "score",
                            "balloonText": "[[title]]<br/><b style='font-size: 130%'>[[value]]</b>",
                            animationPlayed: true
                  }],
                    "chartScrollbar": {
                        "graph": "g5",
                        "oppositeAxis": false,
                        "offset": 30,
                        "scrollbarHeight": 50,
                        "backgroundAlpha": 0,
                        "selectedBackgroundAlpha": 0.1,
                        "selectedBackgroundColor": "#888888",
                        "graphFillAlpha": 0,
                        "graphLineAlpha": 0.5,
                        "selectedGraphFillAlpha": 0,
                        "selectedGraphLineAlpha": 1,
                        "autoGridCount": true,
                        "color": "#AAAAAA"
                    },
                    "chartCursor": {
                        "pan": true,
                        "valueLineEnabled": true,
                        "valueLineBalloonEnabled": true,
                        "cursorAlpha": 0,
                        "valueLineAlpha": 0.2
                    },
                    "categoryField": "date",
                    "categoryAxis": {
                        "parseDates": true,
                        "dashLength": 1,
                        "minorGridEnabled": true,
                        "dateFormats": [{
                            period: 'fff',
                            format: 'JJ:NN:SS'
                    }, {
                            period: 'ss',
                            format: 'JJ:NN:SS'
                    }, {
                            period: 'mm',
                            format: 'JJ:NN'
                    }, {
                            period: 'hh',
                            format: 'JJ:NN'
                    }, {
                            period: 'DD',
                            format: 'MMM DD EEE'
                    }, {
                            period: 'WW',
                            format: 'MMM DD EEE'
                    }, {
                            period: 'MM',
                            format: 'MMM'
                    }, {
                            period: 'YYYY',
                            format: 'YYYY'
                    }]
                    },
                    "legend": {
                        "useGraphSettings": true,
                        "position": "top"
                    },
                    "balloon": {
                        "borderThickness": 1,
                        "shadowAlpha": 0
                    },
                    "export": {
                        "enabled": true
                    },
                    "dataProvider": dataArray
                });




            } else {
                //                if (data.code == 400) {
                //                    window.location.href = "login.html?message=Please Login Again.";
                //                }
                alert(JSON.stringify(data));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Error in message/gethistoryinfo: " + JSON.stringify(data));

        }
    });
}




function drawCityBoundaryAndHashtags(record_id) {
    if (user_id != null && logintoken != null) {
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "message/getonestatisticrecord",
            data: {
                userID: user_id,
                token: logintoken,
                record_id: record_id,
            },
            dataType: "json",
            beforeSend: function () {
                console.log("message/getonestatisticrecord......");
                $("#historyinfo").append("<div class='overlay'><i class='fa fa-refresh fa-spin'></i></div>");
            },
            success: function (data, textStatus) {
                if (data.code == 200) {
                    console.log("Success in message/getonestatisticrecord: " + JSON.stringify(data));
                    getOneStatisticRecordData = data;
                    google.maps.event.trigger(map, 'resize');
                    $("#historyinfo .overlay").remove();
                    //-------------
                    //- DRAW CITY BOUNDARY -
                    //-------------
                    var latMax = data.obj.regInfo.areas[0].north;
                    var latMin = data.obj.regInfo.areas[0].south;
                    var lngMax = data.obj.regInfo.areas[0].east;
                    var lngMin = data.obj.regInfo.areas[0].west;

                    $.each(data.obj.regInfo.areas, function () {
                        if (this.north > latMax) {
                            latMax = this.north;
                        }
                        if (this.south < latMin) {
                            latMin = this.south;
                        }
                        if (this.east > lngMax) {
                            lngMax = this.east;
                        }
                        if (this.west < lngMin) {
                            lngMin = this.west;
                        }
                    });

                    console.log(latMax + "," + lngMax + "  " + latMin + "," + lngMin);

                    map.setCenter(new google.maps.LatLng((latMax + latMin) / 2, (lngMax + lngMin) / 2));

                    boundaryRecArray = [];
                    google.maps.event.addListenerOnce(map, "tilesloaded", function () {
                        if (regionBoundary != null) {
                            regionBoundary.setMap(null);
                        }

                        if ((data.obj.regInfo.box_points != null) && (data.obj.regInfo.box_points != [])) {
                            console.log("box_points exists.");

                            regionBoundary = new google.maps.Polyline({
                                path: $.parseJSON(data.obj.regInfo.box_points),
                                map: map,
                                strokeColor: "#9F353A",
                                strokeOpacity: 0.6,
                                strokeWeight: 1
                            });

                        } else {
                            console.log("box_points not exists.");
                        }
                        var hexInfoArray = new Array();
                        $.each(data.obj.regInfo.areas, function () {
                            boundaryRecArray.push(this);
                            $.each(getHexInsideRec(this), function () {
                                if ($.inArray("" + this.q + this.r + this.s, hexInfoArray) <= -1) {
                                    hexInfoArray.push("" + this.q + this.r + this.s);
                                    drawHexFromHex(this);
                                    hexBoundaryArray.push(this);
                                    //                                console.log("Adding Hex: " + this.q + "," + this.r + "," + this.s);
                                } else {
                                    //                                console.log("Hex already exists.");
                                }
                            });
                        });


                        //redraw hex grid when map zoom changes
                        google.maps.event.addListener(map, 'zoom_changed', function () {
                            console.log("Googlemap zoom changed.");
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
                        map.fitBounds(new google.maps.LatLngBounds(new google.maps.LatLng(latMin, lngMin), new google.maps.LatLng(latMax, lngMax)));


                    });




                    //-------------
                    //- END OF DRAW CITY BOUNDARY -
                    //-------------


                    //-------------
                    //- DISPLAY HOT HASHTAGS -
                    //-------------

                    $("#cityHotHashtags .nav-pills").html("");

                    if (data.obj.hot_topics != []) {
                        var topicLength = (data.obj.hot_topics.length >= topicNum) ? topicNum : data.obj.hot_topics.length;
                        for (var i = 0; i < topicLength; i++) {
                            //        var topicItemHTML = "<li class='item'><div class='product-info'><a class='product-title showTopicDetail'>" + data.obj.hot_topics[i].text + "<span class='label label-warning pull-right'>" + data.obj.hot_topics[i].pulse.pulse_value.toFixed(4) + "</span></a></div></li>";
                            //                            if (data.obj.hot_topics[i].text != null) {
                            //                                var topicItemHTML = "<li hashtagIndex='" + i + "'><a href='#'>#" + data.obj.hot_topics[i].text + "<span class='pull-right text-red'>" + data.obj.hot_topics[i].pulse.pulse_value.toFixed(4) + "</span></a></li>";
                            if (data.obj.hot_topics[i].text != null) {
                                var topicItemHTML = "<li hashtagIndex='" + i + "'><a class='hashtag-word' href='#'>#" + data.obj.hot_topics[i].text + "<span class='label label-number'>" + data.obj.hot_topics[i].count + " msg</span><span class='pull-right label label-score'>" + data.obj.hot_topics[i].pulse.score + "</span></a></li>";
                                $("#cityHotHashtags .nav-pills").append(topicItemHTML);
                            }
                        }

                        $(".front li").click(function () {
                            var index = $(this).attr('hashtagIndex');
                            $("#cityHotHashtagsCard .back #HashtagImg").html("");
                            $("#cityHotHashtagsCard .back #HashtagInfo").html("<h2 style='text-align: center;'>#" + getOneStatisticRecordData.obj.hot_topics[index].text + "</h2>");



                            var dataTable = new Array(3);
                            dataTable[0] = new Array(3);
                            dataTable[1] = new Array(3);
                            dataTable[2] = new Array(3);
                            dataTable[0]["title"] = 'Posotive';
                            dataTable[0]["color"] = '#91AD70';
                            dataTable[0]["value"] = getOneStatisticRecordData.obj.hot_topics[index].pulse.positive_num;
                            dataTable[1]["title"] = 'Neutral';
                            dataTable[1]["color"] = '#FAD689';
                            dataTable[1]["value"] = getOneStatisticRecordData.obj.hot_topics[index].pulse.neutral_num;
                            dataTable[2]["title"] = 'Negative';
                            dataTable[2]["color"] = '#ED784A';
                            dataTable[2]["value"] = getOneStatisticRecordData.obj.hot_topics[index].pulse.negative_num;


                            var chart = AmCharts.makeChart("HashtagChart", {
                                "type": "pie",
                                "theme": "light",
                                "legend": {
                                    "position": "right",
                                    "marginRight": 100,
                                    "autoMargins": true
                                },
                                "dataProvider": dataTable,
                                "titleField": "title",
                                "valueField": "value",
                                "labelRadius": 5,
                                "radius": "42%",
                                "innerRadius": "40%",
                                "startEffect": "elastic",
                                "labelText": "[[title]]",
                                "colorField": "color"
                            });



                            $.each(getOneStatisticRecordData.obj.hot_topics[index].images, function () {

                                $("#cityHotHashtagsCard .back #HashtagImg").append("<div class='nailthumb-container col-md-4'><img src='" + this.img_url + "' /></div>");
                                //                                if (IsValidImageUrl("" + this)) {}
                            });

                            $('#cityHotHashtagsCard .back #HashtagImg .nailthumb-container').nailthumb({
                                width: 80,
                                height: 80,
                                title:'Click to see large pic >w<',
                                titleWhen:'hover'
                            });
                            
                            


                            $("#cityHotHashtagsCard").flip('toggle');
                        });
                    }





                    //-------------
                    //- END OF DISPLAY HOT HASHTAGS -
                    //-------------


                } else {
                    alert(JSON.stringify(data));
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error in message/getonestatisticrecord: " + JSON.stringify(data));
            }
        });
    } else {
        alert("Auth error!");
    }



}






function getCityRanking(date) {
    if (user_id != null && logintoken != null) {
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "message/getregionranks",
            data: {
                userID: user_id,
                token: logintoken,
                //                date: getFormattedDate(date),
                date: "2016_01_24"
            },
            dataType: "json",
            beforeSend: function () {
                console.log("message/getregionranks......");
                $("#CityRanking-col").html("<div class='overlay' style='height:500px;'><i class='fa fa-refresh fa-spin'></i></div>");
            },
            success: function (data, textStatus) {
                if (data.code == 200) {
                    console.log("Success in message/getregionranks: " + JSON.stringify(data));


                    //-------------
                    //- FORM TABLE HTML -
                    //-------------
                    $("#CityRanking-col").html("<div id='owl-demo' class='owl-carousel owl-theme'></div><div class='customNavigation'><a class='btn prev'>Previous</a><a class='btn next'>Next</a></div>");

                    var cityRankingData = data.obj;
                    var numTables = Math.floor(cityRankingData.length / numPerTable) + 1;
                    var tableHTMLArrray = new Array(numTables);
                    var tableHTML = "";
                    for (var i = 0; i < numTables; i++) {
                        tableHTMLArrray[i] = "<div class='item'><div class='table-responsive'><table class='table no-margin cityRanking'><thead><tr><th>Select</th><th>Rank</th><th>City</th><th>Value</th><th>Score</th></tr></thead><tbody>";
                    }

                    $.each(cityRankingData, function () {
                        getregionranksData["" + this.regID] = new Array();
                        getregionranksData["" + this.regID]["date"] = this.date;
                        getregionranksData["" + this.regID]["regName"] = this.regName;
                        getregionranksData["" + this.regID]["rank"] = this.rank;
                        getregionranksData["" + this.regID]["record_key"] = this.record_key;
                        getregionranksData["" + this.regID]["record_id"] = this.record_id;
                        getregionranksData["" + this.regID]["positive_num"] = this.positive_num;
                        getregionranksData["" + this.regID]["negative_num"] = this.negative_num;
                        getregionranksData["" + this.regID]["neutral_num"] = this.neutral_num;
                        getregionranksData["" + this.regID]["score"] = this.score;
                        getregionranksData["" + this.regID]["pulse_value"] = this.pulse_value;
                        getregionranksData["" + this.regID]["timestamp"] = this.timestamp;

                        if (this.pulse.pulse_value != 0.0) {
                            tableHTMLArrray[Math.floor((this.rank - 1) / numPerTable)] += "<tr placeID='" + this.regID + "' recordID='" + this.record_id + "'><td><input type='checkbox' class='minimal'></td><td>" + this.rank + "</td><td><a class='cityName'>" + this.regName.toUpperCase() + "</a></td><td><span class='label label-success'>" + this.pulse.pulse_value.toFixed(4) + "</span></td><td><span class='label label-success'>" + this.pulse.score + "</span></td></tr>";
                        } else {
                            tableHTMLArrray[Math.floor((this.rank - 1) / numPerTable)] += "<tr placeID='" + this.regID + "' recordID='" + this.record_id + "'><td><input type='checkbox' class='minimal'></td><td>" + this.rank + "</td><td><a class='cityName'>" + this.regName.toUpperCase() + "</a></td><td><span class='label label-info'>undefined</span></td><td><span class='label label-success'>" + this.pulse.score + "</span></td></tr>";
                        }
                    });

                    for (var j = 0; j < numTables; j++) {
                        tableHTMLArrray[j] += "</tbody></table></div></div>";
                        tableHTML += tableHTMLArrray[j];
                    }

                    $("#owl-demo").append(tableHTML);

                    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
                        checkboxClass: 'icheckbox_flat-red',
                        radioClass: 'iradio_flat-red'
                    });
                    //-------------
                    //- END OF FORM TABLE HTML -
                    //-------------


                    //-------------
                    //- OWL -
                    //-------------
                    var owl = $("#owl-demo");
                    owl.owlCarousel({
                        items: 3, //10 items above 1000px browser width
                        itemsDesktop: [1200, 3], //5 items between 1000px and 901px
                        itemsDesktopSmall: [992, 2], // betweem 900px and 601px
                        itemsTablet: [768, 2], //2 items between 600 and 0
                        itemsMobile: 1 // itemsMobile disabled - inherit from itemsTablet option
                    });
                    // Custom Navigation Events
                    $(".next").click(function () {
                        owl.trigger('owl.next');
                    });
                    $(".prev").click(function () {
                        owl.trigger('owl.prev');
                    });
                    //-------------
                    //- END OF OWL -
                    //-------------


                    //                    $("#CityRanking-col tbody tr").click(function () {
                    //                        var place_id = $(this).attr("placeID");
                    //                        if ($(this).hasClass("selected")) {
                    //                            $(this).removeClass("selected");
                    //                            //                             alert(place_id+" remove selected!");
                    //                        } else {
                    //                            $(this).addClass("selected");
                    //                            //                             alert(place_id+" add selected!");
                    //                        }
                    //                    });


                    //-------------
                    //- OPEN MULTIPLE ANALYSIS MODAL -
                    //-------------

                    $("#mutipleAnalysis").click(function () {
                        var length = 0;
                        var placeIDs = new Array();
                        $.each($("#CityRanking-col tbody tr"), function () {
                            if ($(this).find(".icheckbox_flat-red").hasClass("checked")) {
                                length++;
                                placeIDs.push($(this).attr("placeID"));
                            }
                        });
                        if (length == 0) {
                            toastr.error("OOps~~ You haven't selected any city!QvQ \n Please select 2~" + multiThreshold + " cities for comparision. =3=");

                        } else if (length == 1) {
                            toastr.error("Whooo~~ You have selected only one city！╭(╯^╰)╮ \n Please select 2~" + multiThreshold + " cities for comparision. =3=");
                        } else if (length > multiThreshold) {
                            toastr.error("Ahhhh~~ You have selected too many cities that is not beautiful to show on the chart so far!> < \n Please select 2~" + multiThreshold + " cities for comparision. =3=");
                        } else {

                            var startDate = $('.input-daterange').datepicker().data('datepicker').pickers[0].getDate();
                            var endDate = $('.input-daterange').datepicker().data('datepicker').pickers[1].getDate();
                            var days = (endDate - startDate) / (60 * 60 * 24 * 1000) + 1;
                            var colorIndex = 0;

                            $.ajax({
                                type: "GET",
                                crossDomain: true,
                                url: serverURL + "message/gethistoryinfo",
                                data: {
                                    userID: user_id,
                                    token: logintoken,
                                    place_ids: placeIDs.toString(),
                                    date_start: getFormattedDate(startDate),
                                    date_end: getFormattedDate(endDate)
                                },
                                dataType: "json",
                                beforeSend: function () {
                                    console.log("message/gethistoryinfo......");
                                    //                                        $("#CityEmotionChange-col").html("<div class='overlay' style='height:200px;'><i class='fa fa-refresh fa-spin'></i></div>");
                                },
                                success: function (data, textStatus) {
                                    //console.log(JSON.stringify(data));
                                    if (data.code == 200) {
                                        console.log("Success in message/gethistoryinfo: " + JSON.stringify(data));
                                        //                                            $("#CityEmotionChange-col .overlay").remove();
                                        $("#cityCompareModal").modal().on("shown.bs.modal", function () {

                                        });

                                        var dataArray = new Array(days);
                                        var graphArray = new Array();
                                        for (var i = 0; i < days; i++) {
                                            dataArray[i] = new Array();
                                            dataArray[i]["date"] = startDate.addDays(i);
                                        }

                                        $.each(placeIDs, function () {
                                            $.each(data.obj[this], function () {
                                                var index = ((new Date(this.date)).getTime() + ((new Date(this.date)).getTimezoneOffset() * 60 * 1000) - startDate.getTime()) / (60 * 60 * 24 * 1000);
                                                //                                                alert(index + "," + this.regName + "," + this.pulse.score);
                                                dataArray[index]["" + this.regName] = this.pulse.pulse_value.toFixed(4);
                                            });

                                            var graph = new AmCharts.AmGraph();
                                            graph.id = "g" + data.obj[this][0].regName;
                                            graph.valueAxis = "v1";
                                            graph.bullet = "round";
                                            graph.bulletBorderAlpha = 1;
                                            graph.bulletColor = "#FFFFFF";
                                            graph.bulletSize = 5;
                                            graph.hideBulletsCount = 50;
                                            graph.lineThickness = 2;
                                            graph.lineColor = colorBox[colorIndex++];
                                            graph.type = "smoothedLine";
                                            graph.title = data.obj[this][0].regName.toUpperCase();
                                            graph.useLineColorForBulletBorder = true;
                                            graph.valueField = "" + data.obj[this][0].regName;
                                            graph.balloonText = "[[title]]<br/><b style='font-size: 130%'>[[value]]</b>";
                                            graph.animationPlayed = true;
                                            graphArray.push(graph);
                                        });



                                        var compareChart = AmCharts.makeChart("CityCompareChart", {
                                            "type": "serial",
                                            "theme": "light",
                                            "dataDateFormat": "YYYY-MM-DD",
                                            "precision": -1,
                                            "valueAxes": [{
                                                "id": "v1",
                                                "title": "Score",
                                                "gridAlpha": 0,
                                                "position": "left",
                                                "autoGridCount": false
                                            }],
                                            "chartScrollbar": {
                                                "graph": "g5",
                                                "oppositeAxis": false,
                                                "offset": 30,
                                                "scrollbarHeight": 50,
                                                "backgroundAlpha": 0,
                                                "selectedBackgroundAlpha": 0.1,
                                                "selectedBackgroundColor": "#888888",
                                                "graphFillAlpha": 0,
                                                "graphLineAlpha": 0.5,
                                                "selectedGraphFillAlpha": 0,
                                                "selectedGraphLineAlpha": 1,
                                                "autoGridCount": true,
                                                "color": "#AAAAAA"
                                            },
                                            "chartCursor": {
                                                "pan": true,
                                                "valueLineEnabled": true,
                                                "valueLineBalloonEnabled": true,
                                                "cursorAlpha": 0,
                                                "valueLineAlpha": 0.2
                                            },
                                            "categoryField": "date",
                                            "categoryAxis": {
                                                "parseDates": true,
                                                "dashLength": 1,
                                                "minorGridEnabled": true
                                            },
                                            "legend": {
                                                "useGraphSettings": true,
                                                "position": "top"
                                            },
                                            "balloon": {
                                                "borderThickness": 1,
                                                "shadowAlpha": 0
                                            },
                                            "export": {
                                                "enabled": true
                                            },
                                            "dataProvider": dataArray
                                        });

                                        $.each(graphArray, function () {
                                            compareChart.addGraph(this);
                                        });





                                    } else {
                                        alert(JSON.stringify(data));
                                        //                                        if (data.code == 400) {
                                        //                                            window.location.href = "login.html?message=Please Login Again.";
                                        //                                        }
                                    }
                                },
                                error: function (jqXHR, textStatus, errorThrown) {
                                    console.log("Error in message/gethistoryinfo: " + JSON.stringify(data));

                                }
                            });


                        }
                    });
                    //-------------
                    //- OPEN MULTIPLE ANALYSIS MODAL -
                    //-------------


                    //-------------
                    //- OPEN CITY INFO MODAL -
                    //-------------
                    $(".cityName").click(function () {
                        $("#cityHotHashtagsCard").flip(false);
                        var city = $(this).html().toLowerCase();
                        var place_id = $(this).parent().parent().attr("placeID");
                        var record_id = $(this).parent().parent().attr("recordID");
                        dataAnalysis(place_id);
                        drawCityBoundaryAndHashtags(record_id);
                        var modalHTML = "";
                        $("#cityInfoModalTitle").html($(this).html());
                        //        $("#cityInfoModalBody").html(modalHTML);
                        //                        $('#cityInfoModal').modal('show');
                        $("#cityInfoModal").modal().on("shown.bs.modal", function () {
                            google.maps.event.trigger(map, 'resize');
                        });
                    });
                    //-------------
                    //- END OF OPEN CITY INFO MODAL -
                    //-------------


                } else {
                    alert(JSON.stringify(data));
                    window.location.href = "login.html?message=Please Login Again.";
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error in message/getregionranks: " + JSON.stringify(data));
            }
        });
    } else {
        alert("Auth error!");
    }

}


Date.prototype.addDays = function (days) {
    var dat = new Date(this.valueOf());
    dat.setDate(dat.getDate() + days);
    return dat;
}




$(function () {





    $('.input-daterange').datepicker({
        startDate: "01/01/2010",
        endDate: "today",
        todayBtn: true,
        clearBtn: true,
        todayHighlight: true,
        datesDisabled: ['01/06/2015', '01/21/2015']
    });
    //    $('.input-daterange').datepicker().data('datepicker').pickers[1].setDate("today");
    //    var today = $('.input-daterange').datepicker().data('datepicker').pickers[1].getDate();
    //    $('.input-daterange').datepicker().data('datepicker').pickers[0].setDate(today.addDays(-7));
    $('.input-daterange').datepicker().data('datepicker').pickers[0].setDate(new Date(2016, 1, 15));
    $('.input-daterange').datepicker().data('datepicker').pickers[1].setDate(new Date(2016, 1, 23));


    $("#cityHotHashtagsCard").flip({
        trigger: 'manual'
    });

    $(".back").click(function () {
        //                        alert("ARASHIIIIIIIIIIIIIIIII!!!!!!");
        $("#cityHotHashtagsCard").flip('toggle');
    });


    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": false,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "onclick": null,
        "showDuration": "300",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };

    $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4&language=en&libraries=geometry,places")
        .done(function () {
            console.log("maps.googleapis.com Loaded.");
            map = new google.maps.Map(document.getElementById('map'), {
                center: new google.maps.LatLng(45.360332, -75.772822),
                zoom: 10,
                mapTypeControl: false
            });
        }).fail(function () {
            console.log("Change to the Chinese Server......");
            $.getScript("http://ditu.google.cn/maps/api/js?sensor=false&language=en&libraries=geometry,places&key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4").done(function () {
                console.log("ditu.google.cn Loaded.");
                map = new google.maps.Map(document.getElementById('map'), {
                    center: new google.maps.LatLng(45.360332, -75.772822),
                    zoom: 10,
                    mapTypeControl: false
                });
            });
        });



    getCityRanking(new Date());


});