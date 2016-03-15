var apiToken = "ArashiArashiFordream";
var googlemapLoaded = 0;
var numPerTable = 10;
var topicNum = 7;
var imgNum = 100;
var multiThreshold = 6;
var cityRankDate;
var currentCityID = null;
var currentCityIDs = null;
var getfullhistoryinfoData = null;
var cityInfoStartDate = null;
var cityInfoEndDate = null;
var map;
var regionBoundary = null;
var hexBoundaryObj = null;
var colorBox = ["#da8b8b", "#8bbdda", "#c6da8b", "#F7C242", "#8B81C3", "#8bdac6", "#B4A582", "#E3916E"];
var weekdayArray = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
var monthArray = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

//实现登陆成功后的初始化数据操作
function checkin_afterChencin() {

}

function drawRegionBoundary(areas) {
    var cityBoundaryPaths = new Array();
    $.each(areas, function () {
        var cityPaths = [new google.maps.LatLng(this.north, this.east), new google.maps.LatLng(this.south, this.east), new google.maps.LatLng(this.south, this.west), new google.maps.LatLng(this.north, this.west)];
        cityBoundaryPaths.push(cityPaths);
    });
    var regionBoundary = mergeRegions(cityBoundaryPaths);
    return regionBoundary;
}


function getFormattedDate(date) {
    var year = date.getFullYear();
    var month = (1 + date.getMonth()).toString();
    month = month.length > 1 ? month : '0' + month;
    var day = date.getDate().toString();
    day = day.length > 1 ? day : '0' + day;
    return year + '-' + month + '-' + day;
}

