angular.module('data')
    .service('stepDataService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 'todolistService',
        'baseService',
        function(url, $http, $q, filterFilter, todoService, filterFilter, todolistService, baseService) {

            var modelType = 'step';
            this.Step = function() {
                this.getApi = function() {
                    var api = url.projectApiUrl(this.projectId) + '/steps/';
                    if (this.id) {
                        api = api + this.id;
                    }
                    return api;
                };
                this.getDTO = function() {
                    return {
                        content: this.content,
                        status: this.status,
                        dueDate: this.dueDate,
                        assigneeId: this.assigneeId,
                        attachType: this.attachType,
                        attachId: this.attachId,
                        idInProject: this.idInProject,
                        projectId: this.projectId,
                        companyId: this.companyId
                    };
                };
                this.afterinit = function() {
                    this.assignee = this.assigneeDTO;
                };
                this.updateIterationStatus = function(status) {
                    this.iterationStatus = status;
                    this.status = status;
                    return this.update();
                };
            };
            this.Step.prototype = baseService.Item;

            baseService.Items[modelType] = this.Step;


            this.findById = function(id) {
                return baseService.getItem(modelType, id);
            };

            this.register = function(DTOs) {
                for (var index = 0; index < DTOs.length; index++) {
                    var step = baseService.addItem(DTOs[index]);
                    DTOs[index] = step;
                }
                return DTOs;
            };
        }
    ])
    .service('companyStepStats', ['url', '$http', '$q', function(url, $http, $q) {
        var _start = 0,
            _end = 0;
        var _steps = [];
        var _url = function() {
            return 'api/' + url.companyId() + '/stats/completedSteps';
        }

        this.getStepStatsData = function(start, end) {
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
                    _steps = response.data;
                    return _steps;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_steps);
                return deferred.promise;
            }
        };
    }]);
