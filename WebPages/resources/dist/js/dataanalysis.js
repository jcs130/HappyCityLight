var apiToken = "ArashiArashiFordream";
var numPerTable = 10;
var topicNum = 10;
var multiThreshold = 6;
var regionBoundary = null;
var getOneStatisticRecordData = null;
var getregionranksData = new Array();
var colorBox = ["#da8b8b", "#8bbdda", "#c6da8b", "#F7C242", "#8B81C3", "#8bdac6", "#B4A582", "#E3916E"];
var hexBoundaryObj = null;


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

function toggleSelectDate() {
    $("#selectDate").toggle();
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
                    var map = new google.maps.Map(document.getElementById('map'), {
                        center: new google.maps.LatLng(45.360332, -75.772822),
                        zoom: 10,
                        mapTypeControl: false
                    });

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


                        var hom = new HexOnMap(map);
                        hexBoundaryObj = new HexBoundary(hom, data.obj.regInfo.areas);
                        hexBoundaryObj.createHexBoundary();

                        //redraw hex grid when map zoom changes
                        google.maps.event.addListener(map, 'zoom_changed', function () {
                            console.log("Googlemap zoom changed.");
                            hexBoundaryObj.updateHexBoundary();
                        });
                        map.fitBounds(new google.maps.LatLngBounds(new google.maps.LatLng(latMin, lngMin), new google.maps.LatLng(latMax, lngMax)));


                    });
                    //-------------
                    //- END OF DRAW CITY BOUNDARY -
                    //-------------


                    //-------------
                    //- DISPLAY HOT HASHTAGS -
                    //-------------
                    $("#hothashtag-title").html("Hot Hashtags - [" + data.obj.local_date + "]");
                    $("#cityHotHashtags .nav-pills").html("");

                    if (data.obj.hot_topics != []) {
                        var vallidDataArray = new Array();
                        $.each(data.obj.hot_topics, function () {
                            if (!((this.pulse.positive_num == 0) && (this.pulse.neutral_num == 0) && (this.pulse.negative_num == 0))) {
                                vallidDataArray.push(this);
                            }
                        });

                        var topicLength = (vallidDataArray.length >= topicNum) ? topicNum : vallidDataArray.length;
                        for (var i = 0; i < topicLength; i++) {
                            //        var topicItemHTML = "<li class='item'><div class='product-info'><a class='product-title showTopicDetail'>" + data.obj.hot_topics[i].text + "<span class='label label-warning pull-right'>" + data.obj.hot_topics[i].pulse.pulse_value.toFixed(4) + "</span></a></div></li>";
                            //                            if (data.obj.hot_topics[i].text != null) {
                            //                                var topicItemHTML = "<li hashtagIndex='" + i + "'><a href='#'>#" + data.obj.hot_topics[i].text + "<span class='pull-right text-red'>" + data.obj.hot_topics[i].pulse.pulse_value.toFixed(4) + "</span></a></li>";
                            if (vallidDataArray[i].text != null) {
                                var topicItemHTML = "<li hashtagIndex='" + i + "'><a class='hashtag-word' href='#'>#" + vallidDataArray[i].text + "<span class='label label-number'>" + vallidDataArray[i].count + " msg</span><span class='pull-right label label-score'>" + vallidDataArray[i].pulse.score.toFixed(0) + "</span></a></li>";
                                $("#cityHotHashtags .nav-pills").append(topicItemHTML);
                            }
                        }


                        $(".front li").click(function () {
                            var index = $(this).attr('hashtagIndex');
                            $("#cityHotHashtagsCard .back #HashtagChart").html("");
                            $("#cityHotHashtagsCard .back #HashtagImg").html("");
                            $("#cityHotHashtagsCard .back #HashtagInfo").html("<h2 style='text-align: center;'>#" + vallidDataArray[index].text + "</h2>");

                            var dataTable = new Array(3);
                            dataTable[0] = new Array(3);
                            dataTable[1] = new Array(3);
                            dataTable[2] = new Array(3);
                            dataTable[0]["title"] = 'Posotive';
                            dataTable[0]["color"] = '#91AD70';
                            dataTable[0]["value"] = vallidDataArray[index].pulse.positive_num;
                            dataTable[1]["title"] = 'Neutral';
                            dataTable[1]["color"] = '#FAD689';
                            dataTable[1]["value"] = vallidDataArray[index].pulse.neutral_num;
                            dataTable[2]["title"] = 'Negative';
                            dataTable[2]["color"] = '#ED784A';
                            dataTable[2]["value"] = vallidDataArray[index].pulse.negative_num;


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
                                "startDuration": 0,
                                "startEffect": "elastic",
                                "labelText": "[[title]]",
                                "colorField": "color"
                            });



                            var imgArray = new Array();
                            $.each(vallidDataArray[index].images, function () {
                                //                                if (IsValidImageUrl("" + this.img_url)) {}
                                if ($.inArray(this.img_url, imgArray) < 0) {
                                    $("#cityHotHashtagsCard .back #HashtagImg").append("<a class='nailthumb-container col-md-4 col-xs-4' href='" + this.img_url + "'><img src='" + this.img_url + "' /></a>");
                                    imgArray.push(this.img_url);
                                }
                            });
                            $('#cityHotHashtagsCard .back #HashtagImg .nailthumb-container').nailthumb({
                                width: 80,
                                height: 80,
                                title: 'Click to see large pic >w<',
                                titleWhen: 'hover'
                            });

                            $(".yoxview").yoxview();
                            $("#cityHotHashtagsCard .front .row").hide();
                            $("#cityHotHashtagsCard .back .row").show();
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





function dataAnalysis(place_id, startDate, endDate) {
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
                $("#emotionchange-title").html("Emotion Change - [" + getFormattedDate(startDate) + "] ~ [" + getFormattedDate(endDate) + "]");
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
                    dataArray[i]["score"] = this.pulse.score.toFixed(0);
                    i++;
                });

                var cityInfoChart = AmCharts.makeChart("CityInfoChart", {
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

                cityInfoChart.addListener("rollOverGraphItem", function (e) {
//                    console.log((e.item.serialDataItem.axes["v2"].graphs["g5"].values.value) / ((e.item.serialDataItem.axes["v1"].graphs["g1"].values.value) + (e.item.serialDataItem.axes["v1"].graphs["g3"].values.value)));
                    var value = (e.item.serialDataItem.axes["v2"].graphs["g5"].values.value) / ((e.item.serialDataItem.axes["v1"].graphs["g1"].values.value) + (e.item.serialDataItem.axes["v1"].graphs["g3"].values.value));
                    if (hexBoundaryObj != null) {
                        hexBoundaryObj.changeHexBoundaryColorFromValue(value);
                    }
                });


            } else {
                alert(JSON.stringify(data));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Error in message/gethistoryinfo: " + JSON.stringify(data));

        }
    });
}




