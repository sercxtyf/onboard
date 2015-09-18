/**
 * Created by Steven on 2015/7/14.
 */
angular.module('data')
    .service('cqaService', ['url', '$http', function(url, $http) {

        this.createAnalyzeTask = function(source, branch) {
            var postUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/analyze-tasks'].join("");
            var postParas = {
                name: "test",
                source: source,
                branch: branch
            };
            return $http.post(postUrl, postUrl).then(
                function(result) {
                    console.log('success"' + result);
                },
                function(result) {
                    console.log('failure"' + result);
                }
            );
        };
    }]);