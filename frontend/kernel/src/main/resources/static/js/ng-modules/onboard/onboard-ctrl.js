/**
 * Created by harttle on 12/9/14.
 */

angular.module('onboard')

// Onboard 全局控制器
.controller('onboardCtrl', ['$scope', '$rootScope', 'user', 'websocket', 'company', 'url', 'project', '$location', 'pluginService',
                            'storyDataService', 'iterationDataService', 'stepDataService', 'bugDataService', 'userDataService',
                            'topicDataService', 'discussionDataService',
    function($scope, $rootScope, user, websocket, company, url, project, $location, pluginService) {

        // 导航栏需要用户信息

        user.getCurrentUser().then(function(u) {
            $scope.currentUser = u;
        });
        $scope.headerProjects = [];

        $scope.openProjectsList = function() {
            $scope.headerProjects = [];
            company.getProjects(false, url.companyId()).then(function (projects) {
                $scope.headerProjects = projects;
                $("#projectsList ul").show();
            });
        };

        $scope.closeProjectsList = function() {
            $("#projectsList ul").hide();
        };

        $scope.openTeamOpList = function() {
            $("#teamOpList ul").show();
        };

        $scope.closeTeamOpList = function() {
            $("#teamOpList ul").hide();
        };

        $scope.openAccountDropDown = function() {
            $("#accountDropDown ul").show();
        };

        $scope.closeAccountDropDown = function() {
            $("#accountDropDown ul").hide();
        };

        $scope.openProjectPluginsList = function() {
            $("#projectPlugins ul").show();
        };

        $scope.closeProjectPluginsList = function() {
            $("#projectPlugins ul").hide();
        };

        $scope.openCompanyPluginsList = function() {
            $("#companyPlugins ul").show();
        };

        $scope.closeCompanyPluginsList = function() {
            $("#companyPlugins ul").hide();
        };

        $scope.createNewProject = function(){
            project.getActiveProjectNumber(true).then(function(data){
                    $location.url("teams/"+url.companyId()+"/projects/new");
            });
        };
        /*
        $scope.updateProjects = function() {
            $scope.headerProjects = [];
            company.getProjects(true, url.companyId()).then(function (projects) {
                $scope.headerProjects = projects;
            });
        };
        */
        $scope.last = project.getLastVisited();

        $scope.sidePlugins = pluginService.getSidePlugins();
        $scope.companyPlugins = pluginService.getCompanyPlugins();
        $scope.projectPlugins = pluginService.getProjectPlugins();
        $scope.footerPlugins = pluginService.getFooterPlugins();

        // 全局页面逻辑
        websocket.init();
        // Highchart initialize
        Highcharts.setOptions({
            global: {
                useUTC: false
            },
            lang: {
                months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
                shortMonths: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                weekdays: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
                printChart: '下载统计图',
                downloadJPEG: '下载JPEG格式图片',
                downloadPDF: '下载PDF格式图片',
                downloadPNG: '下载PNG格式图片',
                downloadSVG: '下载SVG格式图片',
                loading: '正在下载...',
                noData: '没有数据T_T'
            }
        });
    }
]);
