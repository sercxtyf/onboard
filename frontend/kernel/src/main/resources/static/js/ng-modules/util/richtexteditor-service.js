/**
 * Created by dudongdong on 2014/12/03.
 *
 * Provide 2 customized rich text editor initialization methods. One for wiki and another for comment.
 *
 *
 */
angular.module('util')
    .service('richtexteditor', ['url', '$http', '$upload', function(url, $http, $upload) {

        /**
         *
         * @param $div
         * @param config  object contains summernote options
         * @param attachmentList (required) array to store uploaded image data
         * http://www.usrtriton.nl/assets/bower/summernote/features.html#heightandfocus
         *
         */
        this.initEditAreaFullTools = function($div, config, attachmentList) {
            //var cfg = { height: 500 };
            var cfg = {};
            $.extend(cfg, config);
            initEditor($div, cfg, attachmentList);
        };

        var attachments;

        var initEditor = function($div, config, attachmentList) {
            attachments = attachmentList;
            if(attachments) {
                attachments.splice(0);
            } else {
                attachments = [];
            }
            var cfg = {
                lang         : 'zh-CN',
                onImageUpload: function(files) {
                    sendfile2(files, $div, attachments);
                }
            };
            $.extend(cfg, config);

            $($div).destroy();
            $($div).summernote(cfg);
        };

        /**
         * @param $div
         * @param config  object contains summernote options
         * http://www.usrtriton.nl/assets/bower/summernote/features.html#heightandfocus
         */
        this.initEditAreaSimpleTools = function($div, config, attachmentList) {
            var cfg = {
                toolbar: [
                    //[groupname, [button list]]
                    ['style', ['bold', 'italic', 'underline', 'clear']],
                    ['font', ['strikethrough']],
                    ['fontsize', ['fontsize']],
                    ['color', ['color']],
                    ['para', ['ul', 'ol', 'paragraph', 'height']],
                    ['insert', ['hr', 'link', 'picture']],
                    ['view', ['codeview']]
                ],
                focus  : true
            };
            $.extend(cfg, config);
            initEditor($div, cfg, attachmentList);
        };

        var sendfile2 = function(files, $div, attachments) {
            for(var i = 0; i < files.length; i++) {
                //控制文件上传大小
                if(files[i].size >= 33554432) {
                    var msg = '上传失败：上传文件过大！';
                    console.log(msg);
                    return;
                }
                var tempfile = files[i];
                //临时保存
                $upload.upload({
                    url : url.projectApiUrl() + '/attachments/stageUploadedImage',
                    file: tempfile
                }).success(function(data) {
                    attachments.push(data);
                    var imgurl = [url.projectApiUrl(), '/attachments/image/', data.id].join("");
                    $($div).summernote("insertImage", imgurl, data.name);
                    return data;
                }).error(function(data, status, headers, config) {
                    var msg = '上传文件失败';
                    console.log(msg)
                })
            }
        };

    }]);
