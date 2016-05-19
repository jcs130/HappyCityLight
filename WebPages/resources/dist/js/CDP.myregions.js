        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Huiwen Hong -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------


        var map = null;
        var getuserregsData;
        var getListenPlaceListData;
        var hexBoundaryObjArray = new Array();
        var currentReg = null;
        var all_overlays = [];
        var selectedShape;


        //get public region list and store into local variable
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
                    beforeSend: function () {
                        console.log("collector/getlistenplacelist......");
                    },
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            console.log("success in collector/getlistenplacelist: " + JSON.stringify(data));
                            getListenPlaceListData = data.obj;
                        } else {
                            if (data.code == 400) {
                                window.location.href = "login.html?message=Please Login Again.";
                            }
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log("error in collector/getlistenplacelist: " + JSON.stringify(data));
                    }
                });
            } else {
                alert("Auth error");
            }

        }


        //get user region list and store into local variable
        function getUserRegions() {
            if (user_id != null && logintoken != null) {
                $.ajax({
                    type: "GET",
                    crossDomain: true,
                    url: serverURL + "user/getuserregs",
                    data: {
                        userID: user_id,
                        token: logintoken
                    },
                    beforeSend: function () {
                        $(".userreg > .box").hide();
                        $(".userreg > .userregerror").hide();
                        $(".userreg > .userregloading").show();
                        console.log("user/getuserregs......");
                    },
                    dataType: "json",
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            console.log("success in user/getuserregs: " + JSON.stringify(data));
                            $(".userreg > .box").show();
                            $(".userreg > .userregerror").hide();
                            $(".userreg > .userregloading").hide();
                            getuserregsData = data.obj;
                            displayUserRegions(getuserregsData);
                        } else {
                            if (data.code == 400) {
                                window.location.href = "login.html?message=Please Login Again.";
                            }
                            $(".userreg > .box").hide();
                            $(".userreg > .userregerror").show();
                            $(".userreg > .userregloading").hide();
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log("error in user/getuserregs: " + JSON.stringify(data));
                        $(".userreg > .box").hide();
                        $(".userreg > .userregerror").show();
                        $(".userreg > .userregloading").hide();
                    }
                });
            } else {
                alert("Auth error!");
            }
        }

        //display the user region list on the left/top of the page 
        function displayUserRegions(getuserregsData) {
            console.log("displayUserRegions()");
            $("#userregtbody").html("<tr><th width='8'>No.</th><th>Region Name</th><th>Add Date</th></tr>");
            var i = 1;
            $.each(getuserregsData, function () {
                $("#userregtbody").append("<tr regID='" + this.reg_id + "'><td>" + i + "</td><td><a class='hover-cursor' onclick='setAddRegBtnFalse();clearBoundaryHex();getPlaceEdge(" + this.reg_id + ")' style='color:#FFF;font-weight:bold;'>" + this.reg_name.toUpperCase() + "</a></td><td>" + getFormattedDate(new Date(this.add_date)) + "</td></tr>");
                i++;
            });
        }

        function setAddRegBtnFalse() {
            $('#btn-adduerreg').prop('disabled', true);
        }


        //decide if a region is in user region list
        function inUserRegList(regName) {
            var flag = 0;
            var reg;
            $.each(getuserregsData, function () {
                if (this.reg_name.toLowerCase() == regName) {
                    console.log("inUserRegList:" + this.reg_id);
                    flag = 1;
                    reg = this;
                }
            });
            if (flag == 1) {
                return reg;
            } else {
                return -1;
            }
        }

        //decide if a region is in public region list
        function inPublicRegList(regName) {
            var flag = 0;
            var reg;
            $.each(getListenPlaceListData, function () {
                if (this.reg_name.toLowerCase() == regName) {
                    console.log("inPublicRegList:" + this.reg_id);
                    flag = 1;
                    reg = this;
                }
            });
            if (flag == 1) {
                return reg;
            } else {
                return -1;
            }
        }






        //load googlemap script
        function loadGooglemap() {
            $(".map-div > .map").hide();
            $(".map-div > .controlpanel").hide();
            $(".map-div > .maperror").hide();
            $(".map-div > .maploading").show();
            $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4&language=en&libraries=geometry,places,drawing")
                .done(function () {
                    console.log("Map Loaded.");
                    initGooglemap();
                }).fail(function () {
                    console.log("Change to the Chinese Server");
                    $.getScript("http://ditu.google.cn/maps/api/js?sensor=false&language=en&libraries=geometry,places,drawing&key=AIzaSyAC01nTmNbpdoTQ5eu5v9vs1PpVb-Pbpq4").done(function () {
                        console.log("Map Loaded.");
                        initGooglemap();
                    }).fail(function () {
                        loadGooglemapError();
                    });
                });
        }

        //change html element when load googlemap script failure
        function loadGooglemapError() {
            $(".map-div > .map").hide();
            $(".map-div > .controlpanel").hide();
            $(".map-div > .maperror").show();
            $(".map-div > .maploading").hide();
        }


        //operations after loading googlemap scripts
        function initGooglemap() {
            map = new google.maps.Map(document.getElementById('map'), {
                center: new google.maps.LatLng(45.42929873257377, -75.38818359375),
                zoom: 9,
                mapTypeControl: false,
                streetViewControl: false,
                signInControl: false,
            });
            $(".map-div > .map").show();
            $(".map-div > .controlpanel").show();
            $(".map-div > .maperror").hide();
            $(".map-div > .maploading").hide();

            google.maps.event.addListener(map, 'zoom_changed', function () {
                if (hexBoundaryObjArray.length > 0) {
                    $.each(hexBoundaryObjArray, function () {
                        this.updateHexBoundary();
                    });
                }
            });


            //-------------
            //- GOOGLEMAP DRAWING TOOL INIT -
            //-------------
            var drawingManager = new google.maps.drawing.DrawingManager({
                drawingMode: google.maps.drawing.OverlayType.null,
                drawingControl: true,
                drawingControlOptions: {
                    position: google.maps.ControlPosition.BOTTOM_CENTER,
                    drawingModes: [
                    google.maps.drawing.OverlayType.RECTANGLE
                ]
                },
                rectangleOptions: {
                    strokeColor: '#D7B98E',
                    strokeOpacity: 0.8,
                    strokeWeight: 3,
                    fillColor: '#D7B98E',
                    fillOpacity: 0.4,
                    draggable: true,
                    editable: true
                }
            });
            drawingManager.setMap(map);
            google.maps.event.addListener(drawingManager, 'overlaycomplete', function (e) {
                all_overlays.push(e);
                if (e.type != google.maps.drawing.OverlayType.MARKER) {
                    // Switch back to non-drawing mode after drawing a shape.
                    drawingManager.setDrawingMode(null);

                    // Add an event listener that selects the newly-drawn shape when the user mouses down on it.
                    var newShape = e.overlay;
                    newShape.type = e.type;
                    google.maps.event.addListener(newShape, 'click', function () {
                        setSelection(newShape);
                    });
                    setSelection(newShape);
                }
            });

            // Clear the current selection when the drawing mode is changed, or when the map is clicked.
            google.maps.event.addListener(drawingManager, 'drawingmode_changed', clearSelection);
            google.maps.event.addListener(map, 'click', clearSelection);
            google.maps.event.addDomListener(document.getElementById('delete-button'), 'click', deleteSelectedShape);
            google.maps.event.addDomListener(document.getElementById('delete-all-button'), 'click', deleteAllShape);


            var geocoder = new google.maps.Geocoder;
            //    geocodeLatLng(geocoder, map);

            $("#add-new-reg-button").click(function () {
                if (all_overlays.length > 0) {
                    var location_areas = new Array();
                    var latMax = all_overlays[0].overlay.getBounds().getNorthEast().lat();
                    var latMin = all_overlays[0].overlay.getBounds().getSouthWest().lat();
                    var lngMax = all_overlays[0].overlay.getBounds().getNorthEast().lng();
                    var lngMin = all_overlays[0].overlay.getBounds().getSouthWest().lng();
                    for (var i = 0; i < all_overlays.length; i++) {
                        location_areas[i] = {};
                        location_areas[i].north = all_overlays[i].overlay.getBounds().getNorthEast().lat();
                        location_areas[i].south = all_overlays[i].overlay.getBounds().getSouthWest().lat();
                        location_areas[i].east = all_overlays[i].overlay.getBounds().getNorthEast().lng();
                        location_areas[i].west = all_overlays[i].overlay.getBounds().getSouthWest().lng();

                        if (location_areas[i].north > latMax) {
                            latMax = location_areas[i].north;
                        }
                        if (location_areas[i].south < latMin) {
                            latMin = location_areas[i].south;
                        }
                        if (location_areas[i].east > lngMax) {
                            lngMax = location_areas[i].east;
                        }
                        if (location_areas[i].west < lngMin) {
                            lngMin = location_areas[i].west;
                        }

                    }
                    var centerLatLng = new google.maps.LatLng((latMax + latMin) / 2, (lngMax + lngMin) / 2);
                    var regname = $('#addregname').val();
                    if (regname != "") {
                        geocodeLatLng(geocoder, map, centerLatLng, JSON.stringify(location_areas), regname);
                    } else {
                        toastr.info("Pleae enter an region name! ^_^");
                    }
                } else {
                    toastr.error("Sorry! No shapes found right now!╭(╯^╰)╮");
                }
            });



            //-------------
            //- GOOGLEMAP AUTOCOMPLETE WITH REGION BOUNDARY API -
            //-------------
            var input = document.getElementById('reginput');
            var autocomplete = new google.maps.places.Autocomplete(input);
            autocomplete.bindTo('bounds', map);
            var regMarker = new google.maps.Marker({
                map: map
            });

            autocomplete.addListener('place_changed', function () {
                // get new place info
                var place = autocomplete.getPlace();
                if (!place.geometry) {
                    alert("Autocomplete's returned place contains no geometry");
                    return;
                } else {
                    var inUserReg = inUserRegList(place.name.toLowerCase());
                    var inPublicReg = inPublicRegList(place.name.toLowerCase());
                    if (inUserReg.reg_id > 0) {
                        getPlaceEdge(inUserReg.reg_id);
                        $("#btn-adduerreg").prop('disabled', true);
                        return;
                    } else if (inPublicReg.reg_id > 0) {
                        getPlaceEdge(inPublicReg.reg_id);
                        currentReg = inPublicReg;
                        clearBoundaryHex();
                        $("#btn-adduerreg").prop('disabled', false);
                        return;
                    } else {
                        regMarker.setIcon(({
                            url: place.icon,
                            size: new google.maps.Size(71, 71),
                            origin: new google.maps.Point(0, 0),
                            anchor: new google.maps.Point(17, 34),
                            scaledSize: new google.maps.Size(35, 35)
                        }));
                        regMarker.setPosition(place.geometry.location);
                        map.setCenter(place.geometry.location);
                        var address = '';
                        if (place.address_components) {
                            address = [(place.address_components[0] && place.address_components[0].short_name || ''),
                                (place.address_components[1] && place.address_components[1].short_name || ''),
                                (place.address_components[2] && place.address_components[2].short_name || '')].join(' ');
                        }
                        $("#btn-adduerreg").prop('disabled', false);
                    }
                }
            });

        }


        //get a region boundary info from the server and display on the map
        function getPlaceEdge(regID) {
            if (user_id != null && logintoken != null) {
                $.ajax({
                    type: "GET",
                    crossDomain: true,
                    url: serverURL + "collector/getplaceinfo",
                    data: {
                        userID: user_id,
                        token: logintoken,
                        reg_id: regID,
                    },
                    dataType: "json",
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            console.log("success in collector/getplaceinfo");

                            if ((data.obj != null) && (data.obj.areas != [])) {
                                var latMax = data.obj.areas[0].north;
                                var latMin = data.obj.areas[0].south;
                                var lngMax = data.obj.areas[0].east;
                                var lngMin = data.obj.areas[0].west;

                                $.each(data.obj.areas, function () {
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
                                var hom = new HexOnMap(map);
                                hexBoundaryObj = new HexBoundary(hom, data.obj.areas);
                                hexBoundaryObj.createHexBoundary();
                                hexBoundaryObj.changeHexBoundaryColor("#EB7A77");
                                hexBoundaryObjArray.push(hexBoundaryObj);

                                map.fitBounds(new google.maps.LatLngBounds(new google.maps.LatLng(latMin, lngMin), new google.maps.LatLng(latMax, lngMax)));
                            }

                        } else {
                            console.log(JSON.stringify(data));
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




        function clearBoundaryHex() {
            if (hexBoundaryObjArray != []) {
                $.each(hexBoundaryObjArray, function () {
                    this.deleteHexBoundary();
                });
                hexBoundaryObjArray = [];
            }
        }

        function clearSelection() {
            if (selectedShape) {
                selectedShape.setEditable(false);
                selectedShape = null;
            }
        }


        function deleteSelectedShape() {
            if (selectedShape) {
                selectedShape.setMap(null);
            }
        }

        function deleteAllShape() {
            if (all_overlays) {
                for (var i = 0; i < all_overlays.length; i++) {
                    all_overlays[i].overlay.setMap(null);
                }
                all_overlays = [];
            }
        }

        function setSelection(shape) {
            clearSelection();
            selectedShape = shape;
            shape.setEditable(true);
        }


        //get country and timezone before adding a new private region into user region list
        function geocodeLatLng(geocoder, map, centerLatLng, location_areas, regname) {
            var latlng = {
                lat: centerLatLng.lat(),
                lng: centerLatLng.lng()
            };
            var country;
            var timezone;
            geocoder.geocode({
                'location': latlng
            }, function (results, status) {
                if (status === google.maps.GeocoderStatus.OK) {
                    if (results[1]) {
                        $.each(results, function () {
                            var _this = this;
                            $.each(this.types, function () {
                                if (this == 'country') {
                                    country = _this.formatted_address;
                                    $.ajax({
                                        url: "https://maps.googleapis.com/maps/api/timezone/json?location=" + centerLatLng.lat() + "," + centerLatLng.lng() + "&timestamp=" + (Math.round((new Date().getTime()) / 1000)).toString() + "&sensor=false&key=AIzaSyB3HuRFkopnUs8tgJvp5ES4LPyJPJmtjvQ",
                                    }).done(function (response) {
                                        if (response.timeZoneId != null) {
                                            timezone = (response.rawOffset) / 3600;
                                            addNewUserRegions(location_areas, centerLatLng, country, timezone, regname);
                                        } else {
                                            console.log("error in https://maps.googleapis.com/maps/api/timezone");
                                        }
                                    }).fail(function () {
                                        console.log("error in https://maps.googleapis.com/maps/api/timezone");
                                    });
                                }
                            });
                        });
                    } else {
                        console.log('geocodeLatLng: No results found');
                    }
                } else {
                    console.log('geocodeLatLng: Geocoder failed due to ' + status);
                }
            });
        }



        //add a new private region into user region list
        function addNewUserRegions(location_areas, centerLatLng, country, timezone, regname) {
            if (user_id != null && logintoken != null) {
                $.ajax({
                    type: "POST",
                    crossDomain: true,
                    url: serverURL + "user/adduserreg",
                    data: {
                        userID: user_id,
                        token: logintoken,
                        reg_name: regname,
                        location_areas: location_areas,
                        center_lat: centerLatLng.lat(),
                        center_lan: centerLatLng.lng(),
                        country: country,
                        time_zone: timezone
                    },
                    beforeSend: function () {
                        $("#btn-adduerreg").prop('disabled', true);
                    },
                    dataType: "json",
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            toastr.success("Adding new region successfully! (｡･∀･)ﾉﾞ");
                            $("#btn-adduerreg").prop('disabled', false);
                            getUserRegions();
                            deleteAllShape();
                            clearBoundaryHex();
                            $('#addregname').val("");
                        } else {
                            $("#btn-adduerreg").prop('disabled', false);
                            toastr.info("Sorry QAQ ....... The adding region operation is not successful > < ......");
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log("error in user/adduserreg......");
                        toastr.error("Sorry QAQ ....... The adding region operation is not successful > < ......");
                    }
                });
            } else {
                alert("Auth error!");
            }
        }



        //add a public region into user region list
        function addUserRegions(reg) {
            if (user_id != null && logintoken != null) {
                $.ajax({
                    type: "POST",
                    crossDomain: true,
                    url: serverURL + "user/adduserreg",
                    data: {
                        userID: user_id,
                        token: logintoken,
                        reg_id: reg.reg_id,
                        reg_name: reg.reg_name
                    },
                    beforeSend: function () {
                        $("#btn-adduerreg").prop('disabled', true);
                    },
                    dataType: "json",
                    success: function (data, textStatus) {
                        if (data.code == 200) {
                            toastr.success("Adding region into my region successfully! (｡･∀･)ﾉﾞ");
                            $("#btn-adduerreg").prop('disabled', false);
                            getUserRegions();
                            deleteAllShape();
                        } else {
                            $("#btn-adduerreg").prop('disabled', false);
                            toastr.info("Sorry QAQ ....... The adding region operation is not successful > < ......");
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        toastr.info("Sorry QAQ ....... The adding region operation is not successful > < ......");
                    }
                });
            } else {
                alert("Auth error!");
            }
        }



        $(function () {
            $('.content').css('height', $(window).height() - 101);
            $(window).resize(function () {
                setTimeout(function () {
                    $('.content').css('height', $(window).height() - 101);
                }, 30);
            });


            loadGooglemap();
            $(".refreshmap").click(function () {
                loadGooglemap();
            });


            getListenPlaceList();
            getUserRegions();
            $(".refreshreg").click(function () {
                getUserRegions();
            });

            $("#btn-adduerreg").click(function () {
                $("#btn-adduerreg").prop('disabled', true);
                addUserRegions(currentReg);
            });


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
            //    addUserRegions(currentReg);
        });