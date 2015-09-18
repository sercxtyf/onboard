angular.module('statistics')
    .controller('userMenuCtrl', ['$scope', 'user', '$rootScope',
        function($scope, user, $rootScope) {

            var groupUsers = function() {
                var ret = d3.nest().key(function(d) {
                    return d.tag;
                }).entries($scope.userList);
                //console.log(ret);
                return ret;
            };

            $scope.groups = {};
            $scope.userList = [];
            user.getProjectUsers().then(function(userList) {
                $scope.userList = userList;
                $scope.groups = groupUsers();
            });

            var bindToggleMembers = function() {
                $('.userMenu .dropdown-header').parent().unbind('click');
                $('.userMenu .dropdown-header').parent().bind('click', function(event) {
                    if (event.target.tagName !== 'A') {
                        $(this).find('li:gt(0)').toggle();
                        return false;
                    }
                });
            };

            $scope.chooseUser = function(user) {
                $scope.$emit('chooseUser', user);
                //$event.stopPropagation();
            };

            $('#userMenu').one('click', function() {
                bindToggleMembers();
            });

            // pie chart click
            $scope.$on('sliced', function(event, name) {
                if($scope.notListen)
                    return;
                var user;
                for (var i = 0; i < $scope.userList.length; i++) {
                    user = $scope.userList[i];
                    if (user.name === name) {                        
                        $('.userMenu button').click();
                        $scope.$emit('chooseUser', user);
                        $('.userMenu button').click();
                        break;
                    }
                }
            });
        }
    ]);
