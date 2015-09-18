angular.module('data')
    .service('storyDataService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 'todolistService',
        'baseService',
        function (url, $http, $q, filterFilter, todoService, filterFilter, todolistService, baseService) {

            var modelType = 'story';
            var initCont = '<ul><li>作为.....</li><li>我需要能.....</li><li>这样我就可以.....</li></ul>';
            var initAcce = '<p ng-show="story.acceptanceLevel == null">该需求验收时需要检查：</p><ol><li></li><li></li>'+
                '<li></li></ol>';

            this.Story = function(){
                this.getApi = function(){
                    var api = url.projectApiUrl(this.projectId, this.companyId) + "/stories/";
                    if(this.id){
                        api = api + this.id;
                    }
                    return api;
                };
                this.afterinit = function(){
                    this.boardables = this.boardableDTOs ? this.boardableDTOs : [];
                    this.childStoryDTOs = this.childStoryDTOs ? this.childStoryDTOs : [];
                    if(this.acceptanceLevel == null || this.acceptanceLevel == ''){
                        this.acceptanceLevel = initAcce;
                    }
                    if(this.content == null || this.content == ''){
                        this.content = initCont;
                    }
                };
                this.getDTO = function(){
                    return {
                        acceptanceLevel: this.acceptanceLevel,
                        companyId: this.companyId,
                        completable: this.completable,
                        completed: this.completed,
                        completedTime: this.completedTime,
                        content: this.content,
                        deleted: this.deleted,
                        description: this.description,
                        id: this.id,
                        parentStoryId: this.parentStoryId,
                        position: this.position,
                        priority: this.priority,
                        projectId: this.projectId,
                        pre: this.pre,
                        post: this.post
                    };
                };
                this.type = modelType;
            };
            this.Story.prototype = baseService.Item;
            
            baseService.Items[modelType] = this.Story;

            this.findById = function(id){
                return baseService.getItem(modelType, id);
            };
            
            this.register = function(DTOs){
                for(var index = 0; index < DTOs.length; index++){
                    var story = baseService.addItem(DTOs[index]);
                    DTOs[index] = story;
                }
                return DTOs;
            };

        }]);