angular.module('data')
    .service('storyService', ['url', '$http', '$q', 'storyDataService', 'dataProxy', 'utilService',
                              function (url, $http, $q, storyDataService, dataProxy, utilService) {

        this.priorities = [
            {desc: '非常紧急', value: 1},
            {desc: '紧急', value: 2},
            {desc: '重要', value: 3},
            {desc: '普通', value: 4},
            {desc: '可忽略', value: 5}
        ];
        
        var openStories = [];

        this.getOpenStories = dataProxy(function(update, projectId, companyId){
            var _storiesUrl = [url.projectApiUrl(projectId, companyId), '/stories'].join("");
            return $http.get(_storiesUrl + "?type=uncompleted").then(function (response) {
                return openStories = storyDataService.register(response.data);
            });
        });

        this.getArchivedStories = dataProxy(function(update, projectId, companyId){
            var _storiesUrl = [url.projectApiUrl(projectId, companyId), '/stories'].join("");
            return $http.get(_storiesUrl + "?type=completed").then(function (response) {
                var results = storyDataService.register(response.data);
                return results;
            });
        });

          this.getAllStories = dataProxy(function(update, projectId, companyId){
              var _storiesUrl = [url.projectApiUrl(projectId, companyId), '/stories'].join("");
              return $http.get(_storiesUrl).then(function (response) {
                  return openStories = storyDataService.register(response.data);
              });
          });

        this.getStoryById = dataProxy(function (update, projectId, companyId, storyId) {
            var _storiesUrl = [url.projectApiUrl(projectId, companyId), '/stories'].join("");
            return $http.get(_storiesUrl + "/" + storyId).then(function (response) {
                var storys = [response.data];
                return storyDataService.register(storys)[0];
            });
        });

        this.completeStory = function (story) {
            story.completed = true;
            return $http.put(story.getApi() + "/complete").then(function () {
                return "success";
            })
        };

        this.reopenStory = function (story) {
            story.completed = false;
            return $http.put(story.getApi() + "/reopen").then(function (response) {
                return story;
            });
        };

        this.webSocketCreateStory = function(story) {
            var story = storyDataService.register([story])[0];
            if(story.parentStoryId === 0){
                if(!utilService.contains(openStories, story)){
                    openStories.push(story);
                }
            }else{
                var parentStory = storyDataService.findById(story.parentStoryId);
                if(!utilService.contains(parentStory.childStoryDTOs, story)){
                    parentStory.childStoryDTOs.push(story);
                }
            }
        };
        this.webSocketUpdateStory = function(story) {
            recursiveReplaceStory(story);
        };
        
        this.webSocketDeleteStory = function(story) {
            recursiveDeleteStory(story);
        };
        
        var recursiveReplaceStory = function (story) {
            storyDataService.register([story])[0];
        };
        
        var recursiveDeleteStory = function (stories, story) {
            story = storyDataService.register([story])[0];
            var parentStory = storyDataService.findById(story.parentStoryId);
            parentStory.childStoryDTOs.splice(parentStory.childStoryDTOs.indexOf(story), 1);
        };

        this.getNew = function(story, description){
            var newStory = new storyDataService.Story().init({
                description: description,
                position: story.position,
                priority: story.priority,
                parentStoryId: story.id
            });
            var deferred = $q.defer();
            deferred.resolve(newStory);
            return deferred.promise;
        };
    }]);
