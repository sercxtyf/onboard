/**
 * Created by ChenLong on 12/10/14.
 */

angular.module('data')
    .service('todoStats', ['url', '$http', '$q', 'todoDataService', function(url, $http, $q, todoDataService) {

        var _start = 0,
            _end = 0;
        var _todos = [];
        var _api = function(p) {
            return url.projectApiUrl(p) + "/todos/completed";
        };
        var _projectId = url.projectId();

        this.getTodoStatsData = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            var projectId = url.projectId();
            if (projectId !== _projectId || _start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $http.get(_api(projectId), {
                    params: {
                        start: start,
                        end: end
                    }
                }).then(function(response) {
                    _start = since;
                    _end = until;
                    _projectId = projectId;
                    _todos = todoDataService.registerTodos(response.data);
                    return _todos;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_todos);
                return deferred.promise;
            }
        };
    }])
    .service('companyTodoStats', ['url', '$http', '$q', 'todoDataService', 'user', function(url, $http, $q, todoDataService, user) {
        var _start = 0,
            _end = 0;
        var _todos = [];
        var _url = 'api/' + url.companyId() + '/stats/completedTodos';

        var processTodo = function(data, userList) {
            return data.map(function(d) {
                if (d.assigneeId === undefined) {
                    d.assigneeId = -1;
                }
                d.assigneeName = "未分配责任人";
                for (var key in userList) {
                    if (userList[key].id === d.assigneeId) {
                        d.assigneeName = userList[key].name;
                        break;
                    }
                }
                return d;
            });
        };

        this.getTodoStatsData = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            if (_start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $q.all([$http.get(_url, {
                    params: {
                        start: start,
                        end: end
                    }
                }), user.getCompanyUsers(false, url.companyId())]).then(function(result) {
                    var userList = result[1];
                    var todos = result[0].data;
                    _start = since;
                    _end = until;
                    //_todos = todoDataService.registerTodos(response.data);
                    _todos = processTodo(todos, userList)
                    return _todos;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_todos);
                return deferred.promise;
            }
        };
    }]);
