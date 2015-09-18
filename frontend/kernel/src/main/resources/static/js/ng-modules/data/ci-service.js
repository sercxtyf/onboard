/**
 * Created by pkutxq on 15-4-24.
 */
angular.module('data')
    .service('ciService', ['url', '$http',
        function (url, $http) {
            var _buildDTOs = [],
             _ciProjectList = [],
                _ciProject,
                _repository;

            var findCIProjectDTOIdx = function (ciProjectDTO) {
                for (var i = 0; i < _ciProjectList.length; i++) {
                    if (ciProjectDTO.id === _ciProjectList[i].id) {
                        return i;
                    }
                }
                return -1;
            };

            var findCIBuildDTOIdx = function (ciBuildDTO) {
                for (var i = 0; i < _buildDTOs.length; i++) {
                    if (ciBuildDTO.id === _buildDTOs[i].id) {
                        return i;
                    }
                }
                return -1;
            };

            // 从后台获取数据
            this.getCiProjects = function () {
                return $http.get([url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject'].join(""))
                    .then(function (result) {
                        _repository = result.data.repository === null ? "" : result.data.repository;
                        _ciProjectList = result.data.ciProjectList === null ? "" : result.data.ciProjectList;
                        return {
                            repository: _repository,
                            ciProjectList: _ciProjectList
                        };
                    });
            };
            this.getCiBuilds = function (ciProjectId) {
                return $http.get([url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/',ciProjectId].join(""))
                    .then(function (result) {
                        _ciProject = result.data === null ? "" : result.data;
                        _buildDTOs = _ciProject.ciBuildDTOs;
                        return {
                            ciProject: _ciProject
                        };                    
                    });
            };
            this.createCiProject = function (ciProject) {
                var project = angular.copy(ciProject);
                for (var key in ciProject.dockerConfigs) {
                    project.dockerConfigs[key] = ciProject.dockerConfigs[key].value;
                }
                return $http.post([url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/create'].join(""), project).then(function (result) {
                    _ciProject = result.data;
                    return _ciProject;
                });
            };
            this.updateCIProject = function (ciProject, ciProjectId) {
                var project = angular.copy(ciProject);
                for (var key in ciProject.dockerConfigs) {
                    project.dockerConfigs[key] = ciProject.dockerConfigs[key].value;
                }
                return $http.post([url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/', ciProjectId, '/update'].join(""), project).then(function (result) {
                    _ciProject = result.data;
                    return _ciProject;
                });
            };
            this.deleteCIProject = function (ciProjectId) {
                var _delUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/', ciProjectId, '/delete'].join("");
                $http.delete(_delUrl).then(function (result) {
                    return result.data;
                });
            };

            this.createBuild = function (ciProjectId) {
                var _createCiBuildUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/', ciProjectId, '/builds/new'].join("");
                return $http.post(_createCiBuildUrl).then(function (response) {
                    return response.data;
                });
            };
            this.deleteBuild = function(ciProjectId,ciBuildId){
                var _deleteCiBuildUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/', ciProjectId, '/builds/',ciBuildId,'/delete'].join("");
                return $http.delete(_deleteCiBuildUrl).then(function(response){
                   return response.data;
                });
            };
            this.getCiBuildDetail = function (ciProjectId, buildId) {
                var _getCiBuildResultUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/', ciProjectId, '/builds/', buildId].join("");
                return $http.get(_getCiBuildResultUrl).then(function (response) {
                    return response.data;
                });
            };
            this.getCiBuildTestReport = function (ciProjectId, buildId) {
                var _getCiBuildResultUrl = [url.projectApiUrl(url.projectId(), url.companyId()), '/ciProject/', ciProjectId, '/builds/', buildId,'/testresult'].join("");
                return $http.get(_getCiBuildResultUrl).then(function (response) {
                    return response.data;
                });
            };

            this.getCIProjectById = function (companyId, projectId, ciProjectId) {
                var _getCIProjectByIdUrl = [url.projectApiUrl(projectId, companyId), '/ciProject/', ciProjectId].join("");
                return $http.get(_getCIProjectByIdUrl).then(function (response) {
                    return response.data;
                });
            };
            this.webSocketCreateCIProject = function (ciProjectDTO) {
                // _ciProjectList.slice(0,0,data);
                _ciProject = ciProjectDTO;
            };
            this.webSocketUpdateCIProject = function (ciProjectDTO) {
                _ciProject = ciProjectDTO;

            };
            this.webSocketCreateCIBuild = function (ciBuildDTO) {
                _buildDTOs.slice(0, 0, ciBuildDTO);

            };
            this.webSocketUpdateCIBuild = function (ciBuildDTO) {
                var idx = findCIBuildDTOIdx(ciBuildDTO);
                if (idx > -1) {
                    _buildDTOs.splice(idx, 1);
                    _buildDTOs.splice(0, 0, ciBuildDTO);
                }
            };
            this.webSocketDeleteCIBuild = function (ciBuildDTO) {
                var idx = findCIBuildDTOIdx(ciBuildDTO);
                if (idx > -1) {
                    _buildDTOs.slice(idx, 1);
                }

            };

        }]);