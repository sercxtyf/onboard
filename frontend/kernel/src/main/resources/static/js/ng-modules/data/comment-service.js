/**
 * Created by Dongdong Du on 12/23/2014.
 */

angular.module('data')
    .service('commentService', ['url', '$http', '$q', 'uploadsService', function(url, $http, $q, uploadsService) {

        var _allComments = {};

        /**
         *
         * @param commentDTO
         *
         * This function is used by comment websocket service
         */
        this.updateAllComments = function(commentDTO) {
            var _key = commentDTO.attachType + commentDTO.attachId;
            this.getComments(commentDTO.attachType, commentDTO.attachId, commentDTO.projectId, commentDTO.companyId).then(function(data) {
                return _allComments[_key];
            });
        };

        /**
         *
         * @param attachType:  String
         * @param attachId:  integer
         * @returns Object {comments: comments, subscribers: subscribers }
         */
        this.getComments = function(attachType, attachId, projectId, companyId) {
            var deferred = $q.defer();

            if(attachType && attachId) {
                var _key = attachType + attachId;
                var fetch_comment_url = [url.projectApiUrl(projectId, companyId), "/comments", "?attachType=", attachType, "&attachId=", attachId].join("");
                return $http.get(fetch_comment_url).then(function(response) {
                    var _comments = response.data.comments;
                    for(var i = 0; i < _comments.length; i++) {
                        var c = _comments[i];
                        for(var j = 0; j < c.attachmentDTOs.length; j++) {
                            if(c.attachmentDTOs[j].contentType.match("image")) {
                                c.attachmentDTOs[j].contentType = "image";
                            }
                        }
                    }
                    if(_allComments[_key]) {
                        _allComments[_key]["comments"].splice(0);
                        $.extend(_allComments[_key]["comments"], _comments);
                        _allComments[_key]["subscribers"].splice(0);
                        $.extend(_allComments[_key]["subscribers"], response.data.subscribers);
                    } else {
                        _allComments[_key] = {
                            "comments"   : _comments,
                            "subscribers": response.data.subscribers
                        };
                    }
                    return _allComments[_key];
                });
            }
            else {

                deferred.reject("Illegal input, please check your attachType or attachId");
                return deferred.promise;
            }
        };

        /**
         *
         * @param comment
         *  comment : { content    : content,
                        attachType : attachType,
                        attachId   : attachId,
                        attachments: uploadedFiles,
                        subscribers: subscribers
         *  }
         * @returns  Object {newComment: newCommentDTO, existSubscribers: existSubscriberDTOs}
         */
        this.createComment = function(comment, projectId, companyId) {
            var postUrl = url.projectApiUrl(projectId, companyId) + "/comments";
            return $http.post(postUrl, comment).then(function(response) {
                return response.data;
            });
        };

        /**
         *
         * @param commentId: integer
         * @returns {*}
         */
        this.deleteComment = function(commentId, projectId, companyId) {
            var delete_url = [url.projectApiUrl(projectId, companyId), 'comments', commentId].join("/");
            return $http.delete(delete_url, {
                data   : {id: commentId},
                headers: {'Content-Type': 'application/json'}
            }).then(function(response) {
                return response.data;
            })
        };

        this.updateComment = function(updatedComment, projectId, companyId) {
            var update_url = [url.projectApiUrl(projectId, companyId), 'comments', updatedComment.id].join("/");
            return $http.put(update_url, updatedComment).then(function(response) {
                return response.data;
            });
        };

    }]);
