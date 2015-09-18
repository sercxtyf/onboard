/**
 * Created by harttle on 12/23/14.
 */

// 项目列表
angular.module('company')

    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            // default child state
            .state('company.projects', {
                url: '',
                templateUrl: 'projects.html',
                controller: 'projectsCtrl'
            })
    }])

    .controller('projectsCtrl', ['$scope', 'url', 'company', 'theme', '$http','project', '$location',function($scope, url, company, theme, $http,project, $location){
        $scope.themes = theme.themes;
        $scope.projectFilter = {deleted: false};

        $scope.createNewProject = function(){
            project.getActiveProjectNumber(true).then(function(data){
                $location.url("teams/"+url.companyId()+"/projects/new");
            });
        };
        company.getProjects(true, url.companyId()).then(function(projects){
            $scope.projects = projects;
            $scope.sortableOptions = {
                stop: function (e, ui) {
                    $http.post(url.projectApiUrl() + 'sort', {ids: $scope.projects.map(function(project) {
                        return project.id;
                    })});
                }
            };
            $scope.initTooltip = function(){
                $('[rel="tooltip"]').tooltip();
            };
        });
    }]);