/**
 * Created by R on 2014/11/19.
 */

// 关联任务页面
angular.module('todo')
    .controller('linkTodoCtrl', ['$scope', '$rootScope', '$http', 'filterFilter', "todolistService", "url",
        function ($scope, $rootscope, $http, filterFilter, todolistService, url) {
            $scope.linkingTodolists = [];
            $scope.seletedLinkingTodolist = {todos: []};
            function init(event, data){
                $scope.linkingTodolists = [];
                $scope.seletedLinkingTodolist = {todos: []};
                var tempTodolists = [];
                for(var index in $scope.todolists){
                    var tmpTodolist = $scope.todolists[index].getTodolistDTO();
                    tmpTodolist.todos = $scope.todolists[index].todos;
                    tempTodolists.push(tmpTodolist);
                }
                $scope.todoId = data.todoId;
                var linkedTodoIds = [];
                if (data.linkedTodolists != undefined) {
                    for (var i = 0; i < data.linkedTodolists.length; i++) {
                        for (var j = 0; j < data.linkedTodolists[i].todos.length; j++) {
                            linkedTodoIds.push(data.linkedTodolists[i].todos[j].id);
                        }
                    }
                }
                for (var i = 0; i < tempTodolists.length; i++) {
                    //filter by todo type
                    if (data.todoType != undefined) {
                        tempTodolists[i].todos = filterFilter(tempTodolists[i].todos,
                            {todoType: data.todoType});
                    }
                    //filter by todo id
                    for (var j = 0; j < tempTodolists[i].todos.length; j++) {
                        if (tempTodolists[i].todos[j].id == $scope.todoId) {
                            tempTodolists[i].todos[j].disable = true;
                            tempTodolists[i].todos[j].active = false;
                        } else {
                            tempTodolists[i].todos[j].disable = false;
                            tempTodolists[i].todos[j].active = false;
                        }
                        for (var k = 0; k < linkedTodoIds.length; k++) {
                            if(tempTodolists[i].todos[j].id == linkedTodoIds[k]){
                                tempTodolists[i].todos[j].disable = false;
                                tempTodolists[i].todos[j].active = true;
                            }
                        }
                    }
                    //remove empty todolists
                    if (tempTodolists[i].todos.length > 0) {
                        $scope.linkingTodolists.push(tempTodolists[i]);
                    }
                }
                $("#linkTodo").modal("show");
            }
            $scope.$on("showAddLinkedTask", function (event, data) {
                //目前只支持将某个对象关联到todo上
                if (data == undefined || data.todoId == undefined) {
                    return;
                }
                //init data
                todolistService.getTodolists(data.projectId, data.companyId, true).then(function(todolists){
                    $scope.todolists = todolists;
                    init(event, data);
                });
            });
            $scope.selectTodolist = function ($event, todolist) {
                $scope.seletedLinkingTodolist = todolist;
                var todolistUl = $($event.target).parent();
                todolistUl.find("li").each(function () {
                    $(this).removeClass("active");
                });
                $($event.target).addClass("active");
            };
            $scope.changedTodos = {};
            $scope.linkTodo = function ($event, todo) {
                if (todo.disable) {
                    return;
                }
                $scope.changedTodos[todo.id] = todo;
                if (todo.active) {
                    todo.active = false;
                    $($event.target).removeClass("active");
                } else {
                    todo.active = true;
                    $($event.target).addClass("active");
                }
            };
            $scope.commitChanges = function(){
                var todoIds = Object.keys($scope.changedTodos);
                var updateTodoDataMap = {
                    "add" : [],
                    "remove": []
                };
                var updateTodoMap = {
                    "add" : [],
                    "remove": []
                };
                for(var index in todoIds){
                    var todoId = todoIds[index];
                    var data = {};
                    data["todoId"] = $scope.todoId;
                    data["attachType"] = "todo";
                    data["attachId"] = $scope.changedTodos[todoId].id;
                    if($scope.changedTodos[todoId].active == true){
                        updateTodoDataMap["add"].push(data);
                        updateTodoMap["add"].push($scope.changedTodos[todoId]);
                    }else{
                        updateTodoDataMap["remove"].push(data);
                        updateTodoMap["remove"].push($scope.changedTodos[todoId]);
                    }
                }
                $http.put(url.projectApiUrl() + "/todos/attach", updateTodoDataMap);
                $rootscope.$broadcast("updateTodoTab", updateTodoMap);
                $scope.changedTodos = {};
                $("#linkTodo").modal("hide");
            };
        }
    ]);

