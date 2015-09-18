angular.module('statistics')
    .factory('punchCard', ['settings', function (settings) {
        var init = function () {
            var that = settings();
            var spec = that.getSpec();

            that.timeFormat = d3.time.format("%w %H");
            spec.tick_x = 24;
            spec.tick_y = 7;
            spec.margin.left = 100;
            spec.margin.right = 135;

            spec.xfunc = function (d) {
                return d.key.split(" ")[1] - 0;
            };
            spec.yfunc = function (d) {
                return d.key.split(" ")[0] - 0;
            };
            spec.rfunc = function (d) {
                return d.values;
            };
            //spec.formatTime = d3.time.format("%Y-%m-%d");
            spec.text = "";
            spec.addTooltip = function () {
                var div = d3.select(spec.id).append("div")
                    .attr("class", "tooltip")
                    .style("opacity", 0);
                $(".tooltip").css("pointer-events", "none");
                spec.svg.selectAll("circle")
                    .on("mouseover", function (d) {
                        div.transition()
                            .duration(200)
                            .style("opacity", 0.9);
                        div.html(spec.rfunc(d) + " " + spec.text);
                        $('.tooltip').offset({
                            top: d3.event.pageY - 28,
                            left: d3.event.pageX
                        });
                    })
                    .on("mouseout", function (d) {
                        div.transition().duration(500).style("opacity", 0);
                    });
            };

            that.setupCanvas = function (id) {
                spec.id = id;
                spec.width = spec.canvasWidth - spec.margin.left - spec.margin.right;
                spec.height = spec.canvasHeight - spec.margin.top - spec.margin.bottom;

                // Set the ranges
                spec.x = d3.scale.linear().domain([0, 23]).range([0, spec.width]);
                spec.y = d3.scale.linear().domain([0, 6]).range([spec.height, 0]);

                // Define the axes
                spec.xAxis = d3.svg.axis().scale(spec.x).orient("bottom")
                    .ticks(24)
                    .tickFormat(function (d, i) {
                        //return d + "点";
                        return d;
                    });
                spec.yAxis = d3.svg.axis().scale(spec.y).orient("left")
                    .ticks(7)
                    .tickFormat(function (d, i) {
                        return ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][d];
                    });


                spec.grid_x = function make_x_axis() {
                    return d3.svg.axis()
                        .scale(spec.x)
                        .orient("bottom")
                        .ticks(spec.tick_x);
                };
                spec.grid_y = function make_y_axis() {
                    return d3.svg.axis()
                        .scale(spec.y)
                        .orient("left")
                        .ticks(spec.tick_y);
                };
            };
            that.draw = function (data) {
                //console.log(data);
                spec.svg = d3.select(spec.id)
                    .append("svg")
                    .attr("width", spec.canvasWidth)
                    .attr("height", spec.canvasHeight)
                    .append("g")
                    .attr("transform", "translate(" + spec.margin.left + "," + spec.margin.top + ")");


                // Add the X Axis
                spec.svg.append("g")
                    .attr("class", "x axis")
                    .attr("transform", "translate(0," + spec.height + ")")
                    .call(spec.xAxis);

                // Add the Y Axis
                spec.svg.append("g")
                    .attr("class", "y axis")
                    .call(spec.yAxis);

                // Add the grids
                if (spec.grid_x) {
                    //console.log("Adding grids...");
                    spec.svg.append("g")
                        .attr("class", "grid")
                        .attr("transform", "translate(0," + spec.height + ")")
                        .call(spec.grid_x()
                            .tickSize(-spec.height, 0, 0)
                            .tickFormat(""));
                }

                if (spec.grid_y) {
                    spec.svg.append("g")
                        .attr("class", "grid")
                        .call(spec.grid_y()
                            .tickSize(-spec.width, 0, 0)
                            .tickFormat(""));
                }

                // set r
                spec.r = d3.scale.linear().domain([0, d3.max(data, spec.rfunc)])
                    .range([5, 20]);

                // Add the circles
                spec.svg.selectAll("circle")
                    .data(data)
                    .enter()
                    .append("circle")
                    .attr("class", "circle")
                    .attr("cx", function (d) {
                        return spec.x(spec.xfunc(d));
                    })
                    .attr("cy", function (d) {
                        return spec.y(spec.yfunc(d));
                    })
                    .transition()
                    .duration(800)
                    .attr("r", function (d) {
                        return spec.r(spec.rfunc(d));
                    })
                    .attr("opacity", function (d) {
                        return spec.r(spec.rfunc(d)) / 15 * 0.7;
                    });

                // draw the tooltips
                spec.addTooltip();
            };

            return that;
        };
        return init;

    }]);

