/**
 * Created by harttle on 12/9/14.
 */

angular.module('project', ['ui.router', 'util', 'todo', 'iteration', 'discussions', 'upload',
    'statistics', 'stories', 'angularMemberselector', 'bugs'])
    .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.when('/{companyId:[0-9]+}/projects/{projectId:[0-9]+}',
            '/{companyId:[0-9]+}/projects/{projectId:[0-9]+}/todolists');

        $stateProvider
            .state('company.project', {
                url        : '/projects/{projectId:[0-9]+}',
                templateUrl: 'project.html',
                controller : 'projectCtrl',
                abstract   : true
            })
    }]);
