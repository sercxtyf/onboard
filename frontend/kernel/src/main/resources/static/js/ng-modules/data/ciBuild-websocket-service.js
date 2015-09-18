/**
 * Created by pkutxq on 15-5-9.
 */
angular.module('data')
    .service('ciBuildWebsocketService',['ciService',
        function(ciService){
            this.add = function(ciBuildDTO){
                ciService.webSocketCreateCIBuild(ciBuildDTO);
                console.log('add ciBuild');
            };
            this.update = function(ciBuildDTO){
                ciService.webSocketUpdateCIBuild(ciBuildDTO);
                console.log('update ciBuild');
            };
            this.delete = function(ciBuildDTO){
                ciService.webSocketDeleteCIBuild(ciBuildDTO);
                console.log('delete ciBuild');
            }
        }
    ]);