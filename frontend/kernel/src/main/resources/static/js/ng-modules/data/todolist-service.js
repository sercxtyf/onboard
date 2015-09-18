angular.module('data')
    .service('todolistService', ['url', '$http', '$q', 'todolistDataService',
        function (url, $http, $q, todolistDataService) {

            var _todolistApi = function(projectId, companyId){
                return url.projectApiUrl(projectId, companyId) + "/todolists/";
            };
            var _todolistService = this;
            
            var todolists = {};

            /**
             * 获取项目下的所有项目列表
             * @param update
             * @returns {*}
             */
            this.getTodolists = function (projectId, companyId, update, direct) {
                if(direct){
                    if(todolists[companyId] && todolists[companyId][projectId]){
                        return todolists[companyId][projectId];
                    }else{
                        return null;
                    }
                }
                var deferred = $q.defer();
                if (update || todolists[companyId] === undefined 
                        || todolists[companyId][projectId] === undefined) {
                    return $http.get(_todolistApi(projectId, companyId)).then(function (response) {
                        var _todolists = todolistDataService.registerTodolists(response.data);
                        if(todolists[companyId]){
                            if(todolists[companyId][projectId]){
                                todolists[companyId][projectId].splice(0);
                                $.extend(todolists[companyId][projectId], _todolists);
                            }else{
                                todolists[companyId][projectId] = _todolists;
                            }
                        }else{
                            todolists[companyId] = {};
                            todolists[companyId][projectId] = _todolists;
                        }
                        return todolists[companyId][projectId];
                    });
                }
                else {
                    deferred.resolve(todolists[companyId][projectId]);
                }
                return deferred.promise;
            };
            
            this.getArchivedTodolists = function(projectId, companyId){
                return $http.get(_todolistApi(projectId, companyId) + "/archive").then(function (response) {
                    var _todolists = todolistDataService.registerTodolists(response.data);
                    return _todolists;
                });
            };

            /**
             * 根据id获取todolist
             * @param id
             * @param todolists
             * @returns {*}
             */
            this.getTodolistById = function (projectId, companyId, id, update, direct) {
                if(direct){
                    return todolistDataService.getTodolistById(id);
                }
                var deferred = $q.defer();
                if (update || !todolistDataService.getTodolistById(id)) {
                    return $http.get(_todolistApi(projectId, companyId) + id).then(function (response) {
                        var todolistDTOs = [response.data];
                        todolistDataService.registerTodolists(todolistDTOs);
                        return todolistDTOs[0];
                    });
                }
                else {
                    deferred.resolve(todolistDataService.getTodolistById(id));
                }
                return deferred.promise;
            };

            this.getNewTodolist = function(projectId, companyId){
                var newTodolist = new todolistDataService.Todolist().updateByTodolistDTO({
                    projectId: projectId,
                    name: null,
                    position: 0.0,
                    deleted: false,
                    companyId: companyId,
                    archived: false
                });
                return $q.all([this.getTodolists(projectId, companyId)]).then(function(result) {
                    _todolists = result[0];
                    if(_todolists.length > 0){
                        newTodolist.position = _todolists[0].position + 1;
                    }else{
                        newTodolist.position = 0;
                    }
                    return newTodolist;
                });
            };
            
            this.createTodolist = function(todolist){
//                todolists[todolist.companyId][todolist.projectId].splice(0, 0, todolist);
                return todolist.create();
            };

            this.copyTodolist = function(todolist, id) {
                return $http.get(_todolistApi() + todolist.id + "/copy", {
                    params: {
                        targetProjectId: id
                    }
                }).then(function(response) {
                    var _todolists = todolistDataService.registerTodolists(response.data);
                    return _todolists;
                });
            };
            
        }]);