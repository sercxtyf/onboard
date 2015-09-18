/**
 * Created by 胡天翔 on 2015-06-23.
 */

angular
    .module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            .state('company.notifications', {
                url        : '/notifications',
                templateUrl: 'notifications.html',
                controller : 'notificationsCtrl'
            })
            /*
            .state('company.notifications.all', {
                url        : '/notifications/all',
                templateUrl: 'notifications.html',
                controller : 'notificationsCtrl'
            })
            .state('company.notifications.unread', {
                url        : '/notifications/unread',
                templateUrl: 'notifications.html',
                controller : 'notificationsCtrl'
            });*/
    }])
    .controller('notificationsCtrl', ['$scope', '$state', '$http', 'url', 'company', 'user', 'drawer', function($scope, $state, $http, url, company, user, drawer) {
        $scope.notifications = [];
        $scope.loadMoreNotifications = function() {
            $http.get("/api/" + $scope.companyId + "/notificatioins?start=" + $scope.notifications.length + "&limit=-1").success(function(data) {
                Array.prototype.push.apply($scope.notifications, data);
                console.log($scope.notifications);
            }).error(function(){
                //alert('add failed!');
            });
        }
        $scope.filterAsUnread = function() {
            $scope.selectPanel = 0;
            $scope.notifications = [];
            $scope.loadMoreNotifications();
            $scope.subpageTitle = "未读通知";
        };
        $scope.filterAsAll = function () {
            $scope.selectPanel = 1;
            $scope.notifications = [];
            $scope.loadMoreNotifications();
            $scope.subpageTitle = "全部通知";
        };
        $scope.filterAsUnread();
    }]);
