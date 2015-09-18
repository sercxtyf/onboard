/**
 * Created by harttle on 12/11/14.
 */

angular.module('data')
    .service('company', ['$q', 'url', '$http', 'dataProxy', function ($q, url, $http, dataProxy) {

        // 得到团队信息
        // warning: 不要尝试使用该函数得到项目信息！如需要项目列表，请使用 getProjects()
        this.getCompanyInfo = dataProxy(function(update, companyId){
            return $http.get(url.teamApiUrl(companyId)).then(function (response) {
                return response.data;
            });
        });

        // 获得团队的项目列表
        this.getProjects = dataProxy(function(update, companyId){
            return this.getCompanyInfo(update, companyId).then(function(companyInfo){
                return companyInfo.projects;
            })
        });

        //this.getProjects = function(update){
        //    return this.getCompanyInfo(update).then(function(companyInfo){
        //        return companyInfo.projects;
        //    })
        //};

        this.getProjectById = function(update, id){
            return this.getProjects(update).then(function(ps){
                var matched = ps.filter(function(p){ return p.id == id; });
                return matched.length>0 ? matched[0] : null;
            });
        };

        // 获得团队列表
        var teams = {};
        this.getTeams = function(update){
            if(update || !teams._loaded){
                teams._loaded = true;

                return $http.get('/api/').then(function(response){
                    return teams = response.data;
                });
            }
            else {
                var deferred = $q.defer();
                deferred.resolve(teams);
                return deferred.promise;
            }
        };

        // 创建团队
        this.createTeam = function(data){
            return $http.post('/api/',data).then(function(response){
                var team = response.data;
                teams.push(team);
                return team;
            });
        }

    }]);