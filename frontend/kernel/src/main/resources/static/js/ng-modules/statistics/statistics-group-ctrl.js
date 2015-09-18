angular.module('statistics')
    .controller('statisticsGroupCtrl', ['$scope', '$http', '$state', 'activityPunchCard', 'user', 'url', 'activityStats', 'pieChart',
        function($scope, $http, $state, activityPunchCard, user, url, activityStats, pieChart) {
            $scope.tab = 1;
            $scope.isSet = function(checkTab) {
                return $scope.tab === checkTab;
            };
            $scope.setTab = function(activeTab, $event) {
                if ($scope.tab !== activeTab) {
                    $scope.$broadcast('tabSwitch', activeTab);
                }
                $scope.tab = activeTab;
                $event.stopPropagation();
            };

            $scope.graphInfo = function() {
                if (this.tab === 1) {
                    return '各个整点的活动数量';
                } else {
                    return '所有成员在Onboard上的活动';
                }
            };

            $scope.user = {};
            $scope.activities = [];
            $scope.busy = false;
            $scope.busy_pie = false;
            $scope.len = 20;
            $scope.noMorePages = function() {
                return $scope.len >= $scope.activities.length;
            };
            $scope.addMore = function() {
                $scope.len += 20;
            };
            var formatString = '%Y-%m-%d';
            var today = new Date();
            var monthBefore = new Date();
            //$scope.end = d3.time.day(today);
            $scope.end = today - 0;
            //monthBefore.setMonth(monthBefore.getMonth() - 1);
            monthBefore.setDate(monthBefore.getDate() - 6);
            //$scope.start = d3.time.format(formatString)(monthBefore);
            $scope.start = monthBefore - 0;
            //$scope.start = d3.time.day(monthBefore);

            // punchCard graph
            var id = '#activityChart';
            $chart = $('#activityChart');
            var height = parseInt($(id).css('height'), 10);
            var width = parseInt($(id).css('width'), 10);

            var drawActivityPunchCardSetUp = function() {
                activityPunchCard.setDimension(height, width);
                activityPunchCard.setupCanvas(id);
            };

            var processData = function(data) {
                var result = [];
                if (data.length === 0)
                    return result;
                result.push({
                    created: data[0].created,
                    projectName: data[0].projectName
                });
                var latest = data[0];
                for (var i = 1; i <= data.length - 1; i++) {
                    //如果生成的活动记录间隔在1分钟以上
                    if (latest.created - data[i].created >= 60000) {
                        result.push({
                            created: data[i].created,
                            projectName: data[i].projectName
                        });
                        latest = data[i];
                    }
                }
                return result;
            };
            var addLoading = function() {
                $chart.find('.loading').remove();
                d3.select("#activityChart>svg")
                    .append("text")
                    .attr("class", "loading")
                    .text("Loading...")
                    .attr("x", function() {
                        return width / 2;
                    })
                    .attr("y", function() {
                        return height / 2;
                    });
            };
            var getData = function(id) {
                if ($scope.busy === true)
                    return;
                $scope.busy = true;
                addLoading();
                $http.get(url.projectApiUrl() + '/users/' + id + '/activities', {
                        params: {
                            start: $scope.start - 0,
                            end: $scope.end - 0
                        }
                    })
                    .success(function(data) {
                        $scope.activities = data;
                        var acts = processData(data);
                        activityPunchCard.setRawData(acts);
                        //console.log(data);
                        if (data.length !== 0) {
                            $('#activityChart').children().remove();
                            //$('.loading').remove();
                            activityPunchCard.drawGraph();
                        } else {
                            $('#activityChart').children().remove();
                            activityPunchCard.drawGraph();
                            addLoading();
                            $('.loading').text('没有数据^^');
                        }
                        $scope.busy = false;
                    })
                    .error(function() {
                        $scope.busy = false;
                        $('.loading').remove();
                        alert('获取个人活动数据超时！');
                    });
            };

            var getActivities = function() {
                if ($scope.busy_pie === true)
                    return;
                $scope.busy_pie = true;
                var activities = [];
                activityStats.getActivityStats($scope.start - 0, $scope.end - 0).then(function(data) {
                    var array = d3.nest().key(function(d) {
                        return d.creatorName;
                    }).entries(data);
                    array.forEach(function(d) {
                        $.merge(activities, activityStats.processData(d.values));
                    });
                    if (activities.length > 0) {
                        $('#pie-activity').css('width', width);
                        drawPieChart(activities);
                    }
                    $scope.busy_pie = false;
                }, function() {
                    $scope.busy_pie = false;
                    alert('获取团队活动数据超时！');
                });
            };

            var drawPieChart = function(data) {
                $('#pie-activity').children().remove();
                var result = pieChart.groupActivityData(data, $scope.currentUser);
                var specs = {
                    id: '#pie-activity',
                    title: '所有成员在Onboard上一共有' + result.total + '次活动',
                    name: '活动数目',
                    data: result.data
                };
                pieChart.draw(specs);
            };

            $scope.refresh = function() {
                if ($scope.start > $scope.end) {
                    alert('起始时间不能晚于截止时间！');
                } else {
                    var limit = new Date($scope.end - 0);
                    limit.setMonth(limit.getMonth() - 6);
                    if ($scope.start < limit) {
                        alert('日期范围不能超出半年-_-');
                    } else {
                        getData($scope.user.id);
                        getActivities();
                    }
                }
            };

            $scope.$on('chooseUser', function(event, user) {
                if (user.id !== $scope.user.id) {
                    $scope.user = user;
                }
                getData(user.id);
            });

            $scope.selectTimeSpan = function(num) {
                $scope.started = false;
                $scope.ended = false;
                var t = new Date($scope.end - 0);
                switch (num) {
                    case 1:
                        $scope.start = t.setDate(t.getDate() - 6) - 0;
                        break;
                    case 2:
                        $scope.start = t.setDate(t.getDate() - 13) - 0;
                        break;
                    case 3:
                        $scope.start = t.setMonth(t.getMonth() - 1) - 0;
                        break;
                    case 4:
                        $scope.start = t.setMonth(t.getMonth() - 3) - 0;
                        break;
                    case 5:
                        $scope.start = t.setMonth(t.getMonth() - 6) - 0;
                        break;
                }
                $scope.refresh();
            };

            //date picker
            $scope.started = false;
            $scope.ended = false;
            $scope.openDTPicker = function($event, num) {
                $event.preventDefault();
                $event.stopPropagation();
                if (num === 1) {
                    $scope.started = true;
                    $scope.ended = false;
                } else {
                    $scope.ended = true;
                    $scope.started = false;
                }
            };

            drawActivityPunchCardSetUp();
            activityPunchCard.draw($scope.activities);

            user.getCurrentUser().then(function(user) {
                $scope.user = user;
                getData($scope.user.id);
                getActivities();
            });
        }
    ]);
