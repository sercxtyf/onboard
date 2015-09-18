// 上传文件子页面
angular.module('upload')
    .config(['$stateProvider',
        function($stateProvider) {
            $stateProvider
                .state('company.project.uploads', {
                    url        : '/uploads',
                    templateUrl: 'uploads.html',
                    controller : 'uploadsCtrl'
                });
            //.state('company.project.upload-detail', {
            //    url        : '/uploads/{uploadId:[0-9]+}',
            //    templateUrl: 'uploads.html'
            //});
        }
    ])
    .controller('uploadsCtrl', ['$scope', '$http', '$upload', '$rootScope', 'iasPager', '$state', 'drawer', 'url', 'uploadsService',
        '$modal', function($scope, $http, $upload, $rootScope, iasPager, $state, drawer, url, uploadsService, $modal) {
            $scope.projectId = $state.params.projectId;
            $scope.companyId = $state.params.companyId;
            $scope.downloadLink = function(attachment_id) {
                return [url.projectApiUrl($scope.projectId, $scope.companyId), 'attachments', attachment_id, 'download'].join('/');
            };
            $scope.attachmentImageUrl = url.projectApiUrl($scope.projectId, $scope.companyId) + '/attachments/image';
            $scope.busy = false;

            $scope.initUploads = function() {
                uploadsService.InitUploadsList($scope.projectId, $scope.companyId).then(function(data) {
                    $scope.attachments = data.attachmentDTOs;
                    $scope.totalCounts = uploadsService.getTotalCounts();
                    pagination();
                });
            };
            $scope.initUploads();

            $scope.hasNext = function() {
                return ($scope.attachments && $scope.totalCounts && ($scope.attachments.length < $scope.totalCounts));
            };

            $scope.displayNoMorePage = function() {
                return ($scope.attachments && $scope.attachments.length > 20 && !$scope.hasNext());
            };

            $scope.addMoreUploads = function() {
                if($scope.hasNext() && $scope.busy === false) {
                    //console.log("getting data of page " + $scope.nextPage);
                    $scope.busy = true;
                    uploadsService.getUploadsByStartNum($scope.attachments.length, $scope.projectId, $scope.companyId).then(function(data) {
                        $scope.busy = false;
                        pagination();
                    });
                }
            };

            var pagination = function() {
                // 自己实现的分页
                var $page = $('#uploads table');
                var $pageContainer = $page.scrollParent();
                var margin = 20;
                var scrollTrigger = 0.95;
                var scrollFun = function() {
                    var wintop = $pageContainer.scrollTop(),
                        docheight = $page.height(),
                        winheight = $pageContainer.height(),
                        _trigger = wintop / (docheight - winheight + margin);
                    if(_trigger > scrollTrigger) {
                        if($scope.busy === false) {
                            $scope.addMoreUploads();
                            $pageContainer.unbind('scroll', scrollFun);
                        }
                    }
                };
                $pageContainer.scroll(scrollFun);
            };

            $scope.uploads = uploadsService.getUploads();

            // 上传文件
            $scope.uploadFile = function($files) {

                //show loadprogressbar
                $("#uploadProgress").show();
                uploadsService.uploadFile($files, $scope.projectId, $scope.companyId)
                    .then(function(data) {
                        $("#uploadProgress").hide();
                        if(data.stat && data.stat === "error") {
                            $scope.stat = 'error';
                            $scope.msg = data.msg;
                            return;
                        }
                        $scope.stat = 'success';
                        $scope.msg = '上传成功！';
                    });

            };

            $scope.deleteAttachment = function(attachment) {
                if(confirm('确认删除该文件?')) {
                    $http.delete(url.projectApiUrl($scope.projectId, $scope.companyId) + "/attachments/" + attachment.id).error(function() {
                        confirm("删除失败！");
                    });
                    $scope.attachments.splice($scope.attachments.indexOf(attachment), 1);
                }
            };

            $scope.quitUpload = function(upload) {
                $scope.uploads.splice($scope.uploads.indexOf(upload), 1);
            };

            $scope.showAttachmentDetail = function(attachment) {
                $scope.tempItems = [attachment, $scope.attachments];
                $modal.open({
                    templateUrl: 'attachmentDetail.html',
                    controller : 'attachmentInfoCtrl',
                    size       : 'lg',
                    resolve    : {
                        items: function() {
                            return $scope.tempItems;
                        }
                    }
                });

            };

        }]);
