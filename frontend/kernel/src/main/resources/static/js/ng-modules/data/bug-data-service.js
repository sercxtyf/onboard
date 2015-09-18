angular.module('data')
    .service('bugDataService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 'todolistService',
        'baseService',
        function(url, $http, $q, filterFilter, todoService, filterFilter, todolistService, baseService) {

            var modelType = 'bug';
            this.Bug = function() {
                this.getApi = function() {
                    var api = [url.projectApiUrl(this.projectId, this.companyId), 'bugs/'].join("/");
                    if (this.id) {
                        api = api + this.id;
                    }
                    return api;
                };
                this.afterinit = function() {
                    this.content = this.title;
                    this.assignee = this.bugAssigneeDTO;
                };
                this.getDTO = function() {
                    return {
                        assigneeId: this.assigneeId,
                        bugType: this.bugType,
                        dueDate: this.dueDate,
                        deleted: this.deleted,
                        description: this.description,
                        dueTime: this.dueTime,
                        id: this.id,
                        priority: this.priority,
                        status: this.status,
                        title: this.title,
                        idInProject: this.idInProject,
                        projectId: this.projectId,
                        companyId: this.companyId
                    };
                };

                this.updateIterationStatus = function(status) {
                    this.iterationStatus = status;
                    if (status === 'closed') {
                        this.status = 0;
                    } else if (status === 'todo') {
                        this.status = 1;
                    } else if (status === 'inprogress') {
                        this.status = 2;
                    } else if (status === 'fixed') {
                        this.status = 3;
                    } else if (status === 'approved') {
                        this.status = 4;
                    } else if (status === 'reviewed') {
                        this.status = 5;
                    } else if (status === 'verified') {
                        this.status = 6;
                    }

                    return this.update();
                };
            };
            this.Bug.prototype = baseService.Item;


            baseService.Items[modelType] = this.Bug;


            this.findById = function(id) {
                return baseService.getItem(modelType, id);
            };

            this.register = function(DTOs) {
                for (var index = 0; index < DTOs.length; index++) {
                    var Bug = baseService.addItem(DTOs[index]);
                    DTOs[index] = Bug;
                }
                return DTOs;
            };
        }
    ])
    .service('companyBugStats', ['url', '$http', '$q', function(url, $http, $q) {
        var _start = 0,
            _end = 0;
        var _bugs = [];
        var _url = function() {
            return 'api/' + url.companyId() + '/stats/completedBugs';
        }

        this.getBugStatsData = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            if (_start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $http.get(_url(), {
                    params: {
                        start: start,
                        end: end
                    }
                }).then(function(response) {
                    _start = since;
                    _end = until;
                    _bugs = response.data;
                    return _bugs;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_bugs);
                return deferred.promise;
            }
        };
    }]);
