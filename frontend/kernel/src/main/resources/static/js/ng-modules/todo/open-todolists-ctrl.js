// 任务列表的列表

angular.module('todo')
  .controller('openTodolistsCtrl', ['$scope', '$rootScope', '$http', '$state', '$document', 'filterFilter', 'todolistService', 'user', 'url', 'drawer', 'todoUtilService', 'todoService', '$modal', 'company',
        function ($scope, $rootScope, $http, $state, $document, filterFilter, todolistService, user, url, drawer, todoUtilService, todoService, $modal, company) {
            $scope.todoUtilService = todoUtilService;
            todolistService.getTodolists(url.projectId(), url.companyId(), true).then(function (todolists) {
                $scope.todolists = todolists;
            });
            
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
            $scope.copyTodolist = function (todolist) {
                $modal.open({
                templateUrl: 'todlistCopy.html',
                controller : 'copyTodolistCtrl',
                size       : 'md',
                resolve    : {
                    todolist: function() {
                        return todolist;
                    },
                    projects: function() {
                        return company.getProjects();
                    }
                }});
            };

            function collapaseByLinkElement(element){
                var panel = $(element).closest('div.todo-panel');
                var icon = $(element).find('i');
                var body = panel.find('div.todo-panel-body');
                // var header = panel.find('div.todo-panel-heading')

                body.slideToggle(200);
                icon.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
                // header.toggleClass('').toggleClass('border-bottom');
                return false;
            }

            //todolist expand
            $scope.allExpanded = true;
            $scope.expandAll = function(){
                $scope.allExpanded = !$scope.allExpanded;
                var collapseLinks = $(".collapse-link");
                if($scope.allExpanded){
                    collapseLinks.map(function(index, linkElement){
                        if($(linkElement).find("i").hasClass("fa-chevron-down")){
                            collapaseByLinkElement(linkElement);
                        }
                    });
                }else{
                    collapseLinks.map(function(index, linkElement){
                        if($(linkElement).find("i").hasClass("fa-chevron-up")){
                            collapaseByLinkElement(linkElement);
                        }
                    });
                }
            };

            $scope.collapseTodolist = function (event) {
                var element = event.target;
                if ($(element).hasClass("fa")) {
                    element = $(element).parent();
                }
                event.preventDefault();
                collapaseByLinkElement(element);
            };

            // sortable!!
            $scope.todosSortableOptions = {
                placeholder: "todo-item-placeholder",
                // forcePlaceholderSize: true,
                connectWith: ".todo-drag-body",
                start: function(e, ui) {
                    ui.item.addClass('todo-item-intheair');
                },
                stop: function (e, ui) {
                    ui.item.removeClass('todo-item-intheair');
                    var todo = ui.item.sortable.model;
                    var todolist = ui.item.sortable.droptargetModel;
                    var todolistId = ui.item.sortable.droptarget.attr('data-id');
                    for (var i = 0; i < todolist.length; ++i)
                        if (todolist[i].id == todo.id) {
                            if (i == 0) {
                                if (todolist.length > 1) todo.position = todolist[i + 1].position - 1;
                                else todo.position = 1;
                            } else if (i == todolist.length - 1) {
                                if (todolist.length > 1) todo.position = todolist[i - 1].position + 1;
                                else todo.position = 999;
                            } else {
                                todo.position = (todolist[i - 1].position + todolist[i + 1].position) / 2.0;
                            }
                        }
                    todo.todolistId = todolistId;
                    var data = {
                        todolistId: todo.todolistId,
                        position: todo.position
                    };
                    $http.put(url.projectApiUrl() + "/todos/" + todo.id, data);
                }
            };
            $scope.todolistSortableOptions = {
                handle: ".todo-panel-heading",
                cancel: ".todo-panel-options",
                placeholder: "todo-panel-placeholder",
                forcePlaceholderSize: true,
                start: function(e, ui) {
                    if($scope.allExpanded){
                        $scope.expandAll();
                    }
                    ui.item.addClass('todo-panel-intheair');
                },
                stop: function (e, ui) {
                    ui.item.removeClass('todo-panel-intheair');
                    var todolist = ui.item.sortable.model;
                    for (var i = 0; i < $scope.todolists.length; ++i) {
                        if ($scope.todolists[i].id == todolist.id) {
                            if (i == 0) {
                                if ($scope.todolists.length > 1) todolist.position = $scope.todolists[1].position + 1;
                                else todolist.position = 0;
                            } else if (i == $scope.todolists.length - 1) {
                                if ($scope.todolists.length > 1) todolist.position = $scope.todolists[i - 1].position - 1;
                                else todolist.position = 0;
                            } else {
                                todolist.position = ($scope.todolists[i - 1].position + $scope.todolists[i + 1].position) / 2.0;
                            }
                        }
                    }
                    var data = {
                        id: todolist.id,
                        position: todolist.position
                    };
                    $http.put(url.projectApiUrl() + "/todolists/" + todolist.id, data);

                }
            };

            $scope.isEmpty = function(todolist){
                var todos = filterFilter(todolist.todos, {status: "!closed"});
                return todos.length === 0;
            };

            $scope.searchTodo = {deleted: false, status: "!closed"};
            $scope.searchTodo.todoType = undefined;
            $scope.searchTodo.assigneeId = undefined;

            //map for todo filter
            $scope.filterMap = function(name){
                switch(name){
                    case 'story':
                        return "需求";
                    case 'bug':
                        return "Bug";
                    case 'task':
                        return "任务";
                    default:
                        return "所有类型";
                }
            };
            todoService.getTodoStatuses($scope.projectId, $scope.companyId).then(function(todoStatuses){
                $scope.todoStatuses = angular.copy(todoStatuses);
                //去掉已完成这一状态
                $scope.todoStatuses.splice(6, 1);
            });
            $scope.projectUsers = [];
            user.getProjectUsers(false, $scope.projectId).then(function(users){
                $scope.projectUsers = users.slice();
            });

            //filter for todo
            $scope.filterTodo = function (todo) {
                for(var attr in $scope.searchTodo){
                    if($scope.searchTodo[attr] === undefined || $scope.searchTodo[attr] === "" || $scope.searchTodo[attr] === 0){
                        continue;
                    }
                    if(attr === "key"){
                        if(todo.content.match($scope.searchTodo[attr]) === null){
                            return false;
                        }
                        continue;
                    }
                    if($scope.searchTodo[attr][0] && $scope.searchTodo[attr][0] === "!"){
                        if($scope.searchTodo[attr].substr(1, $scope.searchTodo[attr].length - 1) === todo[attr]){
                            return false;
                        }
                    }else{
                        if($scope.searchTodo[attr] !== todo[attr]){
                            return false;
                        }
                    }
                }
                return true;
            };
            $scope.getUserName = function(userId){
                if(!userId){
                    return "所有人";
                }
                for(var index = 0; index < $scope.projectUsers.length; index++){
                    if($scope.projectUsers[index].id === userId){
                        return $scope.projectUsers[index].name;
                    }
                }
                return "所有人";
            };
            $scope.getStatusName = function(value){
                if(value === "!closed"){
                    return "所有状态";
                }
                for(var index = 0; index < $scope.todoStatuses.length; index ++){
                    if($scope.todoStatuses[index].value === value){
                        return $scope.todoStatuses[index].name;
                    }
                }
            };


            $scope.canShow = function(todolist){
                if(!$scope.searchTodo.todoType && !$scope.searchTodo.assigneeId && $scope.searchTodo.status === '!closed' && !$scope.searchTodo.key){
                    return true;
                }
                for(var index = 0; index < todolist.todos.length; index ++){
                    if($scope.filterTodo(todolist.todos[index])){
                        return true;
                    }
                }
                if($scope.searchTodo.key && todolist.name.match($scope.searchTodo.key) !== null){
                    return true;
                }
                return false;
            };
        }
    ]);
