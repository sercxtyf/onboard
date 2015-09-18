angular.module('data')
    .service('userDataService', ['url', '$http', '$q', 'filterFilter', 'todoService', 'filterFilter', 'todolistService',
        'baseService',
        function (url, $http, $q, filterFilter, todoService, filterFilter, todolistService, baseService) {

            var modelType = 'user';
            this.User = function(){
                this.getApi = function(){
                    return "/api/account";
                };
                this.afterinit = function(){
                    this.avatarUrl = url.avatarUrl(this.avatar);
                }
            };
            this.User.prototype = baseService.Item;
            baseService.Items[modelType] = this.User;

            this.findById = function(id){
                return baseService.getItem(modelType, id);
            };

            this.register = function(DTOs){
                for(var index = 0; index < DTOs.length; index++){
                    var item = baseService.addItem(DTOs[index]);
                    DTOs[index] = item;
                }
                return DTOs;
            };

        }]);