function multipleCompare(placeIDs, startDate, endDate) {
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
            console.log("startDate:" + getFormattedDate(startDate) + "\nendDate:" + getFormattedDate(endDate));
            console.log("message/gethistoryinfo......");
            $("#cityCompareModal").modal().on("shown.bs.modal", function () {
                $("#yoxview_popupWrap").css('z-index', 50);
            });
            $("#CityCompareChart").html("<div class='overlay' style='height:300px;'><i class='fa fa-refresh fa-spin'></i></div>");
        },
        success: function (data, textStatus) {
            //console.log(JSON.stringify(data));
            if (data.code == 200) {
                console.log("Success in message/gethistoryinfo: " + JSON.stringify(data));
                $("#CityCompareChart").find(".overlay").remove();

                var days = (endDate - startDate) / (60 * 60 * 24 * 1000) + 1;
                var dataArray = new Array(days);
                var graphArray = new Array();
                for (var i = 0; i < days; i++) {
                    dataArray[i] = new Array();
                    dataArray[i]["date"] = startDate.addDays(i);
                }

                $.each(placeIDs, function () {
                    $.each(data.obj[this], function () {
                        var index = ((new Date(this.date)).getTime() + ((new Date(this.date)).getTimezoneOffset() * 60 * 1000) - startDate.getTime()) / (60 * 60 * 24 * 1000);
                        console.log(index + "," + this.regName + "," + this.pulse.score.toFixed(0) + "," + this.pulse.pulse_value.toFixed(4));
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
                        "title": "Pulse Value",
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

                $.each(graphArray, function () {
                    compareChart.addGraph(this);
                });


            } else {
                alert(JSON.stringify(data));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Error in message/gethistoryinfo: " + JSON.stringify(data));

        }
    });
}



function getCityRanking(date) {
    if (user_id != null && logintoken != null) {
        var previousHTML;
        date.setHours(0, 0, 0, 0);
        var cityRankDate = date;
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "message/getregionranks",
            data: {
                userID: user_id,
                token: logintoken,
                date: getFormattedDate(date),
                //                date: "2016_01_24"
            },
            dataType: "json",
            beforeSend: function () {
                console.log("message/getregionranks......");
                previousHTML = $("#CityRanking-col").html();
                $("#CityRanking-col").html("<div class='overlay' style='height:500px;'><i class='fa fa-refresh fa-spin'></i></div>");
            },
            success: function (data, textStatus) {
                if (data.code == 404) {
                    $("#CityRanking-col").html(previousHTML);
                    toastr.info("Sorry QAQ ....... The city rank you request is not ready yet > < ......");
                } else if (data.code == 200) {
                    console.log("Success in message/getregionranks: " + JSON.stringify(data));

                    //-------------
                    //- FORM TABLE HTML -
                    //-------------
                    $("#CityRanking-box .box-title").html("Listened City Score Ranking - " + data.obj[0].date);

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
                            tableHTMLArrray[Math.floor((this.rank - 1) / numPerTable)] += "<tr placeID='" + this.regID + "' recordID='" + this.record_id + "'><td><input type='checkbox' class='minimal'></td><td>" + this.rank + "</td><td><a class='cityName'>" + this.regName.toUpperCase() + "</a></td><td><span class='label label-success'>" + this.pulse.pulse_value.toFixed(4) + "</span></td><td><span class='label label-success'>" + this.pulse.score.toFixed(0) + "</span></td></tr>";
                        } else {
                            tableHTMLArrray[Math.floor((this.rank - 1) / numPerTable)] += "<tr placeID='" + this.regID + "' recordID='" + this.record_id + "'><td><input type='checkbox' class='minimal multiplecity'></td><td>" + this.rank + "</td><td><a class='cityName'>" + this.regName.toUpperCase() + "</a></td><td><span class='label label-info'>undefined</span></td><td><span class='label label-success'>" + this.pulse.score.toFixed(0) + "</span></td></tr>";
                        }
                    });

                    for (var j = 0; j < numTables; j++) {
                        tableHTMLArrray[j] += "</tbody></table></div></div>";
                        tableHTML += tableHTMLArrray[j];
                    }

                    $("#CityRanking-col").html("<div id='owl-demo' class='owl-carousel owl-theme'></div>");
                    $("#owl-demo").append(tableHTML);

                    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
                        checkboxClass: 'icheckbox_flat-red',
                        radioClass: 'iradio_flat-red'
                    });
                    $('input[type="checkbox"].minimal').on('ifChecked', function (event) {
                        $("#selectedCity").append("<span class='label label-danger' style='margin-right:5px;' placeID='" + $(this).parent().parent().parent().attr("placeID") + "'>" + $(this).parent().parent().parent().find(".cityName").html() + "</span>")
                    });
                    $('input[type="checkbox"].minimal').on('ifUnchecked', function (event) {
                        $("#selectedCity").find(".label[placeID='" + $(this).parent().parent().parent().attr("placeID") + "']").remove();
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
                    //-------------
                    //- END OF OWL -
                    //-------------


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
                            var endDate = cityRankDate;
                            var startDate = cityRankDate.addDays(-6);
                            multipleCompare(placeIDs, startDate, endDate);
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
                        var endDate = cityRankDate;
                        var startDate = cityRankDate.addDays(-6);
                        dataAnalysis(place_id, startDate, endDate);
                        drawCityBoundaryAndHashtags(record_id);
                        var modalHTML = "";
                        $("#cityInfoModalTitle").html($(this).html());
                        $("#cityInfoModal").modal().on("shown.bs.modal", function () {

                        });
                    });
                    //-------------
                    //- END OF OPEN CITY INFO MODAL -
                    //-------------


                } else {
                    alert(JSON.stringify(data));
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
    //-------------
    //- DATE PICKER FOR CITY RANK -
    //-------------
    $('#cityRankDate').datepicker({
        startDate: "01/01/2015",
        endDate: "today",
        todayHighlight: false,
        todayBtn: true,

    });
    $('#cityRankDate').datepicker('setDate', new Date());
    $("#getCityRank").click(function () {
        var date = $('#cityRankDate').datepicker('getDate');
        getCityRanking(date);
    });
    //-------------
    //- END OF DATE PICKER FOR CITY RANK -
    //-------------


    //-------------
    //- FLIP INIT -
    //-------------
    $("#cityHotHashtagsCard").flip({
        trigger: 'manual'
    });
    $(".back #HashtagInfo").click(function () {
        $("#cityHotHashtagsCard .front .row").show();
        $("#cityHotHashtagsCard .back .row").hide();
        $("#cityHotHashtagsCard").flip(false);
    });
    $('#cityInfoModal').on('hidden.bs.modal', function () {
        $("#cityHotHashtagsCard .front .row").show();
        $("#cityHotHashtagsCard .back .row").hide();
        $("#cityHotHashtagsCard").flip(false);
    });
    //-------------
    //- END OF FLIP INIT -
    //-------------


    //-------------
    //- TOASTR INIT -
    //-------------
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": false,
        //        "progressBar": true,
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
    //-------------
    //- END OF TOASTR INIT -
    //-------------


    //-------------
    //- LOAD GOOGLEMAP -
    //-------------
    $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4&language=en&libraries=geometry,places")
        .done(function () {
            console.log("maps.googleapis.com Loaded.");
        }).fail(function () {
            console.log("Change to the Chinese Server......");
            $.getScript("http://ditu.google.cn/maps/api/js?sensor=false&language=en&libraries=geometry,places&key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4").done(function () {
                console.log("ditu.google.cn Loaded.");
            });
        });
    //-------------
    //- END OF LOAD GOOGLEMAP -
    //-------------


    //-------------
    //- SET DEFAULT DATE FOR CITY RANK TO YESTERDAY -
    //-------------
    var today = new Date();
    getCityRanking(today.addDays(-17));
    //    getCityRanking(today.addDays(-1));



});