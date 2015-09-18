(function (angular) {
    'use strict';
    angular.module('adminApp', ['ngRoute']).config(function ($routeProvider, $locationProvider) {
        $routeProvider.when('/', {
            redirectTo: '/dashboard',
            controller: 'dashboardCtrl'
        });
        $routeProvider.when('/dashboard', {
            templateUrl: 'dashboard.html',
            controller: 'dashboardCtrl'
        });
        $routeProvider.when('/application', {
            templateUrl: 'application.html',
            controller: 'applicationCtrl'
        });
        $routeProvider.when('/feedback', {
            templateUrl: 'feedback.html',
            controller: 'feedbackCtrl'
        });
        $routeProvider.otherwise({
            redirectTo: '/dashboard',
            controller: 'dashboardCtrl'
        });
        $locationProvider.html5Mode(true);
    }).controller('appCtrl', function ($scope, $location) {
        $scope.navClass = function (page) {
            var currentRoute = $location.path().substring(1) || 'home';
            return page === currentRoute ? 'active' : '';
        };
        $scope.loadDashboard = function () {
            $location.url('/dashboard');
        };
        $scope.loadApplication = function () {
            $location.url('/application');
        };
        $scope.loadFeedback = function () {
            $location.url('/feedback');
        };
    }).controller('dashboardCtrl', function ($scope) {
    }).controller('applicationCtrl', function ($scope, $http) {
        $http.get('/serc/api/trials').success(function(data){
            $scope.trials = data;
            $scope.trials.map(function(item){item.status = item.code ? '已邀请' : '已注册'});
        });
    }).controller('feedbackCtrl', function ($scope) {
    });

})(window.angular);

