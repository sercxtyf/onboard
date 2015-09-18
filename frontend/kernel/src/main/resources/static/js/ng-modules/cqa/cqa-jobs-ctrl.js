/**
 * Created by Steven on 2015/7/8.
 */
angular.module('cqa')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider.state('company.project.cqa.jobs', {
            url: '/jobs',
            templateUrl: 'cqaJobs.html',
            controller: 'cqaJobsCtrl'
        });
    }])
    .controller('cqaJobsCtrl', ['$scope', '$state', function($scope, $state) {

    }]);