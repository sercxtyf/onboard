/**
 * Created by harttle on 12/10/14.
 */

    // 团队成员
angular.module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            // default child state
            .state('company.users', {
                url        : '/users',
                templateUrl: 'users.html',
                controller : 'usersCtrl'
            });
    }])
    .controller('usersCtrl', ['$scope', '$state', '$http', 'url', 'company', 'user', function($scope, $state, $http, url, company, user) {

        $scope.defaultAvatarUrl = url.defaultAvatarUrl;
        $scope.emailAddrs = [];
        $scope.newUserProjectIdList = [];
        $scope.projects = [];
        
        user.getCompanyGroups(false, url.companyId()).then(function(groups) {
            $scope.groups = groups;
            console.log(groups);
        });

        company.getCompanyInfo(false, url.companyId()).then(function(company) {
            $scope.company = company;
           // $scope.isAdmin = ($scope.company.creatorId == $scope.currentUser.id);
        });

        user.getCurrentUser().then( function(u) {
            //console.log(u);
            $http.get('api/'+ url.companyId() + '/users/'+ u.id  +'/privilege').
                success( function(data, status, headers, config) {
                    $scope.isAdmin = data.isUserCompanyAdmin || data.isUserCompanyOwner;
                    if($scope.isAdmin){
                    	// 获取项目列表
                    	$http.get('api/'+ url.companyId() +'/projects').success(function (data) {
                    		 $scope.projects = data;
                    	});
                    }
                });
        });

        user.getCompanyUsers(false, url.companyId()).then( function(data) {
           console.log(data);
        });

        $scope.addEmail = function() {
            $scope.emailAddrs.push({ email: '' });
        };

        $scope.sendInvitation = function() {
        	var newUser = {
        		emailAddresses: $scope.emailAddrs.map(function(item){return item.email}),	
        		projectIdList: $scope.newUserProjectIdList
        	};
            console.log($scope.emailAddrs);
            $http.post('api/' + $scope.company.id + '/invitations',newUser).success(function(data, status, headers, config) {
                $("#invite").modal("hide");
            }).error(function(data, status, headers, config) {
                console.log('修改团队名称失败');
            });
        };

        $scope.resetEditModel = function() {
            $scope.newCompanyName = $scope.company.name;
        };

        $scope.editCompanyName = function() {
            console.log('editCompany');
            $http.put('api/' + $scope.company.id, {
                name: $scope.newCompanyName
            }).success(function(data, status, headers, config) {
            	$("#editCompanyModal").modal("hide");
                $scope.company.name = $scope.newCompanyName;
            }).error(function(data, status, headers, config) {
                console.log('修改团队名称失败');
            });
        };

        $scope.deleteCompany = function() {
            $http.delete('api/' + $scope.company.id)
                .success(function(data, status, headers, config) {
                $state.go('companies');
            }).error(function(data, status, headers, config) {
                console.log('删除团队名称失败');
            });
        };
        $scope.createNewGroup = function() {
            $http.post('api/' + $scope.company.id + '/group', {
                name: $scope.new_group_name
            }).success(function(data, status, headers, config) {
                console.log('创建新分组成功');
                $.extend(data, {
                    users: []
                });
                $scope.groups.push(data);
                $scope.new_group_name = null;
            }).error(function(data, status, headers, config) {
                console.log('创建新分组失败');
            });
        };

        $scope.deleteGroup = function(entry) {
            var idx = $scope.groups.indexOf(entry);
            $http.delete('api/' + $scope.company.id + '/group/' + entry.id).success(function(data, status, headers, config) {
                while($scope.groups[idx].users.length > 0)
                    $scope.unGroupUsers.push($scope.groups[idx].users.shift());
                $scope.groups.splice(idx, 1);
                console.log($scope.unGroupUsers);
            }).error(function(data, status, headers, config) {
                console.log('删除分组名称失败');
            });

        };

        $scope.editGroup = function() {
            $('input[name="group_id"]').val(this.entry.id);
            $('input[name="subgroup_name"]').val(this.entry.name);
        };

        $scope.renameGroup = function() {
            $http.put('api/' + $scope.company.id + '/group/' + $('input[name="group_id"]').val(), {
                name: $scope.group_name
            }).success(function(data, status, headers, config) {
                var tp = config.url.split('/');
                for(var key in $scope.groups)
                    if($scope.groups[key].id == tp[3]) {
                        $scope.groups[key].name = config.data.name;
                    }
                console.log(config);
                $('#editModal').modal('hide');
            }).error(function(data, status, headers, config) {
                console.log('修改分组名称失败');
            });
        };

        $scope.sortableOptions = {
            connectWith: ".dropBody",
            receive    : function(e, ui) {
                var putData = {
                    userId   : ui.item[0].id,
                    companyId: $scope.company.id
                };
                if(e.target.attributes['data-groupid'] != undefined)
                    putData.groupId = e.target.attributes['data-groupid'].value;
                $http.put('api/' + $scope.company.id + '/group/groupuser', putData)
                    .success(function(data, status, headers, config) {
                        //console.log('移动分组成功');
                    })
                    .error(function(data, status, headers, config) {
                        // console.log('移动分组失败');
                    });
            }
        };
    }])
    .directive('singleUser', function() {
        return {
            template: '\
            <div class="thumbnail">\
                <a ui-sref="company.me({userId:user.id})">\
                    <img ng-src="{{ user.avatarUrl }}" alt="avatar" class="img-thumbnail img-circle"/>\
                </a>\
                <div class="caption">\
                    <h4 data-toggle="tooltip" title="{{ user.name }}">{{ user.name }}</h4>\
                    <p>{{ user.email }}</p>\
                <!-- <button th:if="${ canDelete }" class="btn btn-danger btn-sm">删除该成员</button> -->\
                </div>\
            </div>'
        }
    })
    .directive('invitedUser', function() {
        return {
            template: '\
            <div class="thumbnail">\
                <a href="/{{ company.id }}/users/{{invitation.id}}">\
                    <img ng-src="{{ defaultAvatarUrl }}" alt="avatar" class="img-thumbnail img-circle"/>\
                </a>\
                <div class="caption">\
                    <h4 data-toggle="tooltip" title="{{ invitation.email }}">{{ invitation.email }}</h4>\
                    <p>已邀请</p>\
                </div>\
            </div>'
        }
    });
