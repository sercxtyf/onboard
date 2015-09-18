/**
 * Created by pkutxq on 15-5-9.
 */
angular.module('data')
    .service('ciProjectWebsocketService', ['ciService',
        function (ciService) {
            this.add = function(ciProjectDTO){
                ciService.webSocketCreateCIProject(ciProjectDTO);
                console.log('add ciProject');
            };
            this.update = function(ciProjectDTO){
                ciService.webSocketUpdateCIProject(ciProjectDTO);
                console.log('update ciProject');
            };
            this.delete = function(ciProjectDTO){
                ciService.webSocketDeleteCIProject(ciProjectDTO);
                console.log('delete ciProject');
            }
        }
    ]);