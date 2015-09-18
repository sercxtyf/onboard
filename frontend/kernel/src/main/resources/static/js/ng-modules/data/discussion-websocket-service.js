/**
 * Created by Dongdong Du on 12/26/2014.
 */

angular.module('data')
    .service('discussionWebSocketService', ['discussionService', function(discussionService) {

        this.add = function(discussionDTO) {
            discussionService.webSocketCreateDiscussion(discussionDTO);
        };
        this.update = function(discussionDTO) {
            discussionService.webSocketUpdateDiscussion(discussionDTO);
        };
        this.delete = function(discussionDTO) {
            discussionService.webSocketDeleteDiscussion(discussionDTO);
        };

    }]);
