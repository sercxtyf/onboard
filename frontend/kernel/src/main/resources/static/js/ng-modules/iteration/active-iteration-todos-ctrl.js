/**
 * Created by R on 2014/11/25.
 */

angular.module('iteration')
    .controller('activeIterationTodosCtrl', ['$scope', '$http', '$state', 'iterationService', 'url',
        function ($scope, $http, $state, iterationService, url) {
             iterationService.getActiveIterations(true).then(function(iteration){
                if(iteration.id){
                    $scope.activeIteration = iteration;
                }else{
                    $state.go('company.project.iteration.activeIteration.empty');
                }
            });
            // sortable!!
            $scope.sortableOptions = {
                placeholder: "todo-item-placeholder",
                connectWith: ".todo-drag-body",
                start: function (e, ui) {
                    ui.item.addClass('todo-item-intheair');
                },
                stop: function (e, ui) {
                    ui.item.removeClass('todo-item-intheair');
                    var todo = ui.item.sortable.model;
                    var status = ui.item.sortable.droptarget.attr('data-status');
                    todo.status = status;
                    data = {id: todo.id, status: status};
                    $http.put(url.projectApiUrl() + "/todos/" + todo.id, data);
                }
            };

            //filter for todo
            $scope.filterTodo = function (todo) {
                for(var attr in $scope.searchTodo){
                    if($scope.searchTodo[attr] === undefined || $scope.searchTodo[attr] === "" || $scope.searchTodo[attr] === 0){
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

            $scope.getTodosWithFilter = function(todos){
                var result = [];
                for(var index = 0; index < todos.length; index ++){
                    if($scope.filterTodo(todos[index])){
                        result.push(todos[index]);
                    }
                }
                return result;
            };

            $scope.searchTodo = {deleted: false};


            $scope.$watch('user', function(){
                //$scope.user = user;
                $scope.searchTodo = {
                    "assigneeId": $scope.user.id
                };
            });
        }
    ]);
