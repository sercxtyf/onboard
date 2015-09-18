angular.module('statistics')
    .factory('barChart', ['$rootScope', function($rootScope) {
        var that = {};
        that.draw = function(specs) {
            $(specs.id).highcharts({
                chart: {
                    width: 800,
                    height: 400,
                    type: 'column',
                    backgroundColor: 'rgba(0,0,0,0)'
                },
                title: {
                    text: specs.title
                },
                xAxis: {
                    categories: specs.dataList.map( function(ele) {
                        return ele.name;
                    })
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: specs.ytitle
                    }
                },
                tooltip: {
                    formatter: function() {
                        return '<table><tbody><tr><td style="color:{series.color};padding:0">' + this.series.name+ ': </td><td style="padding:0"><b>' + this.y + '</b></td></tr></tbody></table>';
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    column: {
                        pointPadding: 0.2,
                        borderWidth: 0,
                        color: '#90ed7d'
                    }
                },
                series: [{
                    name: specs.name,
                    data: specs.dataList.map( function(ele) {
                        return ele.value;
                    })
                }]
            });
        };

        return that;
    }]);
