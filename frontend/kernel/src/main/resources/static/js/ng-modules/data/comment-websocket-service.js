/**
 * Created by Dongdong Du on 12/24/2014.
 */

angular.module('data')
    .service('commentWebSocketService', ['commentService', function(commentService) {

        this.add = function(commentDTO) {
            commentService.updateAllComments(commentDTO);
        };
        this.update = function(commentDTO) {
            commentService.updateAllComments(commentDTO);
        };
        this.delete = function(commentDTO) {
            commentService.updateAllComments(commentDTO);
        };

    }]);
