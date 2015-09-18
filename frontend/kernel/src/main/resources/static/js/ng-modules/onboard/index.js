/**
 * Created by harttle on 12/9/14.
 */

    // onboard 整体作为一个App。特性模块（包括项目清单、讨论、文件、文档、代码等）以及需要统一配置的第三方模块从这里引入。
    // 该文件包括：全局的service配置、全局常量service。

angular.module('onboard', ['ui.router', 'angularMoment', 'company', 'ng.confirmField',
    'angularFileUpload', "util", 'data', 'websocket', 'plugin'])   //, 'mgcrea.ngStrap'

    .service('onboardHttpInterceptor', ['$q', '$location', '$timeout', '$rootScope', function($q, $location, $timeout, $rootScope) {
        // optional method
        var loadingTimes = 0,
            timeoutSecond = 2000,
            loader_show = "loader_show",
            loader_hide = "loader_hide";

        this.request = function(config) {
            // do something on success
            loadingTimes++;

            $timeout(function() {
                if(loadingTimes > 0) {
                    $rootScope.$broadcast(loader_show);
                }
            }, timeoutSecond);
            return config;
        };

        // optional method
        this.requestError = function(rejection) {
            // do something on error
            loadingTimes++;
            $timeout(function() {
                if(loadingTimes > 0) {
                    $rootScope.$broadcast(loader_show);
                }
            }, timeoutSecond);
            return $q.reject(rejection);
        };

        // optional method
        this.response = function(response) {
            // do something on
            if(--loadingTimes < 1) $rootScope.$broadcast(loader_hide);
            return response;
        };

        // optional method
        this.responseError = function(rejection) {
            // do something on error
            if(--loadingTimes < 1) $rootScope.$broadcast(loader_hide);
            if(rejection.status == 403) {   // did not login
                console.error($location.absUrl() + 'got 403, redirecting...');
                window.location = "/account/signin?next=" + $location.absUrl();
                return;
            }
            return $q.reject(rejection);
        };
    }])

    .config(['$urlRouterProvider', '$locationProvider', '$httpProvider',
        function($urlRouterProvider, $locationProvider, $httpProvider) {
            $urlRouterProvider.otherwise('/teams');
            $locationProvider.html5Mode(true);
            $httpProvider.interceptors.push('onboardHttpInterceptor');
        }])

    .constant('angularMomentConfig', {
        preprocess: 'unix', // optional
        timezone  : 'Asia/Shanghai' // optional
    })

    .run(['amMoment', function(amMoment) {
        amMoment.changeLocale('zh-cn');
    }]);


