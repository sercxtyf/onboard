/**
 * Created by R on 2014/11/25.
 */

angular.module('iteration')
    .controller('activeIterationStatisticsCtrl', ['$scope', 'iterationService', 'burndownChart', 'user', 'url',
        function($scope, iterationService, burndownChart, user, url) {
            $scope.filterMember = {
                "assigneeId": $scope.user.id
            };
            var drawBurnDownChart = function() {
                if ($scope.filterMember.assigneeId !== 0) {
                    burndownChart.showBurnDownChart($scope.activeIteration, "#activeIterationStatistics #burnDownChart", $scope.filterMember);
                } else {
                    burndownChart.showBurnDownChart($scope.activeIteration, "#activeIterationStatistics #burnDownChart");
                }
            };
            iterationService.getActiveIterations(true).then(function(iteration) {
                if (iteration !== undefined && iteration.id) {
                    $scope.activeIteration = iteration;
                    drawBurnDownChart();
                } else {
                    $state.go('company.project.iteration.createdIterations');
                }
            });

            $scope.$watch('user', function(){
                //$scope.user = user;
                $scope.filterMember = {
                    "assigneeId": $scope.user.id
                };
                drawBurnDownChart();
            });
            $scope.$on('timeChange', function(event, data) {
                drawBurnDownChart();
            });
        }
    ]);
