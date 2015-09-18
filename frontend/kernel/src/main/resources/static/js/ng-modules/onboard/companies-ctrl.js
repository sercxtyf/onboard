/**
 * Created by harttle on 12/9/14.
 */

angular.module('onboard')

    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('companies', {
                url: '/teams',
                views:{
                    'nav':{
                        templateUrl: 'onboard-header.html'
                    },
                    'content':{
                        templateUrl: 'companies.html',
                        controller: 'companiesCtrl'
                    }
                }
            })
    }])

    .controller('companiesCtrl', ['$scope','$state','company',
        function ($scope,$state, company) {

            $scope.newTeam={name: ''};

            company.getTeams(true).then(function(companies){
                $scope.companies = companies;
            });

            $scope.createTeam = function(){
                $scope.status = 'creating';

                company.createTeam({name: $scope.newTeam.name}).then(function(team){
                    $scope.status = 'success';
                    $state.go('company.projects', {companyId: team.id});
                }).catch(function(data){
                    $scope.status = 'error';
                    $scope.message = data;
                });
            };

        }]);