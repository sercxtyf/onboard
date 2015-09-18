var redirect = ['$state', 'drawer', function($state, drawer) {
    return {
        link: function($scope, element) {
            var redirect = function(event) {
                event.stopPropagation();
                var targetType = $(element).attr("data-target-type");
                var targetId = $(element).attr("data-target-id");
                var projectId = $(element).attr("data-project-id");
                if (!targetType) {
                    return;
                }
                switch (targetType) {
                    case 'discussion':
                        drawer.open({
                            type: 'discussion',
                            data: {
                                id: targetId,
                                projectId: projectId
                            }
                        });
                        break;
                    case 'todo':
                        drawer.open({
                            type: 'todo',
                            data: {
                                id: targetId,
                                projectId: projectId
                            }
                        });
                        break;
                    case 'todolist':
                        drawer.open({
                            type: 'todolist',
                            data: {
                                id: targetId,
                                projectId: projectId
                            }
                        });
                        break;
                    case 'upload':
                        drawer.open({
                            type: 'upload',
                            data: {
                                id: targetId,
                                projectId: projectId
                            }
                        });
                        break;
                    case 'document':
                        drawer.close();
                        $state.go('company.project.documents.documentDetails', {
                            docId: targetId,
                            projectId: projectId
                        });
                        break;
                    case 'calendarevent':
                        break;
                    case 'pull-request':
                        drawer.close();
                        $state.go('company.project.repository.pullrequests.detail', {
                            id: targetId,
                            projectId: projectId
                        });
                        break;
                    case 'project':
                        drawer.close();
                        $state.go('company.project.todolists', {
                            projectId: targetId
                        });
                        break;
                    default:
                        break;
                }
            };
            element.on("click", redirect);
        }
    };
}];
angular.module('util').directive('redirect', redirect);
