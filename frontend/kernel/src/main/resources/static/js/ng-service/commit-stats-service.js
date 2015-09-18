/**
 * Created by ChenLong on 12/10/14.
 */

angular.module('projectApp')
    .service('commitStats', ['url', '$http', '$q', function(url, $http, $q) {

        var _start = 0,
            _end = 0;
        var _commits = [];
        var _api = url.projectApiUrl + "/repos/stats";

        this.getCommitStatsData = function(start, end) {
            if (start !== _start || end !== _end) {
                return $http.get(_api, {
                    params: {
                        start: start,
                        end: end
                    }
                }).then(function(response) {
                    _start = start;
                    _end = end;
                    _commits = response.data;
                    return _commits;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_commits);
                return deferred.promise;
            }
        };
    }]);
