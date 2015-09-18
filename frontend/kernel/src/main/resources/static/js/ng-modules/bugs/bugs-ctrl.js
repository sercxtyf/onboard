/************************** projectApp angular module **************************/
    // 任务子页面
angular.module('bugs')
    .config(['$stateProvider',
        function($stateProvider) {
            $stateProvider
                .state('company.project.bugs', {
                    url        : '/bugs',
                    templateUrl: 'bugs.html',
                    controller : 'bugsCtrl'
                })
                .state('company.project.bugs.opened', {
                    url        : '/opened',
                    templateUrl: 'bugsOpened.html',
                    controller : 'bugsOpenedCtrl'
                })
                .state('company.project.bugs.finished', {
                    url        : '/finished',
                    templateUrl: 'bugsFinished.html',
                    controller : 'bugsFinishedCtrl'
                }).state('company.project.bugs.stats', {
                    url        : '/stats',
                    templateUrl: 'bugsStats.html',
                    controller : 'bugsStatsCtrl'
                })
        }
    ])
    .controller('bugsCtrl', ['$scope', '$state', 'bugService', 'drawer', '$timeout',
        function($scope, $state, bugService, drawer, $timeout) {
    	
        	$scope.memberFilterHide = false;
            $scope.projectId = $state.params.projectId;
            $scope.companyId = $state.params.companyId;

            $scope.predicate = 'dueTime';
            $scope.reverse = false;

            $scope.openNewBugDrawer = function($event) {
                $event.stopPropagation();
                drawer.open({
                    type: 'new-bug'
                });

            };

            $scope.viewBugDrawer = function($event, bug) {
                $event.stopPropagation();
                $timeout(function() {
                    drawer.open({
                        type  : 'bug',
                        params: { id: bug.id }
                    });
                });
            };

            $scope.priorities = bugService.priorities;

            $scope.defaultUser = { id: 0, name: '全体成员' };
            $scope.user = $scope.defaultUser;
            $scope.$on('chooseUser', function(event, user) {
                $scope.user = user;
            });

            $scope.filterUser = function(item) {
                return $scope.user.id === 0 || $scope.user.id === item.assigneeId;
            };

            $state.go('company.project.bugs.opened');
        }
    ])
    .controller('newBugCtrl', ['$scope', '$state', 'bugService', 'user', 'richtexteditor', 'drawer',
        function($scope, $state, bugService, user, richtexteditor, drawer) {
            $scope.projectId = $state.params.projectId;
            $scope.companyId = $state.params.companyId;

            //bug titile
            $scope.title = "";

            //bug priority
            $scope.defaultPriority = {
                value: 3,
                desc : '重要'
            };
            $scope.priorities = bugService.priorities;
            $scope.setPriority = function(priority) {
                $scope.defaultPriority = $scope.priorities[priority.value - 1];
            };

            //bug assignee
            user.getProjectUsers().then(function(users) {
                $scope.projectUsers = users.slice();
                $scope.initBugDescription();
            });
            user.getCurrentUser().then(function(data) {
                $scope.defaultUser = data;
            });
            $scope.setBugAssignee = function(user) {
                $scope.defaultUser = user;
            };

            //bug dueTime
            $scope.dueTime = new Date().toJSON().slice(0, 10);
            $scope.openDTP = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dtpOpened = true;
            };

            //bug status
            $scope.defaultStatus = { desc: '未开始', value: 1 };
            $scope.setBugStatus = function(status) {
                $scope.defaultStatus = status;
            };
            bugService.getBugStatus($scope.projectId, $scope.companyId).then(function(data) {
                $scope.statusSets = data;
            });

            $scope.initBugDescription = function() {
                //bug description
                $scope.description = "";
                $scope.bugAttachments = [];
                var bugDescDiv = $('#newBug').find('textarea[name="description"]');
                richtexteditor.initEditAreaFullTools(bugDescDiv, {}, $scope.bugAttachments);
                $(bugDescDiv).code('<p>Bug 详细描述：</p><ul><li><br></li></ul><p>Bug 重现步骤：</p><ol><li><br></li><li><br></li><li><br></li></ol><p><br></p><p><br></p>');
                $('#newBug').find('input[name="title"]').focus();
            };

            $scope.createBug = function() {
                var newBug = {
                    title      : $scope.title,
                    priority   : $scope.defaultPriority.value,
                    status     : $scope.defaultStatus.value,
                    dueTime    : $scope.dueTime,
                    assigneeId : $scope.defaultUser.id,
                    description: $('#newBug').find('textarea[name="description"]').code()
                };
                bugService.createBug(newBug, $scope.projectId, $scope.companyId).then(function(data) {
                    $state.go('company.project.bugs.opened',{},{reload:true});
                    drawer.close();
                });
            };

        }
    ])
    .controller('viewBugCtrl', ['$scope', '$state', 'bugService', 'drawer', 'user', 'url', 'richtexteditor', '$sce', '$timeout', 'tab', 'keywordService',
        function($scope, $state, bugService, drawer, user, url, richtexteditor, $sce, $timeout, tab, keywordService) {
    		$scope.$parent.memberFilterHide = false;
    		
            if($scope.projectId === undefined)
                $scope.projectId = $state.params.projectId;
            $scope.companyId = $state.params.companyId;

            $scope.bugTabs = [
                tab.getTabInfo("comment", true),
                tab.getTabInfo("commit"),
                tab.getTabInfo("activity")];

            bugService.getBugById($scope.id, $scope.projectId, $scope.companyId).then(function(data) {
                $scope.bug = data;
                $scope.$broadcast('updateTab', {
                    attachType: 'bug',
                    attachId  : data.id,
                    projectId : $scope.projectId,
                    companyId : $scope.companyId
                });

            }, function(error) {
                console.log("获取bug信息失败");
            });

            $scope.updateBug = function(bug) {
                bugService.updateBug(bug, $scope.projectId, $scope.companyId).then(function(data) {
                    console.log("更新bug完成！");
                }, function(error) {
                    console.log("更新bug信息失败！");
                });
            };

            $scope.deleteBug = function() {
                if(confirm("要删除此bug吗？")) {
                    bugService.deleteBug($scope.bug.id, $scope.projectId, $scope.companyId).then(function(data) {
                        console.log("bug 删除成功");
                        drawer.close();
                        $state.go('company.project.bugs', {}, { reload: true });
                    }, function(error) {
                        console.log("bug 删除失败");
                    });
                }

            };

            //bug title
            $scope.showTitle = false;
            $scope.editTitle = function() {
                $scope.oldTitle = $scope.bug.title;
                $scope.showTitle = true;
                $timeout(function() {
                    $('#viewBug').find('input[name="title"]').focus();
                });

            };
            $scope.updateTitle = function() {
                if($scope.oldTitle !== $scope.bug.title && $scope.bug.title.length <= 100) {
                    $scope.updateBug($scope.bug);
                } else {
                    console.log("更新bug标题失败，请重新修改bug标题！");
                    $scope.bug.title = $scope.oldTitle;
                }
                $scope.showTitle = false;
            };

            // bug priority
            $scope.priorities = bugService.priorities;
            $scope.setPriority = function(priority) {
                $scope.bug.priority = priority.value;
                $scope.updateBug($scope.bug);
            };

            $scope.openDTP = function($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dtpOpened = true;
            };

            //bug status
            $scope.statusSets = [];
            bugService.getBugStatus($scope.projectId, $scope.companyId).then(function(data) {
                $scope.statusSets = data;
            });
            $scope.getStatus = function(bugStatus) {
                var s = $scope.statusSets;
                for(var i = 0; i < s.length; i++) {
                    if(bugStatus === s[i].value) {
                        return s[i];
                    }
                }
            };

            $scope.setBugStatus = function(status) {
                $scope.bug.status = status.value;
                $scope.updateBug($scope.bug);
            };

            //bug assignee
            user.getProjectUsers(false, $scope.projectId, $scope.companyId).then(function(users) {
                $scope.projectUsers = users.slice();
            });
            $scope.setBugAssignee = function(user) {
                $scope.bug.assigneeId = user.id;
                $scope.updateBug($scope.bug);
            };

            // bug description
            $scope.bugTrustDangerousSnippet = function(htmlString) {
                return $sce.trustAsHtml(htmlString);
            };

            $scope.showDescription = false;
            var bugDescDiv = $('#viewBug').find('textarea[name="description"]');
            $scope.editDescription = function() {
                richtexteditor.initEditAreaFullTools(bugDescDiv, {}, $scope.bugAttachments);
                $(bugDescDiv).code($scope.bug.description);
                $scope.showDescription = true;
            };
            $scope.cancelEditDescription = function() {
                bugDescDiv.destroy();
                $scope.showDescription = false;
            };

            $scope.updateBugDesc = function() {
                $scope.bug.description = bugDescDiv.code();
                $scope.showDescription = false;
                $scope.updateBug($scope.bug);
            };

            var projectId = $scope.projectId === undefined ? url.projectId() : $scope.projectId;
            keywordService.getBugRecommend($scope.id, projectId, url.companyId()).then(function(data) {
                $scope.recommend = data;
                console.log($scope.recommend);
            });

        }

    ])
    .controller('bugsOpenedCtrl', ['$scope', '$state', 'bugService', 'drawer', '$http', 'url', '$timeout',
        function($scope, $state, bugService, drawer, $http, url, $timeout) {
    		$scope.$parent.memberFilterHide = false;
            $scope.hasNext = true;
            $scope.page = 0;
            $scope.busy = false;
            $scope.displayMoreBugs = function() {
                return $scope.bugList.length > 30 ? true : false;
            };
            $scope.loadMoreBugs = function() {
                $scope.busy = true;

                bugService.getBugOpenedListByPage($scope.projectId, $scope.companyId, $scope.page).then(function(data) {
                    $scope.hasNext = data.hasNext;
                    $scope.page = data.page;
                    $scope.bugList = data.bugs;
                    $scope.busy = false;
                    getAveBugDuration();
                }, function(error) {
                    console.log("Error happens when fetching bug list data!");
                });

            };

            var getAveBugDuration = function(){
                var _url = url.projectApiUrl() + '/bugs/aveDuration';
                $http.get(_url).then(function(response){
                    var duration = response.data;
                    if(duration !== 0){
                        var now = new Date();
                        $scope.bugList.forEach(function(bug){
                            if(now - bug.createdTime > duration)
                                $.extend(bug, {level: 'warning'});
                        });
                        getThirdQuarterDuration();
                    }
                });
            };

            var getThirdQuarterDuration = function(){
                var _url = url.projectApiUrl() + '/bugs/thirdQuarterDuration';
                $http.get(_url).then(function(response){
                    var duration = response.data;
                    if(duration !== 0){
                        var now = new Date();
                        $scope.bugList.forEach(function(bug){
                            if(now - bug.createdTime > duration)
                                $.extend(bug, {level: 'alert'});
                        });
                    }
                    $timeout(function(){
                        $('.level > [data-toggle="tooltip"]').tooltip();
                    }, 1000);
                });
            };
        }
    ])
    .controller('bugsFinishedCtrl', ['$scope', 'bugService',
        function($scope, bugService) {
    		$scope.$parent.memberFilterHide = false;
            $scope.hasNext = true;
            $scope.page = 0;
            $scope.busy = false;
            $scope.displayMoreFinishedBugs = function() {
                return $scope.bugFinishedList.length > 30 ? true : false;
            };
            $scope.loadMoreFinishedBugs = function() {
                $scope.busy = true;
                bugService.getBugFinishedListByPage($scope.projectId, $scope.companyId, $scope.page).then(function(data) {
                    $scope.hasNext = data.hasNext;
                    $scope.page = data.page;
                    $scope.bugFinishedList = data.bugs;
                    $scope.busy = false;
                }, function(error) {
                    console.log("Error happens when fetching bug list data!");
                });

            };

        }
    ]);


