(function(angular, undefined) {
    angular.module('util').directive('commitCode', ['drawer', '$timeout', '$http', '$compile', 'url', '$state', function(drawer, $timeout, $http, $compile, url, $state) {
        return {
            restrict: 'A',
            scope: {
                projectId: '=',
                content: '='
            },

            link: function(scope, element, attrs, controller) {
                var projectId = scope.projectId === undefined ? url.projectId() : scope.projectId;
                scope.clickedPullrequest = function(pullrequestId) {
                    var id = parseInt(pullrequestId, 10);
                    drawer.close();
                    $state.go('company.project.repository.pullrequests.detail', {
                        id: id,
                        projectId: projectId
                    });
                };

                scope.clicked = function(projectItemId) {
                    var id = parseInt(projectItemId, 10);
                    $http.get(projectItemUrl(projectId, id)).success(successfun);
                };

                var projectItemUrl = function(p, id) {
                    return url.projectApiUrl(p) + '/projectItem/' + id;
                };

                var successfun = function(data) {
                    if (data !== "") {
                        var option = {
                            type: data.type,
                            params: {
                                id: data.id
                            },
                            data: {
                                id: data.id,
                                projectId: data.projectId
                            }
                        };
                        $timeout(function() {
                            drawer.open(option);
                        });
                    }
                };

                function render() {
                    var todo = new RegExp('#([0-9]+)', 'g');
                    var pullrequest = new RegExp('pull request ([0-9]+)', 'g');
                    var content = scope.content.replace(todo, '<a href="#" class="commit-todo" ng-click="clicked(' + '$1' + ')">#' + '$1' + '</a>')
                        .replace(pullrequest, 'pull request ' + '<a href="#" class="pullrequest" ng-click="clickedPullrequest(' + '$1' + ')">' + '$1' + '</a>');
                    var template = '<span>' + content + '</span>';
                    element.html('').append($compile(template)(scope));
                }

                render();
            }
        };
    }]);
})(angular);
