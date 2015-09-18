angular.module('data')
    .service('todoUtilService', ['todoService', 'todolistService', 'user', 'url',
        function (todoService, todolistService, user, url) {
            this.getStatus = function(todo){
                if(!todo){
                    return;
                }
                for(var index = 0; index < todoService.todoStatus.length; index++){
                    if(todoService.todoStatus[index].value === todo.status){
                        return todoService.todoStatus[index];
                    }
                }
            };
            this.getType = function(todo){
                if(!todo){
                    return;
                }
                for(var index = 0; index < todoService.todoTypes.length; index++){
                    if(todoService.todoTypes[index].value === todo.todoType){
                        return todoService.todoTypes[index];
                    }
                }
            };
            this.getPriority = function(todo){
                if(!todo){
                    return;
                }
                for(var index = 0; index < todoService.priorities.length; index++){
                    if(todoService.priorities[index].value === todo.priority){
                        return todoService.priorities[index];
                    }
                }
            };
            this.getTodolist = function(todo){
                if(!todo){
                    return;
                }
                return todolistService.getTodolistById(todo.projectId, todo.companyId, 
                        todo.todolistId, false, true);
            };

            this.getDiffDays = function(todo){
                var oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds
                var currDate = new Date();
                if (this.getStatus(todo).name != "已完成"){
                    if(!todo.dueDate){
                        return "inactive";
                    }else{
                        var dueDate = todo.dueDate;
                        var diffDays = (dueDate - currDate)/oneDay;
                        if(diffDays >= 0){
                            if(diffDays<=7 && diffDays > 3){
                                return "warning";
                            }else if(diffDays<=3){
                                return "danger";
                            }
                        }
                    }
                }
                return "default";               
            };
        }]);