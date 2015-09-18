/**
 * Created by Steven on 2015/7/8.
 */
angular.module('cqa')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider.state('company.project.cqa', {
            url: '/cqa',
            templateUrl: 'cqaIndex.html',
            controller: 'cqaCtrl'
        });
    }])
    .controller('cqaCtrl', ['$scope', '$state', function($scope, $state) {
        // Get to list page for default, otherwise the sub-page will be empty.
        if ($state.$current.toString() == "company.project.cqa") {
            $state.go('company.project.cqa.jobs');
        }
    }]);