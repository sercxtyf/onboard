// 活动tab页面
angular.module('tab')
    .controller('activityTabCtrl', ['$scope', '$http', 'url', 'user', '$q',
        function($scope, $http, url, user, $q) {
            $scope.updateActivityTab = function() {
                if($scope.attachId < 1) {
                    $scope.activities = [];
                    return;
                } else {
                    var promise1 = $http.get(url.projectApiUrl($scope.projectId, $scope.companyId) + "/activities?attachType=" + $scope.attachType + "&attachId=" + $scope.attachId),
                        promise2 = user.getProjectUsers(false, $scope.projectId);

                    // add activity creator avatar
                    $q.all([promise1, promise2]).then(function(results) {
                        var activities = results[0].data,
                            projectUsers = results[1];
                        _.each(activities, function(activity) {
                            var user = _.find(projectUsers, function(user) {
                                return user.id === activity.creatorId;
                            });
                            activity.creator = user;
                        });
                        $scope.activities = activities;
                    });
                }
            };
            $scope.$on("update-tab-detail", function(event, data) {
                if(data == undefined) {
                    return;
                }
                //it's for select one tab
                if(data.tab != undefined) {
                    if(data.tab.name == "activity") {
                        $scope.tab.active = true;
                        $scope.updateActivityTab();
                        return;
                    } else {
                        $scope.tab.active = false;
                        return;
                    }
                } else if(data.attachId != undefined) {
                    if($scope.tab.active) {
                        $scope.updateActivityTab();
                    }
                }
            });
        }
    ]);
