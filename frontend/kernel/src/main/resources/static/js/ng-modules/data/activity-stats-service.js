/**
 * Created by ChenLong on 12/24/14.
 */

angular.module('data')
    .service('activityStats', ['url', '$http', '$q', function(url, $http, $q) {

        var _start = 0,
            _end = 0;
        var _activities = [];
        var _api = function(p) {
            return url.projectApiUrl(p) + "/activities";
        };
        var _projectId = url.projectId();

        this.processData = function(data) {
            var result = [];
            if (data.length === 0)
                return result;
            result.push(data[0]);
            var latest = data[0];
            for (var i = 1; i <= data.length - 1; i++) {
                //如果生成的活动记录间隔在1分钟以上
                if (latest.created - data[i].created >= 60000) {
                    result.push(data[i]);
                    latest = data[i];
                }
            }
            return result;
        };

        this.getActivityStats = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            var projectId = url.projectId();
            if (projectId !== _projectId || _start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $http.get(_api(projectId), {
                    params: {
                        start: start,
                        end: end,
                        stat: true
                    }
                }).then(function(response) {
                    _start = since;
                    _end = until;
                    _projectId = projectId;
                    _activities = response.data;
                    return _activities;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_activities);
                return deferred.promise;
            }
        };
    }])
    .service('companyActivityStats', ['url', '$http', '$q', function(url, $http, $q) {

        var _start = 0,
            _end = 0;
        var _activities = [];
        var _url = 'api/' + url.companyId() + '/stats/activities';

        this.getActivityStats = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            if (_start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $http.get(_url, {
                    params: {
                        start: start,
                        end: end
                    }
                }).then(function(response) {
                    _start = since;
                    _end = until;
                    _activities = response.data;
                    return _activities;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_activities);
                return deferred.promise;
            }
        };
    }]);
