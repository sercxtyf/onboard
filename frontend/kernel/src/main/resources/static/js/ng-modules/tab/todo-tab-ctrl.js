/**
 * Created by R on 2014/11/19.
 */
// 任务tab页面
angular.module('tab')
    .controller('todoTabCtrl', ['$scope', '$rootScope', '$http', 'todolistService', 'url',
        function ($scope, $rootscope, $http, todolistService, url) {
            $scope.initTodoTab = function(todoType){
                $scope.todoType = todoType;
            };
            $scope.updateTodoTab = function(){
                $http.get(url.projectApiUrl($scope.projectId, $scope.companyId) + "/stories/"+ $scope.attachId+"/steps"
                ).success(function(data) {
                    $scope.linkedTodos = data;
                });
            };
            $scope.linkedTodos = [];
            $scope.$on("update-tab-detail", function (event, data){
                if(data == undefined){
                    return;
                }
                //it's for select one tab
                if(data.tab != undefined){
                    if(data.tab.name == "linked" + $scope.todoType){
                        $scope.tab.active = true;
                        $scope.updateTodoTab();
                        return;
                    }else{
                        $scope.tab.active = false;
                        return;
                    }
                }else if(data.attachId != undefined){
                    if($scope.tab.active){
                        $scope.updateTodoTab();
                    }
                }
            });
        }
    ]);

