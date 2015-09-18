angular.module('statistics')
    .factory('stackedBar', function() {
        var that = {};

        var binarySearch = function(value, arr, startIndex, endIndex) {
            if (!value || !(arr instanceof Array)) return;
            var len = arr.length,
                startIndex = typeof startIndex === "number" ? startIndex : 0,
                endIndex = typeof endIndex === "number" ? endIndex : len - 1,
                midIndex = Math.floor((startIndex + endIndex) / 2),
                midval = arr[midIndex];
            if (startIndex > endIndex) return endIndex;
            if (midval === value) {
                return midIndex;
            } else if (midval > value) {
                return binarySearch(value, arr, startIndex, midIndex - 1);
            } else {
                return binarySearch(value, arr, midIndex + 1, endIndex);
            }
        }

        that.groupData = function(start, end, todos, activities, commits) {
            var since = new Date(start);
            var until = new Date(end);
            var span = d3.time.days(since, until, 1);
            if (span.length > 15) {
                span = d3.time.weeks(since, until, 1);
                if (span.length > 15) {
                    span = d3.time.months(since, until, 1);
                }
            }
            since = d3.time.day.floor(since);
            if (since !== span[0]) {
                span.splice(0, 0, since);
            }
            var endpoint = d3.time.day.ceil(until);
            if (endpoint - 0 === until - 0) {
                span.push(d3.time.day.offset(until, 1));
            } else {
                span.push(endpoint);
            }
            var len = span.length;
            var names = [];
            var todo_count = [];
            var activity_count = [];
            var commits_count = [];
            var loc_count = [];
            for (var i = 0; i < len - 1; i++) {
                names.push(d3.time.format("%m-%d")(span[i]) + ' 至 ' + d3.time.format("%m-%d")(span[i + 1]));
                todo_count.push(0);
                activity_count.push(0);
                commits_count.push(0);
                loc_count.push(0);
            }
            var pos;
            for (var i = 0; i < todos.length; i++) {
                pos = binarySearch(todos[i].updated, span);
                todo_count[pos] += 1;
            }
            for (var i = 0; i < activities.length; i++) {
                pos = binarySearch(activities[i].created, span);
                activity_count[pos] += 1;
            }
            for (var i = 0; i < commits.length; i++) {
                pos = binarySearch(commits[i].order, span);
                commits_count[pos] += 1;
                loc_count[pos] += commits[i].loc;
            }
            return {
                todos: todo_count,
                activities: activity_count,
                commits: commits_count,
                loc: loc_count,
                categories: names
            };
        };

        that.drawOverallChart = function(specs) {
            $(specs.id).highcharts({
                chart: {
                    type: 'column'
                },

                title: {
                    text: '整体进度',
                    style: {
                        fontWeight: 'bold'
                    }
                },

                noData: {
                    style: {
                        fontWeight: 'bold',
                        fontSize: '20px',
                        color: '#303030'
                    }
                },

                xAxis: {
                    categories: specs.categories
                },

                yAxis: [{
                    allowDecimals: false,
                    min: 0,
                    title: {
                        text: '次数'
                    }
                }, {
                    allowDecimals: false,
                    min: 0,
                    title: {
                        text: '代码行数'
                    },
                    opposite: true
                }],

                tooltip: {
                    formatter: function() {
                        return '<b>' + this.x + '</b><br/>' +
                            this.series.name + ': ' + this.y;
                    }
                },

                plotOptions: {
                    column: {
                        stacking: 'normal'
                    }
                },

                series: specs.series
            });
        };


        return that;
    });
