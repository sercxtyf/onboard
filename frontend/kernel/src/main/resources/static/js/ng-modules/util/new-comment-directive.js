/**
 * Created by Dongdong Du on 12/18/2014.
 *
 * Usage: <new-comment attach-type="expression" attach-id="expression" project-id="expression" company-id="expression" subscribers=""></new-comment>
 * Example: <new-comment attach-type="'document''" attach-id="doc.Id" project-id="doc.projectId" company-id="doc.companyId"></new-comment>
 *
 *** attach-type and attach-id are both required for using this directive ***
 *
 * note: all attribute values are expressions, if you want to send a string for attachType,
 *  use attach-type="'document'" instead of attach-type="document".
 *
 */

angular.module('util')
    .directive('newComment', ['$http', 'url', 'richtexteditor', 'user', '$upload', 'commentService', 'uploadsService', '$rootScope',
        function($http, url, richtexteditor, user, $upload, commentService, uploadsService, $rootScope) {

            return {
                require    : '^onboardComment',
                restrict   : 'E',
                replace    : true,
                link       : link,
                scope      : {
                    attachType : '=',
                    attachId   : '=',
                    projectId  : '=',
                    companyId  : '=',
                    subscribers: '='
                },
                templateUrl: 'newCommentDirective.html'
            };

            function link(scope, ele, attrs, onboardCommentCtrl) {
                scope.avatarUrlInComment = url.avatarUrl;
                user.getCurrentUser().then(function(user) {
                    scope.currentUser = user;
                }, function(data) {
                    console.log("Error when getting current user data in onboardCommentDirective! ")
                });

                scope.resetError = function() {
                    scope.stat = "";
                    scope.msg = "";
                };
                scope.uploadedImageInTextEditor = [];
                $('.fake-input-content').each(function(idx, element) {
                    richtexteditor.initEditAreaSimpleTools($(element), {}, scope.uploadedImageInTextEditor);
                    $('.note-editor').hide();
                    $(element).show();
                });

                scope.showCommentEditor = function(event) {
                    scope.resetError();
                    var form = $(event.target).parentsUntil('form').parent();
                    scope.uploadedImageInTextEditor.splice(0);
                    form.find('.note-editor').show();
                    form.find('.fake-input-content').hide();
                    scope.initUploadAttachments(form);
                    scope.initCommentSubscribers(form);
                    onboardCommentCtrl.initAtFunc();
                    form.find('.attachments-buttons').show();
                };
                // 编辑器应直接显示出来，此处的逻辑需要重新整理。

                scope.cancelCommentEdit = function(event) {
                    var form = $(event.target).parentsUntil('.new-comment').parent();
                    //form.find('.fake-input-content').destroy();
                    form.find('.note-editor').hide();
                    form.find('.fake-input-content').show();
                    form.find('.attachments-buttons').hide();
                    scope.resetError();
                };

                scope.initUploadAttachments = function(form) {
                    var form = $(form);
                    scope.uploadedFiles = [];
                    scope.onFileSelect = function(files) {
                        var tempfile;
                        for(var i = 0; i < files.length; i++) {
                            tempfile = files[i];

                            if(tempfile.size >= 33554432) {
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

                scope.toggleCommentSubscribers = function($event) {
                    var form = $($event.target).parentsUntil('.new-comment').parent();
                    form.find('#commentSubscribers').toggleClass('hide');
                };

                scope.initCommentSubscribers = function(form) {
                    scope.projectUsers = [];
                    scope.selectedUsers = [];
                    scope.memberselectorOptions = {
                        type  : 'text',
                        column: 4
                    };
                    user.getProjectUsers().then(function(data) {
                        $.merge(scope.projectUsers, data.map(function(user) {
                            return {
                                img     : user.avatarUrl,
                                tag     : user.tag,
                                title   : user.name,
                                selected: false,
                                id      : user.id
                            }
                        }));

                        _.each(scope.subscribers, function(subscriber) {
                            var subscribedUser = _.find(scope.projectUsers, function(user) {
                                return user.id === subscriber.id;
                            });
                            subscribedUser.selected = true;
                        });

                    });

                };

                scope.saveComment = function(event) {
                    var form = $(event.target).parentsUntil('form').parent();
                    var content = form.find('.fake-input-content').code();
                    if(content == '<p><br></p>') {
                        scope.stat = 'error';
                        scope.msg = '您输入的评论为空！';
                        $(event.target).attr('disabled', false);
                        return;
                    }
                    $.merge(scope.uploadedFiles, scope.uploadedImageInTextEditor);

                    var newComment = {
                        content       : content,
                        attachType    : scope.attachType,
                        attachId      : scope.attachId,
                        attachmentDTOs: scope.uploadedFiles,
                        subscriberDTOs: scope.selectedUsers.map(function(id) {
                            return {
                                id: id
                            };
                        })
                    };

                    $.merge(newComment.subscriberDTOs, onboardCommentCtrl.atUserSubscribers);

                    commentService.createComment(newComment, scope.projectId, scope.companyId).then(function(data) {
                        scope.cancelCommentEdit(event);
                        $(event.target).attr('disabled', false);
                        //上传成功后，初始化upload页面
                        uploadsService.InitUploadsList(url.projectId(), url.companyId());

                        $('.fake-input-content').each(function(idx, element) {
                            $(element).code('<p><br></p>');
                        });
                        onboardCommentCtrl.atUserSubscribers.splice(0);

                    }, function(data, status, headers, config) {
                        scope.stat = 'error';
                        scope.msg = '新建评论失败';
                    });

                };

            }

        }]);