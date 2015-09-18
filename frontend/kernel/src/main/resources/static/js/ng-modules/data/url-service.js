/**
 * Created by harttle on 11/29/14.
 */

// url 服务。负责生成和提供项目的url，尤其是controller之间共享的url。便于之后的API整体重构。
// Tips:
// 1. 提供的url结尾不带“/“为好，便于此后的join拼接。
// 2. 注意url与uri概念的区别，我们用到的属于url范畴，规范一下命名。
angular.module('data')

    .service('url', ['upyun', '$location', function url(upyun, $location) {

        // default companyId & projectId
        this.companyId = function(){
            var params = /^\/teams\/(\d+)/.exec($location.path());
            if(params && params.length >= 1)
                return params[1];
            return null;
        };
        this.projectId = function(){
            var params = /^\/teams\/(\d+)\/projects\/(\d+)/.exec($location.path());
            if(params && params.length >= 2 )
                return params[2];
            return null;
        };
        this.iterationId = function(){
            var params = /^\/teams\/(\d+)\/projects\/(\d+)\/iterations\/(\d+)/.exec($location.path());
            if(params && params.length >= 3 )
                return params[3];
            return null;
        };

        // 项目修改url
        this.projectEditUrl = function (isNew) {
            return isNew ?
                ['api', this.companyId(), 'projects' ].join('/') :
                ['api', this.companyId(), 'projects', this.projectId() ].join('/');
        };

        // 项目API url，谢睿的API大多位于此处
        this.projectApiUrl = function (p, c) {
            return ['', 'api', c || this.companyId(), 'projects', p || this.projectId()].join('/');
        };

        // 代码页
        this.repoUrl = function (p, c) {
            return [this.projectApiUrl(p, c), 'repository'].join('/');
        };

        // 团队信息
        this.teamApiUrl = function(c){
            return ['', 'api', c||this.companyId()].join('/');
        };

        // 团队成员信息
        this.companyUserUrl = function (c) {
            return [this.teamApiUrl(c), "users"].join('/');
        };

        // 项目成员信息
        this.projectUserUrl = function (p, c) {
            return [this.projectApiUrl(p, c), 'users'].join('/');
        };

        // 当前用户信息
        this.currentUserUrl = function () {
            return '/api/currentUser';
        };

        // up云
        this.defaultVersion = '!avatar110';
        this.imageStore = upyun.protocol + upyun.host;
        this.avatarUrl = function (avatar) {
            return avatar ? upyun.protocol + upyun.host + avatar + this.defaultVersion : '';
        }.bind(this);

        this.defaultAvatarUrl = this.avatarUrl('/avatar/default.png');
    }]);
