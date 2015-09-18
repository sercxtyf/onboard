/**
 * Created by Nettle on 2015/7/6.
 */

angular.module('data')
    .service('topicDataService', ['url', '$http', '$q', 'filterFilter', 'baseService',
        function (url, $http, $q, filterFilter, baseService) {

            var modelType = 'topic';

            this.Topic = function(){
                this.getApi = function(){
                    var api = url.projectApiUrl(this.projectId, this.companyId) + "/topics/";
                    if(this.id){
                        api = api + this.id;
                    }
                    return api;
                };
                
                this.afterinit = function(){
                    var self = this;
                    if (this.refType == 'story' || this.refType == 'bug') {
                        var ref = baseService.addItem({
                            type: this.refType,
                            id: this.refId
                        });
                        ref.addObserver(self);
                    }
                };
                
                this.afterUpdateItem = function(data){
                    var self = this;
                    if (self.refType == 'bug' && data.title != data.origin.title) {
                        self.title = data.title;
                    }
                    if (self.refType == 'story' && data.description != data.origin.description) {
                        self.title = data.description;
                    }
                };

                this.getDTO = function(){
                    return {
                        companyId: this.companyId,
                        created: this.created,
                        creator: this.creator,
                        deleted: this.deleted,
                        excerpt: this.excerpt,
                        id: this.id,
                        lastUpdatorId: this.lastUpdatorId,
                        lastUpdatorName: this.lastUpdatorName,
                        numOfComment: this.numOfComment,
                        projectId: this.projectId,
                        refId: this.refId,
                        refType: this.refType,
                        stick: this.stick,
                        title: this.title,
                        updated: this.updated,
                        updator: this.updator
                    };
                };
            };

            this.Topic.prototype = baseService.Item;
            baseService.Items[modelType] = this.Topic;


            this.findIterationById = function(id){
                return baseService.getItem(modelType, id);
            };

            this.registerTopics = function(topicDTOs){
                for(var index = 0; index < topicDTOs.length; index++){
                    var topic = baseService.addItem(topicDTOs[index]);
                    topicDTOs[index] = topic;
                }
                return topicDTOs;
            };

        }]);
