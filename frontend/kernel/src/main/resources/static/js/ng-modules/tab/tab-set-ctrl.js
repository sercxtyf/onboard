/**
 * Created by R on 2014/11/18.
 */

// tab子页面
angular.module('tab')
    .controller('tabSetCtrl', ['$scope', '$http',
        function($scope, $http) {
            $scope.attachType = undefined;
            $scope.attachId = -1;
            $scope.updateEvent = "updateTab";
            $scope.$on($scope.updateEvent, function(event, data){
                if(data == undefined || data.attachId == undefined || data.attachType == undefined){
                    return;
                }
                $scope.attachId = data.attachId;
                $scope.attachType = data.attachType;
                $scope.projectId = data.projectId;
                $scope.companyId = data.companyId;
                $scope.$broadcast("update-tab-detail", data);
            });
            $scope.updateTab = function () {
                $scope.$broadcast("update-tab-detail", { tab : this.tab });
            }
        }
    ]);
