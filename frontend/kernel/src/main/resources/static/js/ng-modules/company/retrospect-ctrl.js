/**
 * Created by harttle on 12/10/14.
 */

// 回顾控制器

angular.module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            .state('company.retrospect', {
                url: '/retrospect',
                templateUrl: 'retrospect.html',
                controller: 'retrospectCtrl'
            })
    }])
    .controller('retrospectCtrl', ['$scope', '$http', 'url', 'theme', 'user', 'company', '$q', 'todolistService', 'todoService', '$state', 'drawer', '$timeout',
        function($scope, $http, url, theme, user, company, $q, todolistService, todoService, $state, drawer, $timeout) {
            var activityUrl = url.teamApiUrl() + '/activities';
            $scope.users = [];
            $scope.defaultAvatar = url.defaultAvatarUrl;
            //$scope.themes = theme.themes;

            var getUserById = function(id) {
                return $scope.users.filter(function(u) {
                    return u.id === id;
                })[0];
            };

            $scope.busy = false; // 正在载入
            $scope.hasNext = true; // 没有更多
            $scope.until = '';
            $scope.activities = [];
            var possibleMerge = -1;
            var activities;
            $scope.selectedUser = 0;
            // project selection
            $scope.selectedProject = 0;
            $scope.projectOptions = [{
                id: 0,
                name: '所有项目'
            }];

            $scope.changeProject = function() {
                $scope.activities = [];
                possibleMerge = -1;
                $scope.until = '';
                $scope.hasNext = true;
                $scope.nextPage();
            };
            // type selection
            $scope.selectedType = 'all';
            $scope.typeOptions = [{
                id: 'all',
                name: '所有类型'
            }, {
                id: 'todo',
                name: '任务'
            }, {
                id: 'discussion',
                name: '讨论'
            }, {
                id: 'pullrequest-push',
                name: '代码提交'
            }, {
                id: 'pull-request',
                name: '合并分支请求'
            }, {
                id: 'comment',
                name: '评论'
            }, {
                id: 'story',
                name: '需求'
            }, {
                id: 'step',
                name: '迭代中的任务'
            }, {
                id: 'bug',
                name: 'Bug'
            }];

            $scope.changeType = function() {
                $scope.activities = [];
                possibleMerge = -1;
                $scope.until = '';
                $scope.hasNext = true;
                $scope.nextPage();
            };

            var todolists = {};

            $q.all([
                company.getProjects(false, url.projectId(), url.companyId()),
                user.getCompanyUsersOrderByLastWeekActivities(false, url.companyId())
            ]).then(function(results) {
                $scope.users = results[1];
                var ret = [];
                results[0].forEach(function(ele) {
                    ret.push({
                        id: ele.id,
                        name: ele.name
                    });
                });
                $.merge($scope.projectOptions, ret);
                $scope.changeProject();
            });

            $scope.filterUser = function(userId) {
                if ($scope.busy) {
                    return;
                }
                $scope.selectedUser = userId;
                $scope.activities = [];
                possibleMerge = -1;
                $scope.until = '';
                $scope.hasNext = true;
                $scope.nextPage();
            };

            $scope.isSelected = function(userId) {
                return userId === $scope.selectedUser;
            };

            $scope.nextPage = function() {
                $scope.busy = true;
                activities = [];

                var params = {};
                if ($scope.until) params.until = $scope.until;
                if ($scope.selectedUser !== 0) params.userId = $scope.selectedUser;
                if ($scope.selectedProject !== 0) params.projectId = $scope.selectedProject;
                if ($scope.selectedType !== 'all') params.type = $scope.selectedType;

                $http.get(activityUrl, {
                        params: params
                    })
                    .success(function(data) {
                        $scope.hasNext = data.hasNext;
                        $scope.until = data.nextDay;
                        data.activities.map(function(aInDay) {
                            aInDay.map(function(aInProject) {
                                aInProject.map(function(a) {
                                    a.mergedActivities = [];
                                    a.involvedUsers = [getUserById(a.creatorId)];
                                    activities.push(a);
                                    a.mergedActivities.push(a);
                                });
                            });
                        });
                        var targetActivities = filter(activities);
                        processMerge(targetActivities);
                        //$timeout(bindClick, 1000);
                        $scope.busy = false;
                    }).error(function(data) {
                        $scope.stat = 'error';
                        $scope.msg = '载入错误';
                    });
            };

            var filter = function(data) {
                return data.filter(function(ele) {
                    switch (ele.attachType) {
                        case 'todo':
                            if (ele.action === 'create' || ele.action === 'closed') {
                                todoService.getTodoById(ele.projectId, ele.companyId, ele.attachId).then(
                                    function(todo) {
                                        if (todolists[todo.todolistId] === undefined) {
                                            todolists[todo.todolistId] = {};
                                            todolistService.getTodolistById(ele.projectId, ele.companyId, todo.todolistId).then(function(todolist) {
                                                ele.todolist = todolist;
                                                todolists[todo.todolistId] = todolist;
                                            });
                                        } else {
                                            var tempfun = function() {
                                                if (todolists[todo.todolistId].id === undefined) {
                                                    setTimeout(tempfun, 1000);
                                                } else {
                                                    $.extend(ele, {
                                                        todolist: todolists[todo.todolistId]
                                                    });
                                                }
                                            }
                                            tempfun();
                                        }
                                    }
                                );
                                if (ele.action === 'closed') {
                                    ele.subject = '完成了任务';
                                }
                                return true;
                            } else
                                return false;
                        case 'discussion':
                            if (ele.action === 'create') {
                                return true;
                            } else
                                return false;
                        case 'pullrequest-push':
                            if (ele.action === 'push') {
                                ele.subject = '提交了代码';
                                return true;
                            } else
                                return false;
                        case 'pull-request':
                            if (ele.action === 'merged') {
                                ele.subject = '合并了分支';
                                return true;
                            } else
                                return false;
                        case 'comment':
                            if (ele.action === 'reply') {
                                ele.subject = '回复了';
                                return true;
                            } else
                                return false;
                        case 'step':
                            if (ele.action === 'create' || ele.action === 'closed') {
                                ele.subject = '在迭代中' + ele.subject;
                                return true;
                            } else
                                return false;
                        case 'bug':
                            if (ele.action === 'create' || ele.action === 'complete') {
                                return true;
                            } else
                                return false;
                        case 'story':
                            if (ele.action === 'create')
                                return true;
                            else
                                return false;
                        default:
                            return false;
                    }
                });
            };

            var processMerge = function(data) {
                var temp = [];
                var cursor = -1;
                var len = 0;
                if (possibleMerge != -1) {
                    for (var i = possibleMerge; i < $scope.activities.length; i++) {
                        temp.push($scope.activities[i]);
                        len++;
                    }
                    cursor = 0;
                }
                // 处理新的数据
                for (var i = 0; i < data.length; i++) {
                    if (cursor == -1) {
                        cursor = 0;
                        temp.push(data[i]);
                        len++;
                    } else {
                        var j = cursor;
                        if (len !== temp.length) {
                            alert('算法错误');
                        }
                        for (; j < len; j++) {
                            if (canMerge(temp[j], data[i])) {
                                temp[j].mergedActivities.push(data[i]);
                                if (temp[j].involvedUsers.indexOf(data[i].involvedUsers[0]) === -1) {
                                    temp[j].involvedUsers.push(data[i].involvedUsers[0]);
                                }
                                break;
                            }
                        }
                        //no merge
                        if (j === len) {
                            temp.push(data[i]);
                            len++;
                            // refresh cursor
                            var latest = temp[len - 1].created;
                            for (j = cursor; j < len - 1; j++) {
                                if (withinTimePeriod(temp[j].created, latest))
                                    break;
                            }
                            cursor = j;
                        }
                    }
                }
                // 更新scope.activities
                if (possibleMerge !== -1) {
                    $scope.activities.splice(possibleMerge, $scope.activities.length - possibleMerge);
                    //temp.splice(0, $scope.activities.length - possibleMerge);
                }
                $.merge($scope.activities, temp);
                // 更新possibleMerge
                if (possibleMerge === -1)
                    possibleMerge = cursor;
                else
                    possibleMerge += cursor;
            };

            var thresMilliSeconds = 5 * 60 * 1000;
            var withinTimePeriod = function(later, earlier) {
                if (later - earlier <= thresMilliSeconds)
                    return true;
                else
                    return false;
            }
            var canMerge = function(a1, a2) {
                if (!withinTimePeriod(a1.created, a2.created) || a1.projectId !== a2.projectId || a1.attachType !== a2.attachType)
                    return false;
                switch (a1.attachType) {
                    case 'todo':
                    case 'step':
                    case 'story':
                    case 'bug':
                    case 'discussion':
                        if (a2.creatorId === a1.creatorId && a2.action === a1.action)
                            return true;
                        else
                            return false;
                    case 'pull-request':
                    case 'pullrequest-push':
                        if (a2.creatorId === a1.creatorId)
                            return true;
                        else
                            return false;
                    case 'comment':
                        if (a2.target === a1.target)
                            return true;
                        else
                            return false;
                    default:
                        return false;
                }
            };

            var projectTodoUrl = function(p, id) {
                return url.projectApiUrl(p) + '/todos/projectTodo/' + id;
            };
            /*var bindClick = function() {
                $('a.pullrequest, a.commit-todo').unbind('click');
                $('a.pullrequest').click(function() {
                    var id = parseInt($(this).text());
                    var projectId = parseInt($(this).parents('.record-content').attr('data-project-id'));
                    drawer.close();
                    $state.go('company.project.repository.pullrequests.detail', {
                        id: id,
                        projectId: projectId
                    });
                    return false;
                });

                $('a.commit-todo').click(function() {
                    var id = parseInt($(this).text());
                    //console.log($(this).parents('.record-content'));
                    var projectId = parseInt($(this).parents('.record-content').attr('data-project-id'));
                    $http.get(projectTodoUrl(projectId, id)).success(
                        function(todo) {
                            if (todo !== "") {
                                var option = {
                                    type: 'todo',
                                    params: {
                                        id: todo.id
                                    },
                                    data: {
                                        id: todo.id,
                                        projectId: projectId
                                    }
                                };
                                $timeout(function() {
                                    drawer.open(option);
                                });
                            }
                        });
                    return false;
                });
            };*/
        }
    ]);
    /*.filter('wrapCommitTodoLink', function() {
        return function(input, key) {
            var p_todo = new RegExp('#([0-9]+)', 'g');
            var p_pullrequest = new RegExp('pull request #([0-9]+)', 'g');
            if (input !== undefined) {
                var temp = input.replace(p_pullrequest, '#<a href="#" class="pullrequest">$1</a>');
                return temp.replace(p_todo, '#<a href="#" class="commit-todo">$1</a>');
            } else
                return '';
        };
    });*/
