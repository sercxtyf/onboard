/**
 * Created by Dongdong Du on 12/26/2014.
 */

angular.module('data')
    .service('documentWebSocketService', ['documentService', function(documentService) {

        this.add = function(commentDTO) {
            documentService.webSocketCreateDocument(commentDTO);
        };
        this.update = function(commentDTO) {
            documentService.webSocketUpdateDocument(commentDTO);
        };
        this.delete = function(commentDTO) {
            documentService.webSocketDeleteDocument(commentDTO);
        };

    }]);
