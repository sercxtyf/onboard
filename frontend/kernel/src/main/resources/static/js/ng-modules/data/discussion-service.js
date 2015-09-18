/**
 * Created by ChenLong on 12/17/14.
 */

angular.module('data')
    .service('discussionService', ['user', 'url', '$http', '$q', 'discussionDataService', 'topicDataService',
        function(user, url, $http, $q, discussionDataService, topicDataService) {

        var _topics = [];

        var addAvartarUrl = function(topic) {
            topic.updator.avatarUrl = url.avatarUrl(topic.updator.avatar);
            if (topic.creator) {
                topic.creator.avatarUrl = url.avatarUrl(topic.creator.avatar);
            }
            return topic;
        };

        var findTopicIndex = function(discussionId) {
            for (var key in _topics) {
                if (_topics[key].refType === 'discussion' && _topics[key].refId === discussionId) {
                    return key;
                }
            }
            return -1;
        };

        var removeTopic = function(discussionId) {
            var idx = findTopicIndex(discussionId);
            if (idx > -1) {
                _topics.splice(idx, 1);
            }
        };

        this.getTopics = function(pageNum) {
            var _api = url.projectApiUrl() + '/discussions';
            if (pageNum === 1) {
                _topics = [];
            }
            return $http.get(_api, {
                params: {
                    page: pageNum
                }
            }).then(function(response) {
                var data = response.data.topics.map(addAvartarUrl).map(function(topic) {
                    topic.type = 'topic';
                    return topic;
                });
                if (pageNum === 1) {
                    _topics.splice(0);
                }
                $.merge(_topics, topicDataService.registerTopics(data));
                return {
                    topics: _topics,
                    totalPage: response.data.totalPage
                };
            });
        };

        this.createDiscussion = function(newDiscussion) {
            var _api = url.projectApiUrl() + '/discussions';
            return $http.post(_api, newDiscussion).then(function(response) {
                console.log(response);
                var topics = [response.data];
                return topicDataService.registerTopics(topics)[0].refId;
            });
        };

        this.updateDiscussion = function(id, discussion) {
            var _api = [url.projectApiUrl(), '/discussions/', id].join("");
            return $http.put(_api, discussion).then(function(response) {
                console.log(response);
                var topics = [response.data];
                return topicDataService.registerTopics(topics)[0].refId;
            });
        };

        this.deleteDiscussion = function(id) {
            var _api = [url.projectApiUrl(), '/discussions/', id].join("");
            return $http.delete(_api).then(function(response) {});
        };

        this.recoverDiscussion = function(id) {
            var _api = [url.projectApiUrl(), '/discussions/', id, '/recover'].join("");
            return $q.all([$http.put(_api, {
                params: {
                	recover: true
                }
            }), user.getProjectUsers()]).then(function(result) {
                var response = result[0];
                //topic
                var topic = response.data.topic;                
                removeTopic(id);
                _topics.splice(0, 0, addAvartarUrl(topic));
                // discussion                
                var discussion = response.data.discussion;
                var users = result[1];
                for (var key in users) {
                    if (users[key].id === discussion.creatorId) {
                        discussion.creatorAvatarUrl = users[key].avatarUrl;
                        break;
                    }
                }
                return discussion;
            });
        };

        this.getDiscussion = function(id, projectId) {
            var _api = [url.projectApiUrl(projectId), '/discussions/', id].join("");
            return $q.all([$http.get(_api), user.getProjectUsers(false, projectId)]).then(function(result) {
                var users = result[1];
                var discussion = result[0].data;
                for (var key in users) {
                    if (users[key].id === discussion.creatorId) {
                        discussion.creatorAvatarUrl = users[key].avatarUrl;
                        break;
                    }
                }
                if (discussion.creatorAvatarUrl === undefined) {
                    discussion.creatorAvatarUrl = url.defaultAvatarUrl;
                }
                return discussion;
            });
        };

        var getTopicByDisscussionId = function(discussionId) {
            var _url = [url.projectApiUrl(), 'discussions', discussionId, 'topic'].join("/");
            return $http.get(_url).then(function(response) {
                return response.data;
            });
        };
        this.webSocketCreateDiscussion = function(discussionDTO) {
            getTopicByDisscussionId(discussionDTO.id).then(function(topic) {
                _topics.splice(0, 0, addAvartarUrl(topic));
            });
        };
        this.webSocketUpdateDiscussion = function(discussionDTO) {
            getTopicByDisscussionId(discussionDTO.id).then(function(topic) {
                removeTopic(discussionDTO.id);
                _topics.splice(0, 0, addAvartarUrl(topic));
            });
        };
        this.webSocketDeleteDiscussion = function(discussionDTO) {
            removeTopic(discussionDTO.id);
        };

        var _start = 0,
            _end = 0;
        var _discussions = [];
        var _url = function(){
            return 'api/' + url.companyId() + '/stats/discussions';
        }

        this.getDiscussionStats = function(start, end) {
            var since = d3.time.day.floor(new Date(start)) - 0;
            var until = d3.time.day.ceil(new Date(end)) - 0;
            if (_start === 0 || _end === 0 || since !== _start || until !== _end) {
                return $http.get(_url(), {
                    params: {
                        start: start,
                        end: end
                    }
                }).then(function(response) {
                    _start = since;
                    _end = until;
                    _discussions = response.data;
                    return _discussions;
                });
            } else {
                var deferred = $q.defer();
                deferred.resolve(_discussions);
                return deferred.promise;
            }
        };

    }]);
