/**
 * Created by Dongdong Du on 12/23/2014.
 */

angular.module('data')
    .service('bugService', ['url', '$http', '$q', 'dataProxy', 'bugDataService', function(url, $http, $q, dataProxy, bugDataService) {

        var _bugDetails = {};

        var _bugOpenedList = {
            bugs   : [],
            hasNext: true,
            page   : 0
        };
        var _bugFinishedList = {
            bugs   : [],
            hasNext: true,
            page   : 0
        };

        this.priorities = [
            { desc: '非常紧急', value: 1 },
            { desc: '紧急', value: 2 },
            { desc: '重要', value: 3 },
            { desc: '普通', value: 4 },
            { desc: '可忽略', value: 5 }
        ];

        var status = [
            { desc: '未开始', value: 1, str: 'todo' },
            { desc: '正在做', value: 2, str: 'inprogress' },
            { desc: '已提交', value: 3, str: 'fixed' },
            { desc: '同意完成', value: 4, str: 'approved' },
            { desc: '复审通过', value: 5, str: 'reviewed' },
            { desc: '测试通过', value: 6, str: 'verified' },
            { desc: '已完成', value: 0, str: 'closed' }
        ];

        this.getBugStatus = function(projectId, companyId) {
            var _url = url.projectApiUrl(projectId, companyId) + "/todostatus/";
            return $http.get(_url).then(function(response) {
                var data = response.data;
                var statues = angular.copy(status);
                var retStatus=[];
                for (var i = 0; i < statues.length; i++) {
                    for (var j = 0; j < data.length; j++) {
                        if (statues[i].str == data[j]) {
                            retStatus.push(statues[i]);
                            break;
                        }
                    }
                }
                return retStatus;
            }, function(error) {
                console.log("Error when fetching project todo status!");
                return status;
            });
        };

        var findIdx = function(buglist, bugDTO) {
            for(var i = 0; i < buglist.length; i++) {
                if(bugDTO.id === buglist[i].id) {
                    return i;
                }
            }
            return -1;
        };

        this.webSocketCreateBug = function(bugDTO) {
            this.getBugById(bugDTO.id, bugDTO.projectId, bugDTO.companyId, true).then(function(data) {
                $.extend(_bugDetails[bugDTO.id], data);
                _bugOpenedList.bugs.splice(0, 0, data);
            });

        };

        var updateBugByBugDTO = function(list, bugData) {
            var idx = findIdx(list, bugData);
            if(idx > -1) {
                list.splice(idx, 1, bugData);
            } else {
                list.splice(0, 0, bugData);
            }
        };
        var removeBugFromList = function(list, bugDTO) {
            var idx = findIdx(list, bugDTO);
            if(idx > -1) {
                list.splice(idx, 1);
            }

        };
        this.webSocketUpdateBug = function(bugDTO) {
            this.getBugById(bugDTO.id, bugDTO.projectId, bugDTO.companyId, true).then(function(data) {
                $.extend(_bugDetails[bugDTO.id], data);

                if(data.status) {
                    // bug is in progress
                    updateBugByBugDTO(_bugOpenedList.bugs, data);
                    removeBugFromList(_bugFinishedList.bugs, data);
                } else {
                    // bug is finished
                    updateBugByBugDTO(_bugFinishedList.bugs, data);
                    removeBugFromList(_bugOpenedList.bugs, data);
                }
            });
        };

        this.webSocketDeleteBug = function(bugDTO) {
            delete _bugDetails[bugDTO.id];
            removeBugFromList(_bugOpenedList.bugs, bugDTO);
            removeBugFromList(_bugFinishedList.bugs, bugDTO);
        };

        this.getAllBugList = dataProxy(function(projectId, companyId) {
            var get_buglist_url = url.projectApiUrl(projectId, companyId) + "/bugs/all-bugs";
            return $http.get(get_buglist_url).then(function(response) {
                return response.data;
            });
        });

        this.getOpenBugList = dataProxy(function(projectId, companyId) {
            var get_buglist_url = url.projectApiUrl(projectId, companyId) + "/bugs/open-bugs";
            return $http.get(get_buglist_url).then(function(response) {
                return response.data.bugs;
            });
        });

        this.getClosedBugList = dataProxy(function(projectId, companyId) {
            var get_buglist_url = url.projectApiUrl(projectId, companyId) + "/bugs/closed-bugs";
            return $http.get(get_buglist_url).then(function(response) {
                return response.data;
            });
        });

        this.getBugOpenedListByPage = function(projectId, companyId, page) {
            var get_buglist_url = url.projectApiUrl(projectId, companyId) + "/bugs";
            var params = { page: page };
            return $http.get(get_buglist_url, { params: params }).then(function(response) {
                if(page) {
                    $.merge(_bugOpenedList.bugs, response.data.bugs);
                } else {
                    _bugOpenedList.bugs = response.data.bugs;
                }
                _bugOpenedList.hasNext = response.data.hasNext;
                _bugOpenedList.page = response.data.nextPage;
                return _bugOpenedList;
            });
        };

        this.getBugFinishedListByPage = function(projectId, companyId, page) {
            var get_buglist_url = url.projectApiUrl(projectId, companyId) + "/bugs/finished";
            var params = { page: page };
            return $http.get(get_buglist_url, { params: params }).then(function(response) {
                if(page) {
                    $.merge(_bugFinishedList.bugs, response.data.bugs);
                } else {
                    _bugFinishedList.bugs = response.data.bugs;
                }
                _bugFinishedList.hasNext = response.data.hasNext;
                _bugFinishedList.page = response.data.nextPage;
                return _bugFinishedList;
            });
        };

        this.createBug = function(bug, projectId, companyId) {
            var create_url = url.projectApiUrl(projectId, companyId) + "/bugs";
            return $http.post(create_url, bug).then(function(resposne) {
                return resposne.data;
            });

        };

        this.getBugById = function(bugId, projectId, companyId, update) {
            var deferred = $q.defer();

            if(update || (!_bugDetails[bugId])) {
                var get_bug_url = [url.projectApiUrl(projectId, companyId), 'bugs', bugId].join("/");
                return $http.get(get_bug_url).then(function(response) {
                    var data = bugDataService.register([response.data])[0];
                    data.bugAssigneeDTO.avatar = url.avatarUrl(data.bugAssigneeDTO.avatar);
                    if(!_bugDetails[bugId]) {
                        _bugDetails[bugId] = data;
                    }
                    return data;
                });
            } else {
                deferred.resolve(_bugDetails[bugId]);
                return deferred.promise;
            }

        };

        this.deleteBug = function(bugId, projectId, companyId) {
            var delete_url = [url.projectApiUrl(projectId, companyId), 'bugs', bugId].join("/");
            return $http.delete(delete_url, {
                data   : { id: bugId },
                headers: { 'Content-Type': 'application/json' }
            }).then(function(response) {
                return response.data;
            });
        };

        this.updateBug = function(updatedBug) {
            return updatedBug.update();
        };

    }]);
