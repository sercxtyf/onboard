angular.module('company')
    .config(['$stateProvider', function($stateProvider) {
        $stateProvider
            .state('company.search', {
                url: '/search?key&userId',
                templateUrl: 'search.html',
                controller: 'searchCtrl'
            });
    }])
    .controller('searchCtrl', ['$scope', '$http', '$rootScope', '$state', 'url', '$stateParams', 'user', 'company', '$q',
        function($scope, $http, $rootScope, $state, url, $stateParams, user, company, $q) {
            $scope.companyId = $stateParams.companyId;
            $scope.key = $stateParams.key;
            var defaultUser = {
                id: 0,
                name: '全体成员'
            };
            $scope.userOptions = [defaultUser];

            $scope.url = '/api/' + $scope.companyId + '/search';
            $scope.type = 'all';

            $scope.solar = true;
            $scope.companyUsers = [];
            $scope.projects = [];
            var currentTab = 0;
            var nextPageArray, hasNextArray, itemArray;
            var orders = {
                all: 0,
                todo: 1,
                todolist: 2,
                discussion: 3,
                document: 4,
                comment: 5,
                event: 6
            };
            var total = 7;
            var resetData = function() {
                nextPageArray = [];
                hasNextArray = [];
                itemArray = [];
                for (var i = 0; i < total; i++) {
                    nextPageArray.push(1);
                    hasNextArray.push(true);
                    itemArray.push([]);
                }
                $scope.busy = false;
                $scope.nextPage = 1;
                $scope.items = [];
                $scope.hasNext = true;
            };

            $scope.init = function() {
                resetData();
                $q.all([user.getCompanyUsersOrderByLastWeekActivities(false, url.companyId()), company.getProjects]).then(function(result) {
                    $scope.companyUsers = result[0];
                    $scope.projects = result[1];
                    $scope.userOptions = $scope.userOptions.concat($scope.companyUsers);
                    var user = getUserById($stateParams.userId);
                    if (user !== null) {
                        $scope.userId = $stateParams.userId;
                        $scope.searchUser = user;
                    } else {
                        $scope.userId = 0;
                        $scope.searchUser = defaultUser;
                    }
                    if (!$scope.solar) {
                        itemArray[0] = preProcess(items);
                        $scope.items = itemArray[0];
                    } else {
                        $scope.addMoreResult(0);
                        $scope.items = itemArray[0];
                    }
                });
            };
            $scope.init();

            $scope.isSet = function(type) {
                return $scope.type === type;
            };
            $scope.setType = function(type) {
                if ($scope.busy === true)
                    return;
                if ($scope.type !== type) {
                    $scope.type = type;
                    currentTab = orders[type];
                    $scope.hasNext = hasNextArray[currentTab];
                    $scope.nextPage = nextPageArray[currentTab];
                    $scope.items = itemArray[currentTab];
                    if ($scope.hasNext && $scope.items.length === 0) {
                        $scope.addMoreResult(currentTab);
                    }
                }
            };

            $scope.changeUser = function() {
                resetData();
                if($scope.userId === 0)
                    $scope.searchUser = defaultUser;
                else
                    $scope.searchUser = getUserById($scope.userId);
                $scope.addMoreResult(currentTab);
            };

            /*$scope.filterType = function(item) {
                if ($scope.type === 'all') {
                    return true;
                } else {
                    return item.type === $scope.type;
                }
            };*/

            var getUserById = function(id) {
                for (var key in $scope.companyUsers) {
                    if ($scope.companyUsers[key].id === id) {
                        return $scope.companyUsers[key];
                    }
                }
                return null;
            };

            $scope.getSearchResults = function(successfun) {
                $http.get($scope.url, {
                        params: {
                            page: $scope.nextPage,
                            key: $scope.key,
                            type: $scope.type,
                            userId: $scope.searchUser.id
                        }
                    })
                    .success(successfun)
                    .error(function() {
                        alert('获取搜索结果失败');
                        $scope.busy = false;
                    });
            };

            var pagination = function() {
                // 自己实现的分页
                var $page = $(document);
                var $pageContainer = $(window);
                var scrolltrigger = 0.95;
                var scrollFun = function() {

                    var wintop = $pageContainer.scrollTop(),
                        docheight = $page.height(),
                        winheight = $pageContainer.height();

                    if ((wintop / (docheight - winheight)) > scrolltrigger) {
                        if ($scope.busy === false) {
                            $scope.addMoreResult(currentTab);
                            $pageContainer.unbind('scroll', scrollFun);
                        }
                    }
                };
                $pageContainer.unbind('scroll', scrollFun);
                $pageContainer.scroll(scrollFun);
            };
            var preProcess = function(data) {
                for (var key in data) {
                    if (data[key].creatorAvatar !== undefined) {
                        data[key].creatorAvatarUrl = url.avatarUrl(data[key].creatorAvatar);
                    } else {
                        var creator = $scope.searchUser.id === data[key].creatorId ? $scope.searchUser : getUserById(data[key].creatorId);
                        if (creator === null) {
                            data[key].creatorAvatarUrl = url.defaultAvatarUrl;
                        } else {
                            data[key].creatorAvatarUrl = creator.avatarUrl;
                            if (data[key].creatorName === undefined) {
                                data[key].creatorName = creator.name;
                            }
                        }
                    }
                    // fill assigneeName
                    data[key].assigneeName = '';
                    for (var i = 0; i < data[key].relatorIds.length; i++) {
                        var id = data[key].relatorIds[i];
                        if (id !== data[key].creatorId) {
                            var assignee = getUserById(id);
                            if (assignee !== null)
                                data[key].assigneeName = assignee.name;
                            break;
                        }
                    }
                }
                return data;
            };

            $scope.addMoreResult = function(tab) {
                if (tab === undefined) {
                    tab = currentTab;
                }
                if ($scope.hasNext && $scope.busy === false) {
                    //console.log("getting data of page " + $scope.nextPage);
                    $scope.busy = true;
                    $scope.getSearchResults(function(data) {
                        $.merge(itemArray[tab], preProcess(data.results));
                        hasNextArray[tab] = data.hasNext;
                        nextPageArray[tab] += 1;
                        // in case tab has been switched during the process
                        if (tab === currentTab) {
                            $scope.hasNext = hasNextArray[tab];
                            $scope.nextPage = nextPageArray[tab];
                        }
                        $scope.busy = false;
                        pagination();
                    });
                }
            };

            var items = [{
                modelId: 1905236,
                creatorId: 1,
                creatorName: '罗睿辞',
                modelType: 'event',
                title: 'Mock Test',
                projectName: 'Onboard',
                projectId: 34,
                createdTime: 1417381004696,
                relatorIds: [],
                content: null
            }, {
                modelId: 1,
                creatorId: 1,
                creatorName: '许 辰',
                modelType: 'comment',
                attachTitle: 'onboard 首页',
                projectName: 'Onboard Web',
                projectId: 34,
                createdTime: 1417381982773,
                relatorIds: [1, 2],
                content: 'https://github.com/Trinea/android-open-project#%E7%AC%AC%E5%9B%9B%E9%83%A8%E5%88%86-%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7%E5%8F%8A%E6%B5%8B%E8%AF%95%E5%B7%A5%E5%85%B7 上面这个链接介绍了一些测试工具和库'
            }, {
                modelId: 2,
                creatorId: 2,
                creatorName: '叶蔚',
                modelType: 'todo',
                title: '从teamforge到onboard',
                projectName: 'onboard iPhone app',
                projectId: 34,
                createdTime: 1417381981773,
                relatorIds: [],
                content: '明天下午1点开个会，明天开始大家从teamforge转到onboard（http://serc.3322.org/stash/projects/TF/repos/onboard/browse） 我会跟大家讲具体事宜，大家务必要到。'
            }, {
                modelId: 3,
                creatorId: 1,
                creatorName: null,
                modelType: 'todolist',
                title: 'Onboard iPhone app 测试包',
                projectName: 'onboard iPhone app',
                projectId: 34,
                createdTime: 1417381082773,
                relatorIds: [],
                content: '讨论和任务功能基本做好了，大家可以测试测试。 这是二维码，看看扫描之后是否可以安装 http://www.pgyer.com/onboard'
            }, {
                modelId: 4,
                creatorId: 1,
                creatorName: '陈龙',
                modelType: 'discussion',
                title: 'onboard依赖更新',
                projectName: 'Onboard',
                projectId: 34,
                createdTime: 1417382082773,
                relatorIds: [],
                content: '请大家及时更新virgo&nbsp;usr仓库<br>这次更新变动较大，大家在部署过程或者开发调试过程中发现任何问题，请及时联系我<br>'
            }, {
                modelId: 5,
                creatorId: 2,
                creatorName: '叶蔚',
                modelType: 'document',
                title: 'Onboard的功能特性（初步整理版）',
                projectName: 'OnboardAndroid',
                projectId: 34,
                createdTime: 1417081004696,
                relatorIds: [],
                content: '<div><b>@</b>梁庆鑫<b><br><br>项目管理（一个软件开发项目的容器，所有相关信息尽在其中）</b></div><div><b>无处不在的讨论（任何东西都是可以讨论的，讨论无处不在）</b></div><div><b>任务管理（任务分配，任务跟踪，任务讨论）</b></div><div><b>文件管理（文件上传与下载，文件打标签）</b></div><div><b>在线文档（Wiki，团队知识）</b></div><div><b>团队管理（成员管理，权限管理）</b></div><div><b>团队日历（组织任务与事件）</b></div><div><b>团队回顾（信息全量记录，项目每日回顾，团队回顾）</b></div><div><b>回收站（找回误删的东西）</b></div><div><b>站内提醒（随时可以收到跟你有关的通知）</b></div><div><b>邮件提醒（任何变化和提醒都会第一时间发送到你的邮箱）</b></div><div><b>代码仓库（一键初始化你的代码仓库）</b></div><div><b>Code Review（清晰简单的代码Review，自动化的Merge操作）</b></div><div><b>代码搜索与统计（可视化的代码信息统计，Commit信息，代码的快速搜索）</b></div><div><b>Forking and Branching（高效的软件开发协同模型）</b></div><div><b>开发跟踪（任务和代码紧密关联，有效跟踪开发进度）<br><br>特性的命名会微调，后面的描述还会大改，先写了一个初步版本。</b></div>'
            }, {
                modelId: 6,
                creatorId: 1,
                creatorName: 'Ruici Luo',
                modelType: 'discussion',
                title: 'onboard_android项目已经迁移',
                projectName: 'Onboard Web',
                projectId: 34,
                createdTime: 1417381000096,
                relatorIds: [],
                content: 'http://onboard.cn/1/projects/34/repos/61 以后请基于该仓库开发'
            }, {
                modelId: 36538758,
                creatorId: 1,
                creatorName: '陈龙',
                modelType: 'attachment',
                title: '搭建 Onboard 开发环境步骤',
                projectName: 'Onboard',
                projectId: 34,
                createdTime: 1317382082773,
                relatorIds: [],
                content: '， virgo-tomcat-server-3.6.2.RELEASE 添加另外一个软件 下载 mybatis migration mybatis-migrations-3.1.0 登陆onboard 克隆代码 http://onboard.cn/1/projects/34/repos/59 安装Mysql 服务器 本地安装redis， 下载 redis-2.0.2 文件夹到本地目录， Eclipse里 Virgo 启动后， 进入 redis-2.0.2 目录'
            }, {
                modelId: 36538759,
                creatorId: 1,
                creatorName: '陈龙',
                modelType: 'attachment',
                title: '搭建 Onboard 开发环境步骤',
                projectName: 'Onboard',
                projectId: 34,
                createdTime: 1317382082773,
                relatorIds: [],
                content: '， virgo-tomcat-server-3.6.2.RELEASE 添加另外一个软件 下载 mybatis migration mybatis-migrations-3.1.0 登陆onboard 克隆代码 http://onboard.cn/1/projects/34/repos/59 安装Mysql 服务器 本地安装redis， 下载 redis-2.0.2 文件夹到本地目录， Eclipse里 Virgo 启动后， 进入 redis-2.0.2 目录'
            }, {
                modelId: 36538760,
                creatorId: 1,
                creatorName: '陈龙',
                modelType: 'attachment',
                title: '搭建 Onboard 开发环境步骤',
                projectName: 'Onboard',
                projectId: 34,
                createdTime: 1317382082773,
                relatorIds: [],
                content: '， virgo-tomcat-server-3.6.2.RELEASE 添加另外一个软件 下载 mybatis migration mybatis-migrations-3.1.0 登陆onboard 克隆代码 http://onboard.cn/1/projects/34/repos/59 安装Mysql 服务器 本地安装redis， 下载 redis-2.0.2 文件夹到本地目录， Eclipse里 Virgo 启动后， 进入 redis-2.0.2 目录'
            }, {
                modelId: 8,
                creatorId: 1,
                creatorName: '亮 邢',
                modelType: 'todo',
                title: 'Onboard for Android新版本',
                projectName: 'OnboardAndroid',
                projectId: 34,
                createdTime: 1407381004696,
                relatorIds: [],
                content: ''
            }];

            pagination();
        }
    ])
    .filter('highlight', function() {
        return function(input, key) {
            if (key) {
                return input.replace(new RegExp(key, 'gi'), '<mark>$&</mark>');
            } else {
                return input;
            }
        };
    });
