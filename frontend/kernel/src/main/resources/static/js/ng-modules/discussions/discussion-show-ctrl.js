angular.module('discussions')
    .controller('discussionShowCtrl', ['$scope', '$state', '$http', '$sce', '$document', '$rootScope', 'tab', 'discussionService', 'drawer', '$timeout', 'url',
        function($scope, $state, $http, $sce, $document, $rootScope, tab, discussionService, drawer, $timeout, url) {

            $scope.gotoMePage = function(id) {
                $state.go('company.me', {
                    userId: id
                });
            };

            var successFun = function(data) {
                for (var key in data.attachments) {
                    if (data.attachments[key].contentType.match('image')) {
                        data.attachments[key].contentType = 'image';
                    }
                }
                $scope.discussion = data;
                $scope.$broadcast('updateTab', {
                    attachType: "discussion",
                    attachId: data.id,
                    projectId: data.projectId,
                    companyId: data.companyId,
                    disucssion: data
                });
                $scope.deleted = data.deleted;
            };

            var show = function(discussionId, projectId) {
                $scope.discussion = {};
                $scope.deleted = false;
                $scope.recovering = false;
                $scope.showSubscribers = false;
                discussionService.getDiscussion(discussionId, projectId).then(successFun, function() {
                    alert('未能获取讨论的信息！');
                    $timeout(drawer.close);
                });
            };
            show($scope.id, $scope.projectId);

            $scope.recover = function() {
                $scope.recovering = true;
                discussionService.recoverDiscussion($scope.discussion.id).then(function(data) {
                    //console.log('恢复讨论成功');
                    $scope.recovering = false;
                    $scope.deleted = false;
                    $scope.discussion = data;
                }, function(data) {
                    alert('恢复讨论失败');
                    $scope.recovering = false;
                });
            };

            $scope.toDelete = function() {
                if (confirm('确定要删除该该讨论吗？')) {
                    discussionService.deleteDiscussion($scope.discussion.id).then(function(data) {
                        $scope.deleted = true;
                        $scope.discussion.deleted = true;
                        $timeout(drawer.close);
                        //$state.go('company.project.discussions');
                    }, function(data) {
                        alert('删除讨论失败');
                    });
                }
            };

            $scope.toEdit = function() {
                drawer.open({
                    type: 'discussion-edit',
                    data: {
                        edit: true,
                        discussion: $scope.discussion
                    }
                });
            };

            $scope.getProjectUri = url.projectApiUrl() + '/';

            $scope.attachmentDownloadLink = function(attachmentId) {
                return [url.projectApiUrl($scope.projectId,$scope.companyId), 'attachments', attachmentId, 'download'].join("/");
            };

            $scope.wikiTrustDangerousSnippet = function(htmlString) {
                return $sce.trustAsHtml(htmlString);
            };
            $scope.tabs = [tab.getTabInfo('comment', true), tab.getTabInfo('activity')];
        }
    ]);
