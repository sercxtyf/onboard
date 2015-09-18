/**
 * Created by Nettle on 2015/6/29.
 */
/**
 * Created by Nettle on 2015/6/12.
 */


angular.module('data')
    .service('collectionService', ['url', '$http', '$q', 'dataProxy', function (url, $http, $q, dataProxy) {
        var collections = [];
        this.getCollectionList = dataProxy( function(updata) {
            return $http.get('api/collections').then( function(response) {
                console.log('1111');
                collections = response.data.map( function(colle) {
                    var str = colle.attachType;
                    colle['is' + str.slice(0, 1).toUpperCase() + str.slice(1, str.length)] = true;
                    return colle;
                });
                return collections;
            });
        });

        this.isCollected = function(attachType, attachId) {
            return $http.get('api/collections?attachType='+attachType+'&attachId='+attachId).then( function(response) {
                return response.data;
            });
        };

        this.addCollection = function(attachType, attachId) {
            return $http.post('api/collections', {
                attachType: attachType,
                attachId: attachId
            }).then( function(response) {
                var str = response.data.attachType;
                response.data['is' + str.slice(0, 1).toUpperCase() + str.slice(1, str.length)] = true;
                if (collections == null) collections = [];
                collections.push(response.data);
                return response.data;
            });
        };

        this.delCollection = function(colleId) {
            return $http.delete('api/collections/'+colleId).then( function(response) {
                for (var i = 0; i < collections.length; ++i)
                    if (collections[i].id == colleId) {
                        collections.splice(i, 1);
                        break;
                    }
                return collections;
            });
        }
    }]);
