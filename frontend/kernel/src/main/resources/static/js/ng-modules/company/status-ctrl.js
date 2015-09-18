/**
 * Created by Nettle on 2015/5/10.
 */

// 数据
angular.module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
        // default child state
            .state('company.status', {
            url: '/status',
            templateUrl: 'status.html',
            controller: 'statusCtrl'
        });
    }])
    .controller('statusCtrl', ['$scope', '$state', '$http', '$rootScope', '$q', 'url', 'company', 'user', 'pieChart', 'companyActivityStats', 'companyTodoStats', 'companyCommitStats', 'companyStepStats', 'companyBugStats', 'drawer', '$timeout', 'workModel', 'statusService',  
        function($scope, $state, $http, $rootScope, $q, url, company, user, pieChart, companyActivityStats, companyTodoStats, companyCommitStats, companyStepStats, companyBugStats, drawer, $timeout, workModel, statusService) {
            //$scope.now = new Date();
            //$scope.now.setHours(0, 0, 0);
            var t = new Date();
            $scope.endTime = d3.time.day.ceil(t) - 1;
            $scope.startTime = t.setDate(t.getDate() - 6) - 0;
            $scope.dateText = "前一周";
            $scope.dataType = 0;
            $scope.currentProjectId = 0;
            $scope.nowUserId = 0;
            $scope.nowUserName = null;
            $scope.isBlank = true;
            $scope.details = [];
            $scope.filteredData = null;
            $scope.currentUserName = function() {
                return $scope.nowUserName !== null ? $scope.nowUserName : '全体成员';
            }
            $scope.statsData = null;
            $scope.showLine = true;
            $scope.userList = [];
            $scope.punchCard = [
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            ];
            $scope.labels = [
                '星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'
            ];

            user.getCompanyUsers(false, url.companyId()).then(function(users) {
                $scope.userList = users;
            });

            $scope.changeDate = function(key) {
                startDatePickerShow = false;
                endDatePickerShow = false;
                var t = new Date();
                var tempTime;
                if (key != 1)
                    tempTime = d3.time.day.ceil(t) - 1;
                else
                    tempTime = d3.time.day.floor(t) - 1;
                if (tempTime != $scope.endTime)
                    $scope.endTime = tempTime;
                t = d3.time.day.floor(t);
                if (key == 0) {
                    $scope.dateText = "今天";
                    $scope.startTime = t - 0;
                }
                if (key == 1) {
                    $scope.dateText = "昨天";
                    $scope.startTime = t.setDate(t.getDate() - 1) - 0;
                }
                if (key == 2) {
                    $scope.dateText = "前一周";
                    $scope.startTime = t.setDate(t.getDate() - 6) - 0;
                }
                if (key == 3) {
                    $scope.dateText = "上个月";
                    $scope.startTime = t.setMonth(t.getMonth() - 1) - 0;
                }
                if (key == 4) {
                    $scope.dateText = "上个季度";
                    $scope.startTime = t.setMonth(t.getMonth() - 3) - 0;
                }
                if (key == 5) {
                    $scope.dateText = "过去半年";
                    $scope.startTime = t.setMonth(t.getMonth() - 6) - 0;
                }
                $scope.updateData(true);
            };

            //time operation
            $scope.showTimeForm = function() {
                $('#updateTimeRangeForm').show();
            };
            $scope.hideTimeForm = function() {
                $('#updateTimeRangeForm').hide();
            };
            $scope.updateTimeRange = function() {
                if (!($scope.startTime && $scope.endTime)) {
                    alert("开始时间与截止时间不能为空");
                    return;
                }
                var startTime = $scope.startTime;
                var endTime = $scope.endTime;
                if (typeof(startTime) == "number" || typeof(startTime) == "string") {
                    startTime = new Date(startTime);
                }
                if (typeof(endTime) == "number" || typeof(endTime) == "string") {
                    endTime = new Date(endTime);
                }
                if (endTime - startTime <= 0) {
                    alert("开始时间必须大于截止时间");
                    return;
                }
                if (startTime - new Date() > 0) {
                    alert("开始时间必须小于当前时间");
                    return;
                }
                $scope.dateText = '自定义时间';
                $scope.updateData(true);
            };
            // $scope.$watch('startTime', function() {
            //     if ($scope.startTime - 0 > $scope.endTime - 0) {
            //         $scope.startTime = $scope.endTime;
            //     }
            //     $scope.updateData(true);
            // });
            // $scope.$watch('endTime', function() {
            //     if ($scope.endTime - 0 < $scope.startTime - 0) {
            //         $scope.endTime = $scope.startTime;
            //     }
            //     $scope.updateData(true);
            // });

            //统计数据的类型
            $scope.dataOptions = statusService.getStatusPage();

            $scope.changeType = function() {
                $scope.nowUserId = 0;
                $scope.nowUserName = null;
                $scope.updateData();
            };

            //统计数据的项目
            $scope.projectOptions = [{
                id: 0,
                name: '所有项目'
            }];

            $scope.changeProject = function() {
                $scope.nowUserId = 0;
                $scope.nowUserName = null;
                drawPieChart($scope.statsData);

                $scope.updateData();
            };

            var filterUserData = function(input) {
                var data;
                if ($scope.currentProjectId != 0) {
                    data = input.filter(function(rec) {
                        return rec.projectId == $scope.currentProjectId;
                    });
                } else
                    data = input;
                if ($scope.dataType == 0) {
                    return data.filter(function(rec) {
                        return $scope.nowUserId == 0 || $scope.nowUserId == rec.creatorId;
                    });
                }
                if ($scope.dataType == 1 || $scope.dataType == 4) {
                    return data.filter(function(rec) {
                        return $scope.nowUserId == 0 || $scope.nowUserId == rec.assigneeId;
                    });
                }
                if ($scope.dataType == 2 || $scope.dataType == 3) {
                    return data.filter(function(rec) {
                        return $scope.nowUserName == null || $scope.nowUserName == rec.userName;
                    });
                }
            };

            $scope.updateCharts = function(color) {
                var data = filterUserData($scope.statsData);
                $scope.filteredData = data;
                $scope.isBlank = data.length == 0;
                $scope.details = data;
                $scope.updateLineChart(data, color);
                $scope.updatePunchCard(data);
            };

            $scope.updateLineChart = function(data, color) {
                var col = color == undefined ? Highcharts.getOptions().colors[0] : color;
                if ($scope.nowUserName == null) {
                    col = Highcharts.getOptions().colors[0];
                }
                var st = d3.time.day.floor(new Date($scope.startTime)) - 0;
                var ed = d3.time.day.ceil(new Date($scope.endTime)) - 1;
                // step and date format
                var step;
                var formatD3;
                if (ed - st <= 48 * 3600 * 1000) {
                    step = 3600 * 1000;
                    formatD3 = "%m月%d日 %H:%M";
                } else if (ed - st <= 30 * 24 * 3600 * 1000) {
                    step = 24 * 3600 * 1000;
                    formatD3 = "%m月%d日";
                } else {
                    step = 7 * 24 * 3600 * 1000;
                    formatD3 = "%Y年%m月%d日";
                }
                // set up key
                $scope.lineChartData = data.map(function(rec) {
                    rec.myKey = Math.floor((rec.timestamp - st) / step);
                    // DEBUG
                    if (rec.myKey == -1) {
                        //console.log(rec.timestamp);
                    }
                    return rec;
                });
                // nest and rollup
                if ($scope.dataType == 3) {
                    $scope.lineChartData = d3.nest().key(function(value) {
                        return value.myKey;
                    }).rollup(function(leave) {
                        return leave.reduce(function(acc, ele) {
                            return acc + ele.loc;
                        }, 0);
                    }).entries($scope.lineChartData);
                } else {
                    $scope.lineChartData = d3.nest().key(function(value) {
                        return value.myKey;
                    }).rollup(function(leave) {
                        return leave.length;
                    }).entries($scope.lineChartData);
                }

                $scope.lineChartData.sort(function(a, b) {
                    return a.key > b.key;
                });

                //console.log($scope.lineChartData);
                var limit = Math.floor((ed - st + 1) / (step));
                var tpLineData = new Array();
                for (var i = 0; i <= limit; ++i)
                    tpLineData[i] = 0;
                for (var i = 0; i < $scope.lineChartData.length; ++i) {
                    var tp = $scope.lineChartData[i].key;
                    if (tp >= 0 && tp <= limit) tpLineData[tp] = $scope.lineChartData[i].values;
                    // DEBUG
                    else {
                        console.log(tp);
                        console.log($scope.lineChartData[i].key);
                        console.log(st);
                        console.log(step);
                    }
                }
                //console.log(tpLineData);
                $('.lineChart').highcharts({
                    chart: {
                        width: 650,
                        height: 250,
                        backgroundColor: 'rgba(0,0,0,0)'
                    },
                    title: {
                        text: null
                    },
                    xAxis: {
                        type: 'datetime',
                        title: {
                            text: null
                        },
                        dateTimeLabelFormats: {
                            hour: '%e日 %H:%M',
                            day: '%b %e日',
                            week: '%b %e日',
                            month: '%b %e日',
                            year: '%b %e日'
                        }
                    },
                    yAxis: {
                        title: ''
                    },
                    tooltip: {
                        formatter: function() {
                            var tp = d3.time.format(formatD3)(new Date(this.x));
                            var tp2 = d3.time.format(formatD3)(new Date(this.x + step));
                            return tp + ' ~ ' + tp2 + '<br /><b>' + this.series.name + ': ' + this.y + '</b><br/>';
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    plotOptions: {
                        area: {
                            cursor: 'pointer',
                            color: col,
                            fillColor: {
                                linearGradient: {
                                    x1: 0,
                                    y1: 0,
                                    x2: 0,
                                    y2: 1
                                },
                                stops: [
                                    [0, col],
                                    [1, Highcharts.Color(col).setOpacity(0).get('rgba')]
                                ]
                            },
                            lineWidth: 1,
                            marker: {
                                enabled: false
                            },
                            shadow: false,
                            states: {
                                hover: {
                                    lineWidth: 1
                                }
                            },
                            threshold: null
                        }
                    },

                    series: [{
                        type: 'area',
                        name: $scope.nowUserName != null ? $scope.nowUserName : '全体成员',
                        pointInterval: step,
                        pointStart: d3.time.day.floor(new Date($scope.startTime)) - 0,
                        point: {
                            events: {
                                click: function() {
                                    showLineDetail(this.x, step);
                                }
                            }
                        },
                        data: tpLineData
                    }]
                });

            };

            var showLineDetail = function(startTime, step) {
                $scope.details = $scope.filteredData.filter(function(rec) {
                    return rec.timestamp >= startTime && rec.timestamp < startTime + step;
                });
                $scope.isBlank = $scope.details.length == 0;
                $scope.$apply();
            };

            $scope.$on('brickClicked', function(event, item) {
                $scope.details = $scope.filteredData.filter(function(rec) {
                    var tmp = new Date(rec.timestamp);
                    return tmp.getDay() == item.day && tmp.getHours() == item.hour;
                });
                $scope.isBlank = $scope.details.length == 0;
                //$scope.$apply();
            });

            $scope.updatePunchCard = function(data) {
                for (var i = 0; i < 7; ++i)
                    for (var j = 0; j < 24; ++j)
                        $scope.punchCard[i][j] = 0;

                var tpTime = new Date();
                for (var i = 0; i < data.length; ++i) {
                    tpTime.setTime(data[i].timestamp);
                    if ($scope.dataType == 3) {
                        $scope.punchCard[tpTime.getDay()][tpTime.getHours()] += data[i].loc;
                    } else
                        $scope.punchCard[tpTime.getDay()][tpTime.getHours()] ++;
                }
                $scope.$broadcast('punchCardChanged');
            };

            $scope.updateData = function(updateWorkModel) {
                var start = $scope.startTime - 0;
                var end = $scope.endTime - 0;
                var ceil = d3.time.day.ceil($scope.endTime) - 0;
                if (end == ceil)
                    end += 1;
                if ($scope.dataType == 0)
                    companyActivityStats.getActivityStats(start, end)
                    .then(successFun);
                else if ($scope.dataType == 1) {
                    $q.all([companyTodoStats.getTodoStatsData(start, end), companyStepStats.getStepStatsData(start, end)])
                        .then(function(result) {
                            var data = [];
                            data = data.concat(result[0]);
                            data = data.concat(result[1]);
                            $scope.statsData = setTimestamp(data);
                            drawPieChart($scope.statsData);
                            $scope.updateCharts();
                        });
                } else if ($scope.dataType == 4) {
                    companyBugStats.getBugStatsData(start, end).then(successFun);
                } else
                    companyCommitStats.getCommitStatsData(start, end).then(successFun);
                if (updateWorkModel)
                    workModel.getWorkModelData(start, end);
            };

            var successFun = function(data) {
                $scope.statsData = setTimestamp(data);
                drawPieChart($scope.statsData);
                $scope.updateCharts();
            };

            var setTimestamp = function(data) {
                return data.map(function(rec) {
                    if ($scope.dataType == 0) {
                        rec.timestamp = rec.created;
                    } else if ($scope.dataType == 1 || $scope.dataType == 4) {
                        // 任务 + step + bug
                        rec.completedTime !== undefined ? rec.timestamp = rec.completedTime : rec.timestamp = rec.updated;
                    } else if ($scope.dataType == 2 || $scope.dataType == 3) {
                        rec.timestamp = rec.order;
                    }
                    return rec;
                });
            };

            $scope.openDTPicker = function($event, str) {
                $event.preventDefault();
                $event.stopPropagation();
                //console.log('show ' + str);
                $scope.dateText = "自定义时间";
                if (str == 'start') {
                    $scope.startDatePickerShow = true;
                    $scope.endDatePickerShow = false;
                }
                if (str == 'end') {
                    $scope.startDatePickerShow = false;
                    $scope.endDatePickerShow = true;
                }
                //console.log($scope.startDatePickerShow + ' ' + $scope.endDatePickerShow);
            };

            $scope.dataPickerClose = function(str) {
                //console.log('close ' + str);
                if (str == 'start') {
                    $scope.startDatePickerShow = false;
                }
                if (str == 'end') {
                    $scope.endDatePickerShow = false;
                }
                //console.log($scope.startDatePickerShow + ' ' + $scope.endDatePickerShow);
            };


            var drawPieChart = function(input) {
                $('.pieChart').children().remove();
                var result;
                var specs = {
                    id: '.pieChart',
                    enableDataLabel: true,
                    title: ''
                };
                var currentUser = {
                    name: $scope.nowUserName
                };
                var data;
                if ($scope.currentProjectId != 0) {
                    data = input.filter(function(rec) {
                        return rec.projectId == $scope.currentProjectId;
                    });
                } else
                    data = input;
                switch ($scope.dataType) {
                    case 0:
                        result = pieChart.groupActivityData(data, currentUser);
                        specs.name = '活动数目';
                        specs.data = result.data;
                        pieChart.draw(specs);
                        return;
                    case 1:
                        result = pieChart.groupTodoData(data, currentUser);
                        specs.name = '完成的任务数目';
                        specs.data = result.data;
                        pieChart.draw(specs);
                        return;
                    case 4:
                        result = pieChart.groupTodoData(data, currentUser);
                        specs.name = '修复的Bug数目';
                        specs.data = result.data;
                        pieChart.draw(specs);
                        return;
                    case 2:
                        result = pieChart.groupCommitsData(data, currentUser);
                        specs.name = '代码提交次数';
                        specs.data = result.data;
                        pieChart.draw(specs);
                        return;
                    case 3:
                        result = pieChart.groupLocData(data, currentUser);
                        specs.name = '代码增加行数';
                        specs.data = result.data;
                        pieChart.draw(specs);
                        return;
                    default:
                        return;
                }
            };

            $scope.$on('sliced', function(event, username, color) {
                //todo相关
                //console.log(color);
                if (username == '未分配责任人') {
                    if ($scope.nowUserId == 0 || $scope.nowUserId != -1) {
                        $scope.nowUserId = -1;
                        $scope.nowUserName = username;
                    } else {
                        $scope.nowUserId = 0;
                        $scope.nowUserName = null;
                    }
                    //console.log($scope.nowUserId);
                    $scope.updateCharts(color);
                } else {
                    // commit相关
                    if ($scope.dataType == 2 || $scope.dataType == 3) {
                        $scope.nowUserId = 0;
                        if ($scope.nowUserName == username)
                            $scope.nowUserName = null;
                        else
                            $scope.nowUserName = username;
                        $scope.updateCharts(color);
                    } else {
                        for (var i in $scope.userList) {
                            if ($scope.userList[i].name == username) {
                                if ($scope.nowUserId == 0 || $scope.nowUserId != $scope.userList[i].id) {
                                    $scope.nowUserId = $scope.userList[i].id;
                                    $scope.nowUserName = username;
                                } else {
                                    $scope.nowUserId = 0;
                                    $scope.nowUserName = null;
                                }
                                //console.log($scope.nowUserId);
                                $scope.updateCharts(color);
                                return;
                            }
                        }
                    }
                }
            });

            $scope.changeGraph = function() {
                $scope.showLine = !$scope.showLine;
            };

            $scope.gotoCommit = function(commit) {
                $timeout(function() {
                    drawer.open({
                        type: 'commit',
                        data: {
                            id: commit.id,
                            projectId: commit.projectId
                        }
                    });
                }, 500);
            };

            $scope.gotoTodo = function(todo) {
                $timeout(function() {
                    drawer.open({
                        type: todo.type,
                        data: {
                            id: todo.id,
                            projectId: todo.projectId
                        }
                    });
                }, 500);
            };

            company.getProjects(false, url.projectId(), url.companyId())
                .then(function(projects) {
                    var ret = [];
                    projects.forEach(function(ele) {
                        ret.push({
                            id: ele.id,
                            name: ele.name
                        });
                    });
                    $.merge($scope.projectOptions, ret);
                    $scope.updateData(true);
                });
        }
    ]);
