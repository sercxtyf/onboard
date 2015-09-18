/**
 * Created by R on 2014/11/19.
 */

// 关联任务页面
angular.module('iteration')
    .controller('linkIterationCtrl', ['$scope', '$http', 'filterFilter', 'todolistService', 'url',
        function ($scope, $http, filterFilter, todolistService, url) {
            $scope.linkingTodolists = [];
            $scope.seletedLinkingTodolist = {todos: []};
            function init(event, data){
                $scope.linkingTodolists = [];
                $scope.seletedLinkingTodolist = {todos: []};
                $scope.linkingTodolists = [];
                $scope.iteration = data.iteration;
                var tempTodolists = $scope.todolists.slice();
                var linkedTodoIds = [];
                if (data.linkedTodos != undefined) {
                    for (var i = 0; i < data.linkedTodos.length; i++) {
                        linkedTodoIds.push(data.linkedTodos[i].id);
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
                        tempTodolists[i].todos[j].disable = false;
                        tempTodolists[i].todos[j].active = false;
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
                $("#linkIteration").modal("show");
            }
            $scope.$on("showAddLinkedIteration", function (event, info) {
                if (info == undefined || info.iteration == undefined) {
                    return;
                }
                //init data
                todolistService.getTodolists(info.projectId, info.companyId, true).then(function(todolists){
                    $scope.todolists = todolists;
                    init(event, info);
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
                var updateIterationTodoMap = {
                    "add" : [],
                    "remove": []
                };
                var updateTodoMap = {
                    "add" : [],
                    "remove": [],
                    "iterationId": $scope.iteration.id
                };
                for(index in todoIds){
                    var todoId = todoIds[index];
                    data = {};
                    data["todoId"] = $scope.changedTodos[todoId].id;
                    data["iterationId"] = $scope.iteration.id;
                    data["status"] = "todo";
                    if($scope.changedTodos[todoId].active == true){
                        updateIterationTodoMap["add"].push(data);
                        $scope.iteration.addTodo($scope.changedTodos[todoId]);
                    }else{
                        updateIterationTodoMap["remove"].push(data);
                        $scope.iteration.removeTodo($scope.changedTodos[todoId]);
                    }
                }
                $http.put(url.projectApiUrl() + "/iterationtodos", updateIterationTodoMap);
                $scope.changedTodos = {};
                $("#linkIteration").modal("hide");
            };
        }
    ]);

