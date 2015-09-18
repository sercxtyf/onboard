/**
 * Created by harttle on 12/11/14.
 */

    // 项目编辑与新建。项目编辑在project面板中应提供入口，而项目新建则属于团队模块。

angular.module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            .state('company.projectEdit', {
                url        : '/projects/{projectId:[0-9]+}/edit',
                templateUrl: 'project-edit.html',
                resolve    : {
                    projectStatus: function() {
                        return 'edit';
                    }
                },
                controller : 'projectEditCtrl'
            })
            .state('company.projectNew', {
                url        : '/projects/new',
                templateUrl: 'project-edit.html',
                resolve    : {
                    projectStatus: function() {
                        return 'new';
                    }
                },
                controller : 'projectEditCtrl'
            })
    }])

    .controller('projectEditCtrl', ['$scope', '$http', '$location', 'projectStatus', 'user', 'project', '$state', 'url',
        'theme', '$document', '$timeout', '$q',
        function($scope, $http, $location, projectStatus, user, project, $state, url, theme, $document, $timeout, $q) {
            // init data
            $scope.isNew = (projectStatus === 'new');
            $scope.themes = theme.themes;
            $scope.emailAddrs = [];

            // digest theme bindings first
            $timeout(function() {

                $document.find('.colorselector').colorselector({
                    callback: function(value, color, title) {
                        $scope.project.colorId = parseInt(value);
                    }
                });
                $document.find('.dropdown-colorselector .dropdown-toggle')
                    .removeAttr('href')
                    .addClass('pointer');

                // declare dependencies
                var dependencies = [user.getCurrentUser(), user.getCompanyGroups(false, url.companyId())];
                if(!$scope.isNew)
                    $.merge(dependencies, [
                        project.getProject(false, url.projectId(), url.companyId()),
                        user.getProjectUsers(false, url.projectId(), url.companyId())]);

                $scope.msoption = {
                    type     : 'image',
                    imgHeight: '70px',
                    imgWidth : '70px'
                    //column: 3
                };

                $scope.userSelected = [];

                $q.all(dependencies).then(function(results) {

                    $scope.currentUser = results[0];
                    $scope.groups = results[1];
                    $scope.project = results[2] || { name: '', description: '', id: '', colorId: 1 };
                    $scope.projectUsers = results[3] || [];

                    $(".colorselector").colorselector("setValue", $scope.project.colorId);

                    // set imgSelector data
                    $scope.userList = [];
                    $.merge($scope.userList, $scope.groups.unGrouped.map(function(user) {
                        return {
                            img     : user.avatarUrl,
                            tag     : '未分组成员',
                            title   : user.name,
                            selected: false,
                            id      : user.id
                        }
                    }));

                    $scope.groups.map(function(group) {
                        $.merge($scope.userList, group.users.map(function(user) {
                            return {
                                img  : user.avatarUrl,
                                tag  : group.name,
                                title: user.name,
                                id   : user.id
                            };
                        }));
                    });
                    $scope.userList.map(function(userInList) {
                        var common = $.grep($scope.projectUsers, function(user) {
                            return user.id == userInList.id
                        });
                        if(common.length > 0) userInList.selected = true;

                        if($scope.project.creatorId != undefined && userInList.id == $scope.project.creatorId)
                            userInList.lock = true;

                        if(userInList.id == $scope.currentUser.id) {
                            userInList.lock = true;
                            if($scope.isNew) userInList.selected = true;
                        }
                    });
                });

            });

            // addEmail action
            $scope.addEmail = function() {
                $scope.emailAddrs.push({ email: '' });
            };

            // get list of valid email for post data
            $scope.getValidEmail = function() {
                // 这里为什么不用html表单验证 type="email"
                var patten = new RegExp(/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+(com|cn)$/);
                var ret = [];
                for(var key in $scope.emailAddrs)
                    if(patten.test($scope.emailAddrs[key].email))
                        ret.push($scope.emailAddrs[key].email);
                return ret;
            };

            // create new object
            $scope.updateObject = function() {
                var data = {
                    name       : $scope.project.name,
                    description: $scope.project.description,
                    colorId    : $scope.project.colorId,
                    members    : $scope.userSelected,
                    emails     : $scope.getValidEmail()
                };
                //console.log("url = " + $location.url());
                if(!$scope.isNew) {
                    $http.put(url.projectEditUrl(false), data).success(function() {
                        $('#save').showStatus('保存成功', 'success', 5000);
                        $('#cancel').text('关闭');
                    }).error(function() {
                        $('#save').showStatus('保存失败', 'error', 5000);
                    });
                } else {
                    $http.post(url.projectEditUrl(true), data).success(function(data) {
                        $state.go('company.project.todolists', {
                            projectId: data.id
                        });
                    }).error(function() {
                        alert('创建项目失败，请重试');
                    });
                }
            };

            // button
            $.fn.showStatus = function(info, status, time) {
                var original = $(this).html();
                if(status == 'success') {
                    $(this).attr('disabled', true)
                        .html(info)
                        .removeClass('btn-primary')
                        .addClass('btn-success');
                } else if(status == 'error') {
                    $(this).attr('disabled', true)
                        .html(info)
                        .removeClass('btn-primary')
                        .addClass('btn-error');
                } else {
                    console.log('status unknown or undefined');
                }
                setTimeout(function() {
                    this.attr('disabled', false)
                        .html(original)
                        .removeClass('btn-success')
                        .addClass('btn-primary');
                }.bind(this), time);
            };

            $scope.delete = function(){
                if(confirm("确认删除该项目？")){
                    project.deleteProject($scope.project).then(function(){
                        $state.go('company.projects');
                    });
                }
            }

        }]);
