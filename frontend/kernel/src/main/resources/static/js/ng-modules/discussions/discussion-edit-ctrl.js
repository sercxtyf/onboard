angular.module('discussions')
    .controller('discussionEditCtrl', ['$scope', '$http', '$document', '$rootScope', 'richtexteditor', 'user', 'discussionService', '$timeout', 'drawer',
        function($scope, $http, $document, $rootScope, richtexteditor, user, discussionService, $timeout, drawer) {
            $scope.newDiscussion = {};
            $scope.attachments = [];
            $scope.showSubscribers = false;
            $scope.submitted = false;

            $scope.uploadedImageInTextEditor = [];
            //编辑器
            $timeout(function() {
                richtexteditor.initEditAreaFullTools($('#discussion-content'), {
                    height: 200
                }, $scope.uploadedImageInTextEditor);
                if($scope.edit) {
                    $('#discussion-content').code($scope.discussion.content);
                }
            });

            // create discussion
            if($scope.edit === false) {
                user.getSubscriberList().then(function(users) {
                    $('#newDiscussion .subscribers').memberselector({
                        data   : users,
                        columns: 3
                    });
                });
                // update discussion
            } else {
                //$scope.reset();
                $scope.newDiscussion = $scope.discussion;
                user.getSubscriberList($scope.discussion.subscribers).then(function(users) {
                    $('#newDiscussion .subscribers').memberselector({
                        data   : users,
                        columns: 3
                    });
                });
                $timeout(function() {
                    $scope.$broadcast('addFiles', $scope.discussion.attachments)
                });
                //$('#discussion-content').code($scope.discussion.content);
            }

            var showDiscussion = function(discussionId) {
                drawer.open({
                    type  : 'discussion',
                    params: {
                        id: discussionId
                    }
                });
            };
            //submit new discussion
            $scope.submitNewDiscussion = function() {
                var editorContent = $('#discussion-content').code();
                if(editorContent.length >= 65535) {
                    alert('讨论内容过长，请删减！');
                    return;
                }
                var subUsers = $('#newDiscussion .subscribers').memberselector('getData');
                //console.log('subscribers:');
                //console.log(subUsers);

                // extend attachments with uploaded Image in richTextEditor
                $.merge($scope.attachments, $scope.uploadedImageInTextEditor);

                var sentData = {
                    subject    : $scope.newDiscussion.subject,
                    content    : editorContent,
                    attachments: $scope.attachments,
                    subscribers: subUsers
                };
                $scope.submitted = true;
                if($scope.edit) {
                    sentData.id = $scope.newDiscussion.id;
                    discussionService.updateDiscussion($scope.newDiscussion.id, sentData).then(function(id) {
                        //$scope.reset();
                        $scope.submitted = false;
                        $timeout(function() {
                            showDiscussion(id);
                        });
                    }, function(data) {
                        alert('修改讨论失败');
                    });
                } else {
                    discussionService.createDiscussion(sentData).then(function(id) {
                        //$scope.reset();
                        $scope.submitted = false;
                        $timeout(function() {
                            showDiscussion(id);
                        });
                    }, function(data) {
                        alert('新建讨论失败');
                    });
                }
            };
            $scope.reset = function() {
                $scope.newDiscussion = {};
                $scope.attachments = [];
                $scope.showSubscribers = false;
                user.getSubscriberList().then(function(users) {
                    $('#newDiscussion .subscribers').memberselector({
                        data   : users,
                        columns: 3
                    });
                });
                $('#discussion-content').code('');
                this.newDiscussionForm.subject.$dirty = false;
                $scope.$broadcast('discussionHideAttachment');
            };
            $scope.toggleSubscribers = function() {
                $scope.showSubscribers = !$scope.showSubscribers;
            };
        }
    ]);
