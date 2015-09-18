angular.module('util')
    .directive('onboardComment', ['$http', 'url', 'commentService', 'user', '$sce', '$modal', 'richtexteditor', 'uploadsService', 'emojiService',
        function($http, url, commentService, user, $sce, $modal, richtexteditor, uploadsService, emojiService) {

            var controller = ['$scope', function($scope) {
                $scope.atUserSubscribers = [];
                $scope.atUsersList = {};

                this.initAtFunc = function() {
                    this.atUsersList = $scope.atUsersList;
                    this.atUserSubscribers = $scope.atUserSubscribers;
                    $scope.initAtFunctionality();
                };

                $scope.initAtFunctionality = function() {
                    $scope.atUserSubscribers.splice(0);
                    $('.note-editable').each(function(i, element) {
                        $(element).atwho({
                            at            : "@",
                            data          : $scope.atUsersList,
                            displayTpl    : "<li><span userid='${id}'>${name} - ${email}</span></li>",
                            insertTpl     : "<b>${atwho-at}${name}</b>",
                            searchKey     : "search",
                            startWithSpace: false
                        }).atwho({
                            at            : ":",
                            data          : emojiService.emojiList,
                            limit         : 10,
                            displayTpl    : "<li>${name}<i class='twa twa-lg twa-${name}'></i></li>",
                            insertTpl     : "<i class='twa twa-lg twa-${name}'></i>",
                            startWithSpace: false
                        });

                        $(element).on("inserted.atwho", function(event, $li) {
                            $scope.atUserSubscribers.push({
                                id: $li.find('span').attr('userid')
                            });
                        });
                    });
                };

                $scope.initProjectUser = function() {
                    user.getProjectUsers(true, $scope.projectId, $scope.companyId).then(function(data) {
                        $scope.atUsersList = $.map(data, function(value, i) {
                            return {
                                id     : value.id,
                                name   : value.name,
                                email  : value.email,
                                search : value.name + value.email,
                                userURL: ['/teams/', $scope.companyId, '/users/', value.id, '/activities'].join("")
                            };
                        });
                    });
                };
                $scope.initProjectUser();
            }];

            function link(scope, ele, attrs, controller) {

                scope.avatarUrlInComment = url.avatarUrl;
                user.getCurrentUser().then(function(user) {
                    scope.currentUser = user;
                }, function(data) {
                    console.log("Error when getting current user data in Onboard Comment Directive! ")
                });

                scope.wikiTrustDangerousSnippet = function(htmlString) {
                    return $sce.trustAsHtml(htmlString);
                };

                scope.uploadedImageInTextEditor = [];

                scope.resetError = function() {
                    scope.stat = "";
                    scope.msg = "";
                };

                scope.getAllComments = function() {
                    if(scope.attachId < 1) {
                        scope.comments = [];
                    } else {
                        commentService.getComments(scope.attachType, scope.attachId, scope.projectId, scope.companyId).then(function(data) {
                            scope.comments = data.comments;
                            scope.subscribers = data.subscribers;
                        }, function(data) {
                            console.log("Error when fetching comment data!")
                        });
                    }
                };

                scope.checkCommentAccess = function(comment) {
                    return (comment.creatorId === scope.currentUser.id);
                };

                scope.displayCommentImage = function(attachment, $event) {

                    var commentImageUrl = [url.projectApiUrl(attachment.projectId, attachment.companyId), '/attachments/image/', attachment.id].join(""),
                        commentImageTitle = attachment.name,
                        imageData = {
                            commentImageUrl  : commentImageUrl,
                            commentImageTitle: commentImageTitle
                        };

                    $modal.open({
                        templateUrl: 'commentImageModal.html',
                        controller : 'commentImageModalCtrl',
                        keyboard   : true,
                        backdrop   : true,
                        size       : 'lg',
                        resolve    : {
                            imageData: function() {
                                return imageData;
                            }
                        }
                    });
                };

                scope.editComment = function(comment, $event) {
                    scope.resetError();

                    var curCommentDiv = $($event.target).parentsUntil('.comment').parent().eq(0),
                        editCommentDiv = $('#onboardCommentDirective .edit-comment'),
                        editor = $(editCommentDiv).find('.fake-input-content');

                    scope.commentToBeEdit = comment;
                    scope.uploadedImageInTextEditor.splice(0);
                    richtexteditor.initEditAreaSimpleTools(editor, {}, scope.uploadedImageInTextEditor);
                    $(editor).code(scope.commentToBeEdit.content);
                    scope.initUploadAttachments(editCommentDiv, scope.commentToBeEdit.attachmentDTOs);

                    controller.initAtFunc();

                    $(editCommentDiv).show().insertAfter($(curCommentDiv).hide());
                };

                scope.initUploadAttachments = function(form, attachmentList) {
                    var form = $(form);
                    scope.uploadedFiles = [];
                    if(attachmentList) scope.uploadedFiles = attachmentList;

                    scope.onFileSelect = function($files) {
                        var tempfile;
                        for(var i = 0; i < $files.length; i++) {
                            tempfile = $files[i];

                            if(tempfile.size >= 32000000) {
                                scope.stat = 'error';
                                scope.msg = '上传失败：上传文件过大！';
                                scope.splice(scope.uploadedFiles.indexOf(tempfile), 1);
                                break;
                            }
                            scope.upload = $upload.upload({
                                url : url.projectApiUrl() + '/attachments/stage',
                                file: tempfile
                            }).progress(function(e) {
                                form.find(".attach-Progress").show();
                                scope.dynamic = parseInt(100.0 * e.loaded / e.total);
                            }).success(function(data, status, headers, config) {
                                scope.uploadedFiles.push(data);
                                scope.stat = 'success';
                                scope.msg = '上传成功！';
                                //hide loadprogressbar
                                form.find(".attach-Progress").hide();
                            }).error(function(data, status, headers, config) {
                                scope.stat = 'error';
                                scope.msg = '上传失败：' + data;
                            })
                        }
                    };
                    scope.remove = function(file) {
                        $http.delete(url.projectApiUrl() + "/attachments/" + file.id);
                        var idx = scope.uploadedFiles.indexOf(file);
                        if(idx > -1) {
                            scope.uploadedFiles.splice(idx, 1);
                        }
                    };
                };

                scope.deleteComment = function(comment, $event) {
                    if(confirm("确实要删除评论吗？")) {
                        commentService.deleteComment(comment.id, scope.projectId, scope.companyId).then(function(data) {
                            $($event.target).parentsUntil('.media').parent().hide(500).remove();
                        }, function(data) {
                            scope.stat = 'error';
                            scope.msg = '删除评论失败！'
                        });
                    }
                };

                scope.saveCommentEdit = function($event, commentToBeEdit) {

                    $($event.target).attr('disabled', true);

                    var editDiv = $($event.target).parentsUntil('.edit-comment').parent().eq(0),
                        content = editDiv.find('.fake-input-content').code();
                    if(content == '<p><br></p>') {
                        scope.stat = 'error';
                        scope.msg = '您输入的评论为空！';
                        $($event.target).attr('disabled', false);
                        return;
                    }

                    $.merge(scope.uploadedFiles, scope.uploadedImageInTextEditor);

                    commentToBeEdit.content = content;
                    commentToBeEdit.attachmentDTOs = scope.uploadedFiles;
                    commentToBeEdit.attachments = scope.uploadedFiles;

                    if(controller.atUserSubscribers.length) {
                        if(!commentToBeEdit.subscriberDTOs) {
                            commentToBeEdit.subscriberDTOs = [];
                        }
                        $.merge(commentToBeEdit.subscriberDTOs, controller.atUserSubscribers);
                    }

                    commentService.updateComment(commentToBeEdit, commentToBeEdit.projectId, commentToBeEdit.companyId).then(function(data) {
                        //commentToBeEdit = data;
                        scope.cancelCommentEdit($event);
                        $($event.target).attr('disabled', false);
                        controller.atUserSubscribers.splice(0);

                        //上传成功后，刷新upload页面
                        uploadsService.InitUploadsList(url.projectId(), url.companyId());

                    }, function(data, status, headers, config) {
                        scope.stat = 'error';
                        scope.msg = '文档更新失败';
                        $($event.target).attr('disabled', false);
                    });

                };

                scope.cancelCommentEdit = function($event) {
                    var editDiv = $($event.target).parentsUntil('.edit-comment').parent().eq(0);
                    editDiv.find('.fake-input-content').destroy();
                    editDiv.prev().show();
                    editDiv.hide().insertAfter($('.new-comment'));
                    scope.resetError();
                };

                scope.$watch('attachId', function(v) {
                    if(!v) return;
                    scope.getAllComments();
                });
            }

            return {
                restrict   : 'E',
                replace    : true,
                link       : link,
                controller : controller,
                scope      : {
                    attachType: '=',
                    attachId  : '=',
                    projectId : '=',
                    companyId : '='
                },
                templateUrl: 'onboardCommentDirective.html'
            };

        }
        /* end of directive function */

    ])
;
/* end of directive  */