angular.module('data')
    .service('stepService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 
                                  'todolistService', 'stepDataService', 'dataProxy',
        function (url, $http, $q, filterFilter, todoService, filterFilter, todolistService, stepDataService, dataProxy) {

            var _stepApi = function(projectId){
                return url.projectApiUrl(projectId) + "/steps/";
            }

            /**
             * 根据id获取step
             * @param id
             * @returns {*}
             */
            this.getById = dataProxy(function (update, id, projectId) {
                return $http.get(_stepApi(projectId) + id).then(function (response) {
                    var stepDTOs = [response.data];
                    stepDataService.register(stepDTOs);
                    return stepDTOs[0];
                });
            });


            /**
             * 获取新的iteration
             * @param projectId
             * @param companyId
             * @returns {*}
             */
            this.getNew = function(currentUser, attach){
                return new stepDataService.Step().init({
                	content: "",
                    status: "todo",
                    dueDate: (new Date()).getTime(),
                    assigneeId: currentUser.id,
                    assigneeDTO: currentUser,
                    attachType: attach.type,
                    attachId: attach.id,
                    projectId: attach.projectId,
                    companyId: attach.companyId,
                    iterationStatus: "todo"
                });
            };

        }]);