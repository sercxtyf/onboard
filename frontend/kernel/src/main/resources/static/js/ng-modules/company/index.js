/**
 * Created by harttle on 12/9/14.
 */

// 团队模块
angular.module('company',['ui.router','ui.sortable', 'util', 'infinite-scroll', "checklist-model", 'ui.calendar', 'ngResource',
     'ngAnimate', 'data', 'project', 'ngSanitize', 'sexyDatepicker', 'statistics', 'angularBrick', 'status'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            // abstract parent state
            .state('company', {
                url     : '/teams/{companyId:[0-9]+}',
                views   : {
                    'nav'    : {
                        templateUrl: 'company-header.html',
                        controller : 'companyCtrl'
                    },
                    'content': {
                        template  : '<div id="company"><ui-view></ui-view></div>',
                        controller: 'companyCtrl'
                    }
                },
                abstract: true
            })
    }]);
