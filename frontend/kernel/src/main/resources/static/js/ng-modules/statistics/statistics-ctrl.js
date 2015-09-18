angular.module('statistics')
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('company.project.statistics', {
                url: '/statistics/',
                templateUrl: 'statistics.html',
                controller: 'statisticsCtrl',
                abstract: true
            })
            .state('company.project.statistics.group', {
                url: '',
                templateUrl: 'statistics-group.html',
                controller: 'statisticsGroupCtrl'
            })
            .state('company.project.statistics.code', {
                url: 'code/',
                templateUrl: 'statistics-code.html',
                controller: 'statisticsCodeCtrl'
            })
            .state('company.project.statistics.todo', {
                url: 'todo/',
                templateUrl: 'statistics-todo.html',
                controller: 'statisticsTodoCtrl'
            }).state('company.project.statistics.overall', {
                url: 'overall/',
                templateUrl: 'statistics-overall.html',
                controller: 'statisticsOverallCtrl'
            });
    }])
    .controller('statisticsCtrl', ['$scope', '$http', '$state', function ($scope, $http, $state) {
        $scope.state = $state;
        $scope.users_by_group = {};

    }]);

