// 项目页总控制器

angular.module('project')
    // 逐步干掉这个ctrl
    .controller('projectCtrl', ['$scope', '$rootScope', '$http', '$location', 'drawer', 'url', '$state',
        function ($scope, $rootScope, $http, $location, drawer, url, $state) {

            $scope.$state = $state;

            $scope.toggleSidebar = function () {
                $(".wrap-all").toggleClass("mini-sidebar");
                !function() {
                    if (!$('.wrap-all').hasClass('mini-sidebar')) {
                        // Smoothly show the titles
                        $('#side-menu span').hide();
                        setTimeout(function () {
                            $('#side-menu span').fadeIn(600);
                        }, 1);
                    } else {
                        // Remove all inline styles from jquery fadeIn function to reset menu state
                        $('#side-menu span').removeAttr('style');
                    }
                }();
            };

            $scope.getProjectApiUri = url.projectApiUrl() + "/";
            $http.get($scope.getProjectApiUri).success(function (data) {
                $scope.project = data;
            });

            $scope.init = function (companyId, projectId) {
                $scope.companyId = companyId;
                $scope.projectId = projectId;
                $scope.getProjectUri =  url.projectUrl();
                $scope.getProjectApiUri = url.projectApiUrl();
                $scope.imageStore = "http://teamforge.b0.upaiyun.com";
                //获取项目信息
                $http.get($scope.getProjectApiUri).success(function (data) {
                    $scope.project = data;
                });
            };
        }
    ]);
