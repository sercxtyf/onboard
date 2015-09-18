 /**
 * Created by R on 2014/11/25.
 */

angular.module('iteration')
    .controller('completedIterationCtrl', ['$scope', '$state', 'filterFilter', 'iterationService', '$location', 'url',
        function ($scope, $state, filterFilter, iterationService, $location, url) {
            iterationService.getiterationById($state.params.iterationId).then(function(iteration){
                if(iteration !== undefined && iteration.id){
                    $scope.completedIteration = iteration;
                    $scope.completedIteration.completedTodos = filterFilter($scope.completedIteration.todos, {status: "closed"});
                    if($state.is('company.project.iteration.completedIteration')){
                        $state.go('company.project.iteration.completedIteration.todos', {iterationId: $scope.completedIteration.id });
                    }
                }else{
                    $state.go('company.project.iteration.createdIterations');
                }
            });
            $scope.isActive = function(path) {
                return $location.path().indexOf(path) >= 0;
            };            
        }
    ])
    .controller('completeIterationTodosCtrl', ['$scope', function ($scope) {
    }])
    .controller('completeIterationStatisticsCtrl', ['$scope', 'iterationService', 'burndownChart', 'user', 'url',
        function($scope, iterationService, burndownChart, user, url) {
            var drawBurnDownChart = function() {
                if ($scope.filterMember.assigneeId !== 0) {
                    burndownChart.showBurnDownChart($scope.completedIteration,
                        "#completedIterationStatistics #burnDownChart", $scope.filterMember);
                } else {
                    burndownChart.showBurnDownChart($scope.completedIteration,
                        "#completedIterationStatistics #burnDownChart");
                }
            };
            $scope.$watch("completedIteration", function() {
                $scope.filterMember = {
                    "assigneeId": 0
                };
                if ($scope.completedIteration !== undefined && $scope.completedIteration.id !== undefined) {
                    drawBurnDownChart();
                }
            });

            $scope.$watch('user', function() {
                $scope.filterMember = {
                    "assigneeId": $scope.user.id
                };
                drawBurnDownChart();
            });
            
        }

    ]);
