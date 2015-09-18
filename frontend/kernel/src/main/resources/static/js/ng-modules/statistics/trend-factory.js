angular.module('statistics')
    .factory('trend', ['settings', function (settings) {
        var init = function () {
            var that = settings();
            var spec = that.getSpec();

            spec.parseDate = d3.time.format("%Y-%m-%d %X").parse;
            spec.tick_x = 6;
            spec.tick_y = 9;
            spec.margin.left = 100;
            spec.margin.right = 135;

            spec.xfunc = function (d) {
                return spec.parseDate(d.date);
            };
            spec.yfunc = function (d) {
                return d.values;
            };

            // tooltip setup
            spec.formatTime = d3.time.format("%Y-%m-%d");
            spec.text = "";
            spec.addTooltip = function () {
                var div = d3.select(spec.id).append("div")
                    .attr("class", "tooltip")
                    .style("opacity", 0);
                $(".tooltip").css("pointer-events", "none").css("z-index", "1");
                spec.svg.selectAll("circle")
                    .on("mouseover", function (d) {
                        div.transition()
                            .duration(200)
                            .style("opacity", 0.9);
                        div.html(spec.formatTime(spec.xfunc(d)) + "<br/>" + spec.yfunc(d) + " " + spec.text);
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
                spec.x = d3.time.scale().range([0, spec.width]);
                spec.y = d3.scale.linear().range([spec.height, 0]);

                // Define the axes
                spec.xAxis = d3.svg.axis().scale(spec.x)
                    .orient("bottom").ticks(spec.tick_x)
                    .tickFormat(d3.time.format("%m-%d"));
                spec.yAxis = d3.svg.axis().scale(spec.y)
                    .orient("left").ticks(spec.tick_y)
                    .tickFormat(d3.format("d"))
                    .tickSubdivide(0);

                spec.domain_x = function (data) {
                    return d3.extent(data, spec.xfunc);
                };
                spec.domain_y = function (data) {
                    return [0, d3.max(data, spec.yfunc)];
                };

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

            var addMissingDate = function (data) {
                var range = spec.x.domain();
                //console.log(range);
                var formatter = d3.time.format("%Y-%m-%d");
                var days = d3.time.day.range(range[0], range[1], 1);
                var exists = data.map(function (d) {
                    return d.key;
                });
                var newData = [].concat(data);
                var s;
                days.forEach(function (day) {
                    s = formatter(day);
                    if (exists.indexOf(s) == -1)
                        newData.push({
                            key: s,
                            values: 0
                        });
                });
                newData.sort(function (a, b) {
                    return formatter.parse(a.key) - formatter.parse(b.key);
                });
                return newData;
            };

            that.draw = function (data, opt) {
                spec.svg = d3.select(spec.id)
                    .append("svg")
                    .attr("width", spec.canvasWidth)
                    .attr("height", spec.canvasHeight)
                    .append("g")
                    .attr("transform", "translate(" + spec.margin.left + "," + spec.margin.top + ")");

                var valueline = d3.svg.line()
                    .x(function (d) {
                        return spec.x(spec.xfunc(d));
                    })
                    .y(function (d) {
                        return spec.y(spec.yfunc(d));
                    });
                // Scale the range of the data
                spec.x.domain(spec.domain_x(data));
                if (data.length >= 1) {
                    spec.y.domain(spec.domain_y(data));
                } else
                    spec.y.domain([0, spec.tick_y + 1]);

                // Add the X Axis
                spec.svg.append("g")
                    .attr("class", "x axis")
                    .attr("transform", "translate(0," + spec.height + ")")
                    .call(spec.xAxis)
                    .selectAll("text")
                    .style("text-anchor", "end")
                    .attr("dx", "-0.1em")
                    .attr("dy", "0.8em");
                // .attr("transform", function(d) {
                //     return "rotate(-45)";
                // });

                // Add the Y Axis
                spec.svg.append("g")
                    .attr("class", "y axis")
                    .call(spec.yAxis);

                // Add the grids
                if (spec.grid_x) {
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

                // Add the valueline path
                var lineData = data;
                if (opt !== undefined && opt.addMissingDate == true)
                    lineData = addMissingDate(data);
                spec.svg.append("path")
                    .attr("class", "line")
                    .attr("d", valueline(lineData));

                // draw the data dots and tooltips
                spec.svg.selectAll("dot")
                    .data(data)
                    .enter().append("circle")
                    .attr("r", 5)
                    .style("fill", "navy")
                    .attr("cx", function (d) {
                        return spec.x(spec.xfunc(d));
                    })
                    .attr("cy", function (d) {
                        return spec.y(spec.yfunc(d));
                    });
                spec.addTooltip();

            };

            that.setParseDate = function (func) {
                spec.parseDate = func;
            };
            that.getParseDate = function () {
                return spec.parseDate;
            };
            that.setX = function (func) {
                spec.xfunc = func;
            };
            that.setY = function (func) {
                spec.yfunc = func;
            };
            that.setDomain_X = function (range) {
                spec.domain_x = range;
            };
            that.setDomain_Y = function (range) {
                spec.domain_y = range;
            };

            return that;
        };
        return init;

    }]);
