(function() {
    angular.module('iteration')
        .controller('iterationOverallStatsCtrl', ['$q', '$scope', '$http', '$state', 'url', 'user', 'iterationService', 'todoStats', 'activityStats', 'commitStats', 'stackedBar',
            function($q, $scope, $http, $state, url, user, iterationService, todoStats, activityStats, commitStats, stackedBar) {
                var state = 0;
                if ($state.is('company.project.iteration.activeIteration.overallStats')) {
                    state = 1;
                } else if ($state.is('company.project.iteration.completedIteration.overallStats')) {
                    state = 2;
                }

                //setting data
                $scope.busy = false;
                $scope.todos = [];
                $scope.activities = [];
                $scope.commits = [];

                $scope.graphInfo = function() {
                    return '活动、代码、任务等整体进度一览';
                };

                $chart = $('#overallChart');
                var width = parseInt($('.chart-container').css('width'), 10);
                $chart.css('width', width);

                var initActiveIteration = function() {
                    iterationService.getActiveIterations().then(function(iteration) {
                        if (iteration.id) {
                            $scope.activeIteration = iteration;
                            $scope.start = $scope.activeIteration.startTime;
                            $scope.end = $scope.activeIteration.endTime;
                            getData();
                        } else {
                            $state.go('company.project.iteration.createdIterations');
                        }
                    });
                };
                var initCompletedIteration = function() {
                    if ($scope.completedIteration != undefined && $scope.completedIteration.id != undefined) {
                        $scope.start = $scope.completedIteration.startTime;
                        $scope.end = $scope.completedIteration.endTime;
                        getData();
                    }
                };
                $scope.refresh = function() {
                    if (state === 1) {
                        initActiveIteration();
                    } else if (state === 2) {
                        initCompletedIteration();
                    }
                };
                $scope.userList = [];
                user.getProjectUsers(true).then(function(userList) {
                    $scope.userList = userList;
                    if (state === 1) {
                        initActiveIteration();
                    }
                    if (state === 2) {
                        $scope.$watch("completedIteration", function() {
                            initCompletedIteration();
                        });
                    }
                });

                var processTodoData = function(data) {
                    return data.map(function(d) {
                        if (d.assigneeId === undefined) {
                            //d.assigneeId = d.creatorId;
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
                        return $scope.user.id === 0 || d.assigneeId === $scope.user.id;
                    });
                    // activity
                    var activities = $scope.activities.filter(function(d) {
                        return $scope.user.id === 0 || d.creatorId === $scope.user.id;
                    });
                    // commits
                    var commits = $scope.commits.filter(function(d) {
                        return $scope.user.id === 0 || d.userId === $scope.user.id || d.userName === $scope.user.name;
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

                $scope.$watch('user', function() {
                    reDrawOverallChart();
                });
                $scope.$on('timeChange', function(event, data) {
                    initActiveIteration();
                });

                $scope.ready = function() {
                    if (state === 1)
                        return $scope.iterationId !== 0;
                    else
                        return ($scope.completedIteration !== undefined && $scope.completedIteration.id !== undefined);
                };
            }
        ]);

})();
