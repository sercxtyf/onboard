/**
 * Created by SourceDark on 2015/6/1.
 */
angular.module('util')
    .service('utilService', [function () {
       this.contains = function(array, object){
           for(var i = 0; i < array.length; i++){
               if(object.id === array[i].id){
                   return true;
               }
           }
           return false;
       }
    }]
);