// 用户设置视图：基本资料、密码设置、SSH Key管理

angular.module('onboard')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            .state('account', {
                url  : '/account',
                views: {
                    'nav'    : {
                        templateUrl: 'onboard-header.html'
                    },
                    'content': {
                        templateUrl: '/account',
                        controller : 'accountCtrl'
                    }
                }
            })
            .state('account.profile', {
                url        : '/profile',
                templateUrl: 'profile.html',
                controller : 'profileCtrl'
            })
            .state('account.password', {
                url        : '/password',
                templateUrl: 'password.html',
                controller : 'passwdCtrl'
            });
    }])
    .controller('accountCtrl', ['$scope', '$http', '$location', 'user', 'pluginService', function($scope, $http, $location, user, pluginService) {
        //$http.get("/account?format=json").success(function(data) {
        //    $scope.user = data.updateCommand;
        //    $scope.newUsername = $scope.user.username; // 用户名只能被修改1次
        //});
        //$scope.accountPlugins = [];
        $scope.accountPlugins = pluginService.getAccountPlugins();
        console.log($scope.accountPlugins);
        user.getCurrentUser(false).then(function(user){
            $scope.user = user;
            $scope.newUsername = user.username;
        });

        $scope.isActive = function(viewLocation) {
            return viewLocation === $location.path();
        };
    }])
    .controller('profileCtrl', ['$scope', '$http', '$upload', 'url', function($scope, $http, $upload, url) {
        $scope.$parent.subpageTitle = '用户资料';

        // 更改用户信息
        $scope.save = function() {
            $http.post('/api/account', {
                username   : $scope.newUsername, // 用户名只能被修改1次
                name       : $scope.user.name,
                description: $scope.user.description
            }).success(function(data, status, headers, config) {
                $scope.user.username = $scope.newUsername; // 用户名只能被修改1次
                $scope.stat = 'success';
                $scope.msg = '保存成功！';
            }).error(function(data, status, headers, config) {
                $scope.stat = 'error';
                $scope.msg = '保存失败：' + data;
            });
        };
        // input:file Bootstrap化显示
        $("input[name='avatar']").filestyle({
            input     : false,
            buttonText: "上传新头像",
            icon      : false,
            badge     : false
        });

        // 更改头像
        $scope.chavatar = function($files) {
            var file = $files[0];
            $scope.upload = $upload.upload({
                url : '/api/account-avatar',
                data: {
                    avatar: file
                }
            }).progress(function(evt) {
                //console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
            }).success(function(data, status, headers, config) {

                // TODO: POST 成功后更新 user model
                //$scope.user.avatar = data.url;
                $scope.user.avatarUrl = data.url;

                $scope.stat = 'success';
                $scope.msg = '保存头像成功！';
            }).error(function(data, status, headers, config) {
                $scope.stat = 'error';
                $scope.msg = '保存头像失败：' + data;
            })
        }
    }])
    .controller('passwdCtrl',
    ['$scope', '$http', function($scope, $http) {
        $scope.$parent.subpageTitle = '修改密码';
        $scope.save = function() {
            $http.post('/api/account-password', {
                password   : $scope.user.password,
                newPassword: $scope.user.newPassword
            }).success(function(data, status, headers, config) {
                $scope.stat = 'success'
            }).error(function(data, status, headers, config) {
                $scope.stat = 'error'
            });
        }
    }]);
