angular.module('data')
    .service('todoWebsocketService', ['todoDataService', 'todolistDataService',
        function(todoDataService, todolistDataService) {
            this.add = function(todoDTO) {
                if(!todoDTO) {
                    return;
                }
                var todolist = todolistDataService.getTodolistById(todoDTO.todolistId);
                if(todolist) {
                    var todo = todoDataService.registerTodos([todoDTO], todolist, todolist.modelType)[0];
                    todolist.addTodo(todo);
                }
            };
            this.update = function(todoDTO) {
                var todo = todoDataService.getTodo(todoDTO.id);
                if(todo) {
                    todo.updateByTodoDTO(todoDTO);

                    // manually remove closed todos in me page
                    var todoStr = "div[data-id=" + todo.id + "]";
                    if(todo.status === "closed") {
                        $('#meUncompletedTodos').find(todoStr).remove();
                    }
                    else {
                        $('#meCompletedTodos').find(todoStr).remove();
                    }
                }
            };
            this.delete = function(todoDTO) {
                var todo = todoDataService.getTodo(todoDTO.id);
                if(todo) {
                    todo.updateByTodoDTO(todoDTO);
                }
            };
        }
    ]);