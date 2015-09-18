// 任务列表的列表

angular.module('todo')
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('company.project.todolists', {
                url: '/todolists',
                templateUrl: 'todolists.html',
                controller: 'todolistsCtrl'
            })
            .state('company.project.todolists.open', {
                url: '/open',
                templateUrl: 'opentodolists.html'
            })
            .state('company.project.todolists.archived', {
                url: '/archived',
                templateUrl: 'archivedtodolists.html'
            })
            .state('company.project.todolists-todo', {
                url: '/todos/{todoId:[0-9]+}',
                templateUrl: 'todolists.html',
                controller: 'todolistsCtrl'
            })
    }
    ])
    .controller('todolistsCtrl', ['$scope', '$rootScope', '$http', '$state', '$document', "todolistService", "user", 'url', 
                                  'drawer', 'todoUtilService', '$timeout', 'project',
        function ($scope, $rootScope, $http, $state, $document, todolistService, user, url, drawer, todoUtilService, $timeout, project) {
            $scope.$state = $state;
            if($state.is("company.project.todolists")){
                $state.go("company.project.todolists.open");
            }
            project.setLastVisited(url.projectId(), url.companyId());
            $scope.todolistFilter = {archived: false, deleted: false};
            $scope.todoUtilService = todoUtilService;
            todolistService.getTodolists(url.projectId(), url.companyId(), true).then(function (todolists) {
                $scope.todolists = todolists;
            });
            
            $scope.srcSubmitBtn = {name: "确定", disable: false};
            $scope.submitBtn = angular.copy($scope.srcSubmitBtn);

            //init
            todolistService.getNewTodolist($scope.projectId, $scope.companyId).then(function (todolist) {
                $scope.newTodolist = todolist;
            });


            //todolist operatoin
            $scope.createTodolist = function () {
                if ($scope.newTodolistForm.name.$error.maxlength) {
                    alert("项目清单名称太长");
                    return;
                } else if ($scope.newTodolistForm.$error.required) {
                    alert("请输入清单名称");
                    return;
                } 
                $scope.submitBtn.name = "提交中...";
                $scope.submitBtn.disable = true;
                $scope.newTodolist.create().then(function (todolist) {
                    $scope.todolists.splice(0, 0, todolist);
                    $("form.newTodolistForm").hide();
                    $scope.submitBtn = angular.copy($scope.srcSubmitBtn);
                    todolistService.getNewTodolist($scope.projectId, $scope.companyId).then(function (todolist) {
                        $scope.newTodolist = todolist;
                    });
                });
            };
            $scope.deleteTodolist = function (todolist) {
                if (confirm('确认删除该任务列表?')) {
                    todolist.delete();
                }
                return false;
            };
            $scope.archiveTodolist = function (todolist) {
                if (confirm("是否归档该项目清单？")) {
                    todolist.archive();
                }
                return false;
            };

            //todo operation
            $scope.showNewTodolistForm = function () {
                $scope.newTodolistForm.open = !$scope.newTodolistForm.open;
                return false;
            };

            $scope.focusTodolistName = function(){
                $timeout(function(){
                    $("#new-todolist-name").focus();
                });
            };

        }
    ]);
