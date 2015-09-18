/**
 * Created by ChenLong on 12/10/14.
 */

angular.module('data')
    .service('commitStats', ['url', '$http', '$q', function(url, $http, $q) {

        var _start = 0,
            _end = 0;
        var _commits = [];
        var _api = function(p) {
            return url.projectApiUrl(p) + "/repository-stats";
        };
        var _projectId = url.projectId();

        this.getCommitStatsData = function(start, end) {
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
                    _commits = response.data;
                    return _commits;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_commits);
                return deferred.promise;
            }
        };
    }])
    .service('companyCommitStats', ['url', '$http', '$q', function(url, $http, $q) {

        var _start = 0,
            _end = 0;
        var _commits = [];
        var _url = 'api/' + url.companyId() + '/stats/commits';

        this.getCommitStatsData = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            /*if (_start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $http.get(_url, {
                    params: {
                        start: start,
                        end: end
                    }
                }).then(function(response) {
                    _start = since;
                    _end = until;
                    _commits = response.data;
                    return _commits;
                });
            } else {
            */
                var deferred = $q.defer();
                deferred.resolve(_commits);
                return deferred.promise;
            //}
        };
    }]);
