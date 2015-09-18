angular.module('statistics')
    .controller('statisticsTodoCtrl', ['$scope', '$http', '$state', 'user', 'url', 'todoStats', 'drawer', 'pieChart', 'columnRange',
        function($scope, $http, $state, user, url, todoStats, drawer, pieChart, columnRange) {
            $scope.user = {};
            $scope.todos = [];
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
                return '已完成任务起止时间区间图';
            };

            $scope.userList = [];
            user.getProjectUsers().then(function(userList) {
                $scope.userList = userList;
            }).then(function() {
                user.getCurrentUser().then(function(user) {
                    $scope.user = user;
                    getData($scope.user.id);
                });
            });

            var id = '#todoChart';
            $chart = $('#todoChart');
            var width = parseInt($('.chart-container').css('width'), 10);
            $chart.css('width', width);

            var observer = {
                afterUpdateTodo: function(todo) {
                    if (todo.assigneeId !== todo.origin.assigneeId 
                        || todo.content !== todo.origin.content 
                        || todo.dueDate !== todo.origin.dueDate 
                        || todo.status !== todo.origin.status 
                        || todo.updated !== todo.origin.updated
                        || todo.completerId !== todo.origin.completerId 
                        || todo.completeTime !== todo.origin.completeTime 
                        || todo.startTime !== todo.origin.startTime) {
                        $scope.refresh();
                    }
                },
                afterDeleteTodo: function(todo) {
                    $scope.refresh();
                }
            };

            var processData = function(todos) {
                todos.forEach(function(d) {
                    d.addParent(observer);
                });
                data = angular.copy(todos);
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
                    if (d.content.match(/^#\d/) === null) {
                        if (d.content.length > 20) {
                            d.content = d.content.slice(0, 20) + '...';
                        }
                        d.content = '#' + d.projectTodoId + ' ' + d.content;
                    }
                    return d;
                });
            };

            var getData = function(id) {
                if ($scope.busy === true)
                    return;
                $scope.busy = true;
                todoStats.getTodoStatsData($scope.start - 0, $scope.end - 0).then(function(data) {
                    $scope.todos = processData(data);
                    if (data.length !== 0) {
                        $scope.hasData = true;
                        reDrawTodoChart();
                        $('#pie-todo').css('width', width);
                        drawPieChart($scope.todos);
                    } else {
                        $scope.hasData = false;
                        reDrawTodoChart();
                        $('#pie-todo').children().remove();
                    }
                    $scope.busy = false;
                }, function(data) {
                    $scope.busy = false;
                    alert('获取数据超时！');
                });
            };

            var reDrawTodoChart = function() {
                $chart.children().remove();
                var data = $scope.todos.filter(function(d) {
                    return d.assigneeId === $scope.user.id;
                });
                var height = 200 + 50 * data.length;
                //$chart.css('height', 600 > height ? 600 : height);
                $chart.css('height', height);
                var result = columnRange.groupBarData(data);
                var specs = {
                    id: '#todoChart',
                    title: '共完成任务' + data.length + '个',
                    name: '任务持续时间',
                    name2: '任务期限',
                    todos: data,
                    data: result.data,
                    data2: result.data2,
                    categories: result.categories
                };
                columnRange.drawTodoChart(specs);
            };
            // Pie
            var drawPieChart = function(data) {
                $('#pie-todo').children().remove();
                var result = pieChart.groupTodoData(data, $scope.currentUser);
                var specs = {
                    id: '#pie-todo',
                    title: '所有成员一共完成了' + result.total + '个任务',
                    name: '任务完成数目',
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
                    }
                }
            };

            $scope.$on('chooseUser', function(event, user) {
                if (user.id !== $scope.user.id) {
                    $scope.user = user;
                    reDrawTodoChart();
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
