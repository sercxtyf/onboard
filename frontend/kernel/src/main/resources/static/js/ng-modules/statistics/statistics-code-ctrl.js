angular.module('statistics')
    .controller('statisticsCodeCtrl', ['$scope', '$http', '$state', 'commitsTrend', 'commitsPunchCard', 'user', 'url',

        function($scope, $http, $state, commitsTrend, commitsPunchCard, user, url) {

            $scope.tab = 1;
            $scope.isSet = function(checkTab) {
                return $scope.tab === checkTab;
            };
            $scope.setTab = function(activeTab, $event) {
                if ($scope.tab !== activeTab) {
                    $scope.$broadcast('tabSwitch', activeTab);
                }
                $scope.tab = activeTab;
                $event.stopPropagation();
            };

            $scope.graphInfo = function() {
                if (this.tab === 1) {
                    return '各个整点的代码提交次数';
                } else if (this.tab === 2) {
                    return '代码提交影响行数趋势图';
                } else {
                    return '代码提交次数趋势图';
                }
            };

            //setting data
            $scope.defaultUser = {
                id: 0,
                name: '全体成员',
                avatarUrl: url.defaultAvatarUrl
            };
            $scope.user = $scope.defaultUser;
            $scope.busy = false;
            var formatString = '%Y-%m-%d';
            var today = new Date();
            var monthBefore = new Date();
            //$scope.end = d3.time.d3.time.format(formatString)(today);
            $scope.end = today - 0;
            //monthBefore.setMonth(monthBefore.getMonth() - 1);
            monthBefore.setDate(monthBefore.getDate() - 6);
            //$scope.start = d3.time.format(formatString)(monthBefore);
            $scope.start = monthBefore - 0;

            var updateDates = function() {
                $scope.$broadcast('datesChanged', $scope.tab);
            };

            $scope.refresh = function() {
                if ($scope.start > $scope.end) {
                    alert('起始时间不能晚于截止时间！');
                } else {
                    var limit = new Date($scope.end - 0);
                    limit.setMonth(limit.getMonth() - 6);
                    if ($scope.start < limit) {
                        alert('日期范围不能超出半年-_-');
                    } else {
                        updateDates();
                    }
                }
            };

            var updateUser = function() {
                $scope.$broadcast('userChanged', $scope.tab);
            };

            $scope.$on('chooseUser', function(event, user) {
                if (user.id !== $scope.user.id) {
                    $scope.user = user;
                    updateUser(user.id);
                }
            });

            $scope.selectTimeSpan = function(num) {
                $scope.started = false;
                $scope.ended = false;
                var t = new Date($scope.end - 0);
                switch (num) {
                    case 1:
                        $scope.start = t.setDate(t.getDate() - 6) - 0;
                        break;
                    case 2:
                        $scope.start = t.setDate(t.getDate() - 13) - 0;
                        break;
                    case 3:
                        $scope.start = t.setMonth(t.getMonth() - 1) - 0;
                        break;
                    case 4:
                        $scope.start = t.setMonth(t.getMonth() - 3) - 0;
                        break;
                    case 5:
                        $scope.start = t.setMonth(t.getMonth() - 6) - 0;
                        break;
                }
                $scope.refresh();
            };

            //date picker
            $scope.started = false;
            $scope.ended = false;
            $scope.openDTPicker = function($event, num) {
                $event.preventDefault();
                $event.stopPropagation();
                if (num === 1) {
                    $scope.started = true;
                    $scope.ended = false;
                } else {
                    $scope.ended = true;
                    $scope.started = false;
                }
            };

            $scope.$on('dataRetrieved', function(event, data) {
                $scope.busy = false;
            });
            $scope.$on('requestSent', function(event, data) {
                $scope.busy = true;
            });

            updateDates();
        }
    ]);
