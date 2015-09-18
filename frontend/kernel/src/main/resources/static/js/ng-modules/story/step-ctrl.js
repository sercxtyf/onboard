
// 任务子页面
angular.module('stories')
    .controller('stepCtrl', ['$scope', '$rootScope', '$http', '$document', '$state', 'tab', 'stepService', 'user',
                             'url', '$timeout', 'todoUtilService', 'todoService', 'keywordService',
        function ($scope, $rootScope, $http, $document, $state, tab, stepService, user, url, $timeout, todoUtilService,
                todoService, keywordService) {
            //ready for show todo
            $scope.todoUtilService = todoUtilService;
            $scope.updateTabEvent = "updateTab";
            $scope.todoDueDateMin = new Date();
            $scope.statusFilter = {active: true};
            $scope.newStep = {timeSelect : false};
            
            $scope.getStatus = function(step){
                if(!step){
                    return;
                }
                for(var index = 0; index < todoService.todoStatus.length; index++){
                    if(todoService.todoStatus[index].value === step.status){
                        return todoService.todoStatus[index];
                    }
                }
            };
            
            stepService.getById(true, $scope.id, $scope.projectId).then(function(step){
                $scope.$broadcast($scope.updateTabEvent, {
                    attachType: "step",
                    attachId: step.id,
                    step: step,
                    projectId: $scope.projectId
                });
                $scope.step = step;
            });
            $scope.todoTabs = [
                tab.getTabInfo("comment", true),
                tab.getTabInfo("commit"),
                tab.getTabInfo("activity")
            ];

            // other infos
            user.getProjectUsers(false, $scope.projectId).then(function(users){
                $scope.projectUsers = users.slice();
            });
            todoService.getTodoStatuses($scope.projectId, $scope.companyId).then(function(todoStatuses){
                $scope.todoStatuses = todoStatuses;
            });


            //todo operation
            $scope.openDTP = function($event) {
                if($scope.step.status === "closed"){
                    return;
                }
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dtpOpened = true;
            };
            // 修改todo内容
            $scope.editContent = function(){
                if($scope.step.status === "closed"){
                    return;
                }
                $scope.edit_content = true;
                $timeout(function(){
                    $('#edit_content').focus();
                });
            };
            
            $scope.preDelete = function(){
                if(confirm("是否删除该任务？")){
                    $scope.step.delete().then(function(){
                        $('.drawer').drawer('hide');
                    });
                }
            }

            // recommand
            var projectId = $scope.projectId === undefined ? url.projectId() : $scope.projectId;
            keywordService.getStepRecommend($scope.id, projectId, url.companyId()).then(function(data) {
                $scope.recommend = data;
            });
    }
    ]);
