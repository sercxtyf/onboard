angular.module('data')
    .service('bugWebsocketService', ['bugService',
        function(bugService) {
            this.add = function(bugDTO) {
                bugService.webSocketCreateBug(bugDTO);
                console.log("bug added");
            };
            this.update = function(bugDTO) {
                bugService.webSocketUpdateBug(bugDTO);
                console.log("bug updated");
            };
            this.delete = function(bugDTO) {
                bugService.webSocketDeleteBug(bugDTO);
                console.log("bug deleted");
            };
        }
    ]);