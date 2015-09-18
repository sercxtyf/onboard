/**
 * Created by pkutxq on 15-7-21.
 */
angular.module('account')
    .controller('forgetPasswordCtrl',['$http','$scope',function($http,$scope){
        $scope.forgetPasswdObject = {};
        $scope.stat = 'success';
        $scope.resetInit = "open";
        $scope.sendSuccess = "failure";

        $scope.forgetPasswdSend = function(){
            var forgetPasswordUrl = "/api/account-forget";
            var whetherEmailExistUrl = "/api/account-exist?email-or-username=";
            var forgetPasswdForm = {
                'email':$scope.forgetPasswdObject.email
            };

            $http.get(whetherEmailExistUrl+forgetPasswdForm.email).success(function(response){
                if(response == true){
                    $http.post(forgetPasswordUrl, forgetPasswdForm).success(function (response) {
                        $scope.resetInit = "close";
                        $scope.sendSuccess = "success";
                    }).error(function (response) {
                        console.log(response);
                    });
                }else {
                    $scope.stat = 'error';
                }

            }).error(function(response){
                console.log(response);
            });
        };

    }]);