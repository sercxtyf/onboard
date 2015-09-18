angular.module('upload')
    .controller('uploadDetailsCtrl', ['$scope', '$http', '$state', '$rootScope', '$document', 'tab', 'url', 'uploadsService', 'drawer',
        function($scope, $http, $state, $rootScope, $document, tab, url, uploadsService, drawer) {
            $scope.targetId = $scope.id;
            $scope.uploadIndex = $scope.index;

            uploadsService.getUploadById($scope.targetId, $scope.projectId, $scope.companyId).then(function(data) {

                $scope.deleted = data.uploadDTO.deleted;
                $scope.users = data.usersInProject;
                $scope.project = data.project;
                $scope.upload = data.uploadDTO;

                $scope.$broadcast('updateTab', {
                    attachType: "upload",
                    attachId  : $scope.id,
                    projectId : $scope.upload.projectId,
                    companyId : $scope.upload.companyId
                });

                $scope.uploadMapAttach = data.uploadAttachmentDTO;
                for(var i = 0; i < $scope.users.length; i++) {
                    if($scope.users[i].id == $scope.upload.creatorId) {
                        $scope.uploader = $scope.users[i];
                        break;
                    }
                }
                if($scope.uploadMapAttach.contentType.match('image')) {
                    $scope.uploadContentType = "image";
                } else {
                    $scope.uploadContentType = $scope.uploadMapAttach.contentType;
                }
            });

            $scope.delete = function(upload,size) {
                if(confirm('确认删除该文件?')) {
                    uploadsService.deleteUploadById(upload,size ).then(function(ok) {
                        drawer.close();
                    }, function(error) {
                        console.log('Error when deleting upload!');
                    });
                }
            };
            $scope.uploadTabs = [tab.getTabInfo("comment", true)];

            $scope.imageStore = "http://teamforge.b0.upaiyun.com";

            $scope.attachmentImageUrl = ['/api/', $scope.companyId, '/projects/', $scope.projectId, '/attachments/image'].join("");

            $scope.recover = function() {
                if(confirm('确认恢复该文件?')) {
                    $http.post(url.projectApiUrl() + '/uploads/' + $state.params.uploadId + '/recover').success(function() {

                    })
                }
            };
            $scope.attachmentDownloadLink = function(attachmentId) {
                return [url.projectApiUrl($scope.projectId,$scope.companyId), 'attachments', attachmentId, 'download'].join("/");
            };
        }
    ]);