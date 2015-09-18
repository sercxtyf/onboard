/**
 * Created by TXQ on 2014/11/19.
 */

// 附件详情（包括各种地方来的附件）
angular.module('upload').controller('attachmentInfoCtrl', ['$scope', '$http', 'url','items','$modalInstance','uploadsService',
    function ($scope, $http, url, items, $modalInstance,uploadsService) {

        $scope.updateData = function(){
            //判断是否为图片
            if ($scope.attachment.contentType.match("image")) {
                $scope.getAttachmentUrl = $scope.getUrl + 'attachments/image/' + $scope.attachment.id;
                $scope.isImage = true;
            } else if ($scope.attachment.contentType.match("text")) {
                $http.get($scope.getUrl + 'attachments/text/' + $scope.attachment.id).success(function (data) {
                    $scope.textContene = data;
                    $scope.isText = true;
                })
            } else if ($scope.attachment.contentType.match("pdf")) {
                var myPDF = new PDFObject({
                    url: $scope.getUrl + 'attachments/pdf/' + $scope.attachment.id,
                    height: "750px"
                });
                myPDF.embed("pdfContents");
                $scope.isPdf = true;
            } else {
                $scope.getAttachmentUrl = "/static/img/attachment-icon/file_extension_others.png";
                $scope.others = true;
            }
        };
        //show attachment info
        $scope.tagList = [];
        $scope.attachment = items[0];
        $scope.downloadLink = ['/api', $scope.attachment.companyId, 'projects',$scope.attachment.projectId, 'attachments', $scope.attachment.id, 'download'].join('/');

        $scope.isImage = false;
        $scope.isText = false;
        $scope.isPdf = false;
        $scope.others = false;
        $scope.currentAttachment = $scope.attachment;
        $scope.tagList = $scope.attachment.tagDtos;
        $scope.getUrl = url.projectApiUrl($scope.currentAttachment.projectId, $scope.currentAttachment.companyId) + "/";

        $scope.uploadItems = items[1];

        //隐藏modal
        $scope.hideAttachmentInfo = function () {
            $modalInstance.dismiss('cancel');
        };
        /*$scope.$on("showAttachmentDetail", function (event, data) {
            if (data != undefined && data.attachment != undefined) {
                $scope.showAttachmentDetail(data.attachment);
                //使用上级传递的数组
                $scope.uploadItems = data.uploadItems;
            } else {
            }
        });*/
        $scope.deleteAttachment = function (attachment) {
            if (confirm('确认删除该文件?')) {
                $http.delete($scope.getUrl + "attachments/" + attachment.id).success(function () {
                    //upload当中的显示附件列表的数组
                    $scope.uploadItems.splice($scope.uploadItems.indexOf(attachment), 1);
                    $modalInstance.dismiss('cancel');
                }).error(function () {
                    confirm("删除失败！");
                });
            }
        };
        //初始化一个Tag
        $scope.name = "";

        //弹出新建tag表单
        $scope.showCreateTag = function () {
            $scope.name = "";
            $("form.newTagForm").show();
        };
        //取消新建tag
        $scope.hideCreateTag = function () {
            $("form.newTagForm").hide();
            return false;
        };
        //新建tag
        $scope.createTag = function () {
            $scope.newTag = {
                tagname: $scope.name
            };
            $scope.newTagAttach = {
                attachId: $scope.currentAttachment.id,
                attachType: "attachment"
            };

            $scope.newTagAndTagAttach = {
                tag: $scope.newTag,
                tagAttach: $scope.newTagAttach
            };

            $http.post($scope.getUrl + "tags/", $scope.newTagAndTagAttach).success(function (data) {
                $scope.newTag.id = data.id;
                $("form.newTagForm").hide();
                $scope.tagList.push($scope.newTag);
            }).error(function () {
                confirm("要添加的标签可能已存在，添加失败！");
            });
        };
        //删除Tag
        $scope.deleteTag = function (tag) {
            $scope.newTagAttach = {
                tagId: tag.id,
                attachId: $scope.currentAttachment.id,
                attachType: "attachment"
            };
            $http.delete($scope.getUrl + "tags/" + tag.id + "/" + $scope.newTagAttach.attachType + "/" + $scope.newTagAttach.attachId)
                .success(function (data) {
                });
            $scope.tagList.splice($scope.tagList.indexOf(tag), 1);
        };

    }

]);

