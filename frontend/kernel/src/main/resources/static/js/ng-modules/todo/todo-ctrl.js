// 任务子页面
angular.module('todo')
    .controller('todoCtrl', ['$scope', '$rootScope', '$http', '$document', '$state', 'tab', 'todolistService', 'todoService', 'user', 'url', '$timeout', 'todoUtilService', '$timeout', '$q', 'keywordService',
        function($scope, $rootScope, $http, $document, $state, tab, todolistService, todoService, user, url, $timeout, todoUtilService, $timeout, $q, keywordService) {
            //ready for show todo
            $scope.todoUtilService = todoUtilService;
            $scope.updateTabEvent = "updateTab";
            $scope.todoDueDateMin = new Date();
            $scope.todoTypes = todoService.todoTypes;
            $scope.priorities = todoService.priorities;
            $scope.statusFilter = { active: true };

            todoService.getTodoById($scope.projectId, $scope.companyId, $scope.id, true).then(function(todo) {
                $scope.$broadcast($scope.updateTabEvent, {
                    attachType: "todo",
                    attachId  : todo.id,
                    projectId : todo.projectId,
                    companyId : todo.companyId,
                    todo      : todo
                });
                todolistService.getTodolistById(todo.projectId, todo.companyId,
                    todo.todolistId, false).then(function(todolist) {
                        $scope.todolist = todolist;
                        $scope.todo = todo;
                    });
            });
            $scope.todoTabs = [
                tab.getTabInfo("comment", true),
                tab.getTabInfo("commit"),
                //                tab.getTabInfo("duration"),
//                tab.getTabInfo("linkedtask"),
//                tab.getTabInfo("linkedstory"),
//                tab.getTabInfo("linkedbug"),
                tab.getTabInfo("activity")
                //,tab.getTabInfo("tabSample")
            ];

            // other infos
            user.getProjectUsers(false, $scope.projectId).then(function(users) {
                $scope.projectUsers = users.slice();
                $scope.projectUsers.splice(0, 0, { name: '未分配', avatarUrl: url.defaultAvatarUrl });
            });
            todolistService.getTodolists($scope.projectId, $scope.companyId).then(function(todolists) {
                $scope.todolists = todolists;
            });
            todoService.getTodoStatuses($scope.projectId, $scope.companyId).then(function(todoStatuses) {
                $scope.todoStatuses = todoStatuses;
            });

            //todo operation
            $scope.openDTP = function($event) {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dtpOpened = true;
            };
            // 修改todo内容
            $scope.editContent = function() {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.edit_content = true;
                $timeout(function() {
                    $('#edit_content').focus();
                });
            };
            // 指定负责人
            $scope.assign = function(user) {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.todo.assigneeId = user.id;
                $scope.todo.updateByAttrs(["assigneeId"], true);
            };
            // 更改所述清单
            $scope.changeTodolist = function(todolist) {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.todo.todolistId = todolist.id;
                $scope.todo.updateByAttrs(["todolistId"]);
            };
            // 指定todo类型
            $scope.setTodoType = function(type) {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.todo.todoType = type.value;
                $scope.todo.updateByAttrs(["todoType"]);
            };
            $scope.setPriority = function(value) {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.todo.priority = value;
                $scope.todo.updateByAttrs(["priority"]);
            };
            $scope.setStatus = function(status) {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.todo.status = status.value;
                $scope.todo.updateByAttrs(["status"]);
            };

            $scope.editDesc = function() {
                if($scope.todolist.archived || $scope.todo.status === "closed") {
                    return;
                }
                $scope.edit_description = true;
                $timeout(function() {
                    $('#edit_description').focus();
                });
            };

            $scope.recover = function(todo) {
                if(confirm("是否从回收站恢复该任务？")) {
                    todo.recover();
                    return true;
                }
                return false;
            };

            var projectId = $scope.projectId === undefined ? url.projectId() : $scope.projectId;
            keywordService.getTodoRecommend($scope.id, projectId, url.companyId()).then(function(data) {
                $scope.recommend = data;
                //console.log($scope.recommend);
            });
        }
    ]);
