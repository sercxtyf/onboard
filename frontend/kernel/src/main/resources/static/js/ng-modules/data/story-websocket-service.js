angular.module('data')
    .service('storyWebsocketService', ['storyService',
        function(storyService) {
            this.add = function(story){
                storyService.webSocketCreateStory(story);
            }
            this.update = function(story) {
                storyService.webSocketUpdateStory(story);
                console.log("story updated");
            };
            this.delete = function(story) {
                storyService.webSocketDeleteStory(story);
                console.log("story deleted");
            };
        }
    ]);