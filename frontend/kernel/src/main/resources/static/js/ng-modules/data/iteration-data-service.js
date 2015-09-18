angular.module('data')
    .service('iterationDataService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 'todolistService',
        'baseService',
        function (url, $http, $q, filterFilter, todoService, filterFilter, todolistService, baseService) {

            var modelType = 'iteration';

            this.Iteration = function(){
                this.getApi = function(){
                    var api = url.projectApiUrl(this.projectId, this.companyId) + "/iterations/";
                    if(this.id){
                        api = api + this.id;
                    }
                    return api;
                };
                this.afterinit = function(){
                     this.boardables = [];
                     for(var i = 0; i < this.iterables.length; i++){
                         if(this.iterables[i].boardables){
                             for(var j = 0; j < this.iterables[i].boardables.length; j++){
                                 this.boardables.push(this.iterables[i].boardables[j]);
                             }
                         }else{
                             this.boardables.push(this.iterables[i]);
                         }
                     }
                 };
                 
                 this.getDTO = function(){
                     return {
                         endTime: this.endTime ? this.endTime : null,
                         name: this.name,
                         projectId: this.projectId,
                         startTime: this.startTime ? this.startTime : null,
                         status: this.status,
                         summary: this.summary
                     };
                 };

                 this.complete = function(){
                     this.status = "completed";
                     return this.update();
                 };
                
            };
            this.Iteration.prototype = baseService.Item;
            baseService.Items[modelType] = this.Iteration;

            
            this.findIterationById = function(id){
                return baseService.getItem(modelType, id);
            };
            
            this.registeriterations = function(iterationDTOs){
                for(var index = 0; index < iterationDTOs.length; index++){
                    var iteration = baseService.addItem(iterationDTOs[index]);
                    iterationDTOs[index] = iteration;
                }
                return iterationDTOs;
            };

        }]);