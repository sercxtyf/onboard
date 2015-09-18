/**
 * Created by pkutxq on 15-5-22.
 */
angular.module('stories')
    .controller('archivedStoriesCtrl', ['$scope', '$http', 'storyService', function ($scope, $http, storyService) {

        storyService.getArchivedStories(true).then(function (archivedStories) {
            $scope.archivedStories = archivedStories;
        }, function (error) {
            console.log('获取需求列表失败');
        });

        var tempOpenStories = [];

        $scope.reopenStory = function(story){
            storyService.reopenStory(story).then(function(response){
                storyService.openStories().then(function(openStories){
                    tempOpenStories = openStories;
                    tempOpenStories.pop(story);
                });

                console.log(response);
            })
        }

    }]);