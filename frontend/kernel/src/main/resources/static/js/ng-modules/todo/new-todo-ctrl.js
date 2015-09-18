
// 新任务
angular.module('todo')
    .controller('newTodoCtrl', ['$scope', '$rootScope', '$http', '$document', '$state', 'user', 'url', "todolistService"
        , "todoService", "todoUtilService", "$timeout",
        function ($scope, $rootScope, $http, $document, $state, user, url, todolistService, todoService, todoUtilService
        , $timeout) {
            $scope.todoUtilService = todoUtilService;
            $scope.srcSubmitBtn = { name : "新建", disable : false};
            $scope.submitBtn = angular.copy($scope.srcSubmitBtn);
            //init empty todo for views
            $scope.todo = angular.copy($scope.srcTodo);
            $scope.todoTypes = todoService.todoTypes;
            $scope.priorities = todoService.priorities;
            todolistService.getTodolistById($scope.peojectId, $scope.companyId, $scope.todolistId).then(function(todolist){
                todoService.getNewTodo(todolist).then(function(todo){
                    $scope.todo = todo;
                    $scope.todo.dueDate = new Date();
                    $scope.todo.dueDate.setHours(23, 59, 59);
                    user.getCurrentUser().then( function(data) {
                    	$scope.todo.assigneeId = data.id;
                    	$scope.todo.user = data;
                    });
                    
                    if($scope.searchTodo.todoType !== undefined){
                        $scope.todo.todoType = $scope.searchTodo.todoType;
                    }
                });
            });

            //todo operation
            $scope.createTodo = function () {
                if($scope.todo.content == "" || $scope.todo.content == null){
                    return;
                }
                $scope.submitBtn.name = "提交中...";
                $scope.submitBtn.disable = true;
                $scope.todo.create(todoUtilService.getTodolist($scope.todo)).then(function(){
                    $(".drawer").drawer("hide");
                    $scope.submitBtn = angular.copy($scope.srcSubmitBtn);
                    var todolist = todolistService.getTodolistById($scope.projectId, $scope.companyId, 
                            $scope.todo.todolistId, false, true);
                    todolist.addTodo($scope.todo);
                    todoService.getNewTodo(todolist).then(function(todo){
                        $scope.todo = todo;
                    });
                });
            };

            // other infos
            user.getProjectUsers().then(function(users){
                $scope.projectUsers = users.slice();
                $scope.projectUsers.splice(0, 0, {name:'分配任务', avatarUrl: url.defaultAvatarUrl});
            });
            todolistService.getTodolists($scope.projectId, $scope.companyId).then(function(todolists){
                $scope.todolists = todolists;
            });
            todoService.getTodoStatuses().then(function(todoStatuses){
                $scope.todoStatuses = todoStatuses;
            });


            //todo operation
            $scope.openDTP = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dtpOpened = true;
            };
            // 指定负责人
            $scope.assign = function(user){
                $scope.todo.assigneeId = user.id;
            };
            // 更改所述清单
            $scope.changeTodolist = function(todolist){
                $scope.todo.todolistId = todolist.id;
            };
            // 指定todo类型
            $scope.setTodoType = function(type){
                $scope.todo.todoType = type.value;
            };
            $scope.setPriority = function(value){
                $scope.todo.priority = value;
            };
            $scope.focusName = function(){
                $timeout(function(){
                    $("#todo-content").focus();
                });
            };
        }
    ]);
