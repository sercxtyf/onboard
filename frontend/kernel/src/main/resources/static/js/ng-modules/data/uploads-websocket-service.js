angular.module('data')
    .service('uploadsWebsocketService', ['uploadsService',
        function(uploadsService) {
            this.add = function(uploadDTO) {
                uploadsService.webSocketCreateUpload(uploadDTO);
                console.log("upload add");
            };
            this.update = function(uploadDTO) {
                uploadsService.webSocketUpdateUpload(uploadDTO);
                console.log("upload update");
            };
            this.delete = function(uploadDTO) {
                uploadsService.webSocketDeleteUpload(uploadDTO);
                console.log("upload delete");
            };
        }
    ]);