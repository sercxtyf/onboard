angular.module('data')
    .service('todoService', ['url', '$http', '$q', 'todoDataService', 'todolistService',
        function (url, $http, $q, todoDataService, todolistService) {

            var _todoApi =  function(projectId, companyId){
                return url.projectApiUrl(projectId, companyId) + "/todos/";
            };
            var _todoStatusApi = function(projectId, companyId){
                return url.projectApiUrl(projectId, companyId) + "/todostatus/";
            };
            var todoService = this;

            this.Todo = todoDataService.Todo;

            this.priorities = [
                { desc: '非常紧急', value: 1},
                { desc: '紧急', value: 2},
                { desc: '重要', value: 3},
                { desc: '普通', value: 4},
                { desc: '可忽略', value: 5}
            ];
            this.todoTypes = [
                {name: '任务', value: 'task'},
                {name: '需求', value: 'story'},
                {name: 'Bug', value: 'bug'}
            ];
            this.todoStatus = [{
                value: "todo",
                name: "未开始",
                active: true
            }, {
                value: "inprogress",
                name: "正在做",
                active: true
            }, {
                value: "fixed",
                name: "已提交",
                active: false
            }, {
                value: "approved",
                name: "同意完成",
                active: false
            }, {
                value: "reviewed",
                name: "复审通过",
                active: false
            }, {
                value: "verified",
                name: "测试通过",
                active: false
            }, {
                value: "closed",
                name: "已完成",
                active: true
            }];
            var _todoStatus = {};
            
            this.getTodoById = function(projectId, companyId, id, update, direct){
                if(direct){
                    return todoDataService.getTodo(id);
                }
                var deferred = $q.defer();
                if (update || !todoDataService.getTodo(id)) {
                    return $http.get(_todoApi(projectId, companyId) + id).then(function (response) {
                        var todo = todoDataService.getTodo(id);
                        if(todo){
                            todo.updateByTodoDTO(response.data);
                        }else{
                            todo = todoDataService.registerTodos([response.data])[0];
                            todolistService.getTodolistById(todo.projectId, todo.companyId, todo.todolistId).then(function(todolist){
                                todo.addParent(todolist, todolist.modelType);
                            });
                        }
                        return todo;
                    });
                }
                else {
                    deferred.resolve(todoDataService.getTodo(id));
                }
                return deferred.promise;
            };

            this.getNewTodo = function(todolist){
                var newTodo = new todoDataService.Todo().updateByTodoDTO({
                    projectId: todolist.projectId,
                    todolistId: todolist.id,
                    content: null,
                    position: 0,
                    completed: false,
                    assigneeId: null,
                    deleted: false,
                    companyId: todolist.companyId,
                    doing: false,
                    todoType: "task",
                    description: null,
                    id: null,
                    priority: 3,
                    status: "todo"
                });
                var deferred = $q.defer();
                deferred.resolve(newTodo);
                return deferred.promise;
            };
            
            this.getTodoStatuses = function(projectId, companyId, update){
                var deferred = $q.defer();
                if (update || _todoStatus[companyId] === undefined
                        || _todoStatus[companyId][projectId] === undefined) {
                    return $http.get(_todoStatusApi(projectId, companyId)).then(function (response) {
                        var data = response.data;
                        var statues = angular.copy(todoService.todoStatus);
                        for (var i = 0; i < statues.length; i++) {
                            for (var j = 0; j < data.length; j++) {
                                if (statues[i].value == data[j]) {
                                    statues[i].active = true;
                                    break;
                                }
                            }
                        }
                        if(_todoStatus[companyId]){
                            if(_todoStatus[companyId][projectId]){
                                _todoStatus[companyId][projectId].splice(0);
                                $.extend(_todoStatus[companyId][projectId], statues);
                            }else{
                                _todoStatus[companyId][projectId] = statues;
                            }
                        }else{
                            _todoStatus[companyId] = {};
                            _todoStatus[companyId][projectId] = statues;
                        }
                        return _todoStatus[companyId][projectId];
                    });
                }
                else {
                    deferred.resolve(_todoStatus[companyId][projectId]);
                }
                return deferred.promise;
            };

            this.registerTodos = function(todoDTOs, parent, modelType){
                return todoDataService.registerTodos(todoDTOs, parent, modelType);
            };

        }]);