/**
 * Created by harttle on 12/10/14.
 */

// initialize and bootstrap onBoard App

window.Module['onboard'] = function () {

    // prepare projectApp
    angular.module('data')
        .constant('upyun', {
            'protocol': $('#upyunProtocol').val(),
            'host': $('#upyunHost').val()
        });

    // bootstrap projectApp
    angular.bootstrap($('#onboard')[0], ['onboard']);
};