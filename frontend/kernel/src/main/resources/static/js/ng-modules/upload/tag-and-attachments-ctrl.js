/**
 * Created by TXQ on 2014/11/22.
 */
angular.module('upload')
    .config(['$stateProvider',
        function($stateProvider) {
            $stateProvider
                .state('company.project.tags', {
                    url        : '/tags/{tagId:[0-9]+}',
                    templateUrl: 'tagAndAttachments.html',
                    controller : 'tagAndAttachmentsCtrl'
                })
        }
    ])
    .controller('tagAndAttachmentsCtrl', ['$scope', '$http', '$state', 'iasPager', 'url', '$modal',
        function($scope, $http, $state, iasPager, url, $modal) {
            var changeContentType = function(attachments) {
                for(var i = 0; i < attachments.length; i++) {
                    if(attachments[i].contentType.match("image")) {
                        attachments[i].contentType = "image";
                    }
                }
                return attachments;
            };
            //滚动刷新
            $scope.pager = new iasPager(function(count, last, pageCount) {
                return [url.projectApiUrl(), '/tags/', $state.params.tagId, '?page=', pageCount + 1].join("");
            }, function(data) {
                $scope.currentTag = data.tagDto;
                return changeContentType(data.attachments);
            });

            $scope.showEditTag = function(current_tag) {
                $scope.currentTagName = current_tag.tagname;
                $("form.editTagForm").show();
            };
            //取消编辑tag
            $scope.hideEditTag = function() {
                $("form.editTagForm").hide();
                return false;
            };
            //修改tag
            $scope.updateByName = function() {
                var updateTag = {
                    tagname: $scope.currentTag.tagname
                };
                $http.post(url.projectApiUrl() + "/tags/" + $scope.currentTag.id, updateTag).success(function(data) {
                    $("form.editTagForm").hide();
                }).error(function() {
                    confirm("修改失败！");
                    $("form.editTagForm").hide();
                });
            };

            $scope.getProjectUri = url.projectApiUrl() + '/';

        }]);