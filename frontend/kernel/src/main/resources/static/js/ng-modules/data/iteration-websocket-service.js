/**
 * Created by Nettle on 2015/6/25.
 */
angular.module('data')
    .service('iterationWebsocketService', ['storyService', 'bugService',
        function(storyService, bugService) {
            this.update = function(data) {
                //storyService.webSocketUpdateStory(story);
                console.log(data);
            };
        }
    ]);