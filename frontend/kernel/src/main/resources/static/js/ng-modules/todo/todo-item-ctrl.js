

angular.module('todo')
    .controller('todoItemCtrl', ['$scope', '$rootScope', '$http', '$document', '$state', 
        'tab', 'user', 'todoService', 'url', 'todoUtilService',
        function ($scope, $rootScope, $http, $document, $state, tab, user, todoService, url, todoUtilService) {
            $scope.todoUtilService = todoUtilService;
            $scope.todoDueDateMin = new Date();
            $scope.deleteTodo = function (todo) {
                if (confirm('确认删除该任务?')) {
                    todo.delete();
                }
                return false;
            };

            user.getCurrentUser().then(function(user) {
                $scope.currentUser = user;
            });
            
            $scope.openDueDateCalendar = function(todo, $event) {
                $event.preventDefault();
                $event.stopPropagation();
                todo.dueDateCalendar = !todo.dueDateCalendar;
            };

            $scope.todolists = [];
            $scope.projectUsers = [];
            $scope.todoStatuses = [];
            $scope.getProjectUsers = function() {
                user.getProjectUsers(false, url.projectId(), url.companyId()).then(function(users){
                    $scope.projectUsers = users.slice();
                    $scope.projectUsers.splice(0, 0, {name:'未分配', avatarUrl: url.defaultAvatarUrl});
                });
            };
            $scope.statusFilter = {active: true};
            $scope.getTodoStatus = function(todo) {
                todoService.getTodoStatuses(todo.projectId, todo.companyId).then(function(todoStatuses){
                    $scope.todoStatuses = todoStatuses;
                });
            };
            //open date picker
            $scope.openDTP = function(todo, $event, isList) {
                $event.preventDefault();
                $event.stopPropagation();
                if (isList) {
                    todo.listDTP = true;
                    todo.detailDTP = false;
                }
                else {
                    todo.detailDTP = true;
                    todo.listDTP = false;
                }
            };
        }
    ]);
