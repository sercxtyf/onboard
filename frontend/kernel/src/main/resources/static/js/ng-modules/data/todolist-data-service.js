angular.module('data')
    .service('todolistDataService', ['url', '$http', '$q', 'filterFilter', 'todoDataService',
        function (url, $http, $q, filterFilter, todoDataService) {

            var _todolists = {};
            var todolistDataService = this;
            var Todolist = function(){
                var self = this;
                this.todos = [];
                this.origin = {};
                this.modelType = "todolist";
                this.openTodos = function(){
                    return filterFilter(this.todos, {status: "!closed"});
                };
                this.completedTodos = function(){
                    return filterFilter(this.todos, {status: "closed"});
                };
                this._todolistApi =  function(){
                    return url.projectApiUrl(this.projectId, this.compamyId) + "/todolists/";
                };
                this.updateByTodolistDTO = function(todolistDTO) {
                    //将todoDTO变为todo对象
                    if(todolistDTO.todos){
                        todoDataService.registerTodos(todolistDTO.todos, this, this.modelType);
                        this.todos = todolistDTO.todos;
                    }
                    delete todolistDTO.todos;
                    this.origin = todolistDTO;
                    $.extend(this, todolistDTO);
                    return this;
                };
                /**
                 * 重置之前修改的信息
                 */
                this.reset = function(){
                    $.extend(this, this.origin);
                };
                /**
                 * 根据数据生成todolistDTO
                 */
                this.getTodolistDTO = function(){
                    return {
                        projectId: this.projectId,
                        name: this.name,
                        position: this.position,
                        deleted: this.deleted,
                        companyId: this.companyId,
                        archived: this.archived,
                        id: this.id
                    };
                };
                //todolist的增删改查
                this.create = function(){
                    return $http.post(this._todolistApi(), this.getTodolistDTO()).then(function (response) {
                        self.updateByTodolistDTO(response.data)
                        _todolists[self.id] = self;
                        return self;
                    });
                };
                this.delete = function(){
                    this.deleted = true;
                    var data = {deleted : true};
                    return $http.put(this._todolistApi() + this.id, data).then(function (response) {
                        self.updateByTodolistDTO(response.data);
                    });
                };
                this.archive  = function(){
                    this.archived = true;
                    var data = {archived : true};
                    return $http.put(this._todolistApi() + this.id, data).then(function (response) {
                        self.updateByTodolistDTO(response.data);
                    });
                };
                this.updateAll = function(){
                    return $http.put(this._todolistApi() + this.id, this.getTodolistDTO()).then(function (response) {
                        self.updateByTodolistDTO(response.data);
                    });
                };
                this.updateByAttrs = function(attrs){
                    var data = {};
                    for(var index in attrs){
                        data[attrs[index]] = this[attrs[index]];
                    }
                    return $http.put(this._todolistApi() + this.id, data).then(function (response) {
                        self.updateByTodolistDTO(response.data);
                    });
                };
                this.allCompleted = function(){

                    var openTodos = this.openTodos();
                    var completedTodos = this.completedTodos();

                    var hasOpen = openTodos && openTodos.length;
                    var hasComp = completedTodos && completedTodos.length;

                    return !hasOpen && hasComp;
                };

                this.isEmpty = function(){
                    var todos = $.merge([], this.completedTodos(), this.openTodos());
                    return !todos || !todos.length;
                };

                //维护todo列表
                this.addTodo = function(todo){
                    todo.addParent(this, this.modelType);
                    if(this.todos.indexOf(todo) < 0){
                        this.todos.splice(0, 0, todo);
                    }
                };
                this.removeTodo = function(todo){
                    if(this.todos.indexOf(todo) >= 0){
                        this.todos.splice(this.todos.indexOf(todo), 1);
                    }
                };
                //todo改变后做出相应改变
                this.afterUpdateTodo = function(todo){
                    if(todo.todolistId != this.id){
                        this.removeTodo(todo);
                        var todolist = findTodolisById(todo.todolistId);
                        if(!todolist){
                            return;
                        }
                        todolist.addTodo(todo);
                        return;
                    }
                };
            };
            
            this.Todolist = Todolist;

            function findTodolisById(id){
                return _todolists[id];
            }
            
            this.registerTodolists = function(todolistDTOs){
                for(var index in todolistDTOs){
                    var todolist = findTodolisById(todolistDTOs[index].id);
                    if(!todolist){
                        todolist = new Todolist().updateByTodolistDTO(todolistDTOs[index]);
                        _todolists[todolist.id] = todolist;
                    }else{
                        todolist.updateByTodolistDTO(todolistDTOs[index]);
                    }
                    todolistDTOs[index] = todolist;
                }
                return todolistDTOs;
            };


            /**
             * 根据id获取todolist
             */
            this.getTodolistById = function (id) {
                return findTodolisById(id);
            };

        }]);