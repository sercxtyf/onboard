angular.module('statistics')
    .factory('pieChart', ['$rootScope', function($rootScope) {
        var that = {};
        that.todoUnknownUser = '未分配责任人';
        that.groupActivityData = function(data, currentUser) {
            var total = data.length;
            var array = d3.nest().key(function(d) {
                return d.creatorName;
            }).rollup(function(leave) {
                return leave.length;
            }).entries(data);
            var ret = [];
            array.forEach(function(ele) {
                if (currentUser !== undefined && currentUser.name == ele.key) {
                    ret.push({
                        name: ele.key,
                        y: ele.values / total,
                        value: ele.values,
                        sliced: true,
                        selected: true
                    });
                } else {
                    ret.push({
                        name: ele.key,
                        y: ele.values / total,
                        value: ele.values
                    });
                }
            });
            return {
                total: total,
                data: ret
            };
        };

        //同时支持todo, step和bug
        that.groupTodoData = function(data, currentUser) {
            var total = data.length;
            var array = d3.nest().key(function(d) {
                if(d.assigneeId !== undefined) {
                    if(d.assigneeDTO !== undefined)
                        return d.assigneeDTO.name;
                    else if(d.bugAssigneeDTO !== undefined)
                        return d.bugAssigneeDTO.name;
                    else
                        return d.assigneeName;
                }
                else
                    return that.todoUnknownUser;
            }).rollup(function(leave) {
                return leave.length;
            }).entries(data);
            var ret = [];
            array.forEach(function(ele) {
                if (currentUser !== undefined && currentUser.name == ele.key) {
                    ret.push({
                        name: ele.key,
                        y: ele.values / total,
                        value: ele.values,
                        sliced: true,
                        selected: true
                    });
                } else {
                    ret.push({
                        name: ele.key,
                        y: ele.values / total,
                        value: ele.values
                    });
                }
            });
            return {
                total: total,
                data: ret
            };
        };

        that.groupLocData = function(data, currentUser) {
            var totalLoc = data.reduce(function(acc, ele) {
                return acc + ele.loc;
            }, 0);
            var array = d3.nest().key(function(d) {
                return d.userName;
            }).rollup(function(leave) {
                return leave.reduce(function(acc, ele) {
                    return acc + ele.loc;
                }, 0);
            }).entries(data);
            var ret = [];
            array.forEach(function(ele) {
                if (currentUser !== undefined && currentUser.name == ele.key) {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalLoc,
                        value: ele.values,
                        sliced: true,
                        selected: true
                    });
                } else {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalLoc,
                        value: ele.values
                    });
                }
            });
            return {
                total: totalLoc,
                data: ret
            };
        };

        that.groupWorkloadData = function(data, currentUser) {
            for (var i = 0; i < data.length; ++i)
                data[i].workload = data[i].loc * 0.001833 + 73.12;
            var totalWorkload = data.reduce(function(acc, ele) {
                return acc + ele.workload;
            }, 0);
            var array = d3.nest().key(function(d) {
                return d.userName;
            }).rollup(function(leave) {
                return leave.reduce(function(acc, ele) {
                    return acc + ele.workload;
                }, 0);
            }).entries(data);
            var ret = [];
            array.forEach(function(ele) {
                if (currentUser !== undefined && currentUser.name == ele.key) {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalWorkload,
                        value: ele.values,
                        sliced: true,
                        selected: true
                    });
                } else {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalWorkload,
                        value: ele.values
                    });
                }
            });
            return {
                total: totalWorkload,
                data: ret
            };
        };

        that.groupLocMinusData = function(data, currentUser) {
            var totalLoc = data.reduce(function(acc, ele) {
                return acc + ele.locMinus;
            }, 0);
            var array = d3.nest().key(function(d) {
                return d.userName;
            }).rollup(function(leave) {
                return leave.reduce(function(acc, ele) {
                    return acc + ele.locMinus;
                }, 0);
            }).entries(data);
            var ret = [];
            array.forEach(function(ele) {
                if (currentUser !== undefined && currentUser.name == ele.key) {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalLoc,
                        value: ele.values,
                        sliced: true,
                        selected: true
                    });
                } else {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalLoc,
                        value: ele.values
                    });
                }
            });
            return {
                total: totalLoc,
                data: ret
            };
        };

        that.groupCommitsData = function(data, currentUser) {
            var totalCommits = data.length;
            var array = d3.nest().key(function(d) {
                return d.userName;
            }).rollup(function(leave) {
                return leave.length;
            }).entries(data);
            var ret = [];
            array.forEach(function(ele) {
                if (currentUser !== undefined && currentUser.name == ele.key) {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalCommits,
                        value: ele.values,
                        sliced: true,
                        selected: true
                    });
                } else {
                    ret.push({
                        name: ele.key,
                        y: ele.values / totalCommits,
                        value: ele.values
                    });
                }
            });
            return {
                total: totalCommits,
                data: ret
            };
        };

        that.draw = function(specs) {
            $(specs.id).highcharts({
                chart: {
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
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.value}</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                        	enabled: specs.enableDataLabel === undefined ? true : specs.enableDataLabel,
                            format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                            style: {
                                color: Highcharts.theme
                            }
                        },
                        events: {
                            click: function(event) {
                                $rootScope.$broadcast('sliced', event.point.name, event.point.color);
                            }
                        }
                    }
                },
                series: [{
                    type: 'pie',
                    name: specs.name,
                    data: specs.data
                }]
            });
        };

        return that;
    }]);
