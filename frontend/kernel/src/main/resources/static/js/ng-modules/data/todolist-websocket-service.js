angular.module('data')
    .service('todolistWebsocketService', ['todolistService', 'todolistDataService',
        function(todolistService, todolistDataService) {
            this.add = function(todolistDTO) {
                var todolist = todolistDataService.registerTodolists([todolistDTO])[0];
                var todolists = todolistService.getTodolists(todolistDTO.projectId, todolistDTO.companyId, false, true);
                if (todolists && todolists.indexOf(todolist) >= 0) {
                    todolists.splice(0, 0, todolist);
                }
            };
            this.update = function(todolistDTO) {
                todolistDataService.registerTodolists([todolistDTO]);
            };
            this.delete = function(todolistDTO) {
                todolistDataService.registerTodolists([todolistDTO]);
            };
            this.copy = function(todolistDTO) {
                todolistService.getTodolistById(todolistDTO.projectId, todolistDTO.companyId, todolistDTO.id, true).then(function(todolist) {
                    var todolists = todolistService.getTodolists(todolistDTO.projectId, todolistDTO.companyId, false, true);
                    if (todolists) {
                        todolists.splice(0, 0, todolist);
                    }
                })

            };
        }
    ]);
