/**
 * Created by Dongdong Du on 2015/07/27.
 */
angular.module('util')
    .directive('spinner', ['$rootScope', function($rootScope) {
        return {
            restrict  : 'E',
            replace   : true,
            transclude: true,
            scope     : {
                name  : '@?',
                show  : '=?',
                imgSrc: '@?',
                global: '@?'
            },
            template  : [
                '<div ng-show="show">',
                '<img ng-show="imgSrc" ng-src="{{imgSrc}}" />',
                '<div ng-show="global" class="spinner"><i class="fa fa-spinner fa-spin fa-4x"></i></div>',
                '<span ng-transclude></span>',
                '</div>'
            ].join(''),

            controller: function($scope) {
                // Here we listen the global httpRequest event
                // Details can be found at static\js\ng-modules\onboard\index.js
                if($scope.global) {
                    $rootScope.$on("loader_show", function() {
                        $scope.show = true;
                    });
                    $rootScope.$on("loader_hide", function() {
                        $scope.show = false;
                    });
                }

                // Enable a customized loading div will disable the global Loading spinner
                $scope.$watch('show', function(show) {
                    if(!$scope.global) {
                        if(show) {
                            $('div[name="globalSpinner"]').hide();
                        } else {
                            $('div[name="globalSpinner"]').show();
                        }
                    }
                });
            }
        };
    }]);