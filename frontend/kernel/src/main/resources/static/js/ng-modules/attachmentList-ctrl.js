angular.module('projectApp')
    .controller("attachmentsCtrl",['$scope','$http',function($scope,$http){
        $http.get($scope.getProjectApiUri + 'attachments').success(function(data){
            $scope.attachmentList = data;
        }).error(function(){
            alert('add failed!');
        });

        $scope.attachmentTpe = function(){
            return null;
        };

    }]);