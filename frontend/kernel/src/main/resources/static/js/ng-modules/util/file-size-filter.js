/**
 * Created by harttle on 12/9/14.
 */

angular.module('util')
    .filter('fileSize', function () {
        return function (value) {
            var out = value;
            if(value > 1000000000){
                out = (value / 1000000000).toFixed(2) + 'G';
            }else if (value >= 1000000) {
                out = (value / 1000000).toFixed(2) + 'M';
            }else if (value >= 1000) {
                out = (value / 1000).toFixed(2) + 'K';
            }else{
                out = value + 'B';
            }
            return out;
        };
    });