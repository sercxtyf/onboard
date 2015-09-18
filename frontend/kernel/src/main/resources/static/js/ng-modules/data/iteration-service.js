angular.module('data')
    .service('iterationService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 
                                  'todolistService', 'iterationDataService', 'dataProxy', 'storyService', 'bugService',
        function (url, $http, $q, filterFilter, todoService, filterFilter, todolistService, iterationDataService, dataProxy, storyService, bugService) {

            
            var _iterationApi = function(){
                return url.projectApiUrl() + "/iterations/";
            };

            /**
             * 获取迭代列表
             */
            this.getIterations = dataProxy(function(update, status, projectId, companyId){
                return $http.get(_iterationApi(projectId, companyId) + "?status=" + status).then(function (response) {
                    var results = iterationDataService.registeriterations(response.data);
                    return results;
                });
            });

            /**
             * 获取项目下的未开始迭代
             * @param update
             * @returns {*}
             */
            this.getCreatedIterations = function (update, projectId, companyId) {
                return this.getIterations(update, "created", projectId, companyId);
            };

            /**
             * 获取项目下的當前迭代
             * @param update
             * @returns {*}
             */
            this.getActiveIterations = function (update, projectId, companyId) {
                return this.getIterations(update, "active", projectId, companyId).then(function(iterations){
                    if(iterations.length > 0){
                        return iterations[0];
                    }else{
                        return {};
                    }
                });
            };

            /**
             * 获取项目下的已完成迭代
             * @param update
             * @returns {*}
             */
            this.getCompletedIterations = function (update, projectId, companyId) {
                return this.getIterations(update, "completed", projectId, companyId);
            };

            /**
             * 根据id获取iteration
             * @param id
             * @param iterations
             * @returns {*}
             */
            this.getiterationById = dataProxy(function (update, id) {
                return $http.get(_iterationApi() + id).then(function (response) {
                    var iterationDTOs = [response.data];
                    iterationDataService.registeriterations(iterationDTOs);
                    return iterationDTOs[0];
                });
            });


            /**
             * 获取新的iteration
             * @param projectId
             * @param companyId
             * @returns {*}
             */
            this.getNewiteration = function(projectId, companyId){
                var newiteration = new iterationDataService.Iteration().init({
                    companyId: companyId,
                    name: null,
                    projectId: projectId,
                    status: "created",
                    startTime: this.startTime,
                    endTime: this.endTime
                });
                var deferred = $q.defer();
                deferred.resolve(newiteration);
                return deferred.promise;
            };

            /**
             * 获取项目下的當前迭代的故事Tree
             * @param update
             * @returns {*}
             */
            this.getActiveIterationStories = function (update, projectId, companyId) {
                return this.getIterations(update, "active", projectId, companyId).then(function(iterations){
                    for(var i = 0; i < iterations[0].iterables.length; i++){
                        if(iterations[0].iterables[i].type === "story"){
                            iterations[0].iterables[i].choose = true;
                        }
                    }
                    return storyService.getOpenStories(update, projectId, companyId);
                });
            };

            /**
             * 获取项目下的當前迭代的bug列表
             * @param update
             * @returns {*}
             */
            this.getActiveIterationBugs = function (update, projectId, companyId) {
                return this.getIterations(update, "active", projectId, companyId).then(function(iterations){
                    if(iterations.length > 0){
                        return bugService.getOpenBugList(projectId, companyId).then( function(data) {
                            for (var i = 0; i < iterations[0].iterables.length; ++i)
                                for (var j = 0; j < data.length; ++j)
                                    if (iterations[0].iterables[i].type == 'bug' && iterations[0].iterables[i].id == data[j].id)
                                        data[j].choose = true;
                            return data;
                        });
                    }else{
                        return {};
                    }
                });
            };
        }]);