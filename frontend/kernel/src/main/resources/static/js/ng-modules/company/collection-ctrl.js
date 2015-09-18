/**
 * Created by Nettle on 2015/6/29.
 */

angular.module('company')
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('company.collection', {
                url: '/collection',
                templateUrl: 'collection.html',
                controller: 'collectionCtrl'
            })
    }
    ])
    .controller('collectionCtrl', ['$scope', '$rootScope', '$http', '$state', '$timeout', 'collectionService', 'drawer', '$location',
        function ($scope, $rootScope, $http, $state, $timeout, collectionService, drawer, $location) {

            $scope.isShow = 'all';
            $scope.keywords = '';

            $scope.show = function(colle) {
                var flag = false;
                if ($scope.isShow == colle.attachType || $scope.isShow == 'all')
                    flag = true;
                if ($scope.keywords.length > 0 && flag) {
                    flag = false;
                    if (colle.title.indexOf($scope.keywords) >= 0) flag = true;
                    if (colle.creatorName.indexOf($scope.keywords) >= 0) flag = true;
                    if (colle.projectName.indexOf($scope.keywords) >= 0) flag = true;
                }
                return flag;
            };

            $scope.clickType = function(type) {
                $scope.isShow = type;
            };

            collectionService.getCollectionList().then( function(data) {
                $scope.allCollections = data;
            });

        }
    ]);

