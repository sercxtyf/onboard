/**
 * Created by Steven on 2015/7/12.
 */
angular.module('util')
    .directive('tooltip', function(){
        return {
            restrict: 'A',
            link: function(scope, element){
                $(element).hover(function(){
                    // on mouse_enter
                    $(element).tooltip('show');
                }, function(){
                    // on mouse_leave
                    $(element).tooltip('hide');
                });
            }
        };
    });