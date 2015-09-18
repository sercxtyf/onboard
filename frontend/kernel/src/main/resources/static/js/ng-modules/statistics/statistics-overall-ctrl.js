angular.module('statistics')
    .controller('statisticsOverallCtrl', ['$q', '$scope', '$http', '$state', 'user', 'url', 'todoStats', 'activityStats', 'commitStats', 'stackedBar',
        function($q, $scope, $http, $state, user, url, todoStats, activityStats, commitStats, stackedBar) {
            $scope.user = {};
            $scope.todos = [];
            $scope.activities = [];
            $scope.commits = [];
            $scope.busy = false;
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

            $scope.graphInfo = function() {
                return '活动、代码、任务等整体进度一览';
            };

            $scope.userList = [];
            user.getProjectUsers().then(function(userList) {
                $scope.userList = userList;
            }).then(function() {
                user.getCurrentUser().then(function(user) {
                    $scope.user = user;
                    getData();
                });
            });

            var id = '#overallChart';
            $chart = $('#overallChart');
            var width = parseInt($('.chart-container').css('width'), 10);
            $chart.css('width', width);

            var processTodoData = function(data) {
                return data.map(function(d) {
                    if (d.assigneeId === undefined) {
                        if (d.completerId !== undefined) {
                            d.assigneeId = d.completerId;
                        }
                        else
                            d.assigneeId = -1;
                    }
                    d.assigneeName = "未分配责任人";
                    for (var key in $scope.userList) {
                        if ($scope.userList[key].id === d.assigneeId) {
                            d.assigneeName = $scope.userList[key].name;
                            break;
                        }
                    }
                    if (d.content.length > 20) {
                        d.content = d.content.slice(0, 20) + '...';
                    }
                    return d;
                });
            };

            var getData = function() {
                if ($scope.busy === true)
                    return;
                $scope.busy = true;
                $q.all([todoStats.getTodoStatsData($scope.start - 0, $scope.end - 0),
                        activityStats.getActivityStats($scope.start - 0, $scope.end - 0),
                        commitStats.getCommitStatsData($scope.start - 0, $scope.end - 0)
                    ])
                    .then(function(data) {
                        // todo
                        $scope.todos = processTodoData(data[0]);
                        // activity
                        var array = d3.nest().key(function(d) {
                            return d.creatorName;
                        }).entries(data[1]);
                        array.forEach(function(d) {
                            $.merge($scope.activities, activityStats.processData(d.values));
                        });
                        // commits
                        $scope.commits = data[2];
                        // draw
                        reDrawOverallChart();
                        $scope.busy = false;
                    }, function(data) {
                        $scope.busy = false;
                        alert('获取数据超时！');
                    });
            };

            var reDrawOverallChart = function() {
                $chart.children().remove();
                var series = [];
                // todo
                var todos = $scope.todos.filter(function(d) {
                    return d.assigneeId === $scope.user.id;
                });
                // activity
                var activities = $scope.activities.filter(function(d) {
                    return d.creatorId === $scope.user.id;
                });
                // commits
                var commits = $scope.commits.filter(function(d) {
                    return d.userId === $scope.user.id || d.userName === $scope.user.name;
                });
                // series
                var result = stackedBar.groupData($scope.start - 0, $scope.end - 0, todos, activities, commits);
                series.push({
                    name: '任务',
                    data: result.todos,
                    color: 'rgba(165,170,217,1)',
                    stack: 'count'
                });
                series.push({
                    name: '活动',
                    data: result.activities,
                    color: 'rgba(126,86,134,1)',
                    stack: 'count'
                });
                series.push({
                    name: '代码提交',
                    data: result.commits,
                    color: 'rgba(248,161,63,1)',
                    stack: 'count'
                });
                series.push({
                    name: '代码影响行数',
                    data: result.loc,
                    color: 'rgba(186,60,61,1)',
                    stack: 'loc',
                    yAxis: 1
                });
                // draw
                var specs = {
                    id: '#overallChart',
                    series: series,
                    categories: result.categories
                };
                stackedBar.drawOverallChart(specs);
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
                        getData();
                    }
                }
            };

            $scope.$on('chooseUser', function(event, user) {
                if (user.id !== $scope.user.id) {
                    $scope.user = user;
                    reDrawOverallChart();
                }
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

        }
    ]);
