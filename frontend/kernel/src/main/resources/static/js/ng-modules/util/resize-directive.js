/**
 * Created by R on 2014/11/26.
 */
angular.module('util')
    .directive('resize', ['$timeout', function ($timeout) {
        return {
            link: function ($scope, element) {
                var resize = function () {
                    return element[0].style.height = "" + element[0].scrollHeight + "px";
                };
                element.on("blur keyup change", resize);
                $timeout(resize, 0);
            }
        };
    }]);