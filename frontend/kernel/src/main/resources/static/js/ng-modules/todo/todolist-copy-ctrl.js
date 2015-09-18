angular.module('todo')
    .controller('copyTodolistCtrl', ['theme', '$state', '$scope', 'todolistService', '$modalInstance', 'todolist', 'projects', 'url',
        function(theme, $state, $scope, todolistService, $modalInstance, todolist, projects, url) {
            $scope.todolist = todolist;
            $scope.projects = projects;
            $scope.themes = theme.themes;
            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
            $scope.selected = undefined;
            $scope.select = function(project) {
                $scope.selected = project;
            };
            $scope.status = 'begin';
            $scope.copy = function(project) {
                $scope.status = 'copying';
                $scope.targetId = project.id;
                todolistService.copyTodolist($scope.todolist, project.id).then(function() {
                    $scope.status = 'done';
                }, function() {
                    $scope.cancel();
                    alert('复制任务列表失败！');
                });
            };
            $scope.gotoProject = function(id) {
                $scope.cancel();
                if (id && id !== url.projectId() - 0) {
                    $state.go('company.project.todolists', {
                        projectId: id
                    });
                }
            };
        }
    ]);