function getDisplayFormattedDate(date) {
    var year = date.getFullYear();
    var month = date.getMonth();
    var date = date.getDate();
    //    var day = date.getDay();
    return monthArray[month] + ' ' + date + ', ' + year;
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

Date.prototype.addDays = function (days) {
    var dat = new Date(this.valueOf());
    dat.setDate(dat.getDate() + days);
    return dat;
}


function getHotHashtagRanking(Data, startDate, endDate) {
    var hashTagRankArray = [];
    $.each(Data, function () {
        if ((this != null) && (this != [])) {
            if ((new Date(this.local_date) >= startDate.addDays(-1)) && (new Date(this.local_date) <= endDate)) {
                //            console.log("getHotHashtagRanking: GATHERING HASHTAG INFO WITHIN " + this.local_date);
                var localDate = this.local_date;
                $.each(this.hot_topics, function () {
                    var textID = this.text;
                    var i;
                    var textIDInArray = $.grep(hashTagRankArray, function (e, index) {
                        if (e.text == textID) {
                            i = index;
                        }
                        return e.text == textID;
                    });
                    if (textIDInArray.length == 1) {
                        //                    console.log("getHotHashtagRanking: OBJ EXISTS FOR " + textID + " index=" + i);
                        hashTagRankArray[i].positive_num += this.pulse.positive_num;
                        hashTagRankArray[i].negative_num += this.pulse.negative_num;
                        hashTagRankArray[i].neutral_num += this.pulse.neutral_num;
                        hashTagRankArray[i].score += this.pulse.score;
                        hashTagRankArray[i].count += this.count;
                        if (this.images != []) {
                            $.each(this.images, function () {
                                //                        if () {
                                hashTagRankArray[i].images.push(this);
                                //                        }
                            });
                        }
                        var recordItem = this;
                        recordItem.local_date = localDate;
                        hashTagRankArray[i].record.push(recordItem);
                    } else if (textIDInArray.length == 0) {
                        var hashTagRankArrayItem = {};
                        hashTagRankArrayItem.text = this.text;
                        hashTagRankArrayItem.positive_num = this.pulse.positive_num;
                        hashTagRankArrayItem.negative_num = this.pulse.negative_num;
                        hashTagRankArrayItem.neutral_num = this.pulse.neutral_num;
                        hashTagRankArrayItem.score = this.pulse.score;
                        hashTagRankArrayItem.count = this.count;
                        hashTagRankArrayItem.images = new Array();
                        if (this.images != []) {
                            $.each(this.images, function () {
                                //                        if () {
                                hashTagRankArrayItem.images.push(this);
                                //                        }
                            });
                        }
                        hashTagRankArrayItem.record = new Array();
                        var recordItem = this;
                        recordItem.local_date = localDate;
                        hashTagRankArrayItem.record.push(recordItem);
                        hashTagRankArray.push(hashTagRankArrayItem);
                        //                    console.log("getHotHashtagRanking: CREATE NEW OBJ FOR " + textID + " index=" + $.inArray(hashTagRankArrayItem, hashTagRankArray));
                    } else {
                        console.error("ERROR IN getHotHashtagRanking.");
                    }
                });
            }
        }
    });


    var hashTagRankArraySortByCount = hashTagRankArray.sort(function (hashtagA, hashtagB) {
        //        console.log("Sorting " + hashtagA.text + " and " + hashtagB.text);
        return hashtagB.count - hashtagA.count;
    });

    return hashTagRankArraySortByCount;
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
            $("#CityCompareChart").html("<div class='overlay' style='height:300px;'><i class='fa fa-refresh fa-spin'></i></div>");
        },
        success: function (data, textStatus) {
            //console.log(JSON.stringify(data));
            if (data.code == 200) {
                //                console.log("Success in message/gethistoryinfo: " + JSON.stringify(data));
                console.log("Success in message/gethistoryinfo: ");
                $("#CityCompareChart").find(".overlay").remove();
                $("#currentCompareDateRange").html("<i class='fa fa-calendar'></i><b>" + getDisplayFormattedDate(startDate) + " - " + getDisplayFormattedDate(endDate) + "</b>");

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
                        //                        console.log(index + "," + this.regName + "," + this.pulse.score.toFixed(0) + "," + this.pulse.pulse_value.toFixed(4));
                        dataArray[index][this.regName] = this.pulse.pulse_value.toFixed(4);
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
                    graph.valueField = data.obj[this][0].regName;
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
                        "title": "Sentiment Value",
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
                        "valueLineAlpha": 0.2,
                        categoryBalloonDateFormat: 'MMM DD YYYY EEE'
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



function singleCityAnalysis(place_id, startDate, endDate) {
    $.ajax({
        type: "GET",
        crossDomain: true,
        url: serverURL + "message/getfullhistoryinfo",
        data: {
            userID: user_id,
            token: logintoken,
            place_ids: place_id,
            date_start: getFormattedDate(startDate),
            date_end: getFormattedDate(endDate)
        },
        dataType: "json",
        beforeSend: function () {
            console.log("message/getfullhistoryinfo......");
            $("#CityEmotionChange-col").html("<div class='overlay' style='height:200px;'><i class='fa fa-refresh fa-spin'></i></div>");
        },
        success: function (data, textStatus) {
            //console.log(JSON.stringify(data));
            if (data.code == 200) {
                //                console.log("Success in message/getfullhistoryinfo: " + JSON.stringify(data));
                console.log("Success in message/getfullhistoryinfo");
                $("#CityEmotionChange-col .overlay").remove();
                cityInfoStartDate = startDate;
                cityInfoEndDate = endDate;
                $("#currentDateRange").html("<i class='fa fa-calendar'></i><b>" + getDisplayFormattedDate(cityInfoStartDate) + " - " + getDisplayFormattedDate(cityInfoEndDate) + "</b>");

                //-------------
                //- DRAW CITY BOUNDARY -
                //-------------
                var latMax = data.obj[place_id][0].regInfo.areas[0].north;
                var latMin = data.obj[place_id][0].regInfo.areas[0].south;
                var lngMax = data.obj[place_id][0].regInfo.areas[0].east;
                var lngMin = data.obj[place_id][0].regInfo.areas[0].west;

                $.each(data.obj[place_id][0].regInfo.areas, function () {
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

                //                console.log(latMax + "," + lngMax + "  " + latMin + "," + lngMin);

                map.setCenter(new google.maps.LatLng((latMax + latMin) / 2, (lngMax + lngMin) / 2));


                google.maps.event.addListenerOnce(map, "tilesloaded", function () {
                    //                    if (regionBoundary != null) {
                    //                        regionBoundary.setMap(null);
                    //                    }
                    //
                    //                    regionBoundary = new google.maps.Polyline({
                    //                        map: map,
                    //                        strokeColor: "#9F353A",
                    //                        strokeOpacity: 0.6,
                    //                        strokeWeight: 1
                    //                    });


                    //                        if ((data.obj.regInfo.box_points != null) && (data.obj.regInfo.box_points != [])) {
                    //                            console.log("box_points exists.");
                    //
                    //                            regionBoundary.setPath($.parseJSON(data.obj.regInfo.box_points));
                    //
                    //                        } else {

                    //                    regionBoundary = drawRegionBoundary(data.obj[place_id][0].regInfo.areas);
                    //                    regionBoundary.setMap(map);


                    //                            $.ajax({
                    //                                type: "PUT",
                    //                                crossDomain: true,
                    //                                url: serverURL + "collector/updateareabox",
                    //                                data: {
                    //                                    userID: user_id,
                    //                                    token: logintoken,
                    //                                    place_id: data.obj[0].regInfo.regID,
                    //                                    box_points: JSON.stringify(regionBoundary.getPath().getArray())
                    //                                },
                    //                                dataType: "json",
                    //                                success: function (data, textStatus) {
                    //                                    console.log("Update Area Box successfully......");
                    //                                },
                    //                                error: function (jqXHR, textStatus, errorThrown) {
                    //                                    console.log("error: " + JSON.stringify(data));
                    //                                }
                    //                            });
                    //                        }

                    var hom = new HexOnMap(map);
                    hexBoundaryObj = new HexBoundary(hom, data.obj[place_id][0].regInfo.areas);
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
                getfullhistoryinfoData = data.obj[place_id];
                var vallidDataArray = getHotHashtagRanking(getfullhistoryinfoData, startDate, endDate);

                $("#hothashtag-title").html("<b>Hot Hashtags</b>");
                $("#hasgtaglist tbody").html("");
                //                $("#cityHotHashtags .nav-pills").html("");

                var topicLength = (vallidDataArray.length >= topicNum) ? topicNum : vallidDataArray.length;
                for (var i = 0; i < topicLength; i++) {
                    if (vallidDataArray[i].text != null) {
                        //                        var topicItemHTML = "<li hashtagIndex='" + i + "'><a class='hashtag-word' href='#'>#" + vallidDataArray[i].text + "<span class='label label-number'>" + vallidDataArray[i].count + " msg</span><span class='label label-score'>score:" + vallidDataArray[i].score.toFixed(0) + "</span><span class = 'label label-success moreinfo'>More Info</span><span class='btn btn-lg btn-search-google' onclick=window.open('http://google.com/search?q=" + vallidDataArray[i].text + "');><i class='fa fa-google-plus-square'></i></span><span class='btn btn-lg btn-search-twitter' onclick=window.open('https://twitter.com/search?q=" + vallidDataArray[i].text + "');><i class='fa fa-twitter-square'></i></span></a></li>";
                        var twiUrlEncode = encodeURIComponent("#" + vallidDataArray[i].text + " near:\"" + $("#cityInfoModalTitle").html() + "\" within:15mi since:" + getFormattedDate(startDate) + " until:" + getFormattedDate(endDate));
                        var topicItemHTML = "<tr hashtagIndex='" + i + "'><td class='hashtaglist-rank'>" + (i + 1) + "</td><td class='hashtaglist-text moreinfo'>#" + vallidDataArray[i].text + "</td><td class='hashtaglist-msg'>" + vallidDataArray[i].count + "</td><td class='hashtaglist-score'>" + vallidDataArray[i].score.toFixed(0) + "</td><td class='hashtaglist-twitter' onclick=window.open('https://twitter.com/search?f=tweets&vertical=default&q=" + twiUrlEncode + "');><i class='fa fa-twitter'></i></td><td class='hashtaglist-google' onclick=window.open('http://google.com/search?q=" + vallidDataArray[i].text + "');><i class='fa fa-google'></i></td></tr>";
                        $("#hasgtaglist tbody").append(topicItemHTML);
                        //                        $("#cityHotHashtags .nav-pills").append(topicItemHTML);
                    }
                }




                $("#hasgtaglist tbody .moreinfo").click(function () {

                    var index = $(this).parent().attr('hashtagIndex');
                    $("#cityHotHashtagsCard .back #HashtagImg").html("");
                    $("#cityHotHashtagsCard .back #HashtagInfo").html("<h2 style='text-align: center;'>#" + vallidDataArray[index].text + "</h2>");

                    var imgArray = new Array();
                    var k = 0;
                    $.each(vallidDataArray[index].record, function () {
                        $.each(this.images, function () {
                            if (($.inArray(this.img_url, imgArray) < 0) && (k < imgNum)) {
                                $("#cityHotHashtagsCard .back #HashtagImg").append("<a class='nailthumb-container col-md-4 col-xs-4' href='" + this.img_url + "'><img src='" + this.img_url + "' /></a>");
                                imgArray.push(this.img_url);
                                k++;
                            }
                        });
                    });

                    $("#cityHotHashtagsCard .back #HashtagImg .nailthumb-container img").error(function () {
                        $(this).parent().remove();
                    });


                    $('#cityHotHashtagsCard .back #HashtagImg .nailthumb-container').nailthumb({
                        width: 85,
                        height: 85,
                        title: 'Click to see large pic >w<',
                        titleWhen: 'hover'
                    });
                    $(".yoxview").yoxview();

                    $("#emotionchange-title").html("Sentiment Change Chart For #" + vallidDataArray[index].text);
                    drawEmotionChart(vallidDataArray[index].record, "CityInfoChart");

                    $("#cityHotHashtagsCard .front .row").hide();
                    $("#cityHotHashtagsCard .back .row").show();
                    $("#cityHotHashtagsCard").flip('toggle');
                });
                //-------------
                //- END OF DISPLAY HOT HASHTAGS -
                //-------------


                //-------------
                //- EMOTION CHANGE CHART -
                //-------------
                drawEmotionChart(getfullhistoryinfoData, "CityInfoChart");
                $("#emotionchange-title").html("Sentiment Change Chart");
                //-------------
                //- END OF EMOTION CHANGE CHART -
                //-------------

            } else {
                alert(JSON.stringify(data));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Error in message/getfullhistoryinfo: " + JSON.stringify(data));
        }
    });
}



function drawEmotionChart(chartData, chartArea) {

    var length = chartData.length;
    var dataArray = new Array(length);
    var i = 0;
    $.each(chartData, function () {
        dataArray[i] = new Array(6);
        dataArray[i]["date"] = this.local_date;
        dataArray[i]["positive_num"] = this.pulse.positive_num;
        dataArray[i]["neutral_num"] = this.pulse.neutral_num;
        dataArray[i]["negative_num"] = this.pulse.negative_num;
        dataArray[i]["pulse_value"] = this.pulse.pulse_value.toFixed(4);
        dataArray[i]["score"] = this.pulse.score.toFixed(0);
        i++;
    });

    var cityInfoChart = AmCharts.makeChart(chartArea, {
        "type": "serial",
        "theme": "light",
        "dataDateFormat": "YYYY-MM-DD",
        "precision": -1,
        "valueAxes": [{
                "id": "v1",
                "title": "Number of Messages",
                "position": "left",
                "autoGridCount": false,
                "stackType": "regular",
                  }, {
                "id": "v2",
                "title": "Sentiment Value",
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
                  }, {
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
                  }, {
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
                "title": "Sentiment Value",
                "useLineColorForBulletBorder": true,
                "valueField": "pulse_value",
                "balloonText": "[[title]]<br/><b style='font-size: 130%'>[[value]]</b>",
                animationPlayed: true
                  }],
        "chartScrollbar": {
            "graph": "g5",
            "oppositeAxis": false,
            "offset": 23,
            "scrollbarHeight": 30,
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
            "valueLineAlpha": 0.2,
            categoryBalloonDateFormat: 'MMM DD YYYY EEE'
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

    var mapAnimation = setInterval(function () {
        //        console.log("CDP.dataanalysis.js.drawEmotionChart:Detecting hexBoundaryObj......");
        if (hexBoundaryObj != null) {
            //            console.log("CDP.dataanalysis.js.drawEmotionChart:Find hexBoundaryObj......");
            hexBoundaryObj.playColorAnimation(dataArray, 0, 1400);
            clearInterval(mapAnimation);
        }
    }, 100);

    cityInfoChart.addListener("rollOverGraphItem", function (e) {
        //                    console.log((e.item.serialDataItem.axes["v2"].graphs["g5"].values.value) / ((e.item.serialDataItem.axes["v1"].graphs["g1"].values.value) + (e.item.serialDataItem.axes["v1"].graphs["g3"].values.value)));
        //       alert(e.index);
        //        console.log("cityInfoChart.rollOverGraphItem");
        var value = e.item.serialDataItem.axes["v2"].graphs["g5"].values.value;
        if (hexBoundaryObj != null) {
            hexBoundaryObj.stopColorAnimation();
            hexBoundaryObj.changeHexBoundaryColorFromValue(value);
        }
    });
    cityInfoChart.addListener("rollOutGraphItem", function (e) {
        if (hexBoundaryObj != null) {
            hexBoundaryObj.playColorAnimation(dataArray, e.index, 1400);
        }
    });

}



function getCityRanking(date) {
    if (user_id != null && logintoken != null) {
        var previousHTML;
        date.setHours(0, 0, 0, 0);
        $.ajax({
            type: "GET",
            crossDomain: true,
            url: serverURL + "message/getregionranks",
            data: {
                userID: user_id,
                token: logintoken,
                date: getFormattedDate(date),
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
                    console.log("Success in message/getregionranks");
                    //                    console.log("Success in message/getregionranks: " + JSON.stringify(data));
                    cityRankDate = date;

                    //-------------
                    //- FORM TABLE HTML -
                    //-------------
                    $("#cityRankDateSpan").html(getDisplayFormattedDate(date));


                    var cityRankingData = data.obj;
                    var numTables = Math.floor(cityRankingData.length / numPerTable) + 1;
                    var tableHTMLArrray = new Array(numTables);
                    var tableHTML = "";
                    for (var i = 0; i < numTables; i++) {
                        tableHTMLArrray[i] = "<div class='item'><div class='table-responsive'><table class='table no-margin cityRanking'><thead><tr><th>Select</th><th>Rank</th><th>City</th><th>Score</th><th>Value</th></tr></thead><tbody>";
                    }

                    $.each(cityRankingData, function () {

                        if ((this.pulse.sum_num - this.pulse.unknown_num) >= 500) {
                            tableHTMLArrray[Math.floor((this.rank - 1) / numPerTable)] += "<tr placeID='" + this.regID + "' recordID='" + this.record_id + "'><td><input type='checkbox' class='minimal'></td><td>" + this.rank + "</td><td><a class='cityName'>" + this.regName.toUpperCase() + "</a></td><td><span class='label label-warning'>" + this.pulse.score.toFixed(0) + "</span></td><td><span class='label label-success'>" + this.pulse.pulse_value.toFixed(4) + "</span></td></tr>";
                        } else {
                            tableHTMLArrray[Math.floor((this.rank - 1) / numPerTable)] += "<tr placeID='" + this.regID + "' recordID='" + this.record_id + "'><td><input type='checkbox' class='minimal multiplecity'></td><td>" + this.rank + "</td><td><a class='cityName'>" + this.regName.toUpperCase() + "</a></td><td><span class='label label-warning'>" + this.pulse.score.toFixed(0) + "</span></td><td>undefined</td></tr>";
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

                    $("#selectedCity").html("<span style='margin-right:5px;font-size:16px;color:#67a8ce;font-weight: bold;'>You have selected: </span>");
                    $('input[type="checkbox"].minimal').on('ifChecked', function (event) {
                        $("#selectedCity").append("<span class='label label-city' style='margin-right:5px;' placeID='" + $(this).parent().parent().parent().attr("placeID") + "'>" + $(this).parent().parent().parent().find(".cityName").html() + "</span>")
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
                        itemsDesktop: [1200, 3], //5 items between 1200 and 992
                        itemsDesktopSmall: [992, 2], // betweem 900px and 400
                        itemsTablet: [400, 1], //2 items between 400 and 0
                        itemsMobile: 1 // itemsMobile disabled - inherit from itemsTablet option
                    });
                    //-------------
                    //- END OF OWL -
                    //-------------


                    //-------------
                    //- OPEN CITY INFO MODAL -
                    //-------------
                    $(".cityName").click(function () {
                        console.log("OPEN CITY INFO MODAL");
                        $("#cityHotHashtagsCard").flip(false);
                        var city = $(this).html().toLowerCase();
                        var place_id = $(this).parent().parent().attr("placeID");
                        var endDate = cityRankDate;
                        var startDate = cityRankDate.addDays(-6);
                        singleCityAnalysis(place_id, startDate, endDate);
                        var modalHTML = "";
                        $("#cityInfoModalTitle").html($(this).html());
                        map = new google.maps.Map(document.getElementById('map'), {
                            center: new google.maps.LatLng(45.360332, -75.772822),
                            zoom: 10,
                            mapTypeControl: false,
                            zoomControl: false,
                            draggable: false,
                            streetViewControl: false,
                            disableDoubleClickZoom: true,
                            signInControl: false,
                            scrollwheel: false
                        });
                        $("#cityInfoModal").modal().on("shown.bs.modal", function () {
                            google.maps.event.trigger(map, 'resize');
                            currentCityID = place_id;
                            $('#cityInfoDateRange .input-daterange').datepicker().data('datepicker').pickers[0].setDates(startDate);
                            $('#cityInfoDateRange .input-daterange').datepicker().data('datepicker').pickers[1].setDates(endDate);
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







$(function () {
    //-------------
    //- LOAD GOOGLEMAP -
    //-------------
    $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4&language=en&libraries=geometry,places")
        .done(function () {
            console.log("maps.googleapis.com Loaded.");
            googlemapLoaded = 1;
        }).fail(function () {
            console.log("Change to the Chinese Server......");
            $.getScript("http://ditu.google.cn/maps/api/js?sensor=false&language=en&libraries=geometry,places&key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4").done(function () {
                console.log("ditu.google.cn Loaded.");
                googlemapLoaded = 1;
            }).fail(function () {
                console.log("Fail to load Googlemap.");
                googlemapLoaded = 0;
            });
        });
    //-------------
    //- END OF LOAD GOOGLEMAP -
    //-------------


    //-------------
    //- DATE PICKER FOR CITY RANK -
    //-------------
    $('#cityRankDate').datepicker({
        startDate: "01/23/2016",
        endDate: "today",
        todayHighlight: false,
        todayBtn: true,

    });
    $('#cityRankDate').datepicker('setDate', new Date());
    $("#getCityRank").click(function () {
        var date = $('#cityRankDate').datepicker('getDate');
        getCityRanking(date);
    });
    $('#cityInfoDateRange .input-daterange').datepicker({
        startDate: "01/23/2016",
        endDate: "today",
        todayHighlight: false,
        todayBtn: true,
    });
    $('#cityCompareDateRange .input-daterange').datepicker({
        startDate: "01/23/2016",
        endDate: "today",
        todayHighlight: false,
        todayBtn: true,
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
        $("#emotionchange-title").html("Sentiment Change Chart");
        drawEmotionChart(getfullhistoryinfoData, "CityInfoChart");
    });
    $('#cityInfoModal').on('hidden.bs.modal', function () {
        $("#cityHotHashtagsCard .front .row").show();
        $("#cityHotHashtagsCard .back .row").hide();
        $("#cityHotHashtagsCard").flip(false);
        if (hexBoundaryObj != null) {
            hexBoundaryObj.stopColorAnimation();
            hexBoundaryObj = null;
        }
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
    //- SET DEFAULT DATE FOR CITY RANK TO YESTERDAY -
    //-------------
    var today = new Date();
    cityRankDate = new Date("March 13, 2016 00:00:00");
    getCityRanking(cityRankDate);
    //    getCityRanking(today.addDays(-1));


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
            $("#mutipleAnalysis").prop('disabled', true);
            multipleCompare(placeIDs, startDate, endDate);
            $("#cityCompareModal").modal().on("shown.bs.modal", function () {
                $("#mutipleAnalysis").prop('disabled', false);
                currentCityIDs = placeIDs;
                $('#cityCompareDateRange .input-daterange').datepicker().data('datepicker').pickers[0].setDates(startDate);
                $('#cityCompareDateRange .input-daterange').datepicker().data('datepicker').pickers[1].setDates(endDate);

                $("#getCityCompare-btn").click(function () {
                    var startDate = $('#cityCompareDateRange .input-daterange').datepicker().data('datepicker').pickers[0].getDate();
                    var endDate = $('#cityCompareDateRange .input-daterange').datepicker().data('datepicker').pickers[1].getDate();
                    //        alert(startDate + "~" + endDate);
                    multipleCompare(currentCityIDs, startDate, endDate);
                });
                $("#currentCompareDateRange").click(function () {
                    $('#selectCompareDateRange').slideToggle();
                });
                $("#getCityCompare-btn").click(function () {
                    $('#selectCompareDateRange').hide();
                });
            });
        }
    });
    //-------------
    //- OPEN MULTIPLE ANALYSIS MODAL -
    //-------------

    $("#getCityInfo-btn").click(function () {
        var startDate = $('#cityInfoDateRange .input-daterange').datepicker().data('datepicker').pickers[0].getDate();
        var endDate = $('#cityInfoDateRange .input-daterange').datepicker().data('datepicker').pickers[1].getDate();
        //        alert(startDate + "~" + endDate);
        singleCityAnalysis(currentCityID, startDate, endDate);
    });

    $("#currentDateRange").click(function () {
        $('#selectDateRange').slideToggle();
    });
    $("#getCityInfo-btn").click(function () {
        $('#selectDateRange').hide();
    });

});