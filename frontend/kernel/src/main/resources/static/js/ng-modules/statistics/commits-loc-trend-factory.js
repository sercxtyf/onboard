angular.module('statistics')
    .factory('commitsLocTrend', ['commitsTrend', '$rootScope', 'drawer', function(commitsTrend, $rootScope, drawer) {
        var init = function() {
            var that = commitsTrend();
            var spec = that.getSpec();
            //spec.groupData = groupLocData;
            var yfunc_loc = function(d) {
                var count = 0;
                d.values.forEach(function(e) {
                    count += e.loc;
                });
                d.code = count;
                d.action = '增加了';
                return count;
            };
            var yfunc_locMinus = function(d) {
                var count = 0;
                d.values.forEach(function(e) {
                    count -= e.locMinus;
                });
                d.code = count;
                d.action = '删除了';
                return count;
            };
            var yfunc_absolute = function(d) {
                var count = 0;
                d.values.forEach(function(e) {
                    count += e.locAbsolute;
                });
                d.code = count;
                d.action = '净增';
                return count;
            };
            spec.yfunc = function(d){
                return d.code;
            }
            spec.text = '行代码';

            var processData = function(data) {
                data.forEach(function(d) {
                    d.date = d.date.split(' ')[0];
                });
            };

            spec.groupData = function(data) {
                processData(data);
                return d3.nest().key(function(d) {
                    return d.date;
                }).entries(data);
            };

            that.drawGraph = function(specs) {
                var data = that.getRawData();
                if (spec.groupData !== undefined) {
                    data = spec.groupData(that.filterData(data, specs));
                    that.draw(data);
                }
            };

            var getDomain = function(commits) {
                var min = d3.min(commits.map(yfunc_locMinus));
                var max = d3.max(commits.map(yfunc_loc));
                return [min, max];
            };

            that.draw = function(data, opt) {
                spec.svg = d3.select(spec.id)
                    .append('svg')
                    .attr('width', spec.canvasWidth)
                    .attr('height', spec.canvasHeight)
                    .append('g')
                    .attr('transform', 'translate(' + spec.margin.left + ',' + spec.margin.top + ')');

                var valueline = d3.svg.line()
                    .x(function(d) {
                        return spec.x(spec.xfunc(d));
                    })
                    .y(function(d) {
                        return spec.y(spec.yfunc(d));
                    });
                // Scale the range of the data
                spec.x.domain(spec.domain_x(data));
                var percent = 1;
                if (data.length >= 1) {
                    var range = getDomain(data);
                    spec.y.domain(range);
                    percent = (range[1] - 0) / (range[1] - range[0]);
                } else
                    spec.y.domain([0, spec.tick_y + 1]);

                // Add the X Axis
                spec.svg.append('g')
                    .attr('class', 'x axis')
                    .attr('transform', 'translate(0,' + spec.height * percent + ')')
                    .call(spec.xAxis)
                    .selectAll('text')
                    .style('text-anchor', 'end')
                    .attr('dx', '-0.1em')
                    .attr('dy', '0.8em');

                // Add the Y Axis
                spec.svg.append('g')
                    .attr('class', 'y axis')
                    .call(spec.yAxis);

                // Add the grids
                if (spec.grid_x) {
                    spec.svg.append('g')
                        .attr('class', 'grid')
                        .attr('transform', 'translate(0,' + spec.height + ')')
                        .call(spec.grid_x()
                            .tickSize(-spec.height, 0, 0)
                            .tickFormat(''));
                }

                if (spec.grid_y) {
                    spec.svg.append('g')
                        .attr('class', 'grid')
                        .call(spec.grid_y()
                            .tickSize(-spec.width, 0, 0)
                            .tickFormat(''));
                }

                // Add the valueline path

                // draw loc
                var data1 = that.jsonCopy(data);
                data1.forEach(yfunc_loc);
                //spec.yfunc = yfunc_loc;                
                spec.svg.append('path')
                    .attr('class', 'line')
                    .attr('d', valueline(data1));

                spec.svg.selectAll('dot')
                    .data(data1)
                    .enter().append('circle')
                    .attr('r', 5)
                    .style('fill', 'navy')
                    .attr('cx', function(d) {
                        return spec.x(spec.xfunc(d));
                    })
                    .attr('cy', function(d) {
                        return spec.y(spec.yfunc(d));
                    });

                // draw loc minus
                var data2 = that.jsonCopy(data);
                data2.forEach(yfunc_locMinus);
                //spec.yfunc = yfunc_locMinus;
                spec.svg.append('path')
                    .attr('class', 'line')
                    .attr('d', valueline(data2))
                    .style('stroke', 'red');

                spec.svg.selectAll('dot')
                    .data(data2)
                    .enter().append('circle')
                    .attr('r', 5)
                    .style('fill', 'navy')
                    .attr('cx', function(d) {
                        return spec.x(spec.xfunc(d));
                    })
                    .attr('cy', function(d) {
                        return spec.y(spec.yfunc(d));
                    });

                // draw absolute code number line
                var data3 = that.jsonCopy(data);
                data3.forEach(yfunc_absolute);
                //spec.yfunc = yfunc_locMinus;
                spec.svg.append('path')
                    .attr('class', 'line')
                    .attr('d', valueline(data3))
                    .style('stroke', 'orange')
                    .style('stroke-dasharray', 3);

                // draw tooltips
                spec.addTooltip();
            };

            return that;
        };
        return init;
    }]);
