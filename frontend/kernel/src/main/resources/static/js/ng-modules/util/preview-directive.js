/**
 * Created by harttle on 1/3/15.
 */

angular.module('util')
    .directive('preview', [function() {
        return {
            restrict: 'E',
            templateUrl: 'preview.html',
            scope: {
                "contentType": '=contentType'
            },
            link: function($scope, element, attrs) {

            }
        };
    }]);