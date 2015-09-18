angular.module('iteration')
    .config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider.state('company.project.iteration', {
                url: '/iterations',
                templateUrl: 'iterations.html'
            }).state('company.project.iteration.createdIterations', {
                url: '/created',
                templateUrl: 'created-iterations.html'
            }).state('company.project.iteration.complecoedIterations', {
                url: '/completed',
                templateUrl: 'completed-iterations.html'
            }).state('company.project.iteration.activeIteration', {
                url: '/active',
                templateUrl: 'active-iteration.html'
            }).state('company.project.iteration.completingIteration', {
                url: '/{iterationId:[0-9]+}/completing',
                templateUrl: 'completing-iteration.html'
            }).state('company.project.iteration.activeIteration.todos', {
                url: '/todos',
                templateUrl: 'active-iteration-todos.html'
            }).state('company.project.iteration.activeIteration.statistics', {
                url: '/statistics',
                templateUrl: 'active-iteration-statistics.html'
            }).state('company.project.iteration.completedIteration', {
                url: '/{iterationId:[0-9]+}/',
                templateUrl: 'completed-iteration.html'
            }).state('company.project.iteration.completedIteration.todos', {
                url: '/todos',
                templateUrl: 'completed-iteration-todos.html'
            }).state('company.project.iteration.completedIteration.statistics', {
                url: '/statistics',
                templateUrl: 'completed-iteration-statistics.html'
            }).state('company.project.iteration.activeIteration.codeStats', {
                url: '/codeStats',
                templateUrl: 'iteration-codeStats.html',
                controller: 'iterationCodeStatsCtrl'
            }).state('company.project.iteration.activeIteration.empty', {
                url: '/empty',
                templateUrl: 'iteration-empty.html'
            }).state('company.project.iteration.activeIteration.todoStats', {
                url: '/todoStats',
                templateUrl: 'iteration-todoStats.html',
                controller: 'iterationTodoStatsCtrl'
            }).state('company.project.iteration.activeIteration.overallStats', {
                url: '/overallStats',
                templateUrl: 'iteration-overallStats.html',
                controller: 'iterationOverallStatsCtrl'
            }).state('company.project.iteration.completedIteration.codeStats', {
                url: '/codeStats',
                templateUrl: 'iteration-codeStats.html',
                controller: 'iterationCodeStatsCtrl'
            }).state('company.project.iteration.completedIteration.todoStats', {
                url: '/todoStats',
                templateUrl: 'iteration-todoStats.html',
                controller: 'iterationTodoStatsCtrl'
            }).state('company.project.iteration.completedIteration.overallStats', {
                url: '/overallStats',
                templateUrl: 'iteration-overallStats.html',
                controller: 'iterationOverallStatsCtrl'
            }).state('company.project.iteration.editIterations', {
                url: '/{iterationId:[0-9]+}/edit',
                templateUrl: 'iteration-edit.html',
                controller: 'iterationEditCtrl'
            });
        }
    ])
    .controller('defaultIterationsCtrl', ['$scope', '$rootScope', '$state', 'iterationService', '$location', 'url',
        function ($scope, $rootScope, $state, iterationService, $location, url) {
            $scope.activeIteration = {};
            $scope.searchTodo = {deleted: false};
            if($state.is('company.project.iteration')){
                iterationService.getActiveIterations(true).then(function(iteration){
                    if(iteration.id){
                        $state.go('company.project.iteration.activeIteration.todos');
                    }else{
                        $state.go('company.project.iteration.activeIteration.empty');
                    }
                });
            }
            $scope.linkTodoToIteration = function (iteration) {
                $rootScope.$broadcast("showAddLinkedIteration", {
                    iteration: iteration,
                    linkedTodos: iteration.todos,
                    companyId: url.companyId(),
                    projectId: url.projectId(),
                });
            };
            $scope.updateIterationTodosEvent = "updateIterationTodos";
            //init
            $scope.srcIteration = {
                projectId: url.projectId(),
                companyId: url.companyId(),
                status: "created",
                startTime: null,
                endTime: null,
                todoCount: 0,
                name: null
            };
            iterationService.getNewiteration(url.projectId(), url.companyId()).then(function(iteration){
                $scope.newIteration = iteration;
            });

            $scope.showCreateIteration = function () {
                $scope.newIterationForm.open = !$scope.newIterationForm.open;
                return false;
            };
            $scope.createIteration = function () {
                if($scope.newIteration.startTime && typeof($scope.newIteration.startTime) == "object"){
                    $scope.newIteration.startTime = $scope.newIteration.startTime.getTime();
                }
                if($scope.newIteration.endTime && typeof($scope.newIteration.endTime) == "object"){
                    $scope.newIteration.endTime = $scope.newIteration.endTime.getTime();
                }
                if($scope.newIteration.startTime && $scope.newIteration.endTime
                    && $scope.newIteration.endTime - $scope.newIteration.startTime < 0){
                    alert("开始时间必须大于结束时间！");
                    return;
                }
                $scope.newIteration.create().then(function(){
                    iterationService.getNewiteration(url.projectId(), url.companyId()).then(function(iteration){
                        $scope.newIteration = iteration;
                    });
                    $scope.newIterationForm.open = !$scope.newIterationForm.open;
                    $state.go('company.project.iteration.createdIterations');
                });
            };

            //other view operation
            $scope.openDTPicker = function ($event, opened) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope[opened] = true;
            };
            $scope.isActive = function(path){
                return $location.path().indexOf(path) >= 0;
            };
            
            $scope.updateIterationName = function(iteration){
                if(iteration.name.length > 25){
                    alert("迭代名称不能超过25个字");
                    iteration.reset();
                    return;
                }
                iteration.updateByAttrs(['name']);
            };

            $scope.transDescription = function(description){
                if(!description){
                    return "";
                }
                return description.replace(/\r?\n/g, '<br />')
            };
        }
    ]);
