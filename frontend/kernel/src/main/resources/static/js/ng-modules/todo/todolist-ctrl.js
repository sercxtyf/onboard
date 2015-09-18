
// 任务列表
angular.module('todo')
    .controller('todolistCtrl', ['$scope', '$rootScope', '$http', '$document', '$state', 'tab', "todolistService", 
                                 'todoService', 'user', 'url',
        function ($scope, $rootScope, $http, $document, $state, tab, todolistService, todoService, user, url) {
            $scope.updateTabEvent = "updateTab";
            todolistService.getTodolistById($scope.projectId, $scope.companyId, $scope.id, true).then(function(todolist){
                $scope.$broadcast($scope.updateTabEvent, {
                    attachType: "todolist",
                    attachId: todolist.id,
                    projectId: todolist.projectId,
                    companyId: todolist.companyId,
                    todolist: todolist
                });
                $scope.currentTodolist = todolist;
            });
            $scope.openTodosFiletr = {deleted: false};
//            $scope.openTodosFiletr = {deleted: false, status: "!closed"};
//            $scope.completedTodosFiletr = {deleted: false, status: "closed"};
            $scope.updateTodolist = function (event, todolist) {
                if(!todolist.name){
                    return;
                }
                todolist.updateByAttrs([$(event.target).attr("name")]);
            };
            // other infos
            user.getProjectUsers(false, $scope.projectId, $scope.companyId).then(function(users){
                $scope.projectUsers = users.slice();
                $scope.projectUsers.splice(0, 0, {name:'未分配', avatarUrl: url.defaultAvatarUrl});
            });
            todoService.getTodoStatuses($scope.projectId, $scope.companyId).then(function(todoStatuses){
                $scope.todoStatuses = todoStatuses;
            });
            $scope.todolistTabs = [
                tab.getTabInfo("comment", true),
                tab.getTabInfo("activity")
            ];
        }
    ]);
