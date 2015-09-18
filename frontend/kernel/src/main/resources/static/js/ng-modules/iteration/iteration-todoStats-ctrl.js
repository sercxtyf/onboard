(function() {
    angular.module('iteration')
        .controller('iterationTodoStatsCtrl', ['$scope', '$http', '$state', 'url', 'user', 'iterationService', 'pieChart', 'columnRange',
            function($scope, $http, $state, url, user, iterationService, pieChart, columnRange) {
                var state = 0;
                if ($state.is('company.project.iteration.activeIteration.todoStats')) {
                    state = 1;
                } else if ($state.is('company.project.iteration.completedIteration.todoStats')) {
                    state = 2;
                }

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
                        return '本次迭代已完成任务起止时间区间图';
                    } else {
                        return '本次迭代未完成任务起止时间区间图';
                    }
                };

                //setting data
                $scope.busy = false;

                $chart1 = $('#todoChart');
                $chart2 = $('#uncompleted-todoChart');
                $pie1 = $('#pie-todo');
                $pie2 = $('#uncompleted-pie-todo');
                var width = parseInt($('.chart-container').css('width'), 10);
                $chart1.css('width', width);
                $chart2.css('width', width);

                var initActiveIteration = function() {
                    iterationService.getActiveIterations().then(function(iteration) {
                        if (iteration.id) {
                            $scope.activeIteration = iteration;
                            $scope.start = $scope.activeIteration.startTime;
                            $scope.end = $scope.activeIteration.endTime;
                            setTodos(iteration);
                            showData();
                        } else {
                            $state.go('company.project.iteration.createdIterations');
                        }
                    });
                };
                var initCompletedIteration = function() {
                    if ($scope.completedIteration != undefined && $scope.completedIteration.id != undefined) {
                        $scope.start = $scope.completedIteration.startTime;
                        $scope.end = $scope.completedIteration.endTime;
                        setTodos($scope.completedIteration);
                        showData();
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
                user.getProjectUsers().then(function(userList) {
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
                    var data = angular.copy(todos);
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
                        d.content = '#' + d.projectTodoId + ' ' + d.content;
                        return d;
                    });
                };

                var setTodos = function(iteration) {
                    $scope.todos = [];
                    $scope.uncompletedTodos = [];
                    var todos = processData(iteration.todos);
                    for (var i = 0; i < todos.length; i++) {
                        if (todos[i].status === 'closed') {
                            $scope.todos.push(todos[i]);
                        } else {
                            $scope.uncompletedTodos.push(todos[i]);
                        }
                    }
                };

                var reDrawTodoChart = function() {
                    $chart1.children().remove();
                    var data = $scope.todos.filter(function(d) {
                        return $scope.user.id === 0 || d.assigneeId === $scope.user.id;
                    });
                    var height = 200 + 50 * data.length;
                    //$chart1.css('height', 600 > height ? 600 : height);
                    $chart1.css('height', height);
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

                var reDrawUncompletedTodoChart = function() {
                    $chart2.children().remove();
                    var data = $scope.uncompletedTodos.filter(function(d) {
                        return $scope.user.id === 0 || d.assigneeId === $scope.user.id;
                    });
                    var height = 200 + 50 * data.length;
                    $chart2.css('height', height);
                    var result = columnRange.groupBarData(data);
                    var specs = {
                        id: '#uncompleted-todoChart',
                        title: '共有' + data.length + '个未完成任务',
                        name: '任务持续时间',
                        name2: '任务期限',
                        todos: data,
                        data: result.data,
                        data2: result.data2,
                        categories: result.categories
                    };
                    columnRange.drawTodoChart(specs);
                };

                var drawTodoPieChart = function(data) {
                    $('#pie-todo').children().remove();
                    var result = pieChart.groupTodoData(data, $scope.defaultUser);
                    var specs = {
                        id: '#pie-todo',
                        title: '该迭代中所有成员一共完成了' + result.total + '个任务',
                        name: '迭代任务完成数目',
                        data: result.data
                    };
                    pieChart.draw(specs);
                };

                var drawUncompletedTodoPieChart = function(data) {
                    $('#uncompleted-pie-todo').children().remove();
                    var result = pieChart.groupTodoData(data, $scope.defaultUser);
                    var specs = {
                        id: '#uncompleted-pie-todo',
                        title: '该迭代中所有成员一共有' + result.total + '个未完成任务',
                        name: '迭代未任务完成数目',
                        data: result.data
                    };
                    pieChart.draw(specs);
                };

                var showData = function() {
                    $scope.busy === true;
                    reDrawTodoChart();
                    reDrawUncompletedTodoChart();
                    if ($scope.todos.length !== 0) {
                        $scope.hasData = true;
                        $pie1.css('width', width);
                        drawTodoPieChart($scope.todos);
                    } else {
                        $scope.hasData = false;
                        $pie1.children().remove();
                    }
                    if ($scope.uncompletedTodos.length !== 0) {
                        $scope.uncompleted_hasData = true;
                        $pie2.css('width', width);
                        drawUncompletedTodoPieChart($scope.uncompletedTodos);
                    } else {
                        $scope.uncompleted_hasData = false;
                        $pie2.children().remove();
                    }
                    $scope.busy = false;
                };
                $scope.$watch('user', function(){
                    //$scope.user = user;
                    reDrawTodoChart();
                    reDrawUncompletedTodoChart();
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
