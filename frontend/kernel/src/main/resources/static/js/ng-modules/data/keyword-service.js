/**
 * Created by Nettle on 2015/6/12.
 */


angular.module('data')
    .service('keywordService', ['url', '$http', '$q', 'dataProxy', 'user', function (url, $http, $q, dataProxy, user) {

        deal = function(results) {
            //console.log(results);
            var matrix = results[0];
            var keys = results[1];
            var tot = new Array();
            var total = 0;
            var userValue = new Array();
            for(var i = 0; i < matrix.length; ++i) {
                tot[i] = 0;
                userValue[i] = 0;
                for(var j = 0; j < matrix[i].length; ++j)
                    tot[i] += matrix[i][j].times;
                total += tot[i];
            }

            var maxPos = 0, maxValue = 0, totTimes = 0;
            for(var i = 0; i < keys.length; ++i) {
                maxPos = 0;
                maxValue = 0;
                totTimes = 0;
                for(var j = 0; j < matrix.length; ++j) {
                    var value = 0;
                    for(var k = 0; k < matrix[j].length; ++k)
                        if(keys[i] === matrix[j][k].keyword) {
                            totTimes += matrix[j][k].times;
                            value = matrix[j][k].times;
                            if(value > maxValue || maxPos == 0) {
                                maxValue = value;
                                maxPos = j;
                            }
                            break;
                        }
                }
                if(maxValue > 0) {
                    userValue[maxPos] += maxValue / parseFloat(total) / (maxValue / parseFloat(totTimes));
                }
            }

            maxPos = 0;
            maxValue = 0;
            for(var i = 0; i < userValue.length; ++i)
                if(userValue[i] > maxValue || maxPos == 0) {
                    maxValue = userValue[i];
                    maxPos = i;
                }

            return results[2][maxPos];
        }

        this.getCompanyUsersKeywords = dataProxy( function(updata, projectId, companyId) {
            return user.getProjectUsers(false, projectId).then(function(users){
                users.slice();
                users.splice(0, 0, {name:'未分配', avatarUrl: url.defaultAvatarUrl, id: 0});
                var ask = new Array();
                for (var i = 0; i < users.length; ++i)
                    ask[i] = $http.get('api/'+ companyId + '/user/'+ users[i].id + '/keywords?limit=-1');
                return $q.all(ask).then( function(results) {
                    return results.map( function(data) {
                        return data.data;
                    });
                });
            });
        });

        this.getTodoKeywords = function(todoId, projectId, companyId) {
            return $http.get('api/'+ companyId+'/projects/'+projectId+'/todos/'+todoId+'/keywords').then( function(data) {
               return data.data;
            });
        }

        this.getStepKeywords = function(stepId, projectId, companyId) {
            return $http.get('api/'+ companyId+'/projects/'+projectId+'/steps/'+stepId+'/keywords').then( function(data) {
                return data.data;
            });
        }

        this.getBugKeywords = function(bugId, projectId, companyId) {
            return $http.get('api/'+ companyId+'/projects/'+projectId+'/bugs/'+bugId+'/keywords').then( function(data) {
                return data.data;
            });
        }
        this.getTodoRecommend = function(todoId, projectId, companyId) {
            return ($q.all([
                this.getCompanyUsersKeywords(false, projectId, companyId),
                this.getTodoKeywords(todoId, projectId, companyId),
                user.getProjectUsers(false, projectId)
            ]).then(function (results) {
                return deal(results);
            }));
        };

        this.getStepRecommend = function(stepId, projectId, companyId) {
            return ($q.all([
                this.getCompanyUsersKeywords(false, projectId, companyId),
                this.getStepKeywords(stepId, projectId, companyId),
                user.getProjectUsers(false, projectId)
            ]).then(function(results) {
                return deal(results);
            }));
        };

        this.getBugRecommend = function(bugId, projectId, companyId) {
            return ($q.all([
                this.getCompanyUsersKeywords(false, projectId, companyId),
                this.getBugKeywords(bugId, projectId, companyId),
                user.getProjectUsers(false, projectId)
            ]).then(function(results) {
                return deal(results);
            }));
        };
    }]);
