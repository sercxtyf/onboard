/**
 * Created by Nettle on 2015/4/29.
 */

angular.module('stories', ['angularStoriesTree'])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('company.project.stories', {
                url: '/stories',
                templateUrl: 'stories.html',
                controller: 'storiesCtrl'
            })
            .state('company.project.stories.open', {
                url: '/open',
                templateUrl: 'openStories.html',
                controller: 'storiesCtrl'
            })
            .state('company.project.stories.archived', {
                url: '/archived',
                templateUrl: 'archivedStories.html',
                controller: 'storiesCtrl'
            })
    }
    ])
    .controller('storiesCtrl', ['$scope', '$rootScope', '$http', '$state', '$timeout','storyService',
        function ($scope, $rootScope, $http, $state, $timeout,storyService) {
            $scope.$state = $state;
            if($state.is("company.project.stories")){
                $state.go("company.project.stories.open");
            }
            $scope.noStory = false;
            //gonna add web socket here
            storyService.getOpenStories(true).then( function(data) {
                $scope.stories = [{
                    content: '',
                    id: 0,
                    position: 0,
                    priority: 3,                    
                    completed: false,
                    childStoryDTOs: data
                }];
                if (data.length < 1) $scope.noStory = true;
                //console.log($scope.openStories);
            });
            storyService.getAllStories(true).then( function(data) {
                $scope.allStories = [{
                    content: '',
                    id: 0,
                    position: 0,
                    priority: 3,
                    completed: true,
                    childStoryDTOs: data
                }];
                //console.log($scope.openStories);
            });

            $scope.newSubItem = function(story) {
                var childStoriesNum = story.childStoryDTOs.length === undefined?0:story.childStoryDTOs.length;
                var nodeData = story;
                var newStory = {
                    description: '',
                    priority: 3,
                    parentStoryId:nodeData.id,
                    position:nodeData.id+0.1*childStoriesNum
                };
                nodeData.childStoryDTOs.push(newStory);
                story.uncompletedChildStoryCount ++;
            };

        }

]);

