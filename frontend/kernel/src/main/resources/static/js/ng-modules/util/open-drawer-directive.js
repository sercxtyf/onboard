angular.module('util')
    .directive('openDrawer', ["drawer", "$state", "url", "$http", '$location', function(drawer, $state, url, $http, $location) {
        return {
            scope: {
                "type"  : '=openDrawerType',
                "params": '=openDrawerParams',
                "data"  : '=openDrawerData'
            },
            link : function($scope, element, attrs) {
                var type = attrs.openDrawer;
                if($scope.type) {
                    type = $scope.type;
                }
                function isDrawerType(type) {
                    for(var key in drawer.tpls) {
                        if(type === key) {
                            return true;
                        }
                    }
                    return false;
                }

                var go = function(type, params, data) {
                    if(isDrawerType(type)) {
                        drawer.open({ type: type, params: params, data: data });
                    } else if(type === "comment" || type === "attachment") {
                        $http.get(url.projectApiUrl(data.projectId, data.companyId) + "/" + type + "s/" + params.id).then(function(response) {
                            go(response.data.attachType, { id: response.data.attachId }, data);
                        });
                    } else {
                        var projectId = (data && data.projectId) ? data.projectId : url.projectId();
                        var companyId = (data && data.companyId) ? data.companyId : url.companyId();
                        //$state.go(["", "teams", companyId, "projects", projectId, type + "s", params.id].join('/'));
                        switch(type) {
                            case 'document':
                                drawer.close();
                                $state.go('company.project.documents.documentDetails', {
                                    docId    : params.id,
                                    projectId: projectId
                                });
                                break;
                            case 'calendarevent':
                                break;
                            case 'pull-request':
                                drawer.close();
                                $state.go('company.project.repository.pullrequests.detail', {
                                    id       : params.id,
                                    projectId: projectId
                                });
                                break;
                            case 'project':
                                drawer.close();
                                $state.go('company.project.todolists', {
                                    projectId: projectId
                                });
                                break;
                            default:
                                break;
                        }
                    }
                };
                element.bind('click', function($event) {
                    $event.stopPropagation();
                    go(type, $scope.params, $scope.data);
                });
            }
        };
    }]);