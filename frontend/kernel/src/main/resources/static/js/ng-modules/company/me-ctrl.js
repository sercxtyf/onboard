/** ************************ angular module js ************************* */

angular.module('company')
    .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.when('/teams/{companyId:[0-9]+}/users/{userId:[0-9]+}',
            '/teams/{companyId:[0-9]+}/users/{userId:[0-9]+}/activities');
        // default child state setup
        // ref: https://github.com/angular-ui/ui-router/wiki/Frequently-Asked-Questions#how-to-set-up-a-defaultindex-child-state
        $stateProvider
            .state('company.me', {
                url: '/users/{userId:[0-9]+}',
                templateUrl: 'me.html',
                controller: 'meCtrl'
            })
            .state('company.me.meActivities', {
                url: '/activities', // empty url for default child state
                templateUrl: 'me-activities.html',
                controller: 'meActivitiesCtrl'
            })
            .state('company.me.meUncompletedTodos', {
                url: '/open_todos',
                templateUrl: 'me-uncompleted-todos.html',
                controller: 'meUncompletedTodosCtrl'
            })
            .state('company.me.meCompletedTodos', {
                url: '/completed_todos',
                templateUrl: 'me-completed-todos.html',
                controller: 'meCompletedTodosCtrl'
            })
            .state('company.me.meAttachments', {
                url: '/attachments',
                templateUrl: 'me-attachments.html',
                controller: 'meAttachmentsCtrl'
            })
            .state('company.me.meWorkTime', {
                url: '/work_time',
                templateUrl: 'me-work-time.html',
                controller: 'meWorkTimeCtrl'
            })
            .state('company.me.meCompletedBugs', {
                url: '/completedBugs',
                templateUrl: 'me-completed-bugs.html',
                controller: 'meCompletedBugsCtrl'
            })
            .state('company.me.meUncompletedBugs', {
                url: '/open_bugs',
                templateUrl: 'me-uncompleted-bugs.html',
                controller: 'meUncompletedBugsCtrl'
            })
            .state('company.me.meCompletedSteps', {
                url: '/completedSteps',
                templateUrl: 'me-completed-steps.html',
                controller: 'meCompletedStepsCtrl'
            })
            .state('company.me.meUncompletedSteps', {
                url: '/open_steps',
                templateUrl: 'me-uncompleted-steps.html',
                controller: 'meUncompletedStepsCtrl'
            });
    }])
    .controller('meCtrl', ['$scope', '$http', '$location', '$stateParams', '$state', 'user', 'url', 'wordCloud',
        function($scope, $http, $location, $stateParams, $state, userService, url, wordCloud) {

            $scope.upyunProtocol = "http://";
            $scope.upyunHost = "teamforge.b0.upaiyun.com";
            $scope.AvatarVersion = "!avatar110";

            $scope.avatarUrlInMe = function(avatar) {
                if (typeof(avatar) != 'undefined') {
                    return [$scope.upyunProtocol, $scope.upyunHost, avatar, $scope.AvatarVersion].join("");
                }
            };

            $scope.companyId = $stateParams.companyId;
            $scope.userId = $stateParams.userId;
            var user_url = ['/api/', $scope.companyId, '/user/', $scope.userId].join("");
            $http.get(user_url).success(function(data) {
                $scope.me = data;
            });
            // check viewable user privilege
            var userPrivilege_url = ['api/', $scope.companyId, '/users/', $scope.userId, '/privilege'].join("");
            $http.get(userPrivilege_url).success(function(data) {
                $scope.isMemberCompanyAdmin = data.isUserCompanyAdmin;
            });

            // check current user privilege
            userService.getCurrentUser().then(function(data) {
                $scope.currentUser = data;
                if ($scope.currentUser.id !== $scope.userId) {
                    userPrivilege_url = ['api/', $scope.companyId, '/users/', $scope.currentUser.id, '/privilege'].join("");
                    $http.get(userPrivilege_url).success(function(data) {
                        $scope.isCurrentUserCompanyOwner = data.isUserCompanyOwner;
                    });
                }
                getUserKeyWords($scope.userId);
            });

            $scope.showWordCloud = true;
            var getUserKeyWords = function(id) {
                var user_keyword_url = [url.teamApiUrl(), '/user/', id, '/keywords'].join('');
                $http.get(user_keyword_url).success(function(data) {
                    if(data.length == 0){
                        $scope.showWordCloud = false;
                        return;
                    }
                    var specs = {};
                    var id = '#word-cloud';
                    specs.id = id;
                    specs.words = d3.nest().key(function(d) {
                        return d.keyword;
                    }).rollup(function(leave) {
                        return leave.reduce(function(acc, ele) {
                            return acc + ele.tfidf * 100;
                        }, 0);
                    }).entries(data);
                    specs.width = parseInt($(id).css('width'));
                    specs.height = 300;
                    console.log(data.length);
                    wordCloud.drawWordCloud(specs);
                    bindWordClickEvent(id);
                });
            };

            var bindWordClickEvent = function(id){
                $(id).find('text').click(function(){
                    //console.log($(this).text());
                    $state.go('company.search', {key: $(this).text(), userId: $scope.userId});
                });
            };

            $scope.setUserCompanyPrivilege = function(userId, userPrivilege) {
                var setCP_url = ['api/', $scope.companyId, '/users/', userId, '/privilege?setCompanyAdmin=', userPrivilege].join("");
                $http.post(setCP_url).success(function(data) {
                    $scope.isMemberCompanyAdmin = userPrivilege;
                });
            };

            $scope.isActive = function(viewLocation) {
                return viewLocation === $location.path();
            };

            var path = $location.path();

            if (path.indexOf('open_todos') > 1) $state.go('company.me.meUncompletedTodos');
            else if (path.indexOf('completed_todos') > 1) $state.go('company.me.meCompletedTodos');
            else if (path.indexOf('attachments') > 1) $state.go('company.me.meAttachments');
            else if (path.indexOf('work_time') > 1) $state.go('company.me.meWorkTime');
            else if (path.indexOf('open_bugs') > 1) $state.go('company.me.meUncompletedBugs');
            else if (path.indexOf('completedBugs') > 1) $state.go('company.me.meCompletedBugs');
            else if (path.indexOf('open_steps') > 1) $state.go('company.me.meUncompletedSteps');
            else if (path.indexOf('completedSteps') > 1) $state.go('company.me.meCompletedSteps');
            else $state.go('company.me.meActivities');
        }
    ])
    .controller('meActivitiesCtrl', ['$scope', '$http', '$state', 'drawer', function($scope, $http, $state, drawer) {

        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/activities'].join("");
        $scope.activities = {};
        $scope.activitiesKeys = [];
        $scope.hasNext = true;
        $scope.until = "";
        $scope.busy = false;

        $scope.loadMoreItems = function() {
            $scope.busy = true;
            var params = {};
            if ($scope.until) params.until = $scope.until;

            $http.get(get_url, {
                params: params
            }).success(function(data) {
                $scope.hasNext = data.hasNext;
                $scope.until = data.nextPage;

                $.extend($scope.activities, data.activities);
                var keys = _.keys(data.activities);
                keys = keys.reverse();
                $.merge($scope.activitiesKeys, keys);
                $scope.busy = false;
            });
        };

        if ($scope.activitiesKeys.length === 0) {
            $scope.loadMoreItems();
        }
    }])
    .controller('meUncompletedTodosCtrl', ['$scope', '$http', function($scope, $http) {
        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/open_todos'].join("");
        $http.get(get_url).success(function(data) {
            $scope.uncompletesTodoLists = data.uncompletedTodos;
            $scope.projectsName = data.projectsName;
            $scope.users = data.userDtos;
            $scope.user = data.user;

        });
    }])
    .controller('meCompletedTodosCtrl', ['$scope', '$http', function($scope, $http) {
        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/completed_todos'].join("");

        $scope.busy = false;
        $scope.hasNext = true;
        $scope.until = '';

        $scope.completedTodos = {};
        $scope.projectsName = {};
        $scope.completedTodosKeys = [];

        $scope.loadMoreItems = function() {
            $scope.busy = true;
            var params = {};
            if ($scope.until) params.until = $scope.until;

            $http.get(get_url, {
                params: params
            }).success(function(data) {
                $scope.hasNext = data.hasNext;
                $scope.until = data.nextPage;

                $.extend($scope.completedTodos, data.completedTodos);
                $.extend($scope.projectsName, data.projectsName);
                var keys = _.keys(data.completedTodos);
                keys = keys.reverse();
                $.merge($scope.completedTodosKeys, keys);
                $scope.busy = false;
            });
        };

        if ($scope.completedTodosKeys.length === 0) {
            $scope.loadMoreItems();
        }

    }])
    .controller('meAttachmentsCtrl', ['$scope', '$http', '$rootScope', '$modal', 'drawer', '$state', function($scope, $http, $rootScope, $modal, drawer, $state) {
        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/attachments'].join("");
        $scope.busy = false;
        $scope.hasNext = true;
        $scope.until = '';

        $scope.userAtachmentsMap = {};
        $scope.attachmentsKeys = [];

        $scope.loadMoreItems = function() {
            $scope.busy = true;

            var params = {};
            if ($scope.until) params.until = $scope.until;

            $http.get(get_url, {
                params: params
            }).success(function(data) {
                $scope.until = data.nextPage;
                $scope.hasNext = data.hasNext;

                var attachments = data.userAttachments;
                var keys = _.keys(attachments);
                keys = keys.reverse();

                for (var i = 0; i < keys.length; i++) {
                    for (var j = 0; j < attachments[keys[i]].length; j++) {
                        if (attachments[keys[i]][j].contentType.match('image')) {
                            attachments[keys[i]][j].contentType = "image";
                        } else if (attachments[keys[i]][j].contentType.match('word')) {
                            attachments[keys[i]][j].contentType = "word";
                        }
                    }
                }
                $.extend($scope.userAtachmentsMap, attachments);
                $.merge($scope.attachmentsKeys, keys);
                $scope.busy = false;

            }).error(function(data) {
                console.log("getError");
            });
        };

        if ($scope.attachmentsKeys.length === 0) $scope.loadMoreItems();

        $scope.showAttachmentDetail = function(attachment, dateKey) {
            $scope.tempItems = [attachment, $scope.userAtachmentsMap[dateKey], $scope.companyId];
            $modal.open({
                templateUrl: 'attachmentDetail.html',
                controller: 'attachmentInfoCtrl',
                size: 'lg',
                resolve: {
                    items: function() {
                        return $scope.tempItems;
                    }
                }
            });
        };

    }])
    .controller('meWorkTimeCtrl', ['$scope', '$http', '$rootScope', 'user', function($scope, $http, $rootScope, userService) {
        $scope.canAdd = false;
        userService.getCurrentUser().then(function(data) {
            $scope.currentUser = data;
            if ($scope.currentUser.id == $scope.userId)
                $scope.canAdd = true;
        });
        $scope.boardStart = new Date();
        $scope.dayStart = new Date();
        $scope.now = new Date();

        $scope.boardStart.setTime($scope.now.getTime());
        $scope.boardStart.setHours(0, 0, 0, 0);
        $scope.boardStart.setTime($scope.boardStart.getTime() - 6 * 24 * 3600 * 1000);

        $scope.dayStart.setTime($scope.now.getTime());
        $scope.dayStart.setHours(0, 0, 0, 0);

        $scope.fromBoard = function(t) {
            return Math.floor((t.getTime() - $scope.boardStart.getTime()) / (1800 * 1000));
        };

        $scope.fromDay = function(t) {
            return Math.floor((t.getTime() - $scope.dayStart.getTime()) / (1800 * 1000));
        };

        $scope.setBoard = function(num) {
            return new Date($scope.boardStart.getTime() + num * 1800 * 1000);
        };

        $scope.setDay = function(num) {
            return new Date($scope.dayStart.getTime() + num * 1800 * 1000);
        };

        $scope.getTimeText = function(t) {
            return (t.getHours() < 10 ? '0' : '') + t.getHours() + ':' + (t.getMinutes() ? '30' : '00');
        };

        $scope.getDateText = function(t) {
            return (t.getMonth() + 1) + '月' + t.getDate() + '日';
        };

        $scope.now = $scope.setDay($scope.fromDay($scope.now));

        // getData

        // init
        $scope.ed = $scope.fromDay($scope.now);
        $scope.st = $scope.ed - 4;
        $scope.day = 6;
        if ($scope.st < 0) $scope.st = 0;

        $scope.newDateText = $scope.getDateText($scope.setBoard($scope.day * 48));
        $scope.newStartText = $scope.getTimeText($scope.setDay($scope.st));
        $scope.newEndText = $scope.getTimeText($scope.setDay($scope.ed));

        $('#timeSlider').slider({
            range: true,
            min: 0,
            max: 48,
            values: [$scope.st, $scope.ed],
            slide: function(event, ui) {
                $scope.st = ui.values[0];
                $scope.ed = ui.values[1];
                $scope.newStartText = $scope.getTimeText($scope.setDay($scope.st));
                $scope.newEndText = $scope.getTimeText($scope.setDay($scope.ed));
                if (!$rootScope.$$phase) $rootScope.$apply();
            }
        });

        $('#dateSlider').slider({
            min: 0,
            max: 6,
            values: [6],
            slide: function(event, ui) {
                $scope.day = ui.values[0];
                $scope.newDateText = $scope.getDateText($scope.setBoard($scope.day * 48));
                if (!$rootScope.$$phase) $rootScope.$apply();
            }
        });

        $scope.addWorkTime = function() {
            $('#addTime').prop('disabled', true).text('正在添加');

            var date = {
                start: $scope.setBoard($scope.day * 48).getTime() + $scope.st * 1800 * 1000,
                end: $scope.setBoard($scope.day * 48).getTime() + $scope.ed * 1800 * 1000
            };
            $http.post('api/' + $scope.companyId + '/users/' + $scope.userId + '/durations?start=' + date.start + '&end=' + date.end)
                .success(function(data) {
                    $('#addTime').prop('disabled', false).text('添加时间');
                    $scope.flushWorkRec();
                });
        };

        // workRec
        $scope.dateList = [];
        for (var i = 0; i < 7; ++i)
            $scope.dateList[i] = $scope.getDateText($scope.setBoard(i * 48));

        $scope.show = function($index) {
            if ($index % 4 == 0) return $scope.getTimeText($scope.setDay($index));
            else return '　　　';
        };

        $scope.workRec = new Array();
        for (i = 0; i < 48; ++i) {
            $scope.workRec[i] = new Array();
            for (var j = 0; j < 7; ++j)
                $scope.workRec[i][j] = {
                    worked: false,
                    id: -1
                }
        }

        $scope.flushWorkRec = function() {
            $http.get('api/' + $scope.companyId + '/users/' + $scope.userId + '/durations?start=' + $scope.boardStart.getTime() + '&end=' + $scope.now.getTime())
                .success(function(data) {
                    $scope.workRecord = data;
                    for (i = 0; i < 48; ++i) {
                        for (var j = 0; j < 7; ++j)
                            $scope.workRec[i][j] = {
                                worked: false,
                                id: -1,
                                info: '　'
                            }
                    }
                    data.forEach(function(rec) {
                        var end_t = $scope.fromBoard(new Date(rec.endTime));
                        var start_t = $scope.fromBoard(new Date(rec.startTime));
                        console.log(start_t + ' ' + end_t);
                        $scope.workRec[start_t % 48][Math.floor(start_t / 48)] = {
                            worked: true,
                            id: rec.id,
                            info: $scope.getTimeText(new Date(rec.startTime)) + '-' + $scope.getTimeText(new Date(rec.endTime))
                        };
                        if (end_t == start_t) ++end_t;
                        for (i = start_t + 1; i < end_t; ++i)
                            $scope.workRec[i % 48][Math.floor(i / 48)] = {
                                worked: true,
                                id: rec.id,
                                info: '　'
                            }
                    });
                });
        };

        $scope.flushWorkRec();

        $scope.modify = function(id) {
            if (id < 0) return;
            var rec = new Object;
            for (var i = 0; i < $scope.workRecord.length; ++i)
                if ($scope.workRecord[i].id == id) {
                    rec = $scope.workRecord[i];
                }
            $scope.myModal = {
                st: $scope.fromBoard(new Date(rec.startTime)) % 48,
                ed: $scope.fromBoard(new Date(rec.endTime)) % 48,
                day: Math.floor($scope.fromBoard(new Date(rec.startTime)) / 48),
                sText: $scope.getTimeText(new Date(rec.startTime)),
                eText: $scope.getTimeText(new Date(rec.endTime)),
                dText: $scope.getDateText(new Date(rec.endTime)),
                id: rec.id
            };

            $('#timeSlider_modal').slider({
                range: true,
                min: 0,
                max: 48,
                values: [$scope.myModal.st, $scope.myModal.ed],
                slide: function(event, ui) {
                    $scope.myModal.st = ui.values[0];
                    $scope.myModal.ed = ui.values[1];
                    $scope.myModal.sText = $scope.getTimeText($scope.setDay($scope.myModal.st));
                    $scope.myModal.eText = $scope.getTimeText($scope.setDay($scope.myModal.ed));
                    if (!$rootScope.$$phase) $rootScope.$apply();
                }
            });

            $('#dateSlider_modal').slider({
                min: 0,
                max: 6,
                values: [$scope.myModal.day],
                slide: function(event, ui) {
                    $scope.myModal.day = ui.values[0];
                    $scope.myModal.dText = $scope.getDateText($scope.setBoard($scope.myModal.day * 48));
                    if (!$rootScope.$$phase) $rootScope.$apply();
                }
            });

            $('#modifyWorkRec').modal('toggle');
        };

        $scope.deleteRec = function() {
            $http.delete('api/' + $scope.companyId + '/users/' + $scope.userId + '/durations/' + $scope.myModal.id).success(function(data) {
                console.log('已删除');
                $scope.flushWorkRec();
            });
            $('#modifyWorkRec').modal('toggle');
        };

        $scope.updateRec = function() {
            var date = {
                start: $scope.setBoard($scope.myModal.day * 48).getTime() + $scope.myModal.st * 1800 * 1000,
                end: $scope.setBoard($scope.myModal.day * 48).getTime() + $scope.myModal.ed * 1800 * 1000
            };
            $http.put('api/' + $scope.companyId + '/users/' + $scope.userId + '/durations/' + +$scope.myModal.id + '?start=' + date.start + '&end=' + date.end)
                .success(function(data) {
                    $scope.flushWorkRec();
                });
            $('#modifyWorkRec').modal('toggle');
        };


    }])
    .controller('meUncompletedBugsCtrl', ['$scope', '$http','$state', function($scope, $http, $state) {
        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/uncompletedBugs'].join("");
        $scope.bugKeys=[];
        $scope.uncompletesBugs={};
        $scope.companyId = $state.params.companyId;
        $http.get(get_url).success(function(data) {
            var bugs = data.uncompletedBugs;
            var keys = _.keys(bugs);
            $scope.projectsName = data.projectsName;
            $scope.users = data.userDtos;
            $scope.user = data.user;
            $.extend($scope.uncompletesBugs,bugs);
            $.merge($scope.bugKeys,keys);

        });
    }])
    .controller('meCompletedBugsCtrl', ['$scope', '$http','$state', function($scope, $http, $state) {
        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/completedBugs'].join("");

        $scope.busy = false;
        $scope.hasNext = true;
        $scope.until = '';

        $scope.completedBugs = {};
        $scope.projectsName = {};
        $scope.completedBugsKeys = [];

        $scope.loadMoreItems = function() {
            $scope.busy = true;
            var params = {};
            if ($scope.until) params.until = $scope.until;

            $http.get(get_url, {
                params: params
            }).success(function(data) {
                $scope.hasNext = data.hasNext;
                $scope.until = data.nextPage;
                var keys = _.keys(data.completedBugs);
                keys = keys.reverse();
                $scope.companyId = $state.params.companyId;
                $.extend($scope.completedBugs, data.completedBugs);
                $.extend($scope.projectsName, data.projectsName);

                $.merge($scope.completedBugsKeys, keys);
                $scope.busy = false;
            });
        };

        if ($scope.completedBugsKeys.length === 0) {
            $scope.loadMoreItems();
        }

    }])
    .controller('meUncompletedStepsCtrl', ['$scope', '$http','$state', function($scope, $http, $state) {
    var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/uncompletedSteps'].join("");
    $scope.stepKeys=[];
    $scope.uncompletedSteps={};
    $scope.companyId = $state.params.companyId;
    $http.get(get_url).success(function(data) {
        var steps = data.uncompletedSteps;
        var keys = _.keys(steps);
        $scope.projectsName = data.projectsName;
        $scope.users = data.userDtos;
        $scope.user = data.user;
        $.extend($scope.uncompletedSteps,steps);
        $.merge($scope.stepKeys,keys);

        });
    }])
    .controller('meCompletedStepsCtrl', ['$scope', '$http','$state', function($scope, $http, $state) {
        var get_url = ['/api/', $scope.companyId, '/user/', $scope.userId, '/completedSteps'].join("");

        $scope.busy = false;
        $scope.hasNext = true;
        $scope.until = '';

        $scope.completedSteps = {};
        $scope.projectsName = {};
        $scope.completedStepsKeys = [];

        $scope.loadMoreItems = function() {
            $scope.busy = true;
            var params = {};
            if ($scope.until) params.until = $scope.until;

            $http.get(get_url, {
                params: params
            }).success(function(data) {
                $scope.hasNext = data.hasNext;
                $scope.until = data.nextPage;
                var keys = _.keys(data.completedSteps);
                keys = keys.reverse();
                $scope.companyId = $state.params.companyId;
                $.extend($scope.completedSteps, data.completedSteps);
                $.extend($scope.projectsName, data.projectsName);

                $.merge($scope.completedStepsKeys, keys);
                $scope.busy = false;
            });
        };

        if ($scope.completedStepsKeys.length === 0) {
            $scope.loadMoreItems();
        }

    }]);

