angular.module('tab')
    .controller('commentImageModalCtrl', ['$scope', '$modalInstance', 'imageData',
        function($scope, $modalInstance, imageData) {

            $scope.commentImageUrl = imageData.commentImageUrl;
            $scope.commentImageTitle = imageData.commentImageTitle;

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };

        }  /* end of controller function */
    ]);
/* end of controller  */
