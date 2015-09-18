/**
 * Created by harttle on 12/10/14.
 */

    // 回顾控制器

angular.module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            .state('company.activities', {
                url        : '/activities',
                templateUrl: 'activities.html',
                controller : 'activitiesCtrl'
            })
    }])
    .controller('activitiesCtrl', ['$scope', '$http', 'url', 'theme', 'user', 'company', '$q',
        function($scope, $http, url, theme, user, company, $q) {
            var activityUrl = url.teamApiUrl() + '/activities',
                companyUsers = null;

            $scope.themes = theme.themes;

            $scope.getUserById = function(id) {
                return companyUsers.filter(function(u) {
                    return u.id == id
                })[0];
            };
            user.getCompanyUsers(false, url.companyId()).then(function(users) {
                companyUsers = users;
            });

            $scope.busy = false;    // 正在载入
            $scope.hasNext = true;     // 没有更多
            $scope.activities = [];
            $scope.selectedUser = 0;
            $scope.selectedProject = 0;

            // 操作对应的图标，如其他地方有需要应放到service中：theme-service？
            $scope.icons = {
                "activate"  : "certificate",
                "approved"  : "thumbs-up",
                "archive"   : "archive",
                "complete"  : "check",
                "create"    : "plus",
                "decline"   : "thumbs-down-o",
                "discard"   : "close",
                "join"      : "user",
                "merged"    : "code-fork",
                "move"      : "arrow-right",
                "open"      : "folder-open",
                "push"      : "arrow-up",
                "recover"   : "undo",
                "remove"    : "trash",
                "reopened"  : "folder-open-o",
                "reply"     : "reply",
                "review"    : "repeat",
                "start"     : "circle",
                "unapproved": "thumbs-down",
                "update"    : "refresh"
            };

            $q.all([
                company.getProjects(false, url.projectId(), url.companyId()),
                user.getCompanyUsers(false, url.companyId())
            ]).then(function(results) {

                $scope.projects = results[0];
                $scope.users = results[1];
            });

            $scope.$watchGroup(['selectedProject', 'selectedUser'], function() {
                $scope.activities = [];
                $scope.until = '';
                $scope.hasNext = true;
                $scope.nextPage();
            });

            $scope.nextPage = function() {
                $scope.busy = true;

                var params = {};
                if($scope.until) params.until = $scope.until;
                if($scope.selectedUser != 0) params.userId = $scope.selectedUser;
                if($scope.selectedProject != 0) params.projectId = $scope.selectedProject;

                $http.get(activityUrl, { params: params })
                    .success(function(data) {
                        $scope.hasNext = data.hasNext;
                        $scope.until = data.nextDay;

                        data.activities.map(function(aInDay) {
                            aInDay.map(function(aInProject) {
                                $.merge($scope.activities, aInProject);
                            });
                        });

                        $scope.busy = false;
                    }).error(function(data) {
                        $scope.stat = 'error';
                        $scope.msg = '载入错误';
                    });
            };

            $scope.getThemeByProjectId = function(id) {
                if(!$scope.projects) return undefined;

                var project = $scope.projects.filter(function(p) {
                    return p.id == id;
                })[0];
                return project ? $scope.themes[project.colorId % 6] : undefined;
            };

            $scope.checkSubjectContainDeletion = function(activitySubject) {
                return activitySubject.indexOf("删除") > -1;
            }

        }]);
