angular.module('tab')
    .controller('commentTabCtrl', ['$scope', '$http', '$state', '$upload', '$location', '$sce', 'url',
        'user', 'richtexteditor', 'commentService', '$modal', '$timeout', '$q', 'uploadsService',
        '$rootScope', 'emojiService',
        function($scope, $http, $state, $upload, $location, $sce, url,
                 user, richtexteditor, commentService, $modal, $timeout, $q, uploadsService,
                 $rootScope, emojiService) {

            $scope.avatarUrlInComment = url.avatarUrl;

            user.getCurrentUser().then(function(user) {
                $scope.currentUser = user;
            }, function(data) {
                console.log("Error when getting current user data in commentTabCtrl! ")
            });

            $scope.wikiTrustDangerousSnippet = function(htmlString) {
                return $sce.trustAsHtml(htmlString);
            };
            $scope.updateCommentTab = function() {
                if($scope.attachId < 1) {
                    $scope.comments = [];
                } else {
                    commentService.getComments($scope.attachType, $scope.attachId, $scope.projectId, $scope.companyId).then(function(data) {
                        $scope.comments = data.comments;
                        $scope.subscribers = data.subscribers;
                    }, function(data) {
                        console.log("Error when fetching comment data!")
                    });

                }
            };
            $scope.updateCommentTab();

            $scope.$on("update-tab-detail", function(event, data) {
                if(data == undefined) {
                    return;
                }
                //it's for select one tab
                if(data.tab != undefined) {
                    if(data.tab.name == "comment") {
                        $scope.tab.active = true;
                        $scope.updateCommentTab();
                        return;
                    } else {
                        $scope.tab.active = false;
                        return;
                    }
                } else if(data.attachId != undefined) {
                    if($scope.tab.active) {
                        $scope.updateCommentTab();
                    }
                }
            });

            $scope.resetError = function() {
                $scope.stat = "";
                $scope.msg = "";
            };

            $scope.uploadedImageInTextEditor = [];
            $('.fake-input-content').each(function(idx, element) {
                richtexteditor.initEditAreaSimpleTools($(element), {}, $scope.uploadedImageInTextEditor);
                //$('.note-editor').hide();
                $('.note-editor:last').hide();
                $(element).show();
            });

            $scope.initAtFunctionality = function() {
                $scope.atUserSubscribers = [];
                $('.note-editable').each(function(i, element) {
                    $(element).atwho({
                        at        : "@",
                        data      : $scope.atUsersList,
                        displayTpl: "<li><span userid='${id}'>${name} - ${email}</span></li>",
                        insertTpl : "<b>${atwho-at}${name}</b>",
                        searchKey : "search",
                        startWithSpace: false
                    }).atwho({
                        at        : ":",
                        data      : emojiService.emojiList,
                        limit     : 10,
                        displayTpl: "<li>${name}<i class='twa twa-lg twa-${name}'></i></li>",
                        insertTpl : "<i class='twa twa-lg twa-${name}'></i>",
                        startWithSpace: false
                    });

                    $(element).on("inserted.atwho", function(event, $li) {
                        $scope.atUserSubscribers.push({
                            id: $li.find('span').attr('userid')
                        });
                    });
                });
            };

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
                $scope.initAtFunctionality();
            });

            $scope.showCommentEditor = function($event) {
                $scope.resetError();
                var form = $($event.target).parentsUntil('form').parent();
                $scope.uploadedImageInTextEditor.splice(0);
                form.find('.note-editor').show();
                form.find('.fake-input-content').hide();
                $scope.initUploadAttachments(form);
                $scope.initCommentSubscribers(form);
                $scope.initAtFunctionality();
                form.find('.attachments-buttons').show();
            };

            $scope.cancelCommentCreate = function($event) {
                var form = $($event.target).parentsUntil('form').parent();
                //form.find('.fake-input-content').destroy();
                form.find('.note-editor').hide();
                form.find('.fake-input-content').show();
                form.find('.attachments-buttons').hide();
                $scope.resetError();
            };

            $scope.saveComment = function($event) {

                $($event.target).attr('disabled', true);

                var form = $($event.target).parentsUntil('form').parent();

                var content = form.find('.fake-input-content').code();
                if(content == '<p><br></p>') {
                    $scope.stat = 'error';
                    $scope.msg = '您输入的评论为空！';
                    $($event.target).attr('disabled', false);
                    return;
                }

                $.merge($scope.uploadedFiles, $scope.uploadedImageInTextEditor);

                var newComment = {
                    content       : content,
                    attachType    : $scope.attachType,
                    attachId      : $scope.attachId,
                    attachmentDTOs: $scope.uploadedFiles,
                    subscriberDTOs: $scope.selectedUsers.map(function(id) {
                        return {
                            id: id
                        };
                    })
                };

                $.merge(newComment.subscriberDTOs, $scope.atUserSubscribers);

                commentService.createComment(newComment, $scope.projectId, $scope.companyId).then(function(data) {
                    $scope.updateCommentTab();
                    $scope.cancelCommentCreate($event);
                    $($event.target).attr('disabled', false);
                    //上传成功后，初始化upload页面
                    uploadsService.InitUploadsList(url.projectId(), url.companyId());

                    $('.fake-input-content').each(function(idx, element) {
                        $(element).code('<p><br></p>');
                    });
                    $scope.atUserSubscribers.splice(0);

                }, function(data, status, headers, config) {
                    $scope.stat = 'error';
                    $scope.msg = '文档更新失败';
                    $($event.target).attr('disabled', false);
                });

            };

            $scope.initUploadAttachments = function(form, attachmentList) {
                var form = $(form);
                $scope.uploadedFiles = [];
                if(attachmentList) $scope.uploadedFiles = attachmentList;

                $scope.onFileSelect = function($files) {
                    var tempfile;
                    for(var i = 0; i < $files.length; i++) {
                        tempfile = $files[i];

                        if(tempfile.size >= 32000000) {
                            $scope.stat = 'error';
                            $scope.msg = '上传失败：上传文件过大！';
                            $scope.splice($scope.uploadedFiles.indexOf(tempfile), 1);
                            break;
                        }
                        $scope.upload = $upload.upload({
                            url : url.projectApiUrl() + '/attachments/stage',
                            file: tempfile
                        }).progress(function(e) {
                            form.find(".attach-Progress").show();
                            $scope.dynamic = parseInt(100.0 * e.loaded / e.total);
                        }).success(function(data, status, headers, config) {
                            $scope.uploadedFiles.push(data);
                            $scope.stat = 'success';
                            $scope.msg = '上传成功！';
                            //hide loadprogressbar
                            form.find(".attach-Progress").hide();
                        }).error(function(data, status, headers, config) {
                            $scope.stat = 'error';
                            $scope.msg = '上传失败：' + data;
                        })
                    }
                };
                $scope.remove = function(file) {
                    $http.delete(url.projectApiUrl() + "/attachments/" + file.id);
                    var idx = $scope.uploadedFiles.indexOf(file);
                    if(idx > -1) {
                        $scope.uploadedFiles.splice(idx, 1);
                    }
                };
            };

            $scope.checkCommentAccess = function(comment) {
                return (comment.creatorId === $scope.currentUser.id);
            };

            $scope.editComment = function(comment, $event) {
                $scope.resetError();

                var curCommentDiv = $($event.target).parentsUntil('.comment').parent().eq(0),
                    editCommentDiv = $('.edit-comment'),
                    editor = $(editCommentDiv).find('.fake-input-content');

                $scope.commentToBeEdit = comment;
                $scope.uploadedImageInTextEditor.splice(0);
                richtexteditor.initEditAreaSimpleTools(editor, {}, $scope.uploadedImageInTextEditor);
                $(editor).code($scope.commentToBeEdit.content);
                $scope.initUploadAttachments(editCommentDiv, $scope.commentToBeEdit.attachmentDTOs);

                $scope.initAtFunctionality();

                $(editCommentDiv).show().insertAfter($(curCommentDiv).hide());
            };

            $scope.saveCommentEdit = function($event, commentToBeEdit) {

                $($event.target).attr('disabled', true);

                var editDiv = $($event.target).parentsUntil('.edit-comment').parent().eq(0),
                    content = editDiv.find('.fake-input-content').code();
                if(content == '<p><br></p>') {
                    $scope.stat = 'error';
                    $scope.msg = '您输入的评论为空！';
                    $($event.target).attr('disabled', false);
                    return;
                }

                $.merge($scope.uploadedFiles, $scope.uploadedImageInTextEditor);

                commentToBeEdit.content = content;
                commentToBeEdit.attachmentDTOs = $scope.uploadedFiles;
                commentToBeEdit.attachments = $scope.uploadedFiles;

                if($scope.atUserSubscribers.length){
                    if(!commentToBeEdit.subscriberDTOs){
                        commentToBeEdit.subscriberDTOs = [];
                    }
                    $.merge(commentToBeEdit.subscriberDTOs, $scope.atUserSubscribers);
                }

                commentService.updateComment(commentToBeEdit, commentToBeEdit.projectId, commentToBeEdit.companyId).then(function(data) {
                    $scope.updateCommentTab();
                    $scope.cancelCommentEdit($event);
                    $($event.target).attr('disabled', false);
                    $scope.atUserSubscribers.splice(0);

                    //上传成功后，刷新upload页面
                    uploadsService.InitUploadsList(url.projectId(), url.companyId());

                }, function(data, status, headers, config) {
                    $scope.stat = 'error';
                    $scope.msg = '文档更新失败';
                    $($event.target).attr('disabled', false);
                });

            };

            $scope.cancelCommentEdit = function($event) {
                var editDiv = $($event.target).parentsUntil('.edit-comment').parent().eq(0);
                editDiv.find('.fake-input-content').destroy();
                editDiv.prev().show();
                editDiv.hide().insertAfter($('.new-comment'));
                $scope.resetError();
            };

            $scope.deleteComment = function(comment, $event) {
                if(confirm("确实要删除评论吗？")) {
                    commentService.deleteComment(comment.id, $scope.projectId, $scope.companyId).then(function(data) {
                        $($event.target).parentsUntil('.media').parent().hide(500).remove();

                    }, function(data) {
                        $scope.stat = 'error';
                        $scope.msg = '删除评论失败！'
                    });
                }
            };
            $scope.toggleCommentSubscribers = function($event) {
                var form = $($event.target).parentsUntil('form').parent();

                form.find('#commentSubscribers').toggleClass('hide');
            };

            $scope.initCommentSubscribers = function(form) {
                $scope.projectUsers = [];
                $scope.selectedUsers = [];

                $scope.memberselectorOptions = {
                    type  : 'text',
                    column: 4
                };

                user.getProjectUsers().then(function(data) {
                    $.merge($scope.projectUsers, data.map(function(user) {
                        return {
                            img     : user.avatarUrl,
                            tag     : user.tag,
                            title   : user.name,
                            selected: false,
                            id      : user.id
                        }
                    }));

                    _.each($scope.subscribers, function(subscriber) {
                        var subscribedUser = _.find($scope.projectUsers, function(user) {
                            return user.id === subscriber.id;
                        });
                        subscribedUser.selected = true;
                    });

                    $scope.$broadcast('updateMemberSelectorInputUserList', $scope.projectUsers);
                });
            };

            $scope.displayCommentImage = function(attachment, $event) {

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

            // init the tooltip
            $('.comment-tab').tooltip();

        }
        /* end of controller function */
    ])
;
/* end of controller  */