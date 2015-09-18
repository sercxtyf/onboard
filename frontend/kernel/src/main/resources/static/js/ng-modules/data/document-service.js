/**
 * Created by Dongdong Du on 12/25/2014.
 */

angular.module('data')
    .service('documentService', ['url', '$http', '$q', function(url, $http, $q) {

        var _allDocumentsList = {
                'documents'      : [],
                'latestDocuments': []
            },
            _allDocDetails = {},
            _totalCount = 0;

        this.webSocketCreateDocument = function(documentDTO) {
            _allDocumentsList.documents.unshift(documentDTO);
            this.getLatestThreeDocuments(documentDTO.projectId, documentDTO.companyId);
        };

        var findIdxInDocuments = function(documentDTO) {
            for(var i = 0; i < _allDocumentsList.documents.length; i++) {
                if(documentDTO.id === _allDocumentsList.documents[i].id) {
                    return i;
                }
            }
            console.log("Did not find documentDTO");
            return -1;
        };

        this.webSocketUpdateDocument = function(documentDTO) {
            this.getDocumentById(documentDTO.id).then(function(data) {
                var idx = findIdxInDocuments(documentDTO);
                if(idx > -1) {
                    _allDocumentsList.documents.splice(idx, 1);
                    _allDocumentsList.documents.splice(0, 0, documentDTO);
                }
            });
            this.getLatestThreeDocuments(documentDTO.projectId, documentDTO.companyId);
        };

        this.webSocketDeleteDocument = function(documentDTO) {
            var idx = findIdxInDocuments(documentDTO);
            if(idx > -1) {
                _allDocumentsList.documents.splice(idx, 1);
            }
            this.getLatestThreeDocuments(documentDTO.projectId, documentDTO.companyId);
        };

        this.initDocumentsList = function(projectId, companyId) {
            _allDocumentsList.documents.splice(0);
            _allDocumentsList.latestDocuments.splice(0);
            return $q.all([this.getLatestThreeDocuments(projectId, companyId),
                this.getDocumentsByStartNum(0, projectId, companyId)
            ]).then(function(response) {
                return _allDocumentsList;
            });
        };

        this.getDocsCount = function() {
            return _totalCount;
        };

        this.getLatestThreeDocuments = function(projectId, companyId) {
            var get_latest_docs = url.projectApiUrl(projectId, companyId) + '/documents/latest';
            return $http.get(get_latest_docs).then(function(response) {
                _allDocumentsList.latestDocuments.splice(0);
                $.merge(_allDocumentsList.latestDocuments, response.data.latestDocuments);
                return _allDocumentsList.latestDocuments;
            });
        };

        this.getDocumentsByStartNum = function(startNum, projectId, companyId) {
            var get_docs_path = [url.projectApiUrl(projectId, companyId), '/documents?start=', startNum].join("");
            return $http.get(get_docs_path).then(function(response) {
                _totalCount = response.data.total;
                $.merge(_allDocumentsList.documents, response.data.documents);
                return _allDocumentsList.documents;
            });
        };

        this.docType = {
            TXT : { value: 1, desc: '.txt' },
            MD  : { value: 2, desc: '.md' },
            HTML: { value: 0, desc: '.html' }
        };
        this.createNewDocument = function(title, content, attachments, docType) {
            var newDoc_url = url.projectApiUrl() + '/documents';
            return $http.post(newDoc_url, {
                'title'      : title,
                'content'    : content,
                'attachments': attachments,
                'docType'    : docType
            }).then(function(response) {
                return response.data;
            });
        };

        this.getDocumentById = function(docId) {
            var doc_url = [url.projectApiUrl(), 'documents', docId].join("/");
            return $q.all([$http.get(doc_url).then(function(response) {
                return response.data;
            }), $http.get(doc_url + '/history').then(function(response) {
                return response.data;
            })])
                .then(function(results) {
                    var data1 = results[0];
                    var data2 = results[1];

                    if(_allDocDetails[docId]) {
                        $.extend(_allDocDetails[docId]['documentDetails'], data1.documentDetails);
                        _allDocDetails[docId]['documentHistories'].splice(0);
                        $.extend(_allDocDetails[docId]['documentHistories'], data2.documentHistories);
                    } else {
                        _allDocDetails[docId] = {
                            'documentDetails'  : data1.documentDetails,
                            'documentHistories': data2.documentHistories
                        }
                    }
                    return _allDocDetails[docId];
                });
        };

        this.updateDocument = function(docId, docTitle, docContent, attachments) {
            var update_url = [url.projectApiUrl(), 'documents', docId].join("/");
            var updateData = {
                id         : docId,
                title      : docTitle,
                content    : docContent,
                attachments: attachments
            };
            return $http.put(update_url, updateData).then(function(response) {
                return response.data;
            });
        };

        this.deleteDocument = function(docId) {
            var delete_url = [url.projectApiUrl(), 'documents', docId, 'delete'].join('/');
            return $http.put(delete_url).then(function(response) {
                return response;
            });
        };

        this.getDocumentHistoryInfo = function(docId, version) {
            var docHistoryUrl = [url.projectApiUrl(), 'documents', docId, version].join('/');
            return $http.get(docHistoryUrl).then(function(response) {
                return response.data;
            });
        };

    }]);
