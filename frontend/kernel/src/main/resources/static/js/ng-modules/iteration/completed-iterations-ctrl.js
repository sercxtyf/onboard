/**
 * Created by R on 2014/11/25.
 */

angular.module('iteration')
    .controller('completedIterationsCtrl', ['$scope', 'filterFilter', 'iterationService',
        function ($scope, filterFilter, iterationService) {
            $scope.iterations = [];
            iterationService.getCompletedIterations(true).then(function(iterations){
                $scope.iterations = iterations;
            });

            $scope.getComptetedCount = function(iteration, completed){
                var count = 0;
            	for(var i = 0; i < iteration.iterables.length; i++){
                	if(iteration.iterables[i].iterationCompleted === completed){
                		count++;
                	}
                }
            	return count;
            };
        }
    ]);