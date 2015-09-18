/**
 * Created by R on 2014/11/25.
 */

angular.module('iteration')
    .controller('activeIterationCtrl', ['$rootScope', '$scope', '$http', '$state', '$location',
        'iterationService', 'todoService', 'url','user', 'stepDataService', 'stepService', 'utilService', 'storyService',
        function ($rootScope, $scope, $http, $state, $location, iterationService, todoService, url,
                  user, stepDataService, stepService, utilService, storyService) {
            $scope.activeIteration = {};
            $scope.loaded = false;
            $scope.initActiveIteration = function () {
                iterationService.getActiveIterations(true).then(function (iteration) {
                    $scope.loaded = true;
                    $scope.activeIteration = iteration;
                    $scope.updateStatusTodos();
                });
            };
            //$scope.initActiveIteration();

            $scope.isInfoExpand = true;

            $scope.isStoryEmpty = function(){
                if($scope.activeIteration.iterables === undefined)
                    return false;
                else
                    return $scope.activeIteration.iterables.filter(function(ele){return ele.type === 'story'}).length !== 0;
            };

            $scope.infoToggle = function(){
                $scope.isInfoExpand = !$scope.isInfoExpand;
                $(".active-iteration-info").slideToggle(200);
            };

            $scope.isActive = function (path) {
                return $location.path().indexOf(path) >= 0;
            };
            todoService.getTodoStatuses().then(function (todoStatuses) {
                $scope.allTodoStatus = todoStatuses;
            });
            
            function isAllCompleted(){
                for (var i = 0; i < $scope.activeIteration.boardables.length; i++) {
                    if($scope.activeIteration.boardables[i].iterationStatus !== 'closed')
                        return false;
                }
                return true;
            }

            $scope.isDisplay = function (status) {
                //配置打开的状态
                if(status.active){
                    return true;
                }
                //配置虽然不打开，但仍有任务处于该状态
                if ($scope.statusTodos[status.value].length > 0) {
                    return true;
                }
                return false;
            };
            
            $scope.canDelete = function (status) {
                if (status.value == "todo" || status.value == "inprogress" || status.value == "closed") {
                    return false;
                }
                return true;
            };
            $scope.updateStatusSettings = function (status) {
                if(status.active && $scope.statusTodos[status.value].length > 0){
                    return;
                }
                status.active = !status.active;
                if (status.active) {
                    $http.post(url.projectApiUrl() + "/todostatus/" + status.value);
                } else {
                    $http.delete(url.projectApiUrl() + "/todostatus/" + status.value);
                }
            };

            $scope.toNextStatus = function(todo) {
                var i;
                for (i = 0; i < $scope.allTodoStatus.length; i++) {
                    if (todo.iterationStatus === $scope.allTodoStatus[i].value) {
                        break;
                    }
                }
                for (i = i + 1; i < $scope.allTodoStatus.length; i++) {
                    if($scope.isDisplay($scope.allTodoStatus[i])){
                        todo.updateIterationStatus($scope.allTodoStatus[i].value).then(function(){
                            $scope.updateStatusTodos();
                        });
                        return;
                    }
                }
            };
            //time operation
            $scope.showTimeForm = function () {
                $('#updateIterationForm').show();
            };
            $scope.hideTimeForm = function () {
                $('#updateIterationForm').hide();
            };
            $scope.updateActiveIterationTime = function () {
                if (!($scope.activeIteration.startTime && $scope.activeIteration.endTime)) {
                    alert("开始时间与截止时间不能为空");
                    return;
                }
                var startTime = $scope.activeIteration.startTime;
                var endTime = $scope.activeIteration.endTime;
                if (typeof(startTime) == "number" || typeof(startTime) == "string") {
                    startTime = new Date(startTime);
                }
                if (typeof(endTime) == "number" || typeof(endTime) == "string") {
                    endTime = new Date(endTime);
                }
                if (endTime - startTime <= 0) {
                    alert("开始时间必须大于截止时间");
                    return;
                }
                if (startTime - new Date() > 0) {
                    alert("开始时间必须大于当前时间");
                    return;
                }
                $scope.activeIteration.update();
                $scope.$broadcast('timeChange');
            };

            $scope.searchTodo = {deleted: false};

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
                    todo.updateIterationStatus(status);
                    $scope.updateStatusTodos();
                }
            };

            user.getCurrentUser().then(function(user) {
                $scope.currentUser = user;
                $scope.user = user;
            });
            
            user.getProjectUsers(false, $scope.projectId).then(function(users){
                $scope.projectUsers = users.slice();
            });

            $scope.newStepDueDateMin = new Date();
            $scope.addStepPre = function(story){
                $("#add-step-modal").modal("show");
                $scope.selectedStory = story;
                $scope.newStep = stepService.getNew($scope.currentUser, story);
            };

            $scope.statusTodos =  {
                todo: [],
                inprogress: [],
                fixed: [],
                approved: [],
                reviewed: [],
                verified: [],
                closed: []
            };
            
            var statusTodosUpdater = {
                    type : "statusTodosUpdater",
                    id   : 1,
                    afterUpdateItem: function(boardable){
                        if(boardable.deleted && !boardable.origin.deleted){
                            $scope.activeIteration.boardables.splice($scope.activeIteration.boardables.indexOf(boardable), 1);
                        }
                        $scope.updateStatusTodos();
                    }
            }

            $scope.updateStatusTodos = function(){
                $scope.statusTodos =  {
                        todo: [],
                        inprogress: [],
                        fixed: [],
                        approved: [],
                        reviewed: [],
                        verified: [],
                        closed: []
                    };
                for(var i = 0; i < $scope.activeIteration.boardables.length; i++){
                    if($scope.user.name === $scope.defaultUser.name || $scope.activeIteration.boardables[i].assignee.name === $scope.user.name) {
                        $scope.activeIteration.boardables[i].addObserver(statusTodosUpdater);
                        $scope.statusTodos[$scope.activeIteration.boardables[i].iterationStatus].push($scope.activeIteration.boardables[i]);
                    }
                }
            };

            $scope.addStep = function(){
                if($scope.newStep.content === ""){
                    alert("请输入任务描述！");
                    return;
                }
                $scope.newStep.create().then(function(){
                    $scope.activeIteration.iterables.push($scope.newStep);
                    $scope.activeIteration.boardables.push($scope.newStep);
                    $scope.updateStatusTodos();
                    $("#add-step-modal").modal("hide");
                });
            };
            
            $scope.getProjectUsers = function() {
                user.getProjectUsers(false, url.projectId(), url.companyId()).then(function(users){
                    $scope.projectUsers = users.slice();
                });
            };

            $scope.defaultUser = { name: '全体成员'};
            $scope.user = $scope.defaultUser;
            $scope.$on('chooseUser', function(event, user) {
                $scope.user = user;
                $scope.updateStatusTodos();
            });

            $scope.getAssigneeSet = function(story){
                var assignees = [];
                var boardables = $scope.getSteps(story);
                for(var i = 0; i < boardables.length; i++){
                    if(!boardables[i].assignee || utilService.contains(assignees, boardables[i].assignee)){
                        continue;
                    }
                    assignees.push(boardables[i].assignee);
                }
                return assignees;
            };
            
            $scope.getCompletedSteps = function(story){
                var completedBoardables = [];
                for(var i = 0; i < $scope.activeIteration.iterables.length; i++){
                    if($scope.activeIteration.iterables[i].type === 'step' 
                        && $scope.activeIteration.iterables[i].attachId == story.id
                        && $scope.activeIteration.iterables[i].iterationStatus === 'closed'){
                        completedBoardables.push($scope.activeIteration.iterables[i]);
                    }
                }
                return completedBoardables;
            };
            
            $scope.getSteps = function(story){
                var completedBoardables = [];
                for(var i = 0; i < $scope.activeIteration.iterables.length; i++){
                    if($scope.activeIteration.iterables[i].type === 'step' 
                        && $scope.activeIteration.iterables[i].attachId == story.id){
                        completedBoardables.push($scope.activeIteration.iterables[i]);
                    }
                }
                return completedBoardables;
            };

            $scope.completeStory = function(story) {
                if (story.completed) return ;
                storyService.completeStory(story).then( function(data) {

                });
            };

            $(document).keydown(function(event) {
                if($scope !== undefined && $scope.isInfoExpand !== undefined){ 
                    switch (event.keyCode) {
                        case 38: 
                            if($scope.isInfoExpand === false) {
                                $scope.infoToggle();
                                $scope.$apply();
                                return false;
                            }
                            break;
                        case 40: 
                            if($scope.isInfoExpand === true) {
                                $scope.infoToggle();
                                $scope.$apply();
                                return false;
                            }
                            break;
                        default:
                            break;
                    }
                }
            });

        }
    ]);