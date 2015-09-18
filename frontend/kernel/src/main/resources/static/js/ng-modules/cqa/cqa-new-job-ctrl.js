/**
 * Created by Steven on 2015/7/8.
 */
angular.module('cqa')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider.state('company.project.cqa.newJob', {
            url: '/newJob',
            templateUrl: 'cqaNewJob.html',
            controller: 'cqaNewJobCtrl'
        });
    }])
    .controller('cqaNewJobCtrl', ['$scope', '$state', 'url', '$http', function($scope, $state, url, $http) {
        $scope.submit = function() {
            var postUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/analyze-tasks'].join("");
            var postParas = {
                name: "test",
                source: ".",
                branch: "feature/sonarqube"
            };
            $http.post(postUrl, postParas).then(
                function(result) {
                    console.log(result.data);
                },
                function(result) {
                    console.log('failure');
                }
            );
        }
    }]);