/**
 * Created by Nettle on 2015/7/6.
 */

angular.module('data')
    .service('discussionDataService', ['url', '$http', '$q', 'filterFilter', 'baseService',
        function (url, $http, $q, filterFilter, baseService) {
            var modelType = 'discussion';

            this.Discussion = function(){
                this.getApi = function(){
                    var api = url.projectApiUrl(this.projectId, this.companyId) + "/discussions/";
                    if(this.id){
                        api = api + this.id;
                    }
                    return api;
                };
                this.afterinit = function(){
                };

                this.getDTO = function(){
                    return {
                        attachments: this.attachments,
                        bcId: this.bcId,
                        comments: this.comments,
                        companyId: this.companyId,
                        content: this.content,
                        created: this.created,
                        creatorId: this.creatorId,
                        creatorName: this.creatorName,
                        deleted: this.deleted,
                        discardAttachments: this.discardAttachments,
                        id: this.id,
                        projectId: this.projectId,
                        subject: this.subject,
                        subscribers: this.subscribers,
                        updated: this.updated
                    };
                };
            };

            this.Discussion.prototype = baseService.Item;
            baseService.Items[modelType] = this.Discussion;


            this.findIterationById = function(id){
                return baseService.getItem(modelType, id);
            };

            this.registerDiscussions = function(discussionDTOs){
                for(var index = 0; index < discussionDTOs.length; index++){
                    var discussion = baseService.addItem(discussionDTOs[index]);
                    discussionDTOs[index] = discussion;
                }
                return discussionDTOs;
            };

        }]);