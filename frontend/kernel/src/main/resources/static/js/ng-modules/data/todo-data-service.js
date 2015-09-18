angular.module('data')
    .service('todoDataService', ['$http', 'user', 'url',
        function ($http, user, url) {

            var _todos = {};
            var Todo = function(){
                this.parents = [];
                this.modelType = "todo";
                this.todoDTO = {};
                this.origin = {};
                var self = this;
                this._todoApi =  function(){
                    return url.projectApiUrl(this.projectId, this.compamyId) + "/todos/";
                };
                /**
                 * 根据todoDTO更新todo的信息
                 * @param todoDTO
                 */
                this.updateByTodoDTO = function(todoDTO) {
                    var isCreate = false;
                    if(!this.id){
                        isCreate = true;
                    }
                    this.origin = this.getTodoDTO();
                    $.extend(this, todoDTO);
                    this.todoDTO = todoDTO;
                    self.updateParentsAfterUpdate();
                    self.getUser();
                    return this;
                };
                /**
                 * 重置之前修改的信息
                 */
                this.reset = function(){
                    $.extend(this, this.todoDTO);
                };
                /**
                 * 根据数据生成todoDTO
                 */
                this.getTodoDTO = function(){
                    return {
                        projectId: this.projectId,
                        todolistId: this.todolistId,
                        content: this.content,
                        position: this.position,
                        completed: this.completed,
                        dueDate: this.dueDate,
                        creatorName: this.creatorName,
                        creatorId: this.creatorId,
                        assigneeId: this.assigneeId,
                        deleted: this.deleted,
                        companyId: this.companyId,
                        doing: this.doing,
                        todoType: this.todoType,
                        description: this.description,
                        id: this.id,
                        priority: this.priority,
                        status: this.status,
                        updated: this.updated,
                        completerId: this.completerId,
                        completeTime: this.completeTime,
                        startTime: this.startTime
                    }
                };
                //以下为增删改查操作
                this.create = function(todolist){
                    if (todolist.todos.length > 0) {
                        this.position = todolist.todos[0].position * 1.0 - 100.0;
                    }else{
                        this.position = 0;
                    }
                    self.parents = [todolist];
                    self.updateParentsAfterCreate();
                    return $http.post(this._todoApi(), this.getTodoDTO()).then(function (response) {
                        self.updateByTodoDTO(response.data);
                        _todos[self.id] = self;
                    });
                };
                this.delete = function(){
                    if(!this.id){
                        return;
                    }
                    this.deleted = true;
                    var data = { selective : true , deleted : true};
                    self.updateParentsAfterDelete();
                    return $http.put(this._todoApi() + this.id, data).then(function (response) {});
                };
                this.recover = function(){
                	if(!this.id){
                        return;
                    }
                    this.deleted = false;
                    var data = { selective : true , deleted : false};
                    return $http.put(this._todoApi() + this.id, data).then(function (response) {});
                }
                /**
                 *
                 * @param selective 表示空的数据是否也要更新,默认是不更新为空的字段的，因此selective为true
                 * @returns {*}
                 */
                this.updateAll = function(selective){
                    if(!this.id){
                        return;
                    }
                    if(selective === undefined){
                        selective = true;
                    }
                    var data = this.getTodoDTO();
                    data["selective"] = selective;
                    self.updateParentsAfterUpdate();
                    return $http.put(this._todoApi() + this.id, data).then(function (response) {
                        self.updateByTodoDTO(response.data);
                    });
                };
                this.updateByAttrs = function(attrs, selective){
                    if(!this.id){
                        return;
                    }
                    if(selective === undefined){
                        selective = true;
                    }
                    var data = { selective : selective };
                    for(var index in attrs){
                        if(this[attrs[index]] === undefined){
                            data[attrs[index]] = null
                        }else{
                            data[attrs[index]] = this[attrs[index]];
                        }
                    }
                    self.updateParentsAfterUpdate();
                    return $http.put(this._todoApi() + this.id, data).then(function (response) {
                        self.updateByTodoDTO(response.data);
                    });
                };
                //这是更新后通知其父类做出相应调整的操作
                this.updateParentsAfterUpdate = function(){
                    //update parents can modify the parnets
                    var tmpParents = this.parents.slice();
                    for(var index in tmpParents){
                        if(tmpParents[index].afterUpdateTodo){
                            tmpParents[index].afterUpdateTodo(self);
                        }
                    }
                };
                this.updateParentsAfterCreate = function(){
                    //update parents can modify the parnets
                    var tmpParents = this.parents.slice();
                    for(var index = 0; index < tmpParents.length; index++){
                        if(tmpParents[index].afterCreateTodo){
                            tmpParents[index].afterCreateTodo(self);
                        }
                    }
                };
                this.updateParentsAfterDelete = function(){
                    //update parents can modify the parnets
                    var tmpParents = this.parents.slice();
                    for(var index = 0; index < tmpParents.length; index++){
                        if(tmpParents[index].afterDeleteTodo){
                            tmpParents[index].afterDeleteTodo(self);
                        }
                    }
                };
                //这是为了方便将一些公共操作的封装
                this.closeOrOpen = function(){
                    if(this.status == "closed"){
                        this.status = "todo";
                    }else{
                        this.status = "closed";
                    }
                    this.updateByAttrs(["status"]);
                };
                this.addParent = function(parent, modelType){
                    if(modelType){
                        for(var index = 0; index < this.parents.length; index++){
                            if(this.parents[index].modelType === parent.modelType){
                                this.parents.splice(index, 1);
                            }
                        }
                        this.parents.push(parent);
                    }else{
                        if(this.parents.indexOf(parent) < 0){
                            this.parents.push(parent);
                        }
                    }
                };
                this.assign = function(user){
                    this.assigneeId = user.id;
                    self.user = user;
                    //临时解决一个bug，这两个值是允许为空的，所以需要同时上传，否则会把另一个设置为空
                    this.updateByAttrs(['assigneeId', "dueDate"], false);
                };
                this.setStatus = function(status){
                    self.status = status;
                    self.updateByAttrs(['status']);
                };
                this.setTodolistId = function(todolistId){
                    self.todolistId = todolistId;
                    self.updateByAttrs(['todolistId']);
                };
                this.getUser = function(){
                    self.user = {name:'未分配', avatarUrl: url.defaultAvatarUrl};
                    user.getUserById(false, self.projectId, self.companyId, self.assigneeId).then(function(u){
                        $.extend(self.user, u);
                    });
                    return self.user;
                };
            };

            this.Todo = Todo;

            /**
             * 将任务注册进todoservice，同时将dto变为todo对象
             * @param todoDTOs
             */
            this.registerTodos = function(todoDTOs, parent, modelType){
                for(var index in todoDTOs){
                    var todo = _todos[todoDTOs[index].id];
                    if(!todo){
                        todo = new Todo().updateByTodoDTO(todoDTOs[index]);
                        _todos[todo.id] = todo;
                    }else{
                        todo.updateByTodoDTO(todoDTOs[index]);
                    }
                    if(parent){
                        todo.addParent(parent, modelType);
                    }
                    todoDTOs[index] = todo;
                }
                return todoDTOs;
            };
            
            this.getTodo = function(id){
                return _todos[id];
            }
        }]);
