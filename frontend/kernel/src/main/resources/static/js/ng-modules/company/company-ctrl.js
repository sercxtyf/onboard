/**
 * Created by harttle on 12/9/14.
 */

// 团队
angular.module('company')

    .controller('companyCtrl', ['$scope', 'url', '$http', '$state', 'company',
        function ($scope, url, $http, $state, company) {

            $scope.companyId = url.companyId();
            $scope.$state = $state;

            company.getCompanyInfo($scope.companyId).then(function(company) {
                $scope.currentCompany = company;
            });
        }]);