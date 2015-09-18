/**
 * Created by TXQ on 12/26/14.
 */
angular.module('data')
    .service('uploadsService', ['url', '$http', '$q', '$upload',
        function(url, $http, $q, $upload) {
            var _attachmentDTOs = [],
                _totalAttachmentCounts,
            //要上传的文件数组
                uploads = [],
            // 临时上传文件
                tempfile;


            var findUploadDTOIdx = function (uploadDTO) {
                for (var i = 0; i < _attachmentDTOs.length; i++) {
                    if (uploadDTO.id === _attachmentDTOs[i].attachId) {
                        return i;
                    }
                }
                return -1;
            };

            var changeContentType = function(attachmentDTO) {
                if(attachmentDTO.contentType.match("image")) {
                    attachmentDTO.contentType = "image";
                }
            };


            this.uploadFile = function($files, projectId, companyId) {
                var promises = [];
                for(var i = 0; i < $files.length; i++) {
                    var info = {};
                    //控制文件上传大小
                    if($files[i].size >= 32000000) {
                        info = {
                            stat: "error",
                            msg: $files[i].name+'上传失败：上传文件过大！'
                        }
                    }

                    if(info.stat === "error"){
                        var deferred = $q.defer();
                        deferred.resolve(info);
                        promises.push(deferred.promise);
                        break;
                    }else{
                          uploads.push($files[i]);
                        tempfile = $files[i];

                        //临时保存
                        var promise = $upload.upload({
                            url : url.projectApiUrl(projectId, companyId) + '/attachments/stage',
                            file: tempfile
                        }).then(function(response) {
                            //获取新上传文件的Id
                            return response.data;
                        });
                        promises.push(promise);
                    }


                }
                return $q.all(promises).then(function(response1) {

                    var _promises = [];

                    for(var i = 0; i < response1.length; i++) {
                        if(response1[i].stat && response1[i].stat === "error"){
                            return response1[i];
                        }
                    }

                    for(var i = 0; i < response1.length; i++) {
                        var data = response1[i];
                        var attchId = data.id;
                        var form = {
                            "attachmentIds": [attchId],
                            "subscribers"  : null
                        };

                        var p = $http.post(url.projectApiUrl(projectId, companyId) + '/uploads', form
                        ).then(function(response) {
                                return response.data;
                            });
                        _promises.push(p);

                    }

                    return $q.all(_promises).then(function(response2) {
                        uploads.splice(0);
                        return _attachmentDTOs;
                    });

                });

            };

            this.getUploads = function() {
                return uploads;
            };

            this.InitUploadsList = function(projectId, companyId) {
                _attachmentDTOs.splice(0);

                return this.getUploadsByStartNum(0, projectId, companyId);
            };

            this.getUploadsByStartNum = function(startNum, projectId, companyId) {
                var data = {};
                var get_url = [url.projectApiUrl(projectId, companyId), '/attachments', '?start=', startNum].join("");
                return $http.get(get_url).then(function(result) {
                    _totalAttachmentCounts = result.data.total;
                    var attachments = result.data.attachmentDTOs;
                    for(var i = 0; i < attachments.length; i++) {
                        changeContentType(attachments[i]);
                    }
                    $.merge(_attachmentDTOs, attachments);
                    data = {
                        "attachmentDTOs":_attachmentDTOs
                    };
                    return data;
                });
            };

            this.getTotalCounts = function() {
                return _totalAttachmentCounts;
            };

            this.getUploadById = function(uploadId, projectId, companyId) {
                var get_url = [url.projectApiUrl(projectId, companyId), '/uploads/', uploadId].join("");
                return $http.get(get_url).then(function(response) {
                    return response.data;
                })
            };

            this.deleteUploadById = function(upload,size) {
                var del_url = [url.projectApiUrl(upload.projectId, upload.companyId), '/uploads/', upload.id, '/delete'].join("");

                return $http.delete(del_url).then(function(response){
                    return response;
                });
            };

            this.webSocketCreateUpload = function(uploadDTO) {
                this.getUploadById(uploadDTO.id, uploadDTO.projectId, uploadDTO.companyId).then(function(data) {
                    changeContentType(data.uploadAttachmentDTO);
                    _attachmentDTOs.splice(0, 0, data.uploadAttachmentDTO);
                });
            };

            this.webSocketUpdateUpload = function(uploadDTO) {
                console.log("Did not implement upload edit!");
            };

            this.webSocketDeleteUpload = function(uploadDTO) {
                var idx = findUploadDTOIdx(uploadDTO);
                if(idx > -1) _attachmentDTOs.splice(idx, 1);
            };

        }]);
