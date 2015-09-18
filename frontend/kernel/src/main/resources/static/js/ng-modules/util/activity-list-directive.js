/**
 * Created by harttle on 11/25/14.
 */

// 这个directive应该做的更通用，或者干掉

angular.module('util')
    .directive('activityList', ['$http', 'url', function ($http, url) {

        function link(scope, ele, attrs) {
            scope.reply = function () {
                scope.replyApi = '';

                $http.post(scope.replyApi, {
                    foo: 'bar'
                }).success(function (data) {

                }).error(function (data) {
                    scope.stat = 'error';
                    scope.msg = data;
                })
            };
            scope.avatarUrl = url.avatarUrl;
        }


        return {
            restrict: 'E',
            replace: true,
            link: link,
            scope: {
                activities: '=activityListItems',
                canReply: '=activityListReply',
                replyer: '=activityListReplyer',
                replyApi: '=activityListReplyApi'
            },
            templateUrl: 'activity-list.html'
        }
    }]);