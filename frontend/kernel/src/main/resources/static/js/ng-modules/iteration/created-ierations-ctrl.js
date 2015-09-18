/**
 * Created by R on 2014/11/25.
 */
angular.module('iteration')
.controller('createdIterationsCtrl', ['$scope', '$http', '$state', 'iterationService', 'url',
    function ($scope, $http, $state, iterationService, url) {
        //for filters
        $scope.iterations = [];
        $scope.searchTodo = {};
        $scope.searchTodo.todoType = '';
        $scope.filterIteration = {status: "created"};
        $scope.todoTypes = [{
            value: "",
            name: "所有"
        }, {
            value: "task",
            name: "任务"
        }, {
            value: "story",
            name: "需求"
        }, {
            value: "bug",
            name: "BUG"
        }];
        $scope.deleteIteration = function (iteration) {
            if (confirm('确认删除该迭代?')) {
                iteration.delete();
                $scope.iterations.splice($scope.iterations.indexOf(iteration), 1);
            }
        };
        $scope.activeIteration = {};
        iterationService.getActiveIterations(true).then(function(iteration){
            $scope.activeIteration = iteration;
        });
        iterationService.getCreatedIterations(true).then(function(iterations){
            $scope.iterations = iterations;
        });

        //active operation
        $scope.canActive = function ($first) {
            return $first && $scope.activeIteration && $scope.activeIteration.id == undefined;
        };
        function checkIterationTime(iteration){
            if (iteration.startTime == null || iteration.endTime == null) {
                alert("请选择开始时间和截止时间！！");
                return false;
            }
            if(!(iteration.startTime && iteration.endTime)){
                alert("请选择开始时间和截止时间！！");
                return false;
            }
            var startTime, endTime;
            if(typeof(iteration.startTime) == "number" || typeof(iteration.startTime) == "string"){
                startTime = new Date(iteration.startTime);
            }else{
                startTime = iteration.startTime;
            }
            if(typeof(iteration.endTime) == "number" || typeof(iteration.endTime) == "string"){
                endTime = new Date(iteration.endTime);
            }else{
                endTime = iteration.endTime;
            }
            if(endTime - startTime <= 0){
                alert("开始时间必须大于截止时间");
                return false;
            }
            return true;
        }
        $scope.updateIterationTime = function(iteration){
            if(checkIterationTime(iteration)){
                iteration.updateByAttrs(['startTime', 'endTime']);
                iteration.timeForm = !iteration.timeForm;
            }
        };
        $scope.doActiveIteration = function (iteration) {
            if(checkIterationTime(iteration)){
                $scope.activeIteration = iteration;
                iteration.active().then(function(){
                    $state.go('company.project.iteration.activeIteration');
                });
            }
        };

        // sortable!!
        $scope.sortableOptions = {
            connectWith: ".list-group-dropBody",
            placeholder: "todo-item-placeholder",
            start: function (e, ui) {
                ui.item.addClass('todo-item-intheair');
                $(".list-group-dropBody").css('min-height', '42px');
            },
            stop: function (e, ui) {
                ui.item.removeClass('todo-item-intheair');
                var todo = ui.item.sortable.model;
                var iteratoinId = ui.item.sortable.droptarget.attr('data-id');
                data = {};
                data["todoId"] = todo.id;
                data["iterationId"] = iteratoinId;
                data["status"] = "todo";
                $http.post(url.projectApiUrl() + "/iterationtodos", data);
                $(".list-group-dropBody").css('min-height', '0px');
            }
        };

    }
]);
