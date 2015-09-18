/**
 * Created by harttle on 11/29/14.
 */

angular.module('data')
    .service('user', ['url', '$http', '$q', 'dataProxy', function(url, $http, $q, dataProxy) {

        var self = this;

        // get company user by id
        this.getUserById = function(update, projectId, companyId, userId) {
            return self.getCompanyUsers(update, companyId).then(function(users) {

                var matched = users.filter(function(u) {
                    return u.id == userId
                });
                return matched.length > 0 ? matched[0] : null;
            })
        };

        // 项目成员数组。保证与团队成员是一个对象。
        this.getProjectUsers = dataProxy(function(update, projectId, companyId) {

            return $q.all([
                $http.get(url.projectUserUrl(projectId)),
                self.getCompanyGroups(update, companyId),
                self.getCompanyUsers(update, companyId)
            ]).then(function(result) {

                var response = result[0], groups = result[1], companyUsers = result[2];

                var users = response.data.map(function(user) {

                    var companyUser = companyUsers.filter(function(u) {
                        return u.id == user.id
                    })[0];

                    return $.extend(companyUser, user); // in case there are more info
                });

                addGroupTag(users, groups); // 为了使用memberSelector而添加，感觉不好。 userMenu也要用的！

                return users;
            });

        }.bind(this));

        // 得到项目成员，除了当前用户
        this.getProjectUsersExceptSelf = dataProxy(function(update, projectId, companyId) {

            return $q.all([
                self.getProjectUsers(update, projectId, companyId),
                self.getCurrentUser(update)
            ]).then(function(results) {

                var users = results[0];
                var currentu = results[1];

                return users.filter(function(user) {
                    return user.id != currentu.id;
                });
            });
        }.bind(this));

        // 得到团队分组，返回分组数组。同时推导添加用户头像url
        this.getCompanyGroups = dataProxy(function(update, companyId) {
            return $http.get(url.companyUserUrl(companyId)).then(function(response) {
                var data = response.data;
                data.groups.map(function(g) {
                    g.users.map(function(u) {
                        u.avatarUrl = url.avatarUrl(u.avatar);
                        return u;
                    });
                });

                data.groups.unGrouped = data.unGroupUsers.map(function(u) {
                    u.avatarUrl = url.avatarUrl(u.avatar);
                    return u;
                });

                data.groups.invitations = data.invitations.map(function(u) {
                    u.avatarUrl = url.avatarUrl(u.avatar);
                    return u;
                });

                delete data.unGroupUsers;
                delete data.invitations;

                return data.groups;
            });
        });

        // 得到团队用户，返回用户数组。同时添加用户groupId，groupName
        this.getCompanyUsers = dataProxy(function(update, companyId) {
            return self.getCompanyGroups(update, companyId).then(function(groups) {
                var users = [];
                groups.map(function(group) {
                    return $.merge(users, group.users.map(function(user) {
                        user.groupId = group.id;
                        user.groupName = group.name;
                        return user;
                    }));
                });
                $.merge(users, groups.unGrouped);
                return users;
            });
        });

        // 得到当前用户，永远是唯一的，不需要缓存失效判断
        this.getCurrentUser = dataProxy(function(update) {
            return $http.get(url.currentUserUrl()).then(function(response) {
                return currentUser = $.extend(response.data, {
                    avatarUrl: url.avatarUrl(response.data.avatar)
                });
            });
        });

        // 获取按周活动数排名的用户列表
        this.getCompanyUsersOrderByLastWeekActivities = dataProxy(function(update, companyId) {
            return $http.get(url.companyUserUrl(companyId) + '/orderedByActivitiesCountInLastWeek')
            .then(function(response) {
                return response.data.map(function(user){
                    user.avatarUrl = url.avatarUrl(user.avatar);
                    return user;
                });
            });
        });

        // 给每个用户添加一个所属group的字段
        var addGroupTag = function(users, groups) {
            var flag;
            var user;
            var i, j, k;
            for(i = 0; i < users.length; i++) {
                flag = false;
                user = users[i];
                for(j = 0; j < groups.length; j++) {
                    for(k = 0; k < groups[j].users.length; k++) {
                        if(groups[j].users[k].id === user.id) {
                            flag = true;
                            user.tag = groups[j].name;
                            break;
                        }
                    }
                    if(flag) {
                        break;
                    }
                }
                if(!flag) {
                    for(k = 0; k < groups.unGrouped.length; k++) {
                        if(groups.unGrouped[k].id === user.id) {
                            flag = true;
                            user.tag = '未分组成员';
                            break;
                        }
                    }
                }
                if(!flag) {
                    user.tag = '接受邀请中';
                }
            }
        };

        // 获得订阅者
        this.getSubscriberList = function(subscribers, projectId) {
            return $q.all([this.getProjectUsersExceptSelf(projectId), this.getCurrentUser()]).then(function(result) {
                var users = result[0];
                var currentUser = result[1];
                var ret = [];
                var user;
                var flag;
                for(var i = 0; i < users.length; i++) {
                    user = users[i];
                    flag = false;
                    if(subscribers !== undefined) {
                        for(var j = 0; j < subscribers.length; j++) {
                            if(user.id === subscribers[j].id) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    ret.push({
                        img     : user.avatarUrl,
                        tag     : user.tag,
                        title   : user.name,
                        selected: flag,
                        data    : {
                            id: user.id
                        }
                    });
                }

                return ret;
            });
        };

    }]);
