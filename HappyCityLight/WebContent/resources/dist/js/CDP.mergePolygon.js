        //-------------
        //- Copyright (C) 2016 City Digital Pulse - All Rights Reserved-
        //- Author: Huiwen Hong -
        //- Concept and supervision Abdulmotaleb El Saddik -
        //-------------

        var array_polygon;
        var array_clipper_polygon;
        var cpr;
        var subj_polygons;
        var solution_polygons;
        var clipType;


        //merge multiple regions into polylines
        function mergeRegions(regions) {
            array_polygon = new Array();
            array_clipper_polygon = [];
            cpr = new ClipperLib.Clipper();
            subj_polygons = new ClipperLib.Paths();
            solution_polygons = new ClipperLib.Paths();
            clipType = 5;

            $.each(regions, function () {
                var polygon = new google.maps.Polygon({
                    paths: this,
                    strokeColor: "#0FF000",
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: "#0FF000",
                    fillOpacity: 0.35
                });
                array_polygon.push(polygon.getPath().getArray());
            });


            var clipper_final_poly = mergePolygon();
            var parcelleHeig = new Array();
            for (i = 0; i < clipper_final_poly.length; i++) {
                for (j = 0; j < clipper_final_poly[i].length; j++) {
                    parcelleHeig.push(new google.maps.LatLng(clipper_final_poly[i][j].X, clipper_final_poly[i][j].Y));
                }
            }

            parcelleHeig.push(parcelleHeig[0]);

            polygoneParcelleHeig = new google.maps.Polyline({
                path: parcelleHeig,
                strokeColor: "#9F353A",
                strokeOpacity: 0.6,
                strokeWeight: 1
            });

            //alert(parcelleHeig.toString());
            return polygoneParcelleHeig;

        }

        /********************************************************************************************************************************/



        function mergePolygon() {
            for (j = 0; j < array_polygon.length; j++) {
                array_polygon_clipper = createarray_clipper_polygon(array_polygon[j]);
                if (ClipperLib.JS.AreaOfPolygon(array_polygon_clipper) < 0) {
                    array_polygon_clipper.reverse();
                }
                subj_polygons.push(array_polygon_clipper);
            }
            cpr.AddPaths(subj_polygons, ClipperLib.PolyType.ptSubject, true);
            var succeeded = cpr.Execute(ClipperLib.ClipType.ctUnion, solution_polygons, ClipperLib.PolyFillType.pftNonZero, ClipperLib.PolyFillType.pftNonZero);
            return solution_polygons;
        }

        function getpathsolution(poly) {
            for (i = 0; i < poly.length; i++) {
                for (j = 0; j < poly[i].length; j++) {
                    console.log(poly[i][j].X);
                    console.log(poly[i][j].Y);
                }
            }
        }

        function createarray_clipper_polygon(array) {
            var subj_polygon = new ClipperLib.Path();
            for (i = 0; i < array.length; i++) {
                var latx = array[i].lat();
                var lngx = array[i].lng();
                subj_polygon.push(new ClipperLib.FPoint(latx, lngx));
            }
            return subj_polygon;
        }