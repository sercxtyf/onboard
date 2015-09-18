/**
 * Created by harttle on 11/29/14.
 */

// 实现了缓存和单次请求限制
// 1. update 参数
//    当update为false且缓存hit时，会使用缓存。当update为false时，总会刷新缓存。
// 2. 缓存以resolver参数为key
//    按照resolver提供的参数建立多维缓存数组，参数一致且有数据时才会hit
// 2. 单次请求
//    同时的多次调用只会发生一次请求，当请求到达时所有promise被解析。

// @param resolver: 解析器，当需要更新资源时被调用，需要返回一个promise
// @return: function(update, args...) update为是否强制更新，args为resolver参数列表。

// usage:
// var getSomething = dataProxy(function(update, arg1, arg2, ...){...});
//
// getSomething(update, arg1, arg2, ...)
//  .then(function(data){
//      // process your data...
// })

angular.module('data')

    .factory('dataProxy', ['$q', 'url', function($q, url) {

        return function(resolver){

            var resources={};

            function Resource(){
                // empty cache, ready state, empty callbacks
                this.cache = null;
                this.state = 'ready';
                this.cbs = [];
            }

            function getResourceByKey(key){
                if(!resources[key]) resources[key] = new Resource();
                return resources[key];
            }

            function filterEmpty(list){
                return list.filter(function(arg){
                    return arg !== null && arg !== undefined;
                });
            }

            return function(update){
                var arglist = Array.prototype.slice.call(arguments, 1);
                arglist = filterEmpty(arglist);

                var key = arglist.join('.');
                var resource = getResourceByKey(key);

                // deferred object
                var deferred = $q.defer();

                // cache hit
                if(!update && resource.cache){
                    deferred.resolve(resource.cache);
                    return deferred.promise;
                }

                // cache miss
                if(resource.state == 'ready'){
                    resource.state = 'pending';

                    return resolver.apply(this, arguments).then(function(data){
                        resource.cache = data;
                        resource.state = 'ready';

                        while(resource.cbs.length) resource.cbs.pop()();
                        return data;
                    });
                }
                else{
                    resource.cbs.push(function(){ deferred.resolve(resource.cache);});
                    return deferred.promise;
                }
            }
        };
    }]);
