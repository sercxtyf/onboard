/**
 * Created by harttle on 11/29/14.
 */

angular.module('data')
    .service('project', ['url', '$http', 'dataProxy',function (url, $http, dataProxy) {

        var lastVistedProject = {
            id: undefined,
            name: undefined
        };

        this.getLastVisited = function() {
            return lastVistedProject;
        };

        this.setLastVisited = function(projectId, companyId) {
            this.getProject(true, projectId, null).then( function(data) {
                lastVistedProject.companyId = companyId;
                lastVistedProject.id = data.id;
                lastVistedProject.name = data.name;
                //$rootScope.$apply();
            });
        };

        this.getProject = dataProxy(function (update, projectId, companyId) {

            return $http.get(url.projectApiUrl(projectId, companyId) + '/').then(function (response) {
                return project = response.data;
            })
        });

        this.getActiveProjectNumber = dataProxy(function(){
           return $http.get(url.projectEditUrl(true)+'-count').then(function(response){
               return sum = response.data;
           })
        });

        this.deleteProject = function(project){
            return $http.delete(url.projectApiUrl(project.id, project.companyId) + '/');
        }
    }]);