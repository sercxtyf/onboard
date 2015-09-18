// 任务列表的列表

angular.module('todo').controller('attriveTtodolistsCtrl', 
		['$scope', '$rootScope', '$http', '$state', '$document', "todolistService", "user", 'url', 'drawer', 'todoUtilService',
        function ($scope, $rootScope, $http, $state, $document, todolistService, user, url, drawer, todoUtilService) {
            $scope.todoUtilService = todoUtilService;
            todolistService.getArchivedTodolists(url.projectId(), url.companyId(), true).then(function (todolists) {
                $scope.todolists = todolists;
            });
        }
    ]);
