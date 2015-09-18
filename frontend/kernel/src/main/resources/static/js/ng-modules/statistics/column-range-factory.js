angular.module('statistics')
    .factory('columnRange', ['drawer', function(drawer) {
        var that = {};
        that.groupBarData = function(data) {
            var names = [];
            var dates = [];
            var dueDates = [];
            var today = new Date() - 0;
            for (var i = 0; i < data.length; i++) {
                names.push(data[i].content);
                if (!data[i].dueDate) {
                    data[i].dueDate = today;
                }
                if (!data[i].startTime){
                    data[i].startTime = data[i].created;
                }
                if (data[i].status === 'closed') {
                    if (!data[i].completeTime){
                        data[i].completeTime = data[i].updated;
                    }
                    dates.push([data[i].startTime, data[i].completeTime]);
                } else {
                    dates.push([data[i].startTime, today]);
                }
                dueDates.push([data[i].created, data[i].dueDate]);
            }
            return {
                data2: dueDates,
                data: dates,
                categories: names
            };
        };

        that.drawTodoChart = function(specs) {
            $(specs.id).highcharts({
                chart: {
                    type: 'columnrange',
                    inverted: true,
                    plotBackgroundColor: '#f3f3f4',
                    plotBorderWidth: null,
                    plotShadow: false
                },
                title: {
                    text: specs.title,
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

                yAxis: {
                    title: {
                        text: '日期'
                    },
                    type: 'datetime',
                    labels: {
                        format: '{value: %b%e日%H点}'
                    }
                },

                plotOptions: {
                    columnrange: {
                        dataLabels: {
                            enabled: false,
                            formatter: function() {
                                return Highcharts.dateFormat('%A %B %e %Y', this.y);
                            }
                        },
                        tooltip: {
                            pointFormat: '<span style="color:{series.color}">\u25CF</span> {series.name}: <b>{point.y:%Y-%m-%d %H:%M:%S} 至 {point.high:%Y-%m-%d %H:%M:%S}</b><br/>'
                        },
                        cursor: 'pointer',
                        events: {
                            click: function(event) {
                                drawer.open({
                                    type: 'todo',
                                    data: {
                                        id: specs.todos[event.point.x].id
                                    }
                                });
                            }
                        },
                        grouping: false,
                        shadow: false,
                        borderWidth: 0
                    }
                },

                series: [{
                    name: specs.name2,
                    data: specs.data2,
                    color: 'rgba(186,60,61,1)',
                    pointPadding: 0,
                    pointPlacement: 0
                }, {
                    name: specs.name,
                    data: specs.data,
                    color: 'rgba(248,161,63,0.9)',
                    pointPadding: 0.2,
                    pointPlacement: 0
                }]

            });

        };


        return that;
    }]);